package whispeerer.whispeerer;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dominic on 12/04/2016.
 */
public class OutgoingChatActivity extends ChatActivity implements Observer {

    String toUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String username = intent.getStringExtra(HomeActivity.USERNAME);
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        signaller.setObserver(this);

        JSONObject json = new JSONObject();
        try {
            json.put("from", username);
            json.put("chatType", chatType);
            signaller.send("offer", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_outgoing_call);

    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            JSONObject json = new JSONObject((String) data);
            if (json.has("callStatus")) {
                if (json.get("callStatus") == ChatStatus.ACCEPTED.name()) {
                    establishChat();
                } else {
                    displayErrorDialog("Call Rejected", toUsername + "didn't accept your voice chat");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void establishChat() {
        super.establishChat();
        if (peerConnection != null) {
            peerConnection.createOffer(this, new MediaConstraints());
        } else {
            displayErrorDialog("Call Error", "Failed to establish peer connection");
        }
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        if(peerConnection.signalingState() == PeerConnection.SignalingState.STABLE) {
            openChatActivity(toUsername);
        }
    }
}
