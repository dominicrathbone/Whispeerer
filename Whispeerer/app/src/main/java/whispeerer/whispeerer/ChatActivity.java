package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 12/04/2016.
 */
public abstract class ChatActivity extends AppCompatActivity implements SdpObserver, PeerConnection.Observer {

    Intent intent;
    SessionDescription sessionDescription;
    PeerConnection peerConnection;
    String chatType;
    Signaller signaller;
    Gson gson = new Gson();
    public static final String PEER_CONNECTION = "whispeerer.whispeerer.TO_USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        chatType = intent.getStringExtra(HomeActivity.CHAT_TYPE);
        signaller = gson.fromJson(intent.getStringExtra(HomeActivity.SIGNALLER), Signaller.class);
    }

    void establishChat() {
        peerConnection = createPeerConnection();
        signaller.setPeerConnection(peerConnection, this);
    }

    PeerConnection createPeerConnection() {
        boolean peerConnectionFactoryInitialized = PeerConnectionFactory.initializeAndroidGlobals(
                getApplicationContext(),
                true,
                true,
                true,
                null);

        PeerConnectionFactory peerConnectionFactory;

        if (peerConnectionFactoryInitialized) {
            peerConnectionFactory = new PeerConnectionFactory();
            MediaStreamFactory mediaStreamFactory = new MediaStreamFactory(peerConnectionFactory);

            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            MediaConstraints mediaConstraints = new MediaConstraints();
            iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

            PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(
                    iceServers,
                    mediaConstraints,
                    this
            );
            peerConnection.addStream(mediaStreamFactory.getMediaStream(false));
            return peerConnection;
        } else {
            return null;
        }
    }

    void openChatActivity(String username) {
        Intent intent = null;
        if(chatType == ChatType.VOICE_CHAT.name()) {
            intent = new Intent(this, VoiceChatDisplayActivity.class);
        } else if(chatType == ChatType.VOICE_CHAT.name()) {
            intent = new Intent(this, VideoChatDisplayActivity.class);
        }
        intent.putExtra(StartChatActivity.TO_USERNAME, username);
        intent.putExtra(PEER_CONNECTION, gson.toJson(peerConnection));
        startActivity(intent);
}

    void displayErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        this.sessionDescription = sessionDescription;
        peerConnection.setLocalDescription(this, sessionDescription);
    }

    @Override
    public void onSetSuccess() {
        signaller.send("sdp", gson.toJson(sessionDescription));
    }

    @Override
    public void onCreateFailure(String s) {
        displayErrorDialog("Call Error", "Failed to create peer connection sdp" + s);
    }

    @Override
    public void onSetFailure(String s) {
        displayErrorDialog("Call Error", "Failed to set peer connection sdp" + s);
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {

    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        signaller.send("candidate", gson.toJson(iceCandidate));
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {

    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {

    }

    @Override
    public void onRenegotiationNeeded() {

    }

}
