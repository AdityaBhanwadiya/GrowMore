package com.example.growmoreapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.growmoreapp.DBqueries.categoryModelList;
import static com.example.growmoreapp.DBqueries.firebaseFirestore;
import static com.example.growmoreapp.DBqueries.lists;
import static com.example.growmoreapp.DBqueries.loadCategories;
import static com.example.growmoreapp.DBqueries.loadFragmentData;
import static com.example.growmoreapp.DBqueries.loadedCategoriesName;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {

    }

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private ImageView noInternetConnection;

    private HomePageAdapter adapter;


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        noInternetConnection = view.findViewById(R.id.no_internet_conn);
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawer.setDrawerLockMode(0);
            noInternetConnection.setVisibility(View.GONE);
            categoryRecyclerView = view.findViewById(R.id.category_recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            categoryRecyclerView.setLayoutManager(layoutManager);

            categoryAdapter = new CategoryAdapter(categoryModelList);
            categoryRecyclerView.setAdapter(categoryAdapter);

            if (categoryModelList.size() == 0) {
                loadCategories(categoryAdapter, getContext());
            } else {
                categoryAdapter.notifyDataSetChanged();
            }

            RecyclerView homePageRecyclerView = view.findViewById(R.id.home_page_recyclerview);
            LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
            testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            homePageRecyclerView.setLayoutManager(testingLayoutManager);


            if (lists.size() == 0) {
                loadedCategoriesName.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                adapter = new HomePageAdapter(lists.get((0)));
                loadFragmentData(adapter, getContext(), 0,"Home");
            } else {
                adapter = new HomePageAdapter(lists.get((0)));
                adapter.notifyDataSetChanged();
            }

            homePageRecyclerView.setAdapter(adapter);

        } else {
            MainActivity.drawer.setDrawerLockMode(1);
            Glide.with(this).load(R.drawable.no_internet).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
        }
        return view;
    }

}