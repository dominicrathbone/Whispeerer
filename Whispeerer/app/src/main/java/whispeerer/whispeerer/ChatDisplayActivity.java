package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
public class ChatDisplayActivity extends AppCompatActivity  implements SdpObserver, PeerConnection.Observer {

    private String toUsername;
    PeerConnection peerConnection;
    private SessionDescription sessionDescription;
    Signaller signaller;
    private Gson gson;
    String username;
    public MediaStream mediaStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gson = new Gson();
        username = intent.getStringExtra(SignInActivity.USERNAME);
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
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

        if (peerConnectionFactoryInitialized) {
            PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();
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
            Log.v(username, "PEER CONNECTION CREATED");
            return peerConnection;
        } else {
            return null;
        }
    }


    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        this.sessionDescription = sessionDescription;
        peerConnection.setLocalDescription(this, sessionDescription);
        Log.v(username, "SDP CREATED SUCCESSFULLY");
    }

    @Override
    public void onSetSuccess() {
        signaller.send("sdp", gson.toJson(sessionDescription));
        Log.v(username, "SDP SET SUCCESSFULLY");
    }

    @Override
    public void onCreateFailure(String s) {
        Log.v(username, "SDP CREATION FAILED");
    }

    @Override
    public void onSetFailure(String s) {
        Log.v(username, "SDP SETTING FAILED");
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
        Log.v(username, "ICE CANDIDATE RECEIVED");
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {

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
