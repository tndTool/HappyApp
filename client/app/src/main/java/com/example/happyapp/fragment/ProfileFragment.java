package com.example.happyapp.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.happyapp.R;
import com.example.happyapp.authentication.SigninActivity;
import com.example.happyapp.dialog.LoadingDialog;
import com.example.happyapp.model.User;
import com.example.happyapp.profile.AboutUsActivity;
import com.example.happyapp.profile.FAQsActivity;
import com.example.happyapp.profile.NotificationSettingActivity;
import com.example.happyapp.profile.PrivacySettingActivity;
import com.example.happyapp.profile.ProfileSettingActivity;
import com.example.happyapp.profile.SendUsActivity;
import com.example.happyapp.viewmodal.UserViewModel;

public class ProfileFragment extends Fragment {

    private TextView nameText;
    private TextView mailText;
    private Button logoutButton;
    private UserViewModel userViewModel;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        nameText = rootView.findViewById(R.id.nameText);
        mailText = rootView.findViewById(R.id.mailText);
        logoutButton = rootView.findViewById(R.id.logoutButton);
        loadingDialog = new LoadingDialog(getActivity());

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);


        Bundle bundle = getArguments();
        if (bundle != null) {
            String userEmail = bundle.getString("email");
            if (userEmail != null) {
                loadingDialog.show();
                userViewModel.fetchUserInfo(userEmail);
            }
        }

        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                nameText.setText(user.getName());
                mailText.setText(user.getEmail());
                loadingDialog.dismiss();
            }
        });

        RelativeLayout profileSetting = rootView.findViewById(R.id.profileSetting);
        RelativeLayout notificationSetting = rootView.findViewById(R.id.notificationSetting);
        RelativeLayout privacySetting = rootView.findViewById(R.id.privacySetting);
        RelativeLayout sendUs = rootView.findViewById(R.id.sendUsSetting);
        RelativeLayout aboutUs = rootView.findViewById(R.id.aboutUsSetting);
        RelativeLayout faqs = rootView.findViewById(R.id.faqsSetting);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Yes" - proceed with logout
                                Intent intent = new Intent(getActivity(), SigninActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "No" - do nothing
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        View.OnClickListener settingsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class<?> targetActivity = null;

                if (view.getId() == R.id.profileSetting) {
                    Intent intent = new Intent(getActivity(), ProfileSettingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("email", mailText.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
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

        return rootView;
    }
}