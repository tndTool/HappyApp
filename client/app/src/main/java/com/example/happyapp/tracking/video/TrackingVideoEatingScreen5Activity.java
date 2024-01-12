package com.example.happyapp.tracking.video;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.MainActivity;
import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.dialog.LoadingDialog;
import com.example.happyapp.model.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TrackingVideoEatingScreen5Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private Button submitButton;
    private ArrayList<Question> userResponses;
    private String videoUriString;
    ;
    private String email;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_video_eating_screen5);

        videoUriString = getIntent().getStringExtra("videoUri");
        userResponses = getIntent().getParcelableArrayListExtra("userResponses");

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        loadingDialog = new LoadingDialog(TrackingVideoEatingScreen5Activity.this);

        email = getEmailFromSharedPreferences();

        findView();
        setListeners();
    }

    private void findView() {
        backButton = findViewById(R.id.backButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setListeners() {
        backButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
        } else if (v.getId() == R.id.submitButton) {
            submitAnswers();
        }
    }

    public void submitAnswers() {
        File videoFile = saveVideoToFile(videoUriString);
        loadingDialog.show();

        ApiHelper.behaviorVideo(email, "eating", videoFile, userResponses, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toasty.success(TrackingVideoEatingScreen5Activity.this, "Submit successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TrackingVideoEatingScreen5Activity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                JSONObject errorResponse = new JSONObject(response.body().string());
                                String errorMessage = errorResponse.getString("error");
                                Toasty.error(TrackingVideoEatingScreen5Activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                Toasty.error(TrackingVideoEatingScreen5Activity.this, "Failed to submit.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        loadingDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.error(TrackingVideoEatingScreen5Activity.this, "Failed to submit.", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
            }
        });
    }

    private File saveVideoToFile(String videoUriString) {
        File videoFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "video_" + timeStamp + ".mp4";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            videoFile = new File(storageDir, fileName);

            try (InputStream inputStream = getContentResolver().openInputStream(Uri.parse(videoUriString));
                 OutputStream outputStream = new FileOutputStream(videoFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoFile;
    }

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }
}