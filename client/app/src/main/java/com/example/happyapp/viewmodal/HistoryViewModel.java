package com.example.happyapp.viewmodal;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happyapp.model.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends ViewModel {
    private MutableLiveData<List<History>> historyLiveData;
    private HistoryRepository historyRepository;

    public HistoryViewModel() {
        historyRepository = new HistoryRepository();
        historyLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<History>> getHistoryLiveData() {
        return historyLiveData;
    }

    public void fetchHistoryInfo(String userEmail) {
        historyRepository.getHistoryInfo(userEmail, new HistoryRepository.HistoryCallback() {
            @Override
            public void onSuccess(History history) {
                List<History> historyList = new ArrayList<>();
                historyList.add(history);
                historyLiveData.setValue(historyList);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure case, if needed
            }
        });
    }
}