package com.example.project2.ui.phonebook;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonData {
    private String name;
    private String number;
    private String email;
    private String photo;
    private boolean expanded;

    public JsonData() {
    }

    public JsonData(String name, String number, String email, String photo) {
        this.name = name;
        this.number = number;
        this.email = email;
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

    public ArrayList<JsonData> jsonParsing(String json) throws JSONException {
        JSONArray jarray = new JSONArray(json);
        ArrayList<JsonData> datalist = new ArrayList<>();
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
            String id = jObject.getString("id");
            String name = jObject.getString("name");
            String number = jObject.getString("number");
            JsonData data = new JsonData(name, number, id, id);
            datalist.add(data);
        }
        return datalist;
    }
}
