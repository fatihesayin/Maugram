package com.example.maugramsocial.Model;

public class Post {
    private String postId;
    private String postImage;
    private String postAbout;
    private String postUser;

    public Post() {

    }

    public Post(String postId, String postImage, String postAbout, String postUser) {
        this.postId = postId;
        this.postImage = postImage;
        this.postAbout = postAbout;
        this.postUser = postUser;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostAbout() {
        return postAbout;
    }

    public void setPostAbout(String postAbout) {
        this.postAbout = postAbout;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }
}
