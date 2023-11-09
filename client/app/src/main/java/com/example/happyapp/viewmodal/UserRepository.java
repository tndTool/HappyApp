package com.example.happyapp.viewmodal;

import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserRepository {
    public interface UserCallback {
        void onSuccess(User user);

        void onFailure(String errorMessage);
    }

    public void getUserInfo(String userEmail, UserCallback callback) {
        ApiHelper.getUserInfo(userEmail, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Failed to fetch user information.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject userJson = new JSONObject(responseData);
                        String name = userJson.getString("name");
                        String email = userJson.getString("email");
                        String dateString = userJson.getString("joinDate");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date createdAt = dateFormat.parse(dateString);

                        User user = new User(name, email, createdAt);
                        callback.onSuccess(user);
                    } catch (JSONException e) {
                        callback.onFailure("Failed to parse user information.");
                    } catch (ParseException e) {
                        callback.onFailure("Failed to parse date.");
                    }
                } else {
                    callback.onFailure("Failed to fetch user information.");
                }
            }
        });
    }
}
