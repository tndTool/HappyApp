package com.example.happyapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

public class VerifyOtpSignupActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp_signup);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(VerifyOtpSignupActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}