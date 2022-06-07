package com.example.maugramsocial.Model;

public class Club {
    private String clubName;
    private int clubInfo;
    private int clubImage;

    // Constructor
    public Club(String clubName, int clubInfo, int clubImage) {
        this.clubName = clubName;
        this.clubInfo = clubInfo;
        this.clubImage = clubImage;
    }

    public Club() {
        //Default Constructor
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getClubInfo() {
        return clubInfo;
    }

    public void setClubInfo(int clubInfo) {
        this.clubInfo = clubInfo;
    }

    public int getClubImage() {
        return clubImage;
    }

    public void setClubImage(int clubImage) {
        this.clubImage = clubImage;
    }
}
