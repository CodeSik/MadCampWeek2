package com.example.project2.ui.phonebook;

public class JsonData {
    private String id;
    private String name;
    private String number;
    private String photo;
    private boolean expanded;



    public JsonData(String id, String name, String number, String photo) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
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

    public boolean getExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    }

