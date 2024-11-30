package com.rumanweb.bidsell.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.adapters.MyBidsAdapter;
import com.rumanweb.bidsell.models.MyBidsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyBidsFragment extends Fragment {

    private RecyclerView myBidsRecycler;
    private MyBidsAdapter myBidsAdapter;
    private List<MyBidsModel> myBidsList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_my_bids, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        myBidsRecycler = myView.findViewById(R.id.myBidsRecyclerView);

        myBidsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        myBidsList = new ArrayList<>();
        myBidsAdapter = new MyBidsAdapter(getActivity(), myBidsList);
        myBidsRecycler.setAdapter(myBidsAdapter);

        loadMyBids();
        return myView;
    }

    private void loadMyBids() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            db.collection("biddingList")
                    .document(userId)
                    .collection("User")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                myBidsList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MyBidsModel bid = document.toObject(MyBidsModel.class);
                                    myBidsList.add(bid);
                                }
                                Log.d("MyBidsFragment", "Total bids loaded: " + myBidsList.size());
                                myBidsAdapter.notifyDataSetChanged(); // Notify adapter once after loading all data
                            } else {
                                Toast.makeText(getContext(), "Error loading bids: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
