package com.rumanweb.bidsell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rumanweb.bidsell.models.MyBidsModel;
import com.rumanweb.bidsell.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MyBidsAdapter extends RecyclerView.Adapter<MyBidsAdapter.ViewHolder> {

    private List<MyBidsModel> myBidsList;
    private Context context;

    public MyBidsAdapter(Context context, List<MyBidsModel> myBidsList) {
        this.context = context;
        this.myBidsList = myBidsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_bids_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyBidsModel myBidsModel = myBidsList.get(position);
        
        holder.auctionTitle.setText(myBidsModel.getProductTitle());

//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+06:00"));
//        String formattedDate = dateFormat.format(myBidsModel.getBiddingTime());
        holder.auctionDate.setText(myBidsModel.getBiddingTime().toString());
        // Set bid amount
        holder.bidPrice.setText("৳ " + String.format("%.2f", myBidsModel.getBiddingPrice()));

    }

    @Override
    public int getItemCount() {
        return myBidsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView auctionTitle, auctionDate, bidPrice, bidStatus, bidNotes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            auctionTitle = itemView.findViewById(R.id.auction_title);
            auctionDate = itemView.findViewById(R.id.auction_date);
            bidPrice = itemView.findViewById(R.id.myBids_price);
            bidStatus = itemView.findViewById(R.id.tvMaxBid);
            bidNotes = itemView.findViewById(R.id.tvBidsCount);
        }
    }
}
