package com.example.growmoreapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ProductDetailsAdapter extends FragmentPagerAdapter {

    private int totalTabs;
    private String productDescription, productFarmersName,productFarmersCity,productFarmerContact;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productDescription, String productFarmersName,String productFarmersCity,String productFarmerContact) {
        super(fm);
        this.productDescription = productDescription;
        this.productFarmersName = productFarmersName;
        this.productFarmersCity = productFarmersCity;
        this.productFarmerContact = productFarmerContact;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ProductDescriptionFragment productDescriptionFragment1 = new ProductDescriptionFragment();
                productDescriptionFragment1.body = productDescription;
                return  productDescriptionFragment1;
            case 1:
                ProductFarmerDetails productFarmerDetails1 = new ProductFarmerDetails();
                productFarmerDetails1.descFarmerName = productFarmersName;
                productFarmerDetails1.descFarmerCity = productFarmersCity;
                productFarmerDetails1.descFarmerContact = productFarmerContact;
                return productFarmerDetails1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
