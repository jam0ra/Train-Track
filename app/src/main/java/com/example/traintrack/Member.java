package com.example.traintrack;

public class Member {

    //member variable

    private String name;
    private String Videourl;
    private String search;      //this is for search view, in-case we decide to add it later!

    public Member() {
     //Empty constructor!
    }

    //getter and setter method!

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideourl() {
        return Videourl;
    }

    public void setVideourl(String videourl) {
        Videourl = videourl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
