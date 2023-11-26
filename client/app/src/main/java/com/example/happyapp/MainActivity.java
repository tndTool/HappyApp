package com.example.happyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private String userEmail;
    private Uri videoUri;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> recordVideoLauncher;

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

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            Intent displayIntent = new Intent(this, TrackingCameraActivity.class);
                            displayIntent.putExtra("photo", imageBitmap);
                            startActivity(displayIntent);
                        } else {
                            Toasty.error(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        recordVideoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    videoUri = data.getData();
                    if (videoUri != null) {
                        Intent displayIntent = new Intent(this, TrackingVideoActivity.class);
                        displayIntent.putExtra("videoUri", videoUri.toString());
                        startActivity(displayIntent);
                    } else {
                        Toasty.error(this, "Failed to capture video", Toast.LENGTH_SHORT).show();
                    }
                }
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
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(cameraIntent);
        }
    }

    private void dispatchRecordVideoIntent() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            recordVideoLauncher.launch(videoIntent);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}