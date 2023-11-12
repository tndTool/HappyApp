package com.example.happyapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private ImageView backButton;
    private EditText password, confirmPassword;
    private boolean passwordVisible;
    private Button submit;
    private String email;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        email = getIntent().getStringExtra("email");

        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        submit = findViewById(R.id.submit);
        backButton = findViewById(R.id.backButton);
        password.setOnTouchListener(this);
        confirmPassword.setOnTouchListener(this);
        backButton.setOnClickListener(this);
        submit.setOnClickListener(this);
        loadingDialog = new LoadingDialog(ChangePasswordActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(ChangePasswordActivity.this, ProfileSettingActivity.class);
            startActivity(intent);
            finish();
        }
        if (v.getId() == R.id.submit) {
            String passwordText = password.getText().toString();
            String confirmPasswordText = confirmPassword.getText().toString();

            if (TextUtils.isEmpty(passwordText)) {
                password.setError("Password is required");
                return;
            }

            if (TextUtils.isEmpty(confirmPasswordText)) {
                confirmPassword.setError("Confirm password is required");
                return;
            }

            loadingDialog.show();

            if (passwordText.equals(confirmPasswordText)) {

                ApiHelper.changePassword(email, passwordText, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toasty.success(ChangePasswordActivity.this, "Password change successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ChangePasswordActivity.this, ProfileSettingActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject errorResponse = new JSONObject(response.body().string());
                                        String errorMessage = errorResponse.getString("error");
                                        Toasty.error(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                        Toasty.error(ChangePasswordActivity.this, "Failed to change password.", Toast.LENGTH_SHORT).show();
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
                                Toasty.error(ChangePasswordActivity.this, "Failed to change password.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadingDialog.dismiss();
                    }
                });
            } else {
                Toasty.error(ChangePasswordActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        }
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int Right = 2;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (view == password && motionEvent.getRawX() >= password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()) {
                int selection = password.getSelectionEnd();
                if (passwordVisible) {
                    password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.hide_password_icon, 0);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.show_password_icon, 0);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                passwordVisible = !passwordVisible;
                password.setSelection(selection);
                return true;
            } else if (view == confirmPassword && motionEvent.getRawX() >= confirmPassword.getRight() - confirmPassword.getCompoundDrawables()[Right].getBounds().width()) {
                int selection = confirmPassword.getSelectionEnd();
                if (passwordVisible) {
                    confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.hide_password_icon, 0);
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.show_password_icon, 0);
                    confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                passwordVisible = !passwordVisible;
                confirmPassword.setSelection(selection);
                return true;
            }
        }
        return false;
    }
}