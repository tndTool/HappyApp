package com.example.happyapp.tracking.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;
import com.example.happyapp.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TrackingCameraEatingScreen2Activity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backButton;
    private Button nextButton;
    private LinearLayout questionLayout;
    private List<String[]> questionList;
    private ArrayList<Question> userResponses;
    private List<String> userAnswers;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera_eating_screen2);

        userAnswers = new ArrayList<>();
        photo = getIntent().getParcelableExtra("photo");
        userResponses = getIntent().getParcelableArrayListExtra("userResponses");

        findView();
        setListeners();
        loadQuestionSetFromTXT();
        displayQuestions();
        showPopup();
    }

    private void findView() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        questionLayout = findViewById(R.id.questionLayout);
    }

    private void setListeners() {
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
        } else if (v.getId() == R.id.nextButton) {
            submitAnswers();
        }
    }

    public void loadQuestionSetFromTXT() {
        questionList = new ArrayList<>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.question_screen2);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] question = line.split("\\|");
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
            String questionType = question[1];
            String[] answerOptions = Arrays.copyOfRange(question, 2, question.length);

            TextView questionTextView = new TextView(this);
            questionTextView.setText(questionText);
            questionTextView.setTextColor(Color.BLACK);
            questionTextView.setTypeface(null, Typeface.BOLD);
            questionTextView.setLayoutParams(questionParams);
            questionLayout.addView(questionTextView);

            LinearLayout answerLayout = new LinearLayout(this);
            answerLayout.setOrientation(LinearLayout.VERTICAL);

            if (questionType.equals("single")) {
                RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);

                for (int i = 0; i < answerOptions.length; i++) {
                    RadioButton radioButton = new RadioButton(new ContextThemeWrapper(this, R.style.RadioButtonStyle));
                    radioButton.setText(answerOptions[i]);
                    radioButton.setTextColor(Color.BLACK);
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    radioGroup.addView(radioButton);
                }

                answerLayout.addView(radioGroup);
            } else if (questionType.equals("multiple")) {
                for (int i = 0; i < answerOptions.length; i++) {
                    CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, R.style.RadioButtonStyle));
                    checkBox.setText(answerOptions[i]);
                    checkBox.setTextColor(Color.BLACK);
                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    answerLayout.addView(checkBox);
                }
            }

            questionLayout.addView(answerLayout);
        }
    }

    public void submitAnswers() {
        boolean allQuestionsAnswered = true;

        for (int i = 0; i < questionLayout.getChildCount(); i++) {
            View childView = questionLayout.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout answerLayout = (LinearLayout) childView;

                if (answerLayout.getChildAt(0) instanceof RadioGroup) {
                    // Single select question (radio button)
                    RadioGroup radioGroup = (RadioGroup) answerLayout.getChildAt(0);

                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                        // No option selected
                        allQuestionsAnswered = false;
                    } else {
                        RadioButton selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                        String answer = selectedRadioButton.getText().toString();
                        userAnswers.add(answer);
                    }
                } else {
                    // Multi-select question (checkboxes)
                    ArrayList<String> selectedOptions = new ArrayList<>();

                    for (int j = 0; j < answerLayout.getChildCount(); j++) {
                        View answerView = answerLayout.getChildAt(j);

                        if (answerView instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) answerView;

                            if (checkBox.isChecked()) {
                                String answer = checkBox.getText().toString();
                                selectedOptions.add(answer);
                            }
                        }
                    }

                    if (!selectedOptions.isEmpty()) {
                        String answer = TextUtils.join(", ", selectedOptions);
                        userAnswers.add(answer);
                    } else {
                        allQuestionsAnswered = false;
                    }
                }
            }
        }

        if (allQuestionsAnswered) {
            for (int i = 0; i < questionList.size(); i++) {
                String[] question = questionList.get(i);
                String questionText = question[0];
                String answer = userAnswers.get(i);
                Question q = new Question(questionText, answer);
                userResponses.add(q);
            }

            Intent intent = new Intent(this, TrackingCameraEatingScreen3Activity.class);
            intent.putExtra("photo", photo);
            intent.putParcelableArrayListExtra("userResponses", userResponses);
            startActivity(intent);
        } else {
            Toasty.warning(this, "Please answer all questions!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        builder.setMessage("If you had one meal (breakfast, lunch, or dinner) in the last 4 hours, respond to the following questions about what you ate and the corresponding context. If you had more than one meal in the last 4 hours, choose the most recent one to respond to the questions.\n\nClick \"Next\" to start the questionnaire.");
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}