package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        signaller = new Signaller(toUsername, false);
        signaller.setObserver(this);
        Signaller.incomingChatSignaller.setObserver(this);

        try {
            JSONObject json = new JSONObject();
            json.put("type", "offer");
            json.put("from", username);
            json.put("chatType", chatType);
            signaller.send("offer", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            displayErrorDialog("Call Error", "Couldn't send offer to that user.");
        }
        setContentView(R.layout.activity_outgoing_call);

        Resources res = getResources();
        String text = String.format(res.getString(R.string.to_username_calling_text), toUsername);
        TextView toUsernameCallingText = (TextView) findViewById(R.id.toUsernameCallingText);
        toUsernameCallingText.setText(text);

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaller.disconnect();
                finish();
            }
        });
    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            JSONObject json = new JSONObject((String) data);
            if (json.getString("type").equals("answer")) {
                if (json.get("chatStatus").equals(ChatStatus.ACCEPTED.name())) {
                    openChatDisplayActivity(toUsername);
                } else if (json.get("chatStatus").equals(ChatStatus.DECLINED.name())) {
                    signaller.disconnect();
                    displayErrorDialog("Call Failed", toUsername + " is not available");
                }
            } else if(json.getString("type").equals("offer")) {
                Intent intent = new Intent(this, IncomingChatActivity.class);
                intent.putExtra(HomeActivity.CHAT_TYPE, json.getString("chatType"));
                intent.putExtra(HomeActivity.FROM_USERNAME, json.getString("from"));
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
