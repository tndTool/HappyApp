package com.example.happyapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyapp.R;
import com.example.happyapp.adapter.HomeAdapter;
import com.example.happyapp.dialog.LoadingDialog;
import com.example.happyapp.model.History;
import com.example.happyapp.viewmodal.HistoryViewModel;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment {

    private HomeAdapter homeAdapter;
    private String userEmail;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        homeAdapter = new HomeAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(homeAdapter);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.show();

        HistoryViewModel historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        userEmail = getEmailFromSharedPreferences();
        historyViewModel.fetchHistoryInfo(userEmail);

        historyViewModel.getHistoryLiveData().observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> historyList) {
                if (historyList != null) {
                    homeAdapter.setHistoryList(historyList);
                    Toasty.info(getActivity(), "Null!", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
                loadingDialog.dismiss();
            }
        });

        return rootView;
    }

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }
}

//package com.example.happyapp.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.happyapp.R;
//import com.example.happyapp.adapter.HomeAdapter;
//import com.example.happyapp.model.History;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//
//public class HomeFragment extends Fragment {
//
//    private ArrayList<History> historyList;
//    private HomeAdapter homeAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//
//        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        historyList = new ArrayList<>();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        Date date = null;
//        try {
//            date = dateFormat.parse("03-12-2023");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if (date != null) {
//            historyList.add(new History("Behavior 1", date, "https://res.cloudinary.com/djt8oiwwr/image/upload/v1701916345/behavior-images/nbk50lysbcmmxcqi4gov.jpg"));
//            historyList.add(new History("Behavior 1", date, "https://res.cloudinary.com/djt8oiwwr/image/upload/v1701916345/behavior-images/nbk50lysbcmmxcqi4gov.jpg"));
//            historyList.add(new History("Behavior 1", date, "https://res.cloudinary.com/djt8oiwwr/image/upload/v1701916345/behavior-images/nbk50lysbcmmxcqi4gov.jpg"));
//            historyList.add(new History("Behavior 1", date, "https://res.cloudinary.com/djt8oiwwr/image/upload/v1701916345/behavior-images/nbk50lysbcmmxcqi4gov.jpg"));
//            historyList.add(new History("Behavior 1", date, "https://res.cloudinary.com/djt8oiwwr/image/upload/v1701916345/behavior-images/nbk50lysbcmmxcqi4gov.jpg"));
//            historyList.add(new History("Behavior 1", date, "https://res.cloudinary.com/djt8oiwwr/image/upload/v1701916345/behavior-images/nbk50lysbcmmxcqi4gov.jpg"));
//        }
//
//        // ...
//
//        homeAdapter = new HomeAdapter(getActivity(), historyList);
//        recyclerView.setAdapter(homeAdapter);
//
//        return rootView;
//    }
//}





