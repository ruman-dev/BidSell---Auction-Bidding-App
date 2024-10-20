package com.rumanweb.bidsell.models;

public class MyBidsModel {

    private String title;
    private String img_url;
    private String desc;
//    private int bid_count;
    private double biddingPrice;

    public MyBidsModel(String title, String img_url, double biddingPrice) {
        this.title = title;
        this.img_url = img_url;
        this.desc = desc;
//        this.bid_count = bid_count;
        this.biddingPrice = biddingPrice;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public int getBid_count() {
//        return bid_count;
//    }
//
//    public void setBid_count(int bid_count) {
//        this.bid_count = bid_count;
//    }

    public double getBiddingPrice() {
        return biddingPrice;
    }

    public void setBiddingPrice(double biddingPrice) {
        this.biddingPrice = biddingPrice;
    }
}
