package com.example.maugramsocial.Model;

public class User {
    private String id;
    private String userName;
    private String fullName;
    private String photourl;
    private String bio;

    public User(String id, String userName, String fullName, String photourl, String bio) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.photourl = photourl;
        this.bio = bio;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoURL() {
        return photourl;
    }

    public void setPhotoURL(String photoURL) {
        this.photourl = photoURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
