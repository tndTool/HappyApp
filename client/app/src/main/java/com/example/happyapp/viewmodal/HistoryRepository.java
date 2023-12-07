package com.example.happyapp.viewmodal;

import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.model.History;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HistoryRepository {

    public interface HistoryCallback {
        void onSuccess(History history);

        void onFailure(String errorMessage);
    }

    public void getHistoryInfo(String userEmail, HistoryCallback callback) {
        ApiHelper.getBehaviorInfo(userEmail, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Failed to fetch history information.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();

                        JSONObject jsonObject = new JSONObject(responseData);
                        String behavior = jsonObject.getString("behavior");
                        String createAt = jsonObject.getString("createdAt");
                        String titleImage = jsonObject.getString("image");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date createdAt = dateFormat.parse(createAt);

                        History history = new History(behavior, createdAt, titleImage);
                        callback.onSuccess(history);
                    } catch (JSONException e) {
                        callback.onFailure("Failed to parse history information.");
                    } catch (ParseException e) {
                        callback.onFailure("Failed to parse date.");
                    }
                } else {
                    callback.onFailure("Failed to fetch history information.");
                }
            }
        });
    }
}
