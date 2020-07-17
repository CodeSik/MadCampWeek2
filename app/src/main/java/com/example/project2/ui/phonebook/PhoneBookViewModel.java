package com.example.project2.ui.phonebook;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class PhoneBookViewModel extends ViewModel {
    private MutableLiveData<ArrayList<JsonData>> contactList;
    private ContactRepository repository;

    public PhoneBookViewModel(Context context) {
        repository = new ContactRepository(context);
        contactList = new MutableLiveData<>();
    }

    public LiveData<ArrayList<JsonData>> getContacts() {
        return contactList;
    }

    public void initializeContacts() {
        contactList.setValue(repository.getContactList());
    }
}