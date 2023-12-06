package com.example.happyapp.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backButton;
    private TextView nameText, emailText, timeText;
    private RelativeLayout changePassword, changeName;
    private String email;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;

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

        email = getEmailFromSharedPreferences();

        loadingDialog.show();

        ApiHelper.getUserInfo(email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.error(ProfileSettingActivity.this, "Failed to fetch user information.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject userJson = new JSONObject(responseData);
                                String name = userJson.getString("name");
                                String email = userJson.getString("email");
                                String joinDate = userJson.getString("joinDate");

                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                Date date = inputFormat.parse(joinDate);
                                String formattedJoinDate = outputFormat.format(date);

                                nameText.setText(name);
                                emailText.setText(email);
                                timeText.setText(formattedJoinDate);
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            } finally {
                                loadingDialog.dismiss();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(ProfileSettingActivity.this, "Failed to fetch user information.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadingDialog.dismiss();
                }
            }
        });


        changeName.setOnClickListener(this);
        backButton.setOnClickListener(this);
        changePassword.setOnClickListener(this);
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

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }
}