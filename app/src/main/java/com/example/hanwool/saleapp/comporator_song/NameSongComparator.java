package com.example.hanwool.saleapp.comporator_song;

import com.example.hanwool.saleapp.modal.Song;

import java.util.Comparator;

public class NameSongComparator implements Comparator<Song> {
    @Override
    public int compare(Song s1, Song s2) {
        return s1.name.compareTo(s2.name);
    }

//        @Override
//        public int compare(Sanpham s1, Sanpham s2) {
//            return s1.Tensp.compareTo(s2.Tensp);
//        }
//    }

}
