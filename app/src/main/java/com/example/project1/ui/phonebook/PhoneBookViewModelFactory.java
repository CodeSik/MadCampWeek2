package com.example.project1.ui.phonebook;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PhoneBookViewModelFactory implements ViewModelProvider.Factory {
    private Context context;
    public PhoneBookViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PhoneBookViewModel.class)) {
            return (T) new PhoneBookViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
