package com.example.growmoreapp;

public class ViewAllModel {

    private long totalRatings;
    private String productImage;
    private String productTitle,rating,productPrice,productType;

    public ViewAllModel(String productImage, String productTitle,String productType, String rating, long totalRatings, String productPrice) {
        this.totalRatings = totalRatings;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.rating = rating;
        this.productPrice = productPrice;
        this.productType = productType;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
