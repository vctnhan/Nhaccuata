package com.example.hanwool.saleapp.modal;

import java.io.Serializable;

public class Song implements Serializable {
    public String name;
    public String artist;
    public int duration;
    public String location;
//    public void Song(){
//
//    }

    public Song(String name, String artist, int duration, String location) {
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
