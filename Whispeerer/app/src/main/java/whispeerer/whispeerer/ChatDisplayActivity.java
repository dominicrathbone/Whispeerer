package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import org.webrtc.PeerConnection;

/**
 * Created by Dominic on 12/04/2016.
 */
public class ChatDisplayActivity extends AppCompatActivity {

    String toUsername;
    PeerConnection peerConnection;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        Intent intent = getIntent();
        gson = new Gson();
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        peerConnection = gson.fromJson(intent.getStringExtra(ChatActivity.PEER_CONNECTION), PeerConnection.class);
    }
}

