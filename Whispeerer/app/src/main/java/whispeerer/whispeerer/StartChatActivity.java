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
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;

public class StartChatActivity extends AppCompatActivity {

    public static final String TO_USERNAME = "whispeerer.whispeerer.TO_USERNAME";
    public static final String VIDEO_TRACK = "whispeerer.whispeerer.VIDEO_TRACK";
    public static final String AUDIO_TRACK = "whispeerer.whispeerer.VIDEO_TRACK";

    public enum ChatType {
        VOICE_CHAT, VIDEO_CHAT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        findViewById(R.id.voiceChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(ChatType.VOICE_CHAT);
            }
        });

        findViewById(R.id.videoChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(ChatType.VIDEO_CHAT);
            }
        });
    }

    public void startChat(ChatType chatType) {
        EditText toUsernameInput = (EditText) findViewById(R.id.toUsernameInput);
        String username = toUsernameInput.getText().toString();
        boolean connected;
        boolean videoEnabled = false;
        if(chatType == ChatType.VIDEO_CHAT) {
            videoEnabled = true;
        }
        connected = PeerConnectionFactory.initializeAndroidGlobals(
                getApplicationContext(),
                true,
                videoEnabled,
                true,
                null);
        if(connected) {
            PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();
            VideoSource videoSource;
            AudioSource audioSource;
            VideoTrack outgoingVideoTrack = null;
            AudioTrack outgoingAudioTrack;

            if(videoEnabled) {
                MediaConstraints videoConstraints = new MediaConstraints();
                videoSource = peerConnectionFactory.createVideoSource(getCamera(), videoConstraints);
                outgoingVideoTrack = peerConnectionFactory.createVideoTrack("OUTGOING_VIDEO", videoSource);
            }

            MediaConstraints audioConstraints = new MediaConstraints();
            audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
            outgoingAudioTrack = peerConnectionFactory.createAudioTrack("OUTGOING_AUDIO", audioSource);
            openChatActivity(username, chatType, outgoingAudioTrack, outgoingVideoTrack);
        }
    }

    public void openChatActivity(String username, ChatType chatType, AudioTrack audioTrack, VideoTrack videoTrack) {
        Intent intent;
        Gson gson = new Gson();
        if(chatType == ChatType.VIDEO_CHAT) {
            intent = new Intent(this, VideoChatActivity.class);
            intent.putExtra(VIDEO_TRACK, gson.toJson(videoTrack));
        } else {
            intent = new Intent(this, VoiceChatActivity.class);
        }
        intent.putExtra(TO_USERNAME, username);
        intent.putExtra(AUDIO_TRACK, gson.toJson(audioTrack));

        startActivity(intent);
    }

    public VideoCapturerAndroid getCamera() {
        String cameraName = null;
        if(VideoCapturerAndroid.getDeviceCount() >= 1) {
            cameraName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
            if(cameraName == null) {
                cameraName = VideoCapturerAndroid.getNameOfBackFacingDevice();
            }
        }
        return VideoCapturerAndroid.create(cameraName);
    }

}
