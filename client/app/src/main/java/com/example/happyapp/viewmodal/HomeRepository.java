package com.example.happyapp.viewmodal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.model.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeRepository {

    private MutableLiveData<List<History>> historyListLiveData;

    public LiveData<List<History>> getHistoryList(String userEmail) {
        if (historyListLiveData == null) {
            historyListLiveData = new MutableLiveData<>();
            loadHistoryList(userEmail);
        }
        return historyListLiveData;
    }

    private void loadHistoryList(String userEmail) {
        ApiHelper.getBehaviorInfo(userEmail, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                historyListLiveData.postValue(null);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray behaviorArray = new JSONArray(responseBody);

                        List<History> historyList = new ArrayList<>();
                        for (int i = 0; i < behaviorArray.length(); i++) {
                            JSONObject behaviorObject = behaviorArray.getJSONObject(i);
                            String id = behaviorObject.getString("_id");
                            String behavior = behaviorObject.getString("behavior");
                            String titleImage = behaviorObject.getString("image");
                            String createdAtString = behaviorObject.getString("createdAt");

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date createdAt = null;
                            try {
                                createdAt = dateFormat.parse(createdAtString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (createdAt != null) {
                                History history = new History(id, behavior, createdAt, titleImage);
                                historyList.add(history);
                            }
                        }

                        historyListLiveData.postValue(historyList);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        historyListLiveData.postValue(null);
                    }
                } else {
                    historyListLiveData.postValue(null);
                }
            }
        });
    }
}
