package com.example.project2.ui.phonebook;

import android.net.Uri;

public class JsonData {
    private String name;
    private String number;
    private String email;
    private Uri photo;
    private boolean expanded;

    public JsonData() { }

    public JsonData(String name, String number, String email, Uri photo) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.photo = photo;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
