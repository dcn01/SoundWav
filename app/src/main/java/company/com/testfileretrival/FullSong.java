package company.com.testfileretrival;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class FullSong extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_full_song);
    }

}
