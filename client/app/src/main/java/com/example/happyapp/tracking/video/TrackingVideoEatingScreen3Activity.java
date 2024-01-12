package com.example.happyapp.tracking.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;
import com.example.happyapp.model.Question;
import com.example.happyapp.tracking.camera.TrackingCameraEatingScreen4Activity;
import com.example.happyapp.tracking.camera.TrackingCameraEatingScreen5Activity;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class TrackingVideoEatingScreen3Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private Button nextButton;
    private ArrayList<Question> userResponses;
    private Uri videoUri;
    private TextView questionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_video_eating_screen3);

        videoUri = Uri.parse(getIntent().getStringExtra("videoUri"));
        userResponses = getIntent().getParcelableArrayListExtra("userResponses");

        findView();
        setListeners();
        showPopup();
    }

    private void findView() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        questionTextView = findViewById(R.id.questionTextView);
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

    public void submitAnswers() {
        RadioGroup radioGroup = findViewById(R.id.radio_group_snacks);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);

        if (selectedRadioButton != null) {
            String questionText = questionTextView.getText().toString();
            String answer = selectedRadioButton.getText().toString();

            Question question = new Question(questionText, answer);

            userResponses.add(question);

            if (answer.equals("0")) {
                Intent intent = new Intent(this, TrackingVideoEatingScreen5Activity.class);
                intent.putExtra("videoUri", videoUri.toString());
                intent.putParcelableArrayListExtra("userResponses", userResponses);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, TrackingVideoEatingScreen4Activity.class);
                intent.putExtra("videoUri", videoUri.toString());
                intent.putParcelableArrayListExtra("userResponses", userResponses);
                startActivity(intent);
            }
        } else {
            Toasty.warning(this, "Please select an answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        builder.setMessage("We will ask you a few questions about your consumed snacks in the last 4 hours. Two or more items eaten at the SAME TIME count as ONE snack (for instance, a coffee and a cookie). In contrast, two items eaten at DIFFERENT TIMES count as TWO snacks and so on (for instance, a coffee now and a cookie 30 minutes later).\n\nClick \"Next\" to start the questionnaire.");
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