package com.example.growmoreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class viewAllFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public viewAllFragment() {
    }

    private RecyclerView viewAllRecyclerView;
    public static viewAllFragment newInstance(String param1, String param2) {
        viewAllFragment fragment = new viewAllFragment();
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
        View view =  inflater.inflate(R.layout.fragment_view_all, container, false);
        viewAllRecyclerView = view.findViewById(R.id.my_viewAll_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewAllRecyclerView.setLayoutManager(linearLayoutManager);

        List<ViewAllModel> viewAllModelList = new ArrayList<>();

        ViewAllAdapter viewAllAdapter = new ViewAllAdapter(viewAllModelList);
        viewAllRecyclerView.setAdapter(viewAllAdapter);
        viewAllAdapter.notifyDataSetChanged();
        return view;
    }
}