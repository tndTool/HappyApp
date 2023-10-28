package com.example.happyapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private TextView signupButton;
    private EditText password;
    private boolean passwordVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signupButton = findViewById(R.id.signupButton);
        password = findViewById(R.id.password);

        signupButton.setOnClickListener(this);
        password.setOnTouchListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signupButton) {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int Right = 2;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (motionEvent.getRawX() >= password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()) {
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
            }
        }
        return false;
    }
}