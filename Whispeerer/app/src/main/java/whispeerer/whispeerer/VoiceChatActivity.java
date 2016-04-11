package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VoiceChatActivity extends AppCompatActivity implements Observer, PeerConnection.Observer, SdpObserver {

    String username;
    String toUsername;
    PeerConnectionFactory peerConnectionFactory;
    Signaller signaller;
    MediaStreamFactory mediaStreamFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);
        Intent intent = getIntent();
        Gson gson = new Gson();
        username = intent.getStringExtra(HomeActivity.USERNAME);
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        peerConnectionFactory = gson.fromJson(intent.getStringExtra(StartChatActivity.PEER_CONNECTION_FACTORY), PeerConnectionFactory.class);
        mediaStreamFactory = new MediaStreamFactory(peerConnectionFactory);
        signaller = new Signaller(Signaller.BASE_URI + toUsername);
        signaller.setObserver(this);
        signaller.send("offer", username);
    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            JSONObject json = new JSONObject((String) data);
            if(json.has("callStatus")) {
                if(json.get("callStatus") == CallStatus.ACCEPTED.name()) {
                    createPeerConnection();
                } else {
                    displayRejectedCallDialog();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayRejectedCallDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Call Rejected")
                .setMessage("Call rejected by user")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public PeerConnection createPeerConnection() {
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

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {

    }

    @Override
    public void onSetSuccess() {

    }

    @Override
    public void onCreateFailure(String s) {

    }

    @Override
    public void onSetFailure(String s) {

    }
}
