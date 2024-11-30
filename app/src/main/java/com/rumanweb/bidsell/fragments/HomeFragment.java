package com.rumanweb.bidsell.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.adapters.ProductsAdapter;
import com.rumanweb.bidsell.models.ProductItemModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    ImageSlider imgSlider;
    RecyclerView product_recycler;
    ProductsAdapter pAdapter;
    List<ProductItemModel> productItemList;
    List<ProductItemModel> filteredList;
    ChipGroup filterChipGroup;
    private FirebaseFirestore db;
    private String url = "https://firebasestorage.googleapis.com/v0/b/bidmaster-5cf93.appspot.com/o/";
    private final String[] sliderImages = {"slider-1.jpg?alt=media&token=bfad170a-9ffb-4b26-a644-7c482a4018b1",
            "slider-2.jpg?alt=media&token=fa326857-4116-4939-b3ca-2c5ee42c82ec",
            "slider-3.jpg?alt=media&token=1aead381-2eae-45f6-bb14-db3ed45b0708",
            "slider-4.jpg?alt=media&token=40d8765a-e99c-46b3-9476-90cdf1432edf"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        imgSlider = myView.findViewById(R.id.imageSlider);
        product_recycler = myView.findViewById(R.id.recyclerView);
        filterChipGroup = myView.findViewById(R.id.filterChipGroup);

        db = FirebaseFirestore.getInstance();

        // Setup image slider
        setupImageSlider();

        // Setup RecyclerView
        product_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        productItemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        pAdapter = new ProductsAdapter(getActivity(), filteredList);
        product_recycler.setAdapter(pAdapter);

        // Setup filter chips
        setupFilterChips();

        // Load products
        loadProducts();

        return myView;
    }

    private void setupImageSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        for (int i = 0; i < sliderImages.length; i++) {
            slideModels.add(new SlideModel(url + sliderImages[i], "Image " + (i + 1), ScaleTypes.FIT));
        }
        imgSlider.setImageList(slideModels);
    }

    private void setupFilterChips() {
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                filterProducts("ALL");
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    filterProducts(chip.getText().toString().toUpperCase());
                }
            }
        });
    }

    private void loadProducts() {
        db.collection("ProductList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productItemList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductItemModel p_model = document.toObject(ProductItemModel.class);
                                productItemList.add(p_model);
                            }
                            filterProducts("ALL"); // Show all products initially
                        } else {
                            Toast.makeText(getContext(), (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void filterProducts(String filter) {
        filteredList.clear();
        Date currentDateTime = new Date();

        for (ProductItemModel product : productItemList) {
            Date openDate = new Date(product.getOpen_date().getTime());
            Date closeDate = new Date(product.getClose_date().getTime());

            boolean shouldAdd = false;
            switch (filter) {
                case "UPCOMING":
                    shouldAdd = currentDateTime.before(openDate);
                    break;
                case "RUNNING":
                    shouldAdd = currentDateTime.after(openDate) && currentDateTime.before(closeDate);
                    break;
                case "CLOSED":
                    shouldAdd = currentDateTime.after(closeDate);
                    break;
                case "ALL":
                    shouldAdd = true;
                    break;
            }
            if (shouldAdd) {
                filteredList.add(product);
            }
        }
        pAdapter.notifyDataSetChanged();
    }
}