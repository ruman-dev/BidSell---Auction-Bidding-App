package com.rumanweb.bidsell.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rumanweb.bidsell.activities.ProductDetailsActivity;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.models.ProductItemModel;
import com.rumanweb.bidsell.models.ProductItemModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private Context context;
    private List<ProductItemModel> list;

    public ProductsAdapter(Context context, List<ProductItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.img_url);
        holder.product_title.setText(list.get(position).getTitle());
        holder.product_str_price.setText(String.valueOf(list.get(position).getStarting_price()));

        // Define date formats
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()); // Don't change this format. Database issues

        // Parsing open_date and close_date
        Date openDate, closeDate;
        try {
            openDate = inputFormat.parse(String.valueOf(list.get(position).getOpen_date()));
            closeDate = inputFormat.parse(String.valueOf(list.get(position).getClose_date()));
        } catch (ParseException e) {
            e.printStackTrace();
            openDate = null;
            closeDate = null;
        }

        // Logic for deciding what to show in product_str_time
        Date currentDateTime = new Date();
        String timeToShow = ""; // This will hold the date to display (start date or end date)

        String badge;  // Text for showing "Upcoming", "Running", or "Closed"
        int color;     // Background color for the badge

        if (openDate == null || closeDate == null) {
            holder.product_str_time.setText("Date Error"); // Fallback if parsing fails
            return;
        }

        if (currentDateTime.before(openDate)) {
            // Auction hasn't started yet: Show start date
            badge = "Upcoming";
            color = R.color.brightBlue;
            timeToShow = displayFormat.format(openDate); // Show start date
        } else if (currentDateTime.before(closeDate)) {
            // Auction is running: Show end date
            badge = "Running";
            color = R.color.green;
            timeToShow = displayFormat.format(closeDate); // Show end date
        } else {
            // Auction has closed: Show end date
            badge = "Closed";
            color = R.color.red;
            timeToShow = displayFormat.format(closeDate); // Show end date
        }

        // Set the formatted date and badge
        holder.product_str_time.setText(timeToShow);
        holder.product_badge.setText(badge);
        holder.product_badge.setBackgroundResource(color);

        // Set item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProductDetailsActivity.class);
                myIntent.putExtra("product_serial", list.get(holder.getAdapterPosition()));
                context.startActivity(myIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_url;
        TextView product_title, product_str_price, product_str_time, product_badge;
        LinearLayout productItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_url = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            product_str_price = itemView.findViewById(R.id.product_str_price);
            product_str_time = itemView.findViewById(R.id.product_str_time);
            product_badge = itemView.findViewById(R.id.tvBadge);
            productItemLayout = itemView.findViewById(R.id.productItemLayout);
        }
    }
}
