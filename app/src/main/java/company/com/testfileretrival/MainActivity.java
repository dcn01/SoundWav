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

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> songInfoList = new ArrayList<>();

    ArrayList<String> musicFiles = new ArrayList<>();

    ListView listView;

    Button play;

    Button rewind;

    Button advance;

    ArrayAdapter<String> adapter;

    MediaPlayer mediaPlayer = new MediaPlayer();

    Map<String, String> songToFile = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        play = (Button) findViewById(R.id.play);
        rewind = (Button) findViewById(R.id.rewind);
        advance = (Button) findViewById(R.id.advance);

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
        }
    }

    public void onClick(View v) {
        // Perform action on click
        switch (v.getId()) {
            case R.id.play:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play.setText("Play");
                } else {
                    mediaPlayer.start();
                    play.setText(" | | ");
                }
                break;
            case R.id.advance:
                //Pick a new index
                break;
            case R.id.rewind:
                mediaPlayer.seekTo(0);
                break;
        }
    }

    public void prepareToPlay() {
        listView = (ListView) findViewById(R.id.listView);
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
                mediaPlayer.start();
                play.setText(" | | ");
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
