package com.example.happyapp.tracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.happyapp.MainActivity;
import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.dialog.LoadingDialog;
import com.example.happyapp.model.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TrackingVideoEatingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private LinearLayout questionLayout;
    private List<String[]> questionList;
    private Button submitButton;
    private String email;
    private List<String> userAnswers;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_video_drinking);
        findView();
        setListeners();
        loadQuestionSetFromCSV();
        displayQuestions();
        loadingDialog = new LoadingDialog(TrackingVideoEatingActivity.this);
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        email = getEmailFromSharedPreferences();

        userAnswers = new ArrayList<>();
    }

    private void findView() {
        backButton = findViewById(R.id.backButton);
        submitButton = findViewById(R.id.submitButton);
        questionLayout = findViewById(R.id.questionLayout);
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

    public void loadQuestionSetFromCSV() {
        questionList = new ArrayList<>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.eating);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] question = line.split(",");
                questionList.add(question);
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayQuestions() {
        LinearLayout.LayoutParams questionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        questionParams.setMargins(0, 5, 0, 0);

        for (String[] question : questionList) {
            String questionText = question[0];

            TextView questionTextView = new TextView(this);
            questionTextView.setText(questionText);
            questionTextView.setTextColor(Color.BLACK);
            questionTextView.setTypeface(null, Typeface.BOLD);
            questionTextView.setLayoutParams(questionParams);
            questionLayout.addView(questionTextView);

            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(LinearLayout.VERTICAL);

            for (int i = 1; i < question.length; i++) {
                RadioButton radioButton = new RadioButton(new ContextThemeWrapper(this, R.style.RadioButtonStyle));
                radioButton.setText(question[i]);
                radioButton.setTextColor(Color.BLACK);
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                radioGroup.addView(radioButton);
            }

            questionLayout.addView(radioGroup);
        }
    }

    public void submitAnswers() {
        userAnswers.clear();
        boolean allQuestionsAnswered = true;

        for (int i = 0; i < questionLayout.getChildCount(); i++) {
            View childView = questionLayout.getChildAt(i);

            if (childView instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) childView;
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton radioButton = radioGroup.findViewById(selectedId);
                    String answer = radioButton.getText().toString();
                    userAnswers.add(answer);
                } else {
                    allQuestionsAnswered = false;
                }
            }
        }

        if (allQuestionsAnswered) {
            String videoUriString = getIntent().getStringExtra("videoUri");
            File videoFile = saveVideoToFile(videoUriString);

            loadingDialog.show();

            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < questionList.size(); i++) {
                String[] question = questionList.get(i);
                String questionText = question[0];
                String answer = userAnswers.get(i);
                Question q = new Question(questionText, answer);
                questions.add(q);
            }

            ApiHelper.behaviorVideo(email, "eating", videoFile, questions, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                Toasty.success(TrackingVideoEatingActivity.this, "Submit successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TrackingVideoEatingActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                try {
                                    JSONObject errorResponse = new JSONObject(response.body().string());
                                    String errorMessage = errorResponse.getString("error");
                                    Toasty.error(TrackingVideoEatingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    Toasty.error(TrackingVideoEatingActivity.this, "Failed to submit 1.", Toast.LENGTH_SHORT).show();
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
                            Toasty.error(TrackingVideoEatingActivity.this, "Failed to submit 2.", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            Toasty.warning(this, "Please answer all questions!", Toast.LENGTH_SHORT).show();
        }
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