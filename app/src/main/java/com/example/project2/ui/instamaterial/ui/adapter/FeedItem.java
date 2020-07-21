package com.example.project2.ui.instamaterial.ui.adapter;


public class FeedItem {

    private String id;
    private String photoid;
    private String name;
    private String image;
    private String contents;
    private int like;
    public boolean isLiked;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public FeedItem(String id, String photoid, String name, String image, String contents, int like, boolean isLiked) {
        this.id = id;
        this.photoid = photoid;
        this.name = name;
        this.image = image;
        this.contents = contents;
        this.like = like;
        this.isLiked = isLiked;
    }
}
