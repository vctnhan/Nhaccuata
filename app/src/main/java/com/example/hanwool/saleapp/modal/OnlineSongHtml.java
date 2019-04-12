package com.example.hanwool.saleapp.modal;

import java.io.Serializable;

public class OnlineSongHtml implements Serializable {
    String UrlMp3Html, Image, Title, Singer, View, Download;
    public OnlineSongHtml() {

    }

    public String getUrlMp3Html() {
        return UrlMp3Html;
    }

    public void setUrlMp3Html(String urlMp3Html) {
        UrlMp3Html = urlMp3Html;
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

    public String getView() {
        return View;
    }

    public void setView(String view) {
        View = view;
    }

    public String getDownload() {
        return Download;
    }

    public void setDownload(String download) {
        Download = download;
    }
}
