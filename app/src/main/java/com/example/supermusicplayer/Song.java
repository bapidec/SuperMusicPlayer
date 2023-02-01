package com.example.supermusicplayer;

import android.net.Uri;

public class Song {

    private String name;
    private int duration;

    private Uri uri;

    public Uri getUri() {
        return uri;
    }

    public Song(String name, int duration, Uri uri) {
        this.name = name;
        this.duration = duration;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {

        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;

        String secondsExpanded = String.valueOf(seconds);
        if(seconds/10 == 0)
            secondsExpanded = "0" + seconds;

        String out = minutes+":"+secondsExpanded;

        return out;
    }
}
