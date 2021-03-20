package com.example.growmoreapp;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static android.media.CamcorderProfile.get;

public class CartAdapter extends RecyclerView.Adapter {

    List<CartItemModel> cartItemModelList;

    public CartAdapter(List<CartItemModel> cartItemModelList) {
        this.cartItemModelList = cartItemModelList;
    }


    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate((R.layout.cart_item_layout), parent, false);
                return new CartItemViewHolder(view);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate((R.layout.cart_total_amount_layout), parent, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                String name = cartItemModelList.get(position).getFarmerName();
                String price = cartItemModelList.get(position).getProductPrice();

                ((CartItemViewHolder)holder).setItemDetails(productID,resource,title,name,price);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                String totalItems = cartItemModelList.get(position).getTotalItems();
                String totalItemPrice = cartItemModelList.get(position).getTotalItemPrice();
                String deliveryPrice = cartItemModelList.get(position).getDeliveryPrice();
                String totalAmount = cartItemModelList.get(position).getTotalAmount();

                ((CartTotalAmountViewHolder)holder).setTotalItems(totalItems,totalItemPrice,deliveryPrice,totalAmount);
                break;
            default:
                return;
        }

    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView farmerName;
        private TextView productQuantity;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_incart_title);
            productPrice = itemView.findViewById(R.id.product_incart_price);
            farmerName = itemView.findViewById(R.id.incart_farmer_name2);
            productQuantity = itemView.findViewById(R.id.product_qnty);

        }

        private void setItemDetails(String productID, String  resource, String title, String fname, String productPriceText) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)).into(productImage);
            productTitle.setText(title);
            farmerName.setText(fname);
            productPrice.setText(productPriceText);
            productQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog qunatityDialog = new Dialog(itemView.getContext());
                    qunatityDialog.setContentView(R.layout.quantity_dialog);
                    qunatityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    qunatityDialog.setCancelable(false);

                    final EditText qtyNo = qunatityDialog.findViewById(R.id.quantity_no);
                    Button cancelBtn = qunatityDialog.findViewById(R.id.cancel_btn);
                    Button okBtn = qunatityDialog.findViewById(R.id.ok_btn);

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            qunatityDialog.dismiss();
                        }
                    });
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            productQuantity.setText("Qty: " + qtyNo.getText().toString());
                            qunatityDialog.dismiss();
                        }
                    });
                    qunatityDialog.show();
                }
            });
        }
    }


    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalItems;
        private TextView totalItemsPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totalItems = itemView.findViewById(R.id.total_items);
            totalItemsPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
        }

        protected void setTotalItems(String totalItemsText, String totalItemPriceText, String deliverItemtext, String totalAmountTetx) {
            totalItems.setText(totalItemsText);
            totalItemsPrice.setText(totalItemPriceText);
            deliveryPrice.setText(deliverItemtext);
            totalAmount.setText(totalAmountTetx);
        }
    }
}
