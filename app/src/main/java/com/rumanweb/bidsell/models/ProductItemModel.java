package com.rumanweb.bidsell.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class ProductItemModel implements Serializable {
    String title, img_url;
    String description, highlights;
    int bid_count, quantity;
    double starting_price;
    long listing_no;
    Date open_date, close_date;
    Timestamp currentTime;

    public ProductItemModel() {
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public int getBid_count() {
        return bid_count;
    }

    public void setBid_count(int bid_count) {
        this.bid_count = bid_count;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getStarting_price() {
        return starting_price;
    }

    public void setStarting_price(double starting_price) {
        this.starting_price = starting_price;
    }

    public long getListing_no() {
        return listing_no;
    }

    public void setListing_no(long listing_no) {
        this.listing_no = listing_no;
    }

    public Date getOpen_date() {
        return open_date;
    }

    public void setOpen_date(Date open_date) {
        this.open_date = open_date;
    }

    public Date getClose_date() {
        return close_date;
    }

    public void setClose_date(Date close_date) {
        this.close_date = close_date;
    }

    public ProductItemModel(String title, String img_url, String description, String highlights, int bid_count, int quantity,
                            double starting_price, long listing_no, Date open_date, Date close_date) {
        this.title = title;
        this.img_url = img_url;
        this.description = description;
        this.highlights = highlights;
        this.bid_count = bid_count;
        this.quantity = quantity;
        this.starting_price = starting_price;
        this.listing_no = listing_no;
        this.open_date = open_date;
        this.close_date = close_date;
    }
}
