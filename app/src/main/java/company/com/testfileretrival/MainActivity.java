package company.com.testfileretrival;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // The ListView holding all of the song names
    private static final int MY_PERMISSION_REQUEST = 1; // Int necessary for retriving data from storage

    public static int currentPos = 0; // The current song playing is located at this point in the ArrayLists below and in the ListView

    public ArrayList<String> songInfoList = new ArrayList<>(); // The name, artist, and album of a song are stored as a string this ArrayList contains those strings
    public static ArrayList<String> musicFiles = new ArrayList<>(); // Filepaths to songs

    public Map<String, String> songToFile = new TreeMap<>(); // Map connecting songInformation to its file path

    public static MediaPlayer mediaPlayer = new MediaPlayer(); // Universal MediaPlayer for application

    public static Button play; // Play Button

    public ArrayList<SongInfo> allSongs = new ArrayList<>();

    private SongsAdapter songsAdapter;

    public ArrayList<String> fullSongInfo = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hides the Title Bar from being seen in Application
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        // Ties xml layout to class
        setContentView(R.layout.activity_main);

        // Find all the buttons in the associated view
        play = (Button) findViewById(R.id.play);

        // Find the list view in the associated view
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        songsAdapter = new SongsAdapter(allSongs, this);

        //If everything is set up correctly and the application has access to storage it will begin fetching songs
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        else {
            prepareToPlay();

            // To improve user experience a default song is chosen in-case the user hits play without indicating a song
            try {

                // Sets song to first one in list
                mediaPlayer.setDataSource(musicFiles.get(0));
                mediaPlayer.prepare();

                // Listener to pick up when the song is done playing so it can start playing a new one
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        // If there is another song next play that, else go back up to the top
                        try {
                            mediaPlayer.reset();
                            if (currentPos + 1 < musicFiles.size()) {
                                mediaPlayer.setDataSource(musicFiles.get(currentPos + 1));
                                currentPos += 1;
                            }
                            else {
                                mediaPlayer.setDataSource(musicFiles.get(0));
                                currentPos = 0;
                            }
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
                currentPos = 0;
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void play(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicFiles.get(position));
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentPos = position;
            play.setText("Pause");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void pause() {
            mediaPlayer.pause();
            play.setText("Play");
    }

    public static boolean isPlaying() {
        return (mediaPlayer.isPlaying());
    }


    //return ((Math.max(arg1,arg2) == arg1) ? firstSeq : secondSeq;

    public void onClick(View v) {
        // Perform action on click
        switch (v.getId()) {
            case R.id.play:

                // If a song is playing pause it else play whatever song is loaded in the MediaPlayer
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        play.setText("Play");
                    }
                    else {
                        mediaPlayer.start();
                        play.setText("Pause");
                    }
                }
                break;
            case R.id.advance:

                // Advances song to next in list
                mediaPlayer.reset();
                try {
                    //ToDo: Implement Smart Shuffle
                    /*

                    // Create an array where every index is a random integer between 0 and size exclusive, the array is the size of musicFiles
                    // Make sure there are no doubles
                    // Find the index the current song is at in the array and set the new song as whatever the next index is
                    //ie shuffle<2,4,6,3,5,0,1> if at song 4 go to song 6 and so forth

                    */
                    if (currentPos + 1 < musicFiles.size()) {
                        mediaPlayer.setDataSource(musicFiles.get(currentPos + 1));
                        currentPos += 1;
                    }
                    else {
                        mediaPlayer.setDataSource(musicFiles.get(0));
                        currentPos = 0;
                    }
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                play.setText("Pause");
                mediaPlayer.start();
                break;
            case R.id.rewind:

                // if a song is more than 1/9 in go back to the beginning else go back to the previous song
                if (mediaPlayer.getCurrentPosition() > (mediaPlayer.getDuration()/9)) {
                    mediaPlayer.seekTo(0);
                }
                else {
                    mediaPlayer.reset();
                    try {
                        if (currentPos - 1 >= 0) {
                            mediaPlayer.setDataSource(musicFiles.get(currentPos - 1));
                            currentPos -= 1;
                        }
                        else {
                            mediaPlayer.setDataSource(musicFiles.get(musicFiles.size()-1));
                            currentPos = (musicFiles.size()-1);
                        }
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mediaPlayer.start();
                break;
        }
    }

    public void addSongsToArray() {
        Collections.sort(fullSongInfo);
        for (int x = 0; x < musicFiles.size(); x++) {
            String[] audioInfo = fullSongInfo.get(x).split("###");
            SongInfo songInfo = new SongInfo();
            songInfo.setTitle(audioInfo[0]);
            songInfo.setArtist(audioInfo[2]);
            songInfo.setAlbum(audioInfo[1]);
            allSongs.add(songInfo);
            songsAdapter.notifyDataSetChanged();
        }
    }

    public void prepareToPlay() {
        getMusic();
        for (String file : songToFile.values()) {
            musicFiles.add(file);
        }

        addSongsToArray();

        for (String song : songToFile.keySet()) {
            songInfoList.add(song);
        }
        recyclerView.setAdapter(songsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle  = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist  = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int filePath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentAlbum = songCursor.getString(songAlbum);
                String fileTo = songCursor.getString(filePath);
                if (!currentTitle.contains("Facebook") && !currentTitle.contains("Hang")) {
                    String full = currentTitle + "###" + currentAlbum + "###" + currentArtist;
                    fullSongInfo.add(full);
                    songToFile.put((currentTitle + "\n" + currentArtist + " - " + currentAlbum), fileTo);
                }
            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        prepareToPlay();
                    }
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
