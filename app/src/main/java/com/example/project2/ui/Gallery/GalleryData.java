package com.example.project2.ui.Gallery;

public class GalleryData {

    private String id;
    private String photoid;
    private String image;
    private String contents;
    private int like;



    public GalleryData(String id, String photoid, String photo, String contents, int like) {
        this.id = id;
        this.photoid = photoid;
        this.image = image;
        this.contents = contents;
        this.like = like;
    }

    public String getImage() { return image; }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContents() { return contents; }

    public int getLike() { return like; }
//    public String getName() {
//        return name;
//    }
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
