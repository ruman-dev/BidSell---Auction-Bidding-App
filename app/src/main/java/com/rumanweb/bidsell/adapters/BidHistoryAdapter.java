package com.rumanweb.bidsell.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.models.BidHistoryLVModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// BidHistoryAdapter class
public class BidHistoryAdapter extends RecyclerView.Adapter<BidHistoryAdapter.ViewHolder> {
    private List<BidHistoryLVModel> bidHistory = new ArrayList<>();
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    public void setBidHistory(List<BidHistoryLVModel> bidHistory) {
        this.bidHistory = bidHistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bid_history_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BidHistoryLVModel model = bidHistory.get(position);
        holder.bidderName.setText(model.getBidderName());
        holder.bidPrice.setText(String.valueOf(model.getBiddingPrice()));

        if (model.getBiddingTime() != null) {
            try {
                Date biddingTimeDate = model.getBiddingTime().toDate();
                String updatedBidTime = displayDateFormat.format(biddingTimeDate);
                holder.biddingTime.setText(updatedBidTime);
            } catch (IllegalArgumentException e) {
                holder.biddingTime.setText("N/A");
                Log.e("DateError", "Invalid date format for biddingTime", e);
            }
        } else {
            holder.biddingTime.setText("N/A");
        }

        Glide.with(holder.itemView.getContext())
                .load(model.getBidderImg())
                .placeholder(R.drawable.icon_profile)
                .error(R.drawable.ic_launcher_background)
                .into(holder.bidderImg);
    }

    @Override
    public int getItemCount() {
        return bidHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bidderImg;
        TextView bidderName;
        TextView bidPrice;
        TextView biddingTime;

        ViewHolder(View itemView) {
            super(itemView);
            bidderImg = itemView.findViewById(R.id.bidderImage);
            bidderName = itemView.findViewById(R.id.bidderName);
            bidPrice = itemView.findViewById(R.id.bidPrice);
            biddingTime = itemView.findViewById(R.id.biddingTime);
        }
    }
}