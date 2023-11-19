package com.example.happyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.happyapp.databinding.ActivityMainBinding;
import com.example.happyapp.fragment.HomeFragment;
import com.example.happyapp.fragment.ProfileFragment;
import com.example.happyapp.tracking.TrackingCameraActivity;
import com.example.happyapp.tracking.TrackingVideoActivity;
import com.example.happyapp.viewmodal.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private String userEmail;
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    private static final int REQUEST_VIDEO_CAPTURE = 456;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userEmail = getIntent().getStringExtra("email");
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.profile) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    userViewModel.fetchUserInfo(userEmail);
                    replaceFragment(profileFragment);
                }
                return true;
            }
        });

        binding.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraOptions(view);
            }
        });
    }

    private void showCameraOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.camera_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_photo) {
                    dispatchTakePictureIntent();
                    return true;
                } else if (item.getItemId() == R.id.menu_video) {
                    dispatchRecordVideoIntent();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void dispatchRecordVideoIntent() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoIntent, REQUEST_VIDEO_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Intent displayIntent = new Intent(this, TrackingCameraActivity.class);
            displayIntent.putExtra("photo", imageBitmap);
            startActivity(displayIntent);
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data.getData();

            Intent displayIntent = new Intent(this, TrackingVideoActivity.class);
            displayIntent.putExtra("videoUri", videoUri.toString());
            startActivity(displayIntent);
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}