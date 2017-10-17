package company.com.testfileretrival;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private ListView listView; // The ListView holding all of the song names
    private Button rewind; // Button that brings song back to the begining or goes to previous song
    private Button advance; // Button that advances to the next track
    private static final int MY_PERMISSION_REQUEST = 1; // Int necessary for retriving data from storage

    public int currentPos = 0; // The current song playing is located at this point in the ArrayLists below and in the ListView

    public ArrayList<String> songInfoList = new ArrayList<>(); // The name, artist, and album of a song are stored as a string this ArrayList contains those strings
    public ArrayList<String> musicFiles = new ArrayList<>(); // Filepaths to songs

    public Map<String, String> songToFile = new TreeMap<>(); // Map connecting songInformation to its file path

    public ArrayAdapter<String> adapter; // ArrayAdapter to place values from songInfoList into ListView

    public MediaPlayer mediaPlayer = new MediaPlayer(); // Universal MediaPlayer for application

    public Button play; // Play Button

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
        rewind = (Button) findViewById(R.id.rewind);
        advance = (Button) findViewById(R.id.advance);

        // Find the list view in the associated view
        listView = (ListView) findViewById(R.id.listView);

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

    public void prepareToPlay() {
        getMusic();
        for (String file : songToFile.values()) {
            musicFiles.add(file);
        }
        for (String song : songToFile.keySet()) {
            songInfoList.add(song);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songInfoList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicFiles.get(position));
                    mediaPlayer.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Exception of type : " + e.toString());
                    e.printStackTrace();
                }
                currentPos = position;
                mediaPlayer.start();
                play.setText("Pause");
            }
        });
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
