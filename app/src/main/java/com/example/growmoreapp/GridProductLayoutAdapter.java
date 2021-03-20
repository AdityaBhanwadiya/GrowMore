package com.example.growmoreapp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static android.media.CamcorderProfile.get;

public class GridProductLayoutAdapter extends BaseAdapter {

    List<GridProductModel> gridProductModelList;

    public GridProductLayoutAdapter(List<GridProductModel> gridProductModelList) {
        this.gridProductModelList = gridProductModelList;
    }

    @Override
    public int getCount() {
       return gridProductModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout,null);
            view.setElevation(0);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.getContext().startActivity(new Intent(parent.getContext(),ProductDetailsActivity.class).putExtra("PRODUCT_ID",gridProductModelList.get(position).getProductID()));
                }
            });

            view.setBackgroundColor(Color.parseColor("#ffffff"));
            ImageView productImage = view.findViewById(R.id.grid_product_image);
            TextView productTitle = view.findViewById(R.id.grid_product_title);
            TextView productPrice = view.findViewById(R.id.grid_product_price);

            Glide.with(parent.getContext()).load(gridProductModelList.get(position).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)).into(productImage);
            productTitle.setText(gridProductModelList.get(position).getProductTitle());
            productPrice.setText("Rs. " + gridProductModelList.get(position).getProductPrice()+ "/kg");
        }
        else{
            view = convertView;
        }
        return view;
    }
}
