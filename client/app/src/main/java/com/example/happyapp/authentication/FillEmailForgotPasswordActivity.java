package com.example.happyapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class FillEmailForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton;
    private EditText email;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_email_forgot_password);

        email = findViewById(R.id.email);
        send = findViewById(R.id.send);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(FillEmailForgotPasswordActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        }
        if (v.getId() == R.id.send) {
            String emailText = email.getText().toString();
            ApiHelper.sendEmailFP(emailText, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Handle the registration success response
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.info(FillEmailForgotPasswordActivity.this, "Please verify otp from your mail!", Toast.LENGTH_SHORT).show();
                                // Redirect to the main activity or perform any other necessary action
                                Intent intent = new Intent(FillEmailForgotPasswordActivity.this, VerifyOtpForgotPasswordActivity.class);
                                intent.putExtra("email", emailText);
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
                                    Toasty.error(FillEmailForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    Toasty.error(FillEmailForgotPasswordActivity.this, "Failed to verify forgot password.", Toast.LENGTH_SHORT).show();
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
                            Toasty.error(FillEmailForgotPasswordActivity.this, "Failed to verify forgot password.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}