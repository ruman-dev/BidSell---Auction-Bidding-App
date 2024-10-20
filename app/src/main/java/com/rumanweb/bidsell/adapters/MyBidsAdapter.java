package com.rumanweb.bidsell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.models.MyBidsModel;

import java.util.ArrayList;
import java.util.List;

public class MyBidsAdapter extends RecyclerView.Adapter<MyBidsAdapter.ViewHolder> {

    private List<MyBidsModel> myBidsList;


    public MyBidsAdapter(List<MyBidsModel> myBidsList) {
        this.myBidsList = myBidsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mybids, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyBidsModel myBidsModel = myBidsList.get(position);
        Glide.with(holder.itemView.getContext()).load(myBidsModel.getImg_url()).into(holder.bidderImg);
        holder.biddingTitle.setText(myBidsModel.getTitle());
        holder.biddingPrice.setText(String.valueOf(myBidsModel.getBiddingPrice()));
    }

    @Override
    public int getItemCount() {
        return myBidsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bidderImg;
        TextView biddingTitle, biddingDesc, biddingPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bidderImg = itemView.findViewById(R.id.bidderImg);
            biddingTitle = itemView.findViewById(R.id.biddingTitle);
            biddingDesc = itemView.findViewById(R.id.biddingDesc);
            biddingPrice = itemView.findViewById(R.id.biddingPrice);

        }
    }
}
