package com.example.happyapp.viewmodal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happyapp.model.User;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<User> userLiveData;
    private ProfileRepository userRepository;

    public ProfileViewModel() {
        userRepository = new ProfileRepository();
        userLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }


    public void fetchUserInfo(String userEmail) {
        userRepository.getUserInfo(userEmail, new ProfileRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                userLiveData.postValue(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure case, if needed
            }
        });
    }
}