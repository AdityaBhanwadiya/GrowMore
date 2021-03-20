package com.example.growmoreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductFarmerDetails extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProductFarmerDetails() {
    }

    private TextView farmerName,farmerCity,farmerContact;
    public String descFarmerName,descFarmerCity,descFarmerContact;

    public static ProductFarmerDetails newInstance(String param1, String param2) {
        ProductFarmerDetails fragment = new ProductFarmerDetails();
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
        View view =  inflater.inflate(R.layout.fragment_product_farmer_details, container, false);
        farmerName = view.findViewById(R.id.farmer_name);
        farmerName.setText(descFarmerName);
        farmerCity = view.findViewById(R.id.farmer_city);
        farmerCity.setText(descFarmerCity);
        farmerContact = view.findViewById(R.id.farmer_contact);
        farmerContact.setText(descFarmerContact);

        return view;
    }
}