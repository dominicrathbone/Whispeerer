package dev.app.mobile.drathbone.barpathtracker;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainMenuActivity extends Activity {

    static final int RESULT_LOAD_VIDEO = 1;
    static final int RESULT_RECORD_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("MyApp", "No SDCARD");
        } else {
            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"BarPathTrackerGallery");
            Log.d("MyApp", directory.getAbsolutePath().toString());
            Log.d("MyApp", Boolean.toString(directory.mkdirs()));
        }

        Button loadVideoButton = (Button) findViewById(R.id.btnLoadVideo);
        loadVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent videoGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(videoGalleryIntent, RESULT_LOAD_VIDEO);
            }
        });

        Button recordVideoButton = (Button) findViewById(R.id.btnRecordVideo);
        recordVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (recordVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(recordVideoIntent, RESULT_RECORD_VIDEO);
                }
            }
        });

        Button ViewBarPathsButton = (Button) findViewById(R.id.btnViewBarPaths);
        ViewBarPathsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_VIDEO) {
            if (resultCode == RESULT_OK) {

            }
        }
        if (requestCode == RESULT_RECORD_VIDEO) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

}