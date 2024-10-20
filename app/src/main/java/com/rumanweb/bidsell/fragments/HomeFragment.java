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
import java.util.List;

public class HomeFragment extends Fragment {

    ImageSlider imgSlider;
    RecyclerView product_recycler;
    ProductsAdapter pAdapter;
    List<ProductItemModel> productItemList;
    private FirebaseFirestore db;
    private String urlName = "https://firebasestorage.googleapis.com/v0/b/bidmaster-5cf93.appspot.com/o/";
    private final String[] sliderImages = {"slider-1.jpg?alt=media&token=bfad170a-9ffb-4b26-a644-7c482a4018b1", "slider-2.jpg?alt=media&token=fa326857-4116-4939-b3ca-2c5ee42c82ec",
            "slider-3.jpg?alt=media&token=1aead381-2eae-45f6-bb14-db3ed45b0708","slider-4.jpg?alt=media&token=40d8765a-e99c-46b3-9476-90cdf1432edf"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        imgSlider = myView.findViewById(R.id.imageSlider);
        product_recycler = myView.findViewById(R.id.recyclerView);

        db = FirebaseFirestore.getInstance();

        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(urlName+ sliderImages[0], "Image 1", ScaleTypes.FIT));
        slideModels.add(new SlideModel(urlName+sliderImages[1], "Image 2", ScaleTypes.FIT));
        slideModels.add(new SlideModel(urlName+sliderImages[2], "Image 3",ScaleTypes.FIT));
        slideModels.add(new SlideModel(urlName+sliderImages[3], "Image 4",ScaleTypes.FIT));

        imgSlider.setImageList(slideModels);

        product_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        productItemList = new ArrayList<>();
        pAdapter = new ProductsAdapter(getActivity(), productItemList);
        product_recycler.setAdapter(pAdapter);

        db.collection("ProductList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductItemModel p_model = document.toObject(ProductItemModel.class);
                                productItemList.add(p_model);
                                pAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return myView;
    }
}