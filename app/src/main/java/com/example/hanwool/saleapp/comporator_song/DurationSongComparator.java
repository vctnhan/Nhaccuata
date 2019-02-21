package com.example.hanwool.saleapp.comporator_song;

import com.example.hanwool.saleapp.modal.Song;

import java.util.Comparator;

public class DurationSongComparator implements Comparator<Song> {

        @Override
        public int compare(Song s1, Song s2) {
            if (s1.duration == s2.duration)
                return 0;
            else if (s1.duration > s2.duration)
                return 1;
            else
                return -1;
        }
}
