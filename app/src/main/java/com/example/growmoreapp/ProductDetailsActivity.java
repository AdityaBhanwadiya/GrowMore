package com.example.growmoreapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;

import static com.example.growmoreapp.DBqueries.currentUser;
import static com.example.growmoreapp.MainActivity.showCart;

public class ProductDetailsActivity extends AppCompatActivity {


    public static boolean running_cart_querry = false;
    public static boolean running_rating_query = false;
    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView productType;
    private TextView averageRatingMiniview;
    private TextView totalRatingMiniview;
    private TextView productPrice;
    private TextView badgeCount;
    public static MenuItem cartItem;
    private TextView productUnit;
    private TabLayout viewPagerIndicator;

    /////product description//////////////
    private ConstraintLayout productDetailsTabsOnlyContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private String productDescription, productFarmerDetailName, productFarmerDetailCity, productFarmerDetailContact;
    public static String productID;
    ////////////////////////////////////
    private FloatingActionButton share;


    //////////////Rating Layout///////////////
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNumberContainer;
    private TextView totalRatingsFig;
    private LinearLayout ratingsProgressBarCntainer;
    private TextView avgRating;
    public static int updateRating;
    //////////////////////////////////////////

    private Dialog loadingDialog;

    public static boolean ALREADY_ADDED_TO_CART = false;


    private Button buyNowBtn;
    private LinearLayout addToCartBtn;

    public FirebaseFirestore firebaseFirestore;
    public DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        share = findViewById(R.id.share_product);
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        productTitle = findViewById(R.id.product_title);
        productType = findViewById(R.id.product_type);
        averageRatingMiniview = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniview = findViewById(R.id.total_rating_miniview);
        productPrice = findViewById(R.id.product_price);
        productUnit = findViewById(R.id.prouct_unit);
        productDetailsTabsOnlyContainer = findViewById(R.id.product_details_tabs_container);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNumberContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFig = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarCntainer = findViewById(R.id.ratings_progressbar_container);
        avgRating = findViewById(R.id.average_rating);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        initialRating = -1;
        this.firebaseFirestore = FirebaseFirestore.getInstance();

//        //////////loading dialog
//
//        loadingDialog = new Dialog(ProductDetailsActivity.this);
//        loadingDialog.setContentView(R.layout.loading_progress_dialog);
//        loadingDialog.setCancelable(false);
//        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
//        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        loadingDialog.show();
//
//        //////////loading dialog

        final List<String> productImages = new ArrayList<>();
        productID = getIntent().getStringExtra("PRODUCT_ID");


        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if (task.isSuccessful()) {
                documentSnapshot = task.getResult();
                for (long x = 1; x <= (long) documentSnapshot.get("no_of_product_images"); x++) {
                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                }
                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                productImagesViewPager.setAdapter(productImagesAdapter);

                productTitle.setText(documentSnapshot.get("product_title").toString());
                productType.setText(documentSnapshot.get("product_type").toString());
                averageRatingMiniview.setText(documentSnapshot.get("average_rating").toString());
                totalRatingMiniview.setText("(" + (long) documentSnapshot.get("total_ratings") + ")ratings");
                productPrice.setText("Rs." + documentSnapshot.get("product_price").toString() + "/");
                productUnit.setText(documentSnapshot.get("product_unit").toString());

                productDescription = documentSnapshot.get("product_description").toString();
                productFarmerDetailName = documentSnapshot.get("farmer_name").toString();
                productFarmerDetailCity = documentSnapshot.get("farmer_city").toString();
                productFarmerDetailContact = documentSnapshot.get("farmer_contact_no").toString();


                totalRatings.setText((long) documentSnapshot.get("total_ratings") + "ratings");
                for (int x = 0; x < 5; x++) {
                    TextView rating = (TextView) ratingsNumberContainer.getChildAt(x);
                    rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));
                    ProgressBar progressBar = (ProgressBar) ratingsProgressBarCntainer.getChildAt(x);
                    int max_progress = (Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings"))));
                    progressBar.setMax(max_progress);
                    progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                }
                totalRatingsFig.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                avgRating.setText(documentSnapshot.get("average_rating").toString());
                productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productFarmerDetailName, productFarmerDetailCity, productFarmerDetailContact));
//

                if (DBqueries.myRating.size() == 0) {
                    DBqueries.loadRatingList(ProductDetailsActivity.this);
                }

                if (DBqueries.cartList.size() == 0) {
                    DBqueries.loadCartList(ProductDetailsActivity.this,false);
                }


                if (DBqueries.myRatedIds.contains(productID)) {
                    int index = DBqueries.myRatedIds.indexOf(productID);
                    initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
                    setRating(initialRating);
                }

                /*if (DBqueries.cartList.size() == 0) {
                    DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));
                }*/

                if (DBqueries.cartList.contains(productID)) {
                    ALREADY_ADDED_TO_CART = true;
                } else {
                    ALREADY_ADDED_TO_CART = false;
                }

                addToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!running_cart_querry) {
                            running_cart_querry = true;
                            if (ALREADY_ADDED_TO_CART) {
                                running_cart_querry = false;
                                Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, Object> addProduct = new HashMap<>();
                                addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));

                                firebaseFirestore.collection("USERS").document(currentUser.getUid())
                                        .collection("USER_DATA").document("MY_CART")
                                        .update(addProduct)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (DBqueries.cartItemModelList.size() != 0) {
                                                        DBqueries.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM
                                                                , productID
                                                                , documentSnapshot.get("product_image_1").toString()
                                                                , documentSnapshot.get("product_title").toString()
                                                                , documentSnapshot.get("farmer_name").toString()
                                                                , (long) 100
                                                                , documentSnapshot.get("product_price").toString()));
                                                    }
                                                    ALREADY_ADDED_TO_CART = true;
                                                    DBqueries.cartList.add(productID);
                                                    Toast.makeText(ProductDetailsActivity.this, "Added to Cart Successfully!", Toast.LENGTH_SHORT).show();
                                                    // invalidateOptionsMenu();
                                                    running_cart_querry = false;
                                                } else {
                                                    running_cart_querry = false;
                                                    String err = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

            } else {
                String error = task.getException().getMessage();
                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);

        share.setOnClickListener(v -> {
            try {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey, I'm using GrowMore for sending this crop on WhatsApp\n \nDownload it now: https://play.google.com/store/apps/details?id=com.yourappurlhere";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                //change the app package id as your wish for sharing content to the specific one, WhatsApp's package id is com.whatsapp, and for facebook is com.facebook.katana
                sharingIntent.setPackage("com.whatsapp");
                startActivity(sharingIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Intent sharingIntent1 = new Intent(Intent.ACTION_SEND);
                sharingIntent1.setType("text/plain");
                String shareBody = "Hey, I'm using GrowMore for sending this crop on WhatsApp\n \nDownload it now: https://play.google.com/store/apps/details?id=com.yourappurlhere";
                String shareSubject = "GrowMore";
                sharingIntent1.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent1.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent1, "Share with friends"));
            }
            return;
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ///////////////////rating layout///////////////

        rateNowContainer = findViewById(R.id.rate_now_container);

        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(view -> {

                if (starPosition != initialRating) {
                    if (!running_rating_query) {
                        running_rating_query = true;

                        setRating(starPosition);
                        Map<String, Object> updateRating = new HashMap<>();

                        if (DBqueries.myRatedIds.contains(productID)) {
                            TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                            TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                            updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                            updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                            updateRating.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
                        } else {
                            updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                            updateRating.put("average_rating", calculateAverageRating((long) starPosition + 1, false));
                            updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                        }

                        firebaseFirestore.collection("PRODUCTS").document(productID)
                                .update(updateRating)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> myRating = new HashMap<>();

                                        if (DBqueries.myRatedIds.contains(productID)) {
                                            myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);
                                        } else {
                                            myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                            myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                                            myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                        }

                                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                .update(myRating)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (DBqueries.myRatedIds.contains(productID)) {
                                                                DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                                TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                                                oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                                TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                                                finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                            } else {
                                                                DBqueries.myRatedIds.add(productID);
                                                                DBqueries.myRating.add((long) starPosition + 1);
                                                                totalRatingMiniview.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")ratings");
                                                                totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                                totalRatingsFig.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                                TextView rating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                                                rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                                Toast.makeText(ProductDetailsActivity.this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                                                            }
                                                            for (int x1 = 0; x1 < 5; x1++) {
                                                                TextView ratings = (TextView) ratingsNumberContainer.getChildAt(x1);
                                                                ProgressBar progressBar = (ProgressBar) ratingsProgressBarCntainer.getChildAt(x1);
                                                                int maxProgress = Integer.parseInt(totalRatingsFig.getText().toString());
                                                                progressBar.setMax(maxProgress);
                                                                progressBar.setProgress(Integer.parseInt(ratings.getText().toString()));
                                                            }
                                                            initialRating = starPosition;
                                                            avgRating.setText(calculateAverageRating(0, true));
                                                            averageRatingMiniview.setText(calculateAverageRating(0, true));

                                                        } else {
                                                            setRating(initialRating);
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                        running_rating_query = false;
                                                    }
                                                });

                                    } else {
                                        running_rating_query = false;
                                        setRating(initialRating);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }

            });
        }
        ////////////////////////////////////////

        buyNowBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
            startActivity(intent);
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (DBqueries.myRating.size() == 0) {
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }

            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this,false);
            }

        } else {
            loadingDialog.dismiss();
        }

        if (DBqueries.myRatedIds.contains(productID)) {
            int index = DBqueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        if (DBqueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        invalidateOptionsMenu();

    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNumberContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFig.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFig.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

//        cartItem = menu.findItem(R.id.cart_bar);
//
//
//        cartItem.setActionView(R.layout.badge_layout);
//        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
//        badgeIcon.setImageResource(R.drawable.shopping_cart);
//        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
//        if (currentUser != null) {
//            if (DBqueries.cartList.size() == 0) {
//                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
//            } else {
//                badgeCount.setVisibility(View.VISIBLE);
//                if (DBqueries.cartList.size() < 99) {
//                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
//                } else {
//                    badgeCount.setText("99");
//                }
//            }
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.search_icon) {
            return true;
        } else if (id == R.id.cart_bar) {
            Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
            showCart = true;
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}