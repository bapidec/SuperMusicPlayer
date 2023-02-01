package com.example.supermusicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private List<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String[] getSongTitles() {

        String[] titles = new String[this.songs.size()];

        for(int i = 0; i < this.songs.size(); i++) {
            titles[i] = this.songs.get(i).getName();
        }

        return titles;
    }
}
