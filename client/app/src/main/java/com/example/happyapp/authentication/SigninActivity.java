package com.example.happyapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.MainActivity;
import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private TextView signupButton, forgotPassword;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private boolean passwordVisible;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signupButton = findViewById(R.id.signupButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        loadingDialog = new LoadingDialog(SigninActivity.this);

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

            loadingDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ApiHelper.loginUser(email, password, new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Handle the login response
                            if (response.isSuccessful()) {
                                // Parse the response JSON
                                try {
                                    JSONObject jsonResponse = new JSONObject(response.body().string());
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toasty.success(SigninActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                                // Redirect to the main activity or perform any other necessary action
                                                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        String errorMessage = jsonResponse.getString("error");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toasty.error(SigninActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject errorResponse = new JSONObject(response.body().string());
                                            String errorMessage = errorResponse.getString("error");
                                            Toasty.error(SigninActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        } catch (JSONException | IOException e) {
                                            e.printStackTrace();
                                            Toasty.error(SigninActivity.this, "Failed to log in.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            loadingDialog.dismiss();
                        }
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle the login failure or network error
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toasty.error(SigninActivity.this, "Failed to log in.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            loadingDialog.dismiss();
                        }
                    });
                }
            }, 1000);
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