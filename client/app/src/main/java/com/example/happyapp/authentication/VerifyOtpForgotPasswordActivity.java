package com.example.happyapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class VerifyOtpForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton;
    private EditText otpBox1, otpBox2, otpBox3, otpBox4;
    private Button submit;
    private String email;
    private TextView resendButton;
    private CountDownTimer resendTimer;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp_forgot_password);

        email = getIntent().getStringExtra("email");

        backButton = findViewById(R.id.backButton);
        resendButton = findViewById(R.id.resend);
        otpBox1 = findViewById(R.id.otpBox1);
        otpBox2 = findViewById(R.id.otpBox2);
        otpBox3 = findViewById(R.id.otpBox3);
        otpBox4 = findViewById(R.id.otpBox4);
        submit = findViewById(R.id.submit);

        loadingDialog = new LoadingDialog(VerifyOtpForgotPasswordActivity.this);

        backButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        submit.setOnClickListener(this);

        TextWatcher otpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText currentBox = (EditText) getCurrentFocus();

                if (currentBox != null && s.length() == 0 && start == 0 && before == 1) {
                    currentBox.clearFocus();
                    if (currentBox == otpBox1) {
                        otpBox1.requestFocus();
                    } else if (currentBox == otpBox2) {
                        otpBox1.requestFocus();
                    } else if (currentBox == otpBox3) {
                        otpBox2.requestFocus();
                    } else if (currentBox == otpBox4) {
                        otpBox3.requestFocus();
                    }
                } else if (currentBox != null && s.length() == 1 && start == 0 && before == 0) {
                    if (currentBox == otpBox1) {
                        otpBox2.requestFocus();
                    } else if (currentBox == otpBox2) {
                        otpBox3.requestFocus();
                    } else if (currentBox == otpBox3) {
                        otpBox4.requestFocus();
                    }
                } else if (currentBox != null && s.length() == 0 && start == 1 && before == 0) {
                    if (currentBox == otpBox4) {
                        otpBox3.requestFocus();
                    } else if (currentBox == otpBox3) {
                        otpBox2.requestFocus();
                    } else if (currentBox == otpBox2) {
                        otpBox1.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        otpBox1.addTextChangedListener(otpTextWatcher);
        otpBox2.addTextChangedListener(otpTextWatcher);
        otpBox3.addTextChangedListener(otpTextWatcher);
        otpBox4.addTextChangedListener(otpTextWatcher);

        //  Handle resend OTP
        resendTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                resendButton.setEnabled(false);
                resendButton.setText("Resend in " + secondsRemaining + "s");
            }

            @Override
            public void onFinish() {
                resendButton.setEnabled(true);
                resendButton.setText("Resend");
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
            finish();
        }
        if (v.getId() == R.id.resend) {
            otpBox1.setText("");
            otpBox2.setText("");
            otpBox3.setText("");
            otpBox4.setText("");

            otpBox1.requestFocus();

            resendButton.setEnabled(false);
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long secondsRemaining = millisUntilFinished / 1000;
                    resendButton.setText("Resend in " + secondsRemaining + "s");
                }

                @Override
                public void onFinish() {
                    resendButton.setEnabled(true);
                    resendButton.setText("Resend");
                }
            }.start();

            loadingDialog.show();

            ApiHelper.sendEmailFP(email, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(VerifyOtpForgotPasswordActivity.this, "Resend OTP successfully!", Toast.LENGTH_SHORT).show();
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
                                    Toasty.error(VerifyOtpForgotPasswordActivity.this, "Failed to resend OTP.", Toast.LENGTH_SHORT).show();
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
                            Toasty.error(VerifyOtpForgotPasswordActivity.this, "Failed to resend OTP.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadingDialog.dismiss();
                }
            });
        }
        if (v.getId() == R.id.submit) {
            String otp1Text = otpBox1.getText().toString();
            String otp2Text = otpBox2.getText().toString();
            String otp3Text = otpBox3.getText().toString();
            String otp4Text = otpBox4.getText().toString();

            String otpText = otp1Text + otp2Text + otp3Text + otp4Text;

            loadingDialog.show();
            ApiHelper.verifyOTPFP(email, otpText, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(VerifyOtpForgotPasswordActivity.this, "Verify OTP successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(VerifyOtpForgotPasswordActivity.this, FillNewPasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
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
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(VerifyOtpForgotPasswordActivity.this, "Failed to verify OTP.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadingDialog.dismiss();
                }
            });
        }
    }
}