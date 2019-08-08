package com.walton.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String title;
    private String description;
    private  String price;
    private String imageURL;
    private String imageName;
    private String iD;

    public TravelDeal() {
    }

    public TravelDeal(String title, String description, String price, String imageURL, String imageName) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.imageName = imageName;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}