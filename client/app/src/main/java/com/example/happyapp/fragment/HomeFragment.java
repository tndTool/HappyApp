package com.example.happyapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyapp.R;
import com.example.happyapp.adapter.HomeAdapter;
import com.example.happyapp.model.History;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private ArrayList<History> historyList;
    private HomeAdapter homeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyList = new ArrayList<>();
        // Populate the historyList with your data
        historyList.add(new History("Behavior 1", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 2", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 3", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 4", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 5", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 6", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 7", "03-12-2023", R.drawable.icon_app));
        historyList.add(new History("Behavior 8", "03-12-2023", R.drawable.logo_app));

        // ...

        homeAdapter = new HomeAdapter(getActivity(), historyList);
        recyclerView.setAdapter(homeAdapter);

        return rootView;
    }
}




