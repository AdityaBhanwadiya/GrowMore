package com.example.growmoreapp;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static android.media.CamcorderProfile.get;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private boolean showDeleteBtn;
    private int lastpos = -1;
    private TextView cartTotalAmount;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
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
                //Long productQty = cartItemModelList.get(position).getProductQuantity();

                ((CartItemViewHolder) holder).setItemDetails(productID, resource, title, name, price, position);
                break;

            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0, totalAmount;
                int totalItemsPrice = 0;
                String deliveryPrice = null;

                for (int x = 0; x < cartItemModelList.size(); x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM) {
                        int qty = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + 1;
                        totalItemsPrice = totalItemsPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice());
                    }
                }

                if (totalItemsPrice > 500) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemsPrice;
                } else {
                    deliveryPrice = "60";
                    totalAmount = totalItemsPrice + 60;
                }

                ((CartTotalAmountViewHolder) holder).setTotalAmount(totalItems, totalItemsPrice, deliveryPrice, totalAmount);
                break;
            default:
                return;
        }
        if (lastpos < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastpos = position;
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

        private LinearLayout deleteBtn;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_incart_title);
            productPrice = itemView.findViewById(R.id.product_incart_price);
            farmerName = itemView.findViewById(R.id.incart_farmer_name2);
            productQuantity = itemView.findViewById(R.id.product_qnty);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);

        }

        private void setItemDetails(String productID, String resource, String title, String fname, String productPriceText, int position) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)).into(productImage);
            productTitle.setText(title);
            farmerName.setText(fname);
            productPrice.setText(productPriceText);
            productQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog qunatityDialog = new Dialog(itemView.getContext());
                    qunatityDialog.setContentView(R.layout.quantity_dialog);
                    qunatityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }
            else{
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ProductDetailsActivity.running_cart_querry) {
                        ProductDetailsActivity.running_cart_querry = true;
                        DBqueries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }
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

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPricetext, int totalAmounttext) {
            totalItems.setText("Price(" + totalItemText + " items)");
            totalItemsPrice.setText("Rs." + totalItemPriceText + "/-");
            if (deliveryPricetext.equals("FREE")) {
                deliveryPrice.setText(deliveryPricetext);
            } else {
                deliveryPrice.setText("Rs." + deliveryPricetext + "/-");
            }
            totalAmount.setText("Rs." + totalAmounttext + "/-");
            cartTotalAmount.setText("Rs." + totalAmounttext + "/-");

//            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
//            if (totalItemPriceText == 0) {
//                if (DeliveryActivity.fromCart) {
//                    cartItemModelList.remove(cartItemModelList.size() - 1);
//                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
//                }
//                if (showDeleteBtn) {
//                    cartItemModelList.remove(cartItemModelList.size() - 1);
//                }
//                parent.setVisibility(View.GONE);
//            } else {
//                parent.setVisibility(View.VISIBLE);
//            }
        }
    }
}
