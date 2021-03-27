package com.example.growmoreapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBqueries {

    public static String email;

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesName = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static List<AddressesModel> addressesModelList=new ArrayList<>();
    public static int selectedAddress=-1;

    // public static List<MyOrderItemModel> myOrderItemModelList=new ArrayList<>();

    public static void loadCategories(final CategoryAdapter categoryAdapter, final Context context) {

        categoryModelList = new ArrayList<>();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            categoryModelList.add(new CategoryModel(queryDocumentSnapshot.get("icon").toString(), queryDocumentSnapshot.get("categoryName").toString()));
                        }
                        categoryAdapter.notifyDataSetChanged();

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadFragmentData(final HomePageAdapter adapter, final Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            if ((long) documentSnapshot.get("view_type") == 0) {

                                List<SliderModel> sliderModelList = new ArrayList<>();
                                long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                for (long x = 1; x < no_of_banners + 1; x++) {
                                    sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()
                                            , documentSnapshot.get("banner_" + x + "_background").toString()));
                                }
                                lists.get(index).add(new HomePageModel(0, sliderModelList));
                            } else if ((long) documentSnapshot.get("view_type") == 1) {
                                List<GridProductModel> gridProductModelList = new ArrayList<>();
                                long no_of_products = (long) documentSnapshot.get("no_of_products");
                                for (long x = 1; x < no_of_products + 1; x++) {
                                    gridProductModelList.add(new GridProductModel(documentSnapshot.get("product_ID_" + x).toString()
                                            , documentSnapshot.get("product_image_" + x).toString()
                                            , documentSnapshot.get("product_title_" + x).toString()
                                            , documentSnapshot.get("product_price_" + x).toString()));
                                }
                                lists.get(index).add(new HomePageModel(1, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridProductModelList));

                            } else if ((long) documentSnapshot.get("view_type") == 2) {
                                List<ViewAllModel> viewAllModelList = new ArrayList<>();
                                List<GridProductModel> horizontalProductScrollModel = new ArrayList<>();
                                long no_of_products = (long) documentSnapshot.get("no_of_products");
                                for (long x = 1; x < no_of_products + 1; x++) {
                                    horizontalProductScrollModel.add(new GridProductModel(documentSnapshot.get("product_ID_" + x).toString()
                                            , documentSnapshot.get("product_image_" + x).toString()
                                            , documentSnapshot.get("product_title_" + x).toString()
                                            , documentSnapshot.get("product_price_" + x).toString()));

                                    viewAllModelList.add(new ViewAllModel(documentSnapshot.get("product_image_" + x).toString()
                                            , documentSnapshot.get("product_full_title_" + x).toString()
                                            , documentSnapshot.get("product_type_" + x).toString()
                                            , documentSnapshot.get("average_rating_" + x).toString()
                                            , (long) documentSnapshot.get("total_ratings_" + x)
                                            , documentSnapshot.get("product_price_" + x).toString()));
                                }
                                lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModel, viewAllModelList));

                            }


                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadRatingList(final Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {


                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                        myRating.add((long) task.getResult().get("rating_" + x));
                        if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                            ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                            if (ProductDetailsActivity.rateNowContainer != null) {
                                ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                            }
                        }
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_rating_query = false;
            });
        }
    }

    public static void loadCartList(final Context context ,final boolean loadProductData,final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                cartList.add(task.getResult().get("product_ID_"+x).toString());

                                if (DBqueries.cartList.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                                } else {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                                }

                                if (loadProductData) {
                                    cartItemModelList.clear();
                                    final String productId = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("PRODUCTS").document(productId).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        int index=0;

                                                        if(cartList.size()>=2){
                                                            index=cartList.size()-2;
                                                        }

                                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                                        cartItemModelList.add(index,new CartItemModel(CartItemModel.CART_ITEM
                                                                , productId
                                                                , documentSnapshot.get("product_image_1").toString()
                                                                , documentSnapshot.get("product_title").toString()
                                                                , documentSnapshot.get("farmer_name").toString()
                                                                , (long) 100
                                                                , documentSnapshot.get("product_price").toString()));

                                                        if(cartList.size() == 1){
                                                            cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                        }
                                                        if(cartList.size() == 0) {
                                                            cartItemModelList.clear();
                                                        }
                                                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount) {
        final String removedProductId = cartList.get(index);
        cartList.remove(index);

        Map<String, Object> updateCart = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCart.put("product_ID_" + x, cartList.get(x));
        }
        updateCart.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART").set(updateCart)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (cartItemModelList.size() != 0) {
                                cartItemModelList.remove(index);
                                MyCartFragment.cartAdapter.notifyDataSetChanged();
                            }
                            if(cartList.size() == 0) {
                                cartItemModelList.clear();
                            }
                            Toast.makeText(context, "Removed Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            cartList.add(index, removedProductId);
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_cart_querry = false;
                    }
                });

    }

    public static void loadAddresses(final Context context, final boolean gotoDeliveryActivity){
        addressesModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if((long)task.getResult().get("list_size") == 0){
                                context.startActivity(new Intent(context, AddAddressActivity.class).putExtra("INTENT","deliveryIntent"));
                            }else {
                                for(long x=1;x<=(long)task.getResult().get("list_size");x++){
                                    addressesModelList.add(new AddressesModel(
                                             task.getResult().get("city_"+x).toString()
                                            ,task.getResult().get("locality_"+x).toString()
                                            ,task.getResult().get("flat_no_"+x).toString()
                                            ,task.getResult().get("pincode_"+x).toString()
                                            ,task.getResult().get("landmark_"+x).toString()
                                            ,task.getResult().get("name_"+x).toString()
                                            ,task.getResult().get("mobile_no_"+x).toString()
                                            ,task.getResult().get("alternate_mobile_no_"+x).toString()
                                            ,task.getResult().get("state_"+x).toString()
                                            ,(boolean)task.getResult().get("selected_"+x)

                                    ));
                                    if((boolean)task.getResult().get("selected_"+x)){
                                        selectedAddress=Integer.parseInt(String.valueOf(x-1));
                                    }
                                }
                                if (gotoDeliveryActivity) {
                                    context.startActivity(new Intent(context, DeliveryActivity.class));
                                }
                            }
                        }else {
                            String error=task.getException().getMessage();
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    public static void clearData() {

        categoryModelList.clear();
        lists.clear();
        myRatedIds.clear();
        myRating.clear();
        cartList.clear();
        cartItemModelList.clear();

    }
}
