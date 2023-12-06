package com.example.happyapp.api;

import com.example.happyapp.model.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiHelper {
    //    private static final String BASE_URL = "https://happy-app-server.vercel.app/api/";
    private static final String BASE_URL = "http://192.168.1.6:5000/api/";

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

    public static void changeName(String email, String name, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "user/changename")
                .put(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void changePassword(String email, String password, Callback callback) {
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
                .url(BASE_URL + "user/changepassword")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void saveDataSensor(String email, String value, Callback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "sensor/data")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void behaviorCamera(String email, String behavior, File image, List<Question> questions, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("image/*");
        RequestBody imageBody = RequestBody.create(mediaType, image);

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("behavior", behavior)
                .addFormDataPart("image", image.getName(), imageBody);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            requestBodyBuilder
                    .addFormDataPart("questions[" + i + "][question]", question.getQuestion())
                    .addFormDataPart("questions[" + i + "][answer]", question.getAnswer());
        }

        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder()
                .url(BASE_URL + "camera/behavior")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void behaviorVideo(String email, String behavior, File videoFile, List<Question> questions, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("video/mp4");
        RequestBody videoBody = RequestBody.create(mediaType, videoFile);

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("behavior", behavior)
                .addFormDataPart("video", videoFile.getName(), videoBody);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            requestBodyBuilder
                    .addFormDataPart("questions[" + i + "][question]", question.getQuestion())
                    .addFormDataPart("questions[" + i + "][answer]", question.getAnswer());
        }

        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder()
                .url(BASE_URL + "video/behavior")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
