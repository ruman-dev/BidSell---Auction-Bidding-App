package com.rumanweb.bidsell.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rumanweb.bidsell.activities.ProductDetailsActivity;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.models.ProductItemModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private static final String TAG = "ProductsAdapter";
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
        ProductItemModel product = list.get(position);

        // Load image and set basic text
        Glide.with(context).load(product.getImg_url()).into(holder.img_url);
        holder.product_title.setText(product.getTitle());
        holder.product_str_price.setText(String.valueOf(product.getStarting_price()));

        // Handle dates
        try {
            // Create input format that matches your exact date string format
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

            // Set timezone to handle GMT+06:00 properly
            TimeZone timeZone = TimeZone.getTimeZone("GMT+06:00");
            inputFormat.setTimeZone(timeZone);
            displayFormat.setTimeZone(timeZone);

            Date openDate = inputFormat.parse(String.valueOf(product.getOpen_date()));
            Date closeDate = inputFormat.parse(String.valueOf(product.getClose_date()));

            if (openDate == null || closeDate == null) {
                throw new ParseException("Parsed date is null", 0);
            }

            // Logic for deciding what to show
            Date currentDateTime = new Date();
            String timeToShow;
            String badge;
            int color;

            if (currentDateTime.before(openDate)) {
                badge = "Upcoming";
                color = R.color.brightBlue;
                timeToShow = "Starts: " + displayFormat.format(openDate);
            } else if (currentDateTime.before(closeDate)) {
                badge = "Running";
                color = R.color.green;
                timeToShow = "Ends: " + displayFormat.format(closeDate);
            } else {
                badge = "Closed";
                color = R.color.red;
                timeToShow = "Ended: " + displayFormat.format(closeDate);
            }

            // Set the formatted date and badge
            holder.product_str_time.setText(timeToShow);
            holder.product_badge.setText(badge);
            holder.product_badge.setBackgroundResource(color);

        } catch (ParseException e) {
            Log.e(TAG, "Date parsing failed for product " + product.getTitle() +
                    "\nOpen date: " + product.getOpen_date() +
                    "\nClose date: " + product.getClose_date(), e);

            holder.product_str_time.setText("Invalid Date Format");
            holder.product_badge.setText("Unknown");
        }

        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            Intent myIntent = new Intent(context, ProductDetailsActivity.class);
            myIntent.putExtra("product_serial", list.get(holder.getAdapterPosition()));
            context.startActivity(myIntent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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