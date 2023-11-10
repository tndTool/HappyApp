package com.example.happyapp.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyapp.R;
import com.example.happyapp.adapter.FAQsAdapter;
import com.example.happyapp.model.FAQs;

import java.util.ArrayList;
import java.util.List;

public class FAQsActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    List<FAQs> faQsList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        recyclerView = findViewById(R.id.listFAQs);
        backButton = findViewById(R.id.backButton);


        backButton.setOnClickListener(this);

        initData();
        setRecyclerView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
        }
    }

    private void setRecyclerView() {
        FAQsAdapter faQsAdapter = new FAQsAdapter(faQsList);
        recyclerView.setAdapter(faQsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {
        faQsList = new ArrayList<>();

        String title1 = "Purpose of Apps that Collect Human Behavior";
        String description1 = "Learn about the reasons why apps collect human behavior data and how it benefits users and developers.";

        faQsList.add(new FAQs(title1, description1));

        String title2 = "Collection of Human Behavior Data by Apps";
        String description2 = "Discover the various methods employed by apps to gather information on user behavior and interactions.";

        faQsList.add(new FAQs(title2, description2));

        String title3 = "Privacy and Apps that Collect Human Behavior";
        String description3 = "Understand the privacy measures implemented by apps to safeguard user data and address concerns about information security.";

        faQsList.add(new FAQs(title3, description3));

        String title4 = "Control over Behavior Data Collection";
        String description4 = "Learn about the options available to users to manage and control the collection of their behavior data by these apps.";

        faQsList.add(new FAQs(title4, description4));

        String title5 = "Usage of Behavior Data by App Developers";
        String description5 = "Discover how app developers utilize the collected behavior data to improve user experiences and develop better apps.";

        faQsList.add(new FAQs(title5, description5));

        String title6 = "Security of Collected Behavior Data";
        String description6 = "Explore the measures taken by apps to ensure the security and protection of the behavior data they collect.";

        faQsList.add(new FAQs(title6, description6));

        String title7 = "Consent for Behavior Data Collection";
        String description7 = "Understand the importance of user consent and how apps obtain permission to collect behavior data.";

        faQsList.add(new FAQs(title7, description7));

        String title8 = "Regulations on Behavior Data Collection";
        String description8 = "Learn about the regulations and guidelines app developers must adhere to when collecting behavior data.";

        faQsList.add(new FAQs(title8, description8));

        String title9 = "Storage Duration of Behavior Data";
        String description9 = "Find out how long apps typically retain behavior data and the factors that influence data retention periods.";

        faQsList.add(new FAQs(title9, description9));

        String title10 = "Opting Out of Behavior Data Collection";
        String description10 = "Discover the options available to users who prefer not to participate in behavior data collection by apps.";

        faQsList.add(new FAQs(title10, description10));

        String title11 = "Benefits of Sharing Behavior Data";
        String description11 = "Learn about the potential advantages and improvements that can arise from voluntarily sharing behavior data with apps.";

        faQsList.add(new FAQs(title11, description11));

        String title12 = "Accessing and Reviewing Behavior Data";
        String description12 = "Discover how users can access and review the behavior data collected by apps for transparency and control.";

        faQsList.add(new FAQs(title12, description12));

    }
}