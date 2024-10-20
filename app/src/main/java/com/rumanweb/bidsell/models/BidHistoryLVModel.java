package com.rumanweb.bidsell.models;
import com.google.firebase.Timestamp;

public class BidHistoryLVModel {
    private String bidderName;
    private double biddingPrice;
    private Timestamp biddingTime;
    private String bidderImg;

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

    public String getBidderImg() {
        return bidderImg;
    }

    public void setBidderImg(String bidderImg) {
        this.bidderImg = bidderImg;
    }

    public BidHistoryLVModel(String bidderName, double biddingPrice, Timestamp biddingTime, String bidderImg) {
        this.bidderName = bidderName;
        this.biddingPrice = biddingPrice;
        this.biddingTime = biddingTime;
        this.bidderImg = bidderImg;
    }
}
