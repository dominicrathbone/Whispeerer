package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VoiceChatActivity extends AppCompatActivity {

    String toUsername;
    AudioTrack audioTrack;
    PeerConnectionFactory peerConnectionFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);
        Intent intent = getIntent();
        Gson gson = new Gson();
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        audioTrack = gson.fromJson(intent.getStringExtra(StartChatActivity.AUDIO_TRACK), AudioTrack.class);
        peerConnectionFactory = gson.fromJson(intent.getStringExtra(StartChatActivity.PEER_CONNECTION_FACTORY), PeerConnectionFactory.class);

        PeerConnection.Observer peerConnectionObserver = new PeerConnectionObserver();
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        MediaConstraints mediaConstraints = new MediaConstraints();

        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                mediaConstraints,
                peerConnectionObserver
        );
    }
}
