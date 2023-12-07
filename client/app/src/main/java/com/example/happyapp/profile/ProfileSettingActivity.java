package com.example.happyapp.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.happyapp.R;
import com.example.happyapp.dialog.LoadingDialog;
import com.example.happyapp.viewmodal.ProfileViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backButton;
    private TextView nameText, emailText, timeText;
    private RelativeLayout changePassword, changeName;
    private String email;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        backButton = findViewById(R.id.backButton);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        timeText = findViewById(R.id.timeJoin);
        changePassword = findViewById(R.id.changePassword);
        changeName = findViewById(R.id.changeName);

        loadingDialog = new LoadingDialog(ProfileSettingActivity.this);
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        email = getEmailFromSharedPreferences();

        loadingDialog.show();

        if (!email.isEmpty()) {
            fetchUserInfo(email);
        }

        profileViewModel.getUserLiveData().observe(this, user -> {
            nameText.setText(user.getName());
            timeText.setText(formatDate(user.getCreatedAt()));
            emailText.setText(user.getEmail());
            loadingDialog.dismiss();
        });

        changeName.setOnClickListener(this);
        backButton.setOnClickListener(this);
        changePassword.setOnClickListener(this);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }


    private void fetchUserInfo(String userEmail) {
        loadingDialog.show();
        profileViewModel.fetchUserInfo(userEmail);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
            finish();
        }
        if (v.getId() == R.id.changeName) {
            Intent intent = new Intent(ProfileSettingActivity.this, ChangeNameActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        }
        if (v.getId() == R.id.changePassword) {
            Intent intent = new Intent(ProfileSettingActivity.this, ChangePasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String userEmail = getEmailFromSharedPreferences();

        if (!userEmail.isEmpty()) {
            fetchUserInfo(userEmail);
        }
    }

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }
}