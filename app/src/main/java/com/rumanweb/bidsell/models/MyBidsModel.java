package com.rumanweb.bidsell.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class MyBidsModel {
    private String bidderName;
    private double biddingPrice;

    private Timestamp biddingTime;
    private String productTitle;

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public double getBiddingPrice() {
        return biddingPrice;
    }

    public void setBiddingPrice(double biddingPrice) {
        this.biddingPrice = biddingPrice;
    }

    public Timestamp getBiddingTime() {
        return biddingTime;
    }

    public void setBiddingTime(Timestamp biddingTime) {
        this.biddingTime = biddingTime;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    // Empty constructor for Firebase
    public MyBidsModel() {}

    public MyBidsModel(String bidderName, double biddingPrice, Timestamp biddingTime, String productTitle) {
        this.bidderName = bidderName;
        this.biddingPrice = biddingPrice;
        this.biddingTime = biddingTime;
        this.productTitle = productTitle;
    }


}