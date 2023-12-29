package com.example.happyapp.tracking.camera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import java.io.OutputStream;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TrackingCameraEatingScreen5Activity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backButton;
    private Button submitButton;
    private ArrayList<Question> userResponses;
    private Bitmap photo;
    private String email;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera_eating_screen5);

        photo = getIntent().getParcelableExtra("photo");
        userResponses = getIntent().getParcelableArrayListExtra("userResponses");

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        loadingDialog = new LoadingDialog(TrackingCameraEatingScreen5Activity.this);

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
        File imageFile = saveBitmapToFile(photo);
        loadingDialog.show();

        ApiHelper.behaviorCamera(email, "eating", imageFile, userResponses, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toasty.success(TrackingCameraEatingScreen5Activity.this, "Submit successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TrackingCameraEatingScreen5Activity.this, MainActivity.class);
                            intent.putExtra("showVideoPopup", true);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                JSONObject errorResponse = new JSONObject(response.body().string());
                                String errorMessage = errorResponse.getString("error");
                                Toasty.error(TrackingCameraEatingScreen5Activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                Toasty.error(TrackingCameraEatingScreen5Activity.this, "Failed to submit.", Toast.LENGTH_SHORT).show();
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
                        Toasty.error(TrackingCameraEatingScreen5Activity.this, "Failed to submit.", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
            }
        });
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "temp_image.jpg");
        try (OutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }
}