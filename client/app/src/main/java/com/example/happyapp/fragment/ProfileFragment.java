package com.example.happyapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.profile.AboutUsActivity;
import com.example.happyapp.profile.FAQsActivity;
import com.example.happyapp.profile.NotificationSettingActivity;
import com.example.happyapp.profile.PrivacySettingActivity;
import com.example.happyapp.profile.ProfileSettingActivity;
import com.example.happyapp.profile.SendUsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private TextView nameText;
    private TextView mailText;
    private String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        nameText = rootView.findViewById(R.id.nameText);
        mailText = rootView.findViewById(R.id.mailText);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userEmail = bundle.getString("email");
        }

        RelativeLayout profileSetting = rootView.findViewById(R.id.profileSetting);
        RelativeLayout notificationSetting = rootView.findViewById(R.id.notificationSetting);
        RelativeLayout privacySetting = rootView.findViewById(R.id.privacySetting);
        RelativeLayout sendUs = rootView.findViewById(R.id.sendUsSetting);
        RelativeLayout aboutUs = rootView.findViewById(R.id.aboutUsSetting);
        RelativeLayout faqs = rootView.findViewById(R.id.faqsSetting);

        View.OnClickListener settingsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class<?> targetActivity = null;

                if (view.getId() == R.id.profileSetting) {
                    targetActivity = ProfileSettingActivity.class;
                } else if (view.getId() == R.id.notificationSetting) {
                    targetActivity = NotificationSettingActivity.class;
                } else if (view.getId() == R.id.privacySetting) {
                    targetActivity = PrivacySettingActivity.class;
                } else if (view.getId() == R.id.sendUsSetting) {
                    targetActivity = SendUsActivity.class;
                } else if (view.getId() == R.id.aboutUsSetting) {
                    targetActivity = AboutUsActivity.class;
                } else if (view.getId() == R.id.faqsSetting) {
                    targetActivity = FAQsActivity.class;
                }

                if (targetActivity != null) {
                    Intent intent = new Intent(getActivity(), targetActivity);
                    startActivity(intent);
                }
            }
        };

        profileSetting.setOnClickListener(settingsClickListener);
        notificationSetting.setOnClickListener(settingsClickListener);
        privacySetting.setOnClickListener(settingsClickListener);
        sendUs.setOnClickListener(settingsClickListener);
        aboutUs.setOnClickListener(settingsClickListener);
        faqs.setOnClickListener(settingsClickListener);

        ApiHelper.getUserInfo(userEmail, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.error(getActivity(), "Failed to fetch user information.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject userJson = new JSONObject(responseData);
                                String name = userJson.getString("name");
                                String email = userJson.getString("email");

                                nameText.setText(name);
                                mailText.setText(email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(getActivity(), "Failed to fetch user information.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return rootView;
    }
}