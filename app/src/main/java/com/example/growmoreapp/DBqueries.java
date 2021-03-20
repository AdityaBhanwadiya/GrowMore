package com.example.growmoreapp;

import android.content.Context;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

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

//    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData) {
//        cartList.clear();
//        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
//                .collection("USER_DATA").document("MY_CART").get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
//                                cartList.add(task.getResult().get("product_ID_"+x).toString());
//
//                                if (DBqueries.cartList.contains(ProductDetailsActivity.productID)) {
//                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
//                                } else {
//                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
//                                }
//
//                                if (loadProductData) {
//                                    cartItemModelList.clear();
//                                    final String productId = task.getResult().get("product_ID_" + x).toString();
//                                    firebaseFirestore.collection("PRODUCTS").document(productId).get()
//                                            .addOnCompleteListener(task1 -> {
//                                                if (task.isSuccessful()) {
//                                                    cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM
//                                                            , productId
//                                                            , task.getResult().get("product_image_1").toString()
//                                                            , task.getResult().get("product_title").toString()
//                                                            , task.getResult().get("farmer_name").toString()
//                                                            , (long) task.getResult().get("max_qnty")
//                                                            , task.getResult().get("product_price").toString()));
//                                                    MyCartFragment.cartAdapter.notifyDataSetChanged();
//                                                } else {
//                                                    String error = task.getException().getMessage();
//                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
//                            }
//                        } else {
//                            String error = task.getException().getMessage();
//                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }


    public static void clearData() {

        categoryModelList.clear();
        lists.clear();
        myRatedIds.clear();
        myRating.clear();

    }
}
