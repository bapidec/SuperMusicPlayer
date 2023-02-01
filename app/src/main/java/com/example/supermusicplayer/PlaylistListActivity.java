package com.example.supermusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class PlaylistListActivity extends AppCompatActivity {
    public static final String PLAYLIST_INDEX = "playlistIndex";

    List<Playlist> playlistList = PlaylistList.getInstance().getPlaylistList();

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;

    public void addPlaylist() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add playlist");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_name = input.getText().toString();
                playlistList.add(new Playlist(new_name));
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlists_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_playlist:
                addPlaylist();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists_list_activity);

        recyclerView = findViewById(R.id.playlists_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if(adapter == null) {
            adapter = new PlaylistAdapter(playlistList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        this.adapter.notifyDataSetChanged();
        super.onResume();
    }

    private class PlaylistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView playlistName;
        private TextView songsNumber;
        private Playlist playlist;

        public PlaylistHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.playlists_list_item, parent, false));

            itemView.setOnClickListener(this);

            playlistName = itemView.findViewById(R.id.playlist_name_view);
            songsNumber = itemView.findViewById(R.id.songs_number_view);

        }

        public void bind(Playlist playlist) {
            this.playlist = playlist;
            playlistName.setText(playlist.getName());
            songsNumber.setText(getString(R.string.songsNumberText, playlist.getSongs().size()));
        }


        @Override
        public void onClick(View view) {
            Class activity;
            activity = PlaylistActivity.class;
            Intent intent = new Intent(PlaylistListActivity.this, activity);
            intent.putExtra(PLAYLIST_INDEX, getAdapterPosition());
            startActivity(intent);
        }
    }

    private class PlaylistAdapter extends RecyclerView.Adapter<PlaylistHolder> {

        private List<Playlist> playlistList;

        public PlaylistAdapter(List<Playlist> playlistList) {
            this.playlistList = playlistList;
        }

        @NonNull
        @Override
        public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new PlaylistHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
            Playlist playlist = playlistList.get(position);
            holder.bind(playlist);
        }

        @Override
        public int getItemCount() {
            return playlistList.size();
        }
    }


}