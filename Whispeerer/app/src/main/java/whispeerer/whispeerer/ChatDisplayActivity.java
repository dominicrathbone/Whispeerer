package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dominic on 12/04/2016.
 */
public class ChatDisplayActivity extends AppCompatActivity  implements SdpObserver, PeerConnection.Observer {

    String toUsername;
    PeerConnection peerConnection;
    Signaller signaller;
    private Gson gson;
    String username;
    MediaStreamFactory mediaStreamFactory;
    MediaStreamWrapper mediaStreamWrapper;
    MediaStream mediaStream;
    boolean outgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gson = new Gson();
        username = intent.getStringExtra(HomeActivity.USERNAME);
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
    }

    void establishChat(boolean outgoing, boolean videoEnabled) {
        peerConnection = createPeerConnection(videoEnabled);
        signaller.setPeerConnection(peerConnection, this);
        if (peerConnection == null && outgoing) {
            signaller.disconnect();
        } else if (peerConnection != null && outgoing) {
            peerConnection.createOffer(this, new MediaConstraints());
        } else if (peerConnection != null && !outgoing) {
            signaller.sendInitialChatAnswer(ChatStatus.ACCEPTED);
            Log.v(username, "ANSWER SENT");

        }
    }

    PeerConnection createPeerConnection(Boolean videoEnabled) {
        boolean peerConnectionFactoryInitialized = PeerConnectionFactory.initializeAndroidGlobals(
                getApplicationContext(),
                true,
                true,
                true);

        if (peerConnectionFactoryInitialized) {
            PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();
            mediaStreamFactory = new MediaStreamFactory(peerConnectionFactory);

            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            MediaConstraints mediaConstraints = new MediaConstraints();
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio", "true"));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo", videoEnabled.toString()));

            iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

            PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(
                    iceServers,
                    mediaConstraints,
                    this
            );
            mediaStreamWrapper = mediaStreamFactory.create(videoEnabled);
            peerConnection.addStream(mediaStreamWrapper.getMediaStream());
            Log.v(username, "PEER CONNECTION CREATED");
            return peerConnection;
        } else {
            return null;
        }
    }

    public void disconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(peerConnection != null) {
                    peerConnection.close();
                }
                if(outgoing) {
                    signaller.disconnect();
                }
                finish();
            }
        });
    }


    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        peerConnection.setLocalDescription(this, sessionDescription);
        Log.v(username, "SDP CREATED SUCCESSFULLY");
    }

    @Override
    public void onSetSuccess() {
        //if offerer,
        if(outgoing) {
            //havent yet received an answer
            if(peerConnection.getRemoteDescription() == null) {
                //send offer
                signaller.send("sdp", gson.toJson(peerConnection.getLocalDescription()));
            }
        } else {
            //else if, they are an answerer and havent created an answer yet, create answer.
            if(peerConnection.getLocalDescription() == null) {
                peerConnection.createAnswer(this, new MediaConstraints());
            } else {
                //else if they have created an answer, send answer.
                signaller.send("sdp", gson.toJson(peerConnection.getLocalDescription()));
            }
        }
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
    public void onIceConnectionReceivingChange(boolean b) {

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

     void displayAlertDialog(final Context context, final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
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
        });

    }
}

