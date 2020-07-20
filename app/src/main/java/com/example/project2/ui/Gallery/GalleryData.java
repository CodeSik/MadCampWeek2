package com.example.project2.ui.Gallery;

public class GalleryData {

    private String id;
    private String photoid;
    private String name;
    private String image;
    private String profile;
    private String contents;
    private int like;



    public GalleryData(String id, String photoid, String image, String contents, int like) {
        this.id = id;
        this.photoid = photoid;
        this.image = image;
        this.contents = contents;
        this.like = like;
        this.name = ???;
        this.profile = ???;
    }

    public String getImage() { return image; }

    public String getProfile() { return profile; }

    public String getContents() { return contents; }

    public int getLike() { return like; }

    public String getName() { return name;}


//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getNumber() {
//        return number;
//    }
//
//    public void setNumber(String number) {
//        this.number = number;
//    }
//
//    public boolean getExpanded() {
//        return this.expanded;
//    }
//
//    public void setExpanded(boolean expanded) {
//        this.expanded = expanded;
//    }

}
