package whispeerer.whispeerer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;

/**
 * Created by Dominic on 11/04/2016.
 */
public class IncomingChatActivity extends ChatActivity implements PeerConnection.Observer, SdpObserver {

    private String fromUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromUsername = intent.getStringExtra(HomeActivity.FROM_USERNAME);
        setContentView(R.layout.activity_incoming_call);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.from_username_text), fromUsername);
        TextView fromUsernameText = (TextView) findViewById(R.id.fromUsernameText);
        fromUsernameText.setText(text);

        findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                establishChat();
            }
        });

        findViewById(R.id.declineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setContentView(R.layout.activity_incoming_call);

    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        if(peerConnection.signalingState() == PeerConnection.SignalingState.STABLE) {
            openChatActivity(fromUsername);
        }
    }
}
