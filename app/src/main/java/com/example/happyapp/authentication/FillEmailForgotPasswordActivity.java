package com.example.happyapp.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.happyapp.R;

public class FillEmailForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_email_forgot_password);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(FillEmailForgotPasswordActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        }
    }
}