package com.example.minitwitter.Activities.ui.notifications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.minitwitter.data.ProfileRepository;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;

import org.jetbrains.annotations.NotNull;

public class ProfileViewModel extends AndroidViewModel {

    public LiveData<ResponseUserProfile> userProfile;
    public ProfileRepository profileRepository;
    public LiveData<String> photoProfile;

    public ProfileViewModel(@NonNull @NotNull Application application) {
        super(application);
        profileRepository = new ProfileRepository();
        userProfile = profileRepository.getProfile();
        photoProfile = profileRepository.getPhotoProfile();
    }

    public void updateProfile(RequestUserProfile requestUserProfile) {
        profileRepository.updateProfile(requestUserProfile);
    }

    public void uploadPhoto(String photo) {
        profileRepository.uploadPhoto(photo);
    }

}