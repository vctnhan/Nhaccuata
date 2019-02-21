package com.example.hanwool.saleapp.comporator_song;

import com.example.hanwool.saleapp.modal.Song;

import java.util.Comparator;

public class ArtistSongComparator implements Comparator<Song> {
    @Override
    public int compare(Song s1, Song s2) {
        return s1.artist.compareTo(s2.artist);
    }
}