package com.example.happyapp.api;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiHelper {
    private static final String BASE_URL = "http://192.168.1.24:5000/api/";

    public static void registerUser(String name, String email, String password, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", name);
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "register")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void loginUser(String email, String password, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "login")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void verifyUser(String email, String otp, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "verify")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendEmailFP(String email, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "forgotpassword/sendotp")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void verifyOTPFP(String email, String otp, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "forgotpassword/verifyotp")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void resetPassword(String email, String password, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "forgotpassword/resetpassword")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void resendOtp(String email, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "resendOtp")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
    public static void getUserInfo(String email, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + "user/" + email)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
}
