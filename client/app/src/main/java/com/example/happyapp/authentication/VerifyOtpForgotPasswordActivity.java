package com.example.happyapp.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.happyapp.MainActivity;
import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VerifyOtpForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton backButton ;
    private EditText otpBox1, otpBox2, otpBox3, otpBox4;
    private Button submit;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp_forgot_password);

        email = getIntent().getStringExtra("email");

        backButton = findViewById(R.id.backButton);
        otpBox1 = findViewById(R.id.otpBox1);
        otpBox2 = findViewById(R.id.otpBox2);
        otpBox3 = findViewById(R.id.otpBox3);
        otpBox4 = findViewById(R.id.otpBox4);
        submit = findViewById(R.id.submit);

        backButton.setOnClickListener(this);
        submit.setOnClickListener(this);

        otpBox1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otpBox2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpBox2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otpBox3.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpBox3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    otpBox4.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        otpBox4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                otpBox4.clearFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(VerifyOtpForgotPasswordActivity.this, FillEmailForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        }
        if (v.getId() == R.id.submit) {
            String otp1Text = otpBox1.getText().toString();
            String otp2Text = otpBox2.getText().toString();
            String otp3Text = otpBox3.getText().toString();
            String otp4Text = otpBox4.getText().toString();

            String otpText = otp1Text + otp2Text + otp3Text + otp4Text;

            ApiHelper.verifyOTPFP(email, otpText, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Handle the registration success response
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(VerifyOtpForgotPasswordActivity.this, "Verify OTP successfully!", Toast.LENGTH_SHORT).show();
                                // Redirect to the main activity or perform any other necessary action
                                Intent intent = new Intent(VerifyOtpForgotPasswordActivity.this, FillNewPasswordActivity.class);
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
                                    Toasty.error(VerifyOtpForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    Toasty.error(VerifyOtpForgotPasswordActivity.this, "Failed to verify OTP.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(VerifyOtpForgotPasswordActivity.this, "Failed to verify OTP.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}