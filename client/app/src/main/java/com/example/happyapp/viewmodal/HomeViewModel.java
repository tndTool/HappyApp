package com.example.happyapp.viewmodal;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.happyapp.model.History;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private HomeRepository homeRepository;

    public HomeViewModel() {
        homeRepository = new HomeRepository();
    }

    public LiveData<List<History>> getHistoryList(String userEmail) {
        return homeRepository.getHistoryList(userEmail);
    }
}