package com.rumanweb.bidsell.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.adapters.MyBidsAdapter;
import com.rumanweb.bidsell.models.MyBidsModel;

import java.util.List;

public class MyBidsFragment extends Fragment {

    RecyclerView myBidsRecycler;
    ListView myListview;
    MyBidsAdapter myBidsAdapter;
    List<MyBidsModel> myBidsModelList;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_my_bids, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        myBidsRecycler = myView.findViewById(R.id.myBidsRecyclerView);

//        myBidsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        myBidsModelList = new ArrayList<>();
//        myBidsAdapter = new MyBidsAdapter(myBidsModelList);
//        myBidsRecycler.setAdapter(myBidsAdapter);

//        fetchMyBids();
        return myView;

    }

    private void fetchMyBids() {
        if (auth.getCurrentUser() != null) {

            // Query Firestore to get bids for the logged-in user
            db.collection("biddingList").document(auth.getCurrentUser().getUid()).collection("User")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    // Extract bid data from Firestore document
                                    String title = doc.getString("productTitle");
                                    String img_url = doc.getString("productImgUrl"); // Ensure you have an image URL field in Firestore
                                    double biddingPrice = doc.getDouble("biddingPrice");

                                    // Add bid data to the list
                                    myBidsModelList.add(new MyBidsModel(title, img_url, biddingPrice));
                                }

                                // Notify the adapter that data has been updated
                                myBidsAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("FirestoreError", "Error getting documents: ", task.getException());
                                Toast.makeText(getActivity(), "Failed to load bids.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User not authenticated!", Toast.LENGTH_SHORT).show();
        }
    }
}