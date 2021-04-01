package com.example.growmoreapp;

public class CartItemModel {

    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /////////////////////Cart item//////////////////
    private String productID;
    private String productImage;
    private String productTitle;
    private long productQuantity;
    private long maxQuantity;
    private String farmerName;
    private String productPrice;
    private boolean inStock;


    public CartItemModel(int type, String productID, String productImage, String productTitle, String farmerName, long productQuantity, String productPrice,boolean inStock,long maxQuantity) {
        this.type = type;
        this.productImage = productImage;
        this.productID = productID;
        this.productQuantity = productQuantity;
        this.productTitle = productTitle;
        this.farmerName = farmerName;
        this.productPrice = productPrice;
        this.inStock = inStock;
        this.maxQuantity = maxQuantity;
    }

    public long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    /////////////////////////////////////////////////


    /////////////////////Cart Total////////////////////////
    public CartItemModel(int type){
        this.type = type;
    }

    ////////////////////////////////////////////////////////
}
