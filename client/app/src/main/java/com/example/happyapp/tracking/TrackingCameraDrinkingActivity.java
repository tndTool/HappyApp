package com.example.happyapp.tracking;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.happyapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TrackingCameraDrinkingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private LinearLayout questionLayout;
    private List<String[]> questionList;
    private Button submitButton;
    private List<String> userAnswers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera_drinking);
        findView();
        setListeners();
        loadQuestionSetFromCSV();
        displayQuestions();

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
            InputStream inputStream = getResources().openRawResource(R.raw.drinking);
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
            // All questions answered, proceed with the necessary actions
        } else {
            Toasty.warning(this, "Please answer all questions!", Toast.LENGTH_SHORT).show();
        }
    }
}