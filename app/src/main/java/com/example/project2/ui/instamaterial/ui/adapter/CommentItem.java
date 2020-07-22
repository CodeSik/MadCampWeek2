package com.example.project2.ui.instamaterial.ui.adapter;

public class CommentItem {
    private String id;
    private String photoid;
    private String name;
    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CommentItem(String id, String photoid, String name, String comment) {
        this.id = id;
        this.photoid = photoid;
        this.name = name;
        this.comment = comment;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}