package com.example.growmoreapp;

import java.util.List;

public class HomePageModel {

    public static final int BANNER_SLIDER = 0;
    public static final int GRID_PRODUCT_VIEW = 1;
    public static final int HORIZONTAL_PRODUCT_VIEW = 2;

    private int type;
    private String backgroundColor;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


/////Banner Slider

    private List<SliderModel> sliderModelList;

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

/////Banner Slider




///////horizontal product view and grid product view

    private String title;
    private List<GridProductModel> gridProductModelList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<GridProductModel> getGridProductModelList() {
        return gridProductModelList;
    }

    public void setGridProductModelList(List<GridProductModel> horizontalProductScrollModelList) {
        this.gridProductModelList = horizontalProductScrollModelList;
    }

    public HomePageModel(int type, String title, String backgroundColor , List<GridProductModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroundColor=backgroundColor;
        this.gridProductModelList = horizontalProductScrollModelList;
    }

    ///horizontal product view only
    private List<ViewAllModel> viewAllProductList;

    public HomePageModel(int type, String title,String backgroundColor ,List<GridProductModel> gridProductModelList,List<ViewAllModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backgroundColor=backgroundColor;
        this.gridProductModelList = gridProductModelList;
        this.viewAllProductList=viewAllProductList;
    }

    public List<ViewAllModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<ViewAllModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    ///horizontal product view only

///////horizontal product view and grid product view

}
