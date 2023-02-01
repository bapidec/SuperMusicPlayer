package com.example.supermusicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PlaylistActivity extends AppCompatActivity implements ShakeDetector.Listener {
    private static final int PICK_AUDIO = 1;

    private MediaPlayer musicPlayer;
    private ImageView playPauseButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private TextView currentlyPlaying;
    List<Song> songsList;
    int currentSong;

    private RecyclerView recyclerView;
    private PlaylistActivity.SongAdapter adapter;
    private boolean isPlaying = false;

    SensorManager sensorManager;
    ShakeDetector shakeDetector;

    private void addSong() {
        Intent audio = new Intent();
        audio.setType("audio/mpeg");
        audio.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(audio, "Select Audio"), PICK_AUDIO);

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            String name;

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getApplicationContext(),uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millSecond = Integer.parseInt(durationStr);

            name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);


            if(name == null) {
                ContentResolver resolver = getContentResolver();
                Cursor returnCursor =
                        resolver.query(uri, null, null, null, null);
                assert returnCursor != null;
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                name = returnCursor.getString(nameIndex);
                returnCursor.close();
            }

            if(name == null)
                name = "default song name";

            this.songsList.add(new Song(name, millSecond, uri));

            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_song:
                addSong();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        recyclerView = findViewById(R.id.songs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int playlistIndex = getIntent().getIntExtra(PlaylistListActivity.PLAYLIST_INDEX, -1);

        if(playlistIndex == -1)
            finish();

        this.songsList = PlaylistList.getInstance().getPlaylistList().get(playlistIndex).getSongs();

        this.playPauseButton = findViewById(R.id.play_pause_button);
        this.nextButton = findViewById(R.id.next_button);
        this.prevButton = findViewById(R.id.prev_button);
        this.currentlyPlaying = findViewById(R.id.currently_playing_text);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicPlayer == null)
                    return;
                else if(isPlaying) {
                    pause();
                }
                else if(!isPlaying) {
                    play();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevious();
            }
        });


        if(adapter == null) {
            adapter = new SongAdapter(this.songsList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }



        // SENSOR

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);

        int delay = SensorManager.SENSOR_DELAY_NORMAL;
        shakeDetector.setSensitivity(ShakeDetector.SENSITIVITY_LIGHT);
        shakeDetector.start(sensorManager, delay);


    }

    @Override public void hearShake() {
        if(songsList.size()==0)
            return;
        Random rand = new Random();
        int randomSongIndex = rand.nextInt(songsList.size());
        currentSong = randomSongIndex;
        start(songsList.get(currentSong));
    }

    private void playNext() {
        if(musicPlayer == null || currentSong+1 == songsList.size())
            return;
        currentSong++;
        start(songsList.get(currentSong));

    }
    private void playPrevious() {
        if(musicPlayer == null || currentSong-1 < 0)
            return;
        currentSong--;
        start(songsList.get(currentSong));

    }

    private void start(Song song) {
        if(musicPlayer != null) {
            musicPlayer.stop();
            isPlaying = false;
        }
        musicPlayer = MediaPlayer.create(PlaylistActivity.this, song.getUri());
        currentlyPlaying.setText(song.getName());
        play();
    }

    private void play() {
        if(musicPlayer == null)
            return;
        musicPlayer.start();
        isPlaying = true;
        playPauseButton.setImageResource(R.drawable.ic_pause);
        playPauseButton.invalidate();
    }

    private void pause() {
        musicPlayer.pause();
        isPlaying = false;
        playPauseButton.setImageResource(R.drawable.ic_play);
        playPauseButton.invalidate();
    }

    private class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView songName;
        private TextView songDuration;
        private Song song;
        public SongHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.song_list_item, parent, false));

            itemView.setOnClickListener(this);

            songName = itemView.findViewById(R.id.song_title);
            songDuration = itemView.findViewById(R.id.duration_text);

        }

        public void bind(Song song) {
            this.song = song;
            songName.setText(song.getName());
            songDuration.setText(song.getDuration());
        }


        @Override
        public void onClick(View view) {
            currentSong = getAdapterPosition();
            start(song);
        }
    }

    private class SongAdapter extends RecyclerView.Adapter<PlaylistActivity.SongHolder> {

        private List<Song> songList;

        public SongAdapter(List<Song> songList) {
            this.songList = songList;
        }

        @NonNull
        @Override
        public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new SongHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistActivity.SongHolder holder, int position) {
            Song song = songList.get(position);
            holder.bind(song);
        }

        @Override
        public int getItemCount() {
            return songList.size();
        }
    }


    /*@Override
    protected void onStop() {
        super.onStop();
        if(musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }
}