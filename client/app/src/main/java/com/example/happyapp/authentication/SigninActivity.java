package com.example.happyapp.authentication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.MainActivity;
import com.example.happyapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.*;


public class SigninActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private TextView signupButton, forgotPassword;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private boolean passwordVisible;
    private static final String API_URL = "http://192.168.1.15:5000/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signupButton = findViewById(R.id.signupButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        signupButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        passwordEditText.setOnTouchListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signupButton) {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.forgotPassword) {
            Intent intent = new Intent(SigninActivity.this, FillEmailForgotPasswordActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.loginButton) {
            // Retrieve the email and password
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(email.equals("meo@mailinator.com") && password.equals("25012001"))
            {
                Toasty.success(SigninActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toasty.error(SigninActivity.this, "Login fails", Toast.LENGTH_SHORT).show();
            }

//             Call the login API endpoint
//            new LoginTask().execute(email, password);
        }
    }

    // AsyncTask to handle the login process
    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            try {
                // Create the JSON payload
                JSONObject requestBodyJson = new JSONObject();
                requestBodyJson.put("email", email);
                requestBodyJson.put("password", password);
                String requestBody = requestBodyJson.toString();

                // Create the OkHttp client
                OkHttpClient client = new OkHttpClient();

                // Set the request body
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, requestBody);

                // Create the request
                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                // Execute the request and get the response
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    // Read the response
                    return response.body().string();
                } else {
                    Log.e("LoginTask", "Failed to log in. Response code: " + response.code());
                    return null;
                }
            } catch (IOException | JSONException e) {
                Log.e("LoginTask", "Failed to log in: " + e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null && !response.isEmpty()) {
                // Parse the JSON response and handle accordingly
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.optString("message");
                    String error = jsonObject.optString("error");
                    if (success) {
                        // Login successful
                        Toasty.success(SigninActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        // Login failed
                        Toasty.error(SigninActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("LoginTask", "Failed to parse JSON response: " + e.getMessage());
                }
            } else {
                Toasty.error(SigninActivity.this, "Failed to log in.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int Right = 2;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (motionEvent.getRawX() >= passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[Right].getBounds().width()) {
                int selection = passwordEditText.getSelectionEnd();
                if (passwordVisible) {
                    passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.hide_password_icon, 0);
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.show_password_icon, 0);
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                passwordVisible = !passwordVisible;
                passwordEditText.setSelection(selection);
                return true;
            }
        }
        return false;
    }
}