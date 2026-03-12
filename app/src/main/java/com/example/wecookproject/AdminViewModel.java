package com.example.wecookproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminViewModel extends ViewModel {
    private final MutableLiveData<User> selectedUser = new MutableLiveData<>();

    public void selectUser(User user) {
        selectedUser.setValue(user);
    }

    public LiveData<User> getSelectedUser() {
        return selectedUser;
    }
}
