package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class StartChatActivity extends AppCompatActivity {

    public static final String TO_USERNAME = "TO_USERNAME";
    public static final String PEER_CONNECTION_FACTORY = "whispeerer.whispeerer.PEER_CONNECTION_FACTORY";
    private String username;
    private PeerConnectionFactory peerConnectionFactory;

    public enum ChatType {
        VOICE_CHAT, VIDEO_CHAT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);
        EditText toUsernameInput = (EditText) findViewById(R.id.toUsernameInput);
        final String toUsername = toUsernameInput.getText().toString();
        username = getIntent().getStringExtra(HomeActivity.USERNAME);
        findViewById(R.id.voiceChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(username, toUsername, ChatType.VOICE_CHAT);
            }
        });

        findViewById(R.id.videoChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(username, toUsername, ChatType.VIDEO_CHAT);
            }
        });
    }

    public void startChat(String username, String toUsername, ChatType chatType) {
        Intent intent;
        if (chatType == ChatType.VIDEO_CHAT) {
            intent = new Intent(this, VideoChatActivity.class);
        } else {
            intent = new Intent(this, VoiceChatActivity.class);
        }
        boolean peerConnectionFactoryInitialized = PeerConnectionFactory.initializeAndroidGlobals(
                getApplicationContext(),
                true,
                false,
                true,
                null);

        if (peerConnectionFactoryInitialized) {
            Gson gson = new Gson();
            peerConnectionFactory = new PeerConnectionFactory();
            intent.putExtra(PEER_CONNECTION_FACTORY, gson.toJson(peerConnectionFactory));
        }
        intent.putExtra(HomeActivity.USERNAME, username);
        intent.putExtra(TO_USERNAME, toUsername);
        startActivity(intent);
    }
}

