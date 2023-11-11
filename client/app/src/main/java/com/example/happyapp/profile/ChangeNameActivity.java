package com.example.happyapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ChangeNameActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private TextView newName;
    private Button submitButton;
    private LoadingDialog loadingDialog;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        email = getIntent().getStringExtra("email");

        backButton = findViewById(R.id.backButton);
        submitButton = findViewById(R.id.submit);
        newName = findViewById(R.id.name);
        backButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        loadingDialog = new LoadingDialog(ChangeNameActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(ChangeNameActivity.this, ProfileSettingActivity.class);
            startActivity(intent);
            finish();
        }
        if (v.getId() == R.id.submit) {
            String nameText = newName.getText().toString();

            if (TextUtils.isEmpty(nameText)) {
                newName.setError("Name is required");
                return;
            }

            loadingDialog.show();

            ApiHelper.changeName(email, nameText, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(ChangeNameActivity.this, "Change new name successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangeNameActivity.this, ProfileSettingActivity.class);
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
                                    Toasty.error(ChangeNameActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    Toasty.error(ChangeNameActivity.this, "Failed to change name.", Toast.LENGTH_SHORT).show();
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
                            Toasty.error(ChangeNameActivity.this, "Failed to change name.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    loadingDialog.dismiss();
                }
            });

        }
    }
}