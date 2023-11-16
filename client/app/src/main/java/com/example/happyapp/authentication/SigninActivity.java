package com.example.happyapp.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
import com.example.happyapp.sensor.SensorsActivity;

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
    private SharedPreferences sharedPreferences;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (isLoggedIn()) {
            redirectToMainActivity();
        }

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
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required");
                return;
            }

            loadingDialog.show();


            ApiHelper.loginUser(email, password, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toasty.success(SigninActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                        saveLoginSession(email);
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
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void saveLoginSession(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.apply();
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