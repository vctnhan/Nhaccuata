package com.example.hanwool.saleapp.modal;

import java.io.Serializable;

public class OnlineSongUrlMp3 implements Serializable {
    String UrlMp3, Image, Title, Singer, Views, Downloads;

    public OnlineSongUrlMp3() {

    }

    public OnlineSongUrlMp3(String urlMp3, String image, String title, String singer, String views, String downloads) {
        UrlMp3 = urlMp3;
        Image = image;
        Title = title;
        Singer = singer;
        Views = views;
        Downloads = downloads;
    }

    public String getViews() {
        return Views;
    }

    public void setViews(String views) {
        Views = views;
    }

    public String getDownloads() {
        return Downloads;
    }

    public void setDownloads(String downloads) {
        Downloads = downloads;
    }

    public String getUrlMp3() {
        return UrlMp3;
    }

    public void setUrlMp3(String urlMp3) {
        UrlMp3 = urlMp3;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSinger() {
        return Singer;
    }

    public void setSinger(String singer) {
        Singer = singer;
    }
}
