package com.example.growmoreapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telecom.ConnectionService;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileReader;
import java.net.CookieManager;


public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;
    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int ACCOUNT_FRAGMENT = 3;
    public static boolean showCart = false;
    public static DrawerLayout drawer;
    private int scrollFlags;
    private AppBarLayout.LayoutParams params;

    private int currentFragment = -1;
    private Toolbar toolbar;

    NavigationView navigationView;
    private ImageView actionLogo;


    private TextView badgeCount;

    // private Toolbar toolbar;

    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        actionLogo = findViewById(R.id.action_logo);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayShowTitleEnabled(false);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_sbc, R.id.nav_cart, R.id.nav_orders, R.id.nav_account
                , R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.main_frame_layout);


        if (showCart) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            MenuItem menuItem;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    actionLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                } else if (id == R.id.nav_orders) {
                    gotoFragment("My Orders", new MyOrdersFragment(), ORDERS_FRAGMENT);
                } else if (id == R.id.nav_cart) {
                    gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                } else if (id == R.id.nav_account) {
                    gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                } else if (id == R.id.nav_sbc) {

                } else if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    DBqueries.clearData();
                    DBqueries.email = null;  //my code
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    finish();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem cartItem = menu.findItem(R.id.cart_bar);
//        cartItem.setActionView(R.layout.badge_layout);
//        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
//        badgeIcon.setImageResource(R.drawable.shopping_cart);
//        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
//
//
//        if (currentUser != null) {
//            if (DBqueries.cartList.size() == 0) {
//                DBqueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false, badgeCount, new TextView(MainActivity.this));
//            } else {
//                badgeCount.setVisibility(View.VISIBLE);
//                if (DBqueries.cartList.size() < 99) {
//                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
//                } else {
//                    badgeCount.setText("99");
//                }
//            }
//
//        }
//        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
//
//            }
//        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.cart_bar) {
            gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
            return true;
        } else if (id == R.id.search_icon) {
            return true;
        } else if (id == android.R.id.home) {
            if (showCart) {
                showCart = false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        currentUser= FirebaseAuth.getInstance().getCurrentUser();
//        if(currentUser == null){
//            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(false);
//        }
//        else {
//            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
//        }
//    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {
        actionLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        if (fragmentNo == CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(2).setChecked(true);
            params.setScrollFlags(0);
        } else {
            params.setScrollFlags(scrollFlags);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if (fragmentNo != currentFragment) {
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                if (showCart) {
                    showCart = false;
                    finish();
                } else {
                    actionLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }
}