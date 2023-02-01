package com.example.supermusicplayer;

import java.util.ArrayList;
import java.util.List;

public class PlaylistList {

    private static final PlaylistList instance = new PlaylistList();

    private List<Playlist> playlistList = new ArrayList<>();

    private PlaylistList(){};

    public static PlaylistList getInstance() {
        return instance;
    }

    public List<Playlist> getPlaylistList() {
        return playlistList;
    }

}
