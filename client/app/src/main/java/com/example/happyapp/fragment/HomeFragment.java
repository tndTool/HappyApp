package com.example.happyapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.happyapp.viewmodal.HomeViewModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private HomeAdapter homeAdapter;
    private String userEmail;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;
    private HomeViewModel homeViewModel;
    private TextView noHistoryTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        homeAdapter = new HomeAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(homeAdapter);

        noHistoryTextView = rootView.findViewById(R.id.no_history_textview);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.show();

        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        userEmail = getEmailFromSharedPreferences();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getHistoryList(userEmail).observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> historyList) {
                handleHistoryListChange(historyList);
            }
        });

        return rootView;
    }

    private void handleHistoryListChange(List<History> historyList) {
        loadingDialog.dismiss();

        if (!isAdded()) {
            // Fragment is not attached to the activity anymore
            return;
        }

        if (historyList != null) {
            homeAdapter.setHistoryList(historyList);

            if (historyList.isEmpty()) {
                noHistoryTextView.setVisibility(View.VISIBLE);
            } else {
                noHistoryTextView.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getActivity(), "Failed to fetch behavior information", Toast.LENGTH_SHORT).show();
        }
    }

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }
}







