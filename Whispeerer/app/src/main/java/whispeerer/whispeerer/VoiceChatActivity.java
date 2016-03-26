package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import org.webrtc.AudioTrack;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VoiceChatActivity extends AppCompatActivity {

    String toUsername;
    AudioTrack audioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);
        Intent intent = getIntent();
        Gson gson = new Gson();
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        audioTrack = gson.fromJson(intent.getStringExtra(StartChatActivity.AUDIO_TRACK), AudioTrack.class);
    }
}
