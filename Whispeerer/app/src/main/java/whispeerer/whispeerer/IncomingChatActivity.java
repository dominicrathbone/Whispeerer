package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dominic on 11/04/2016.
 */
public class IncomingChatActivity extends ChatActivity {

    private String fromUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromUsername = intent.getStringExtra(HomeActivity.FROM_USERNAME);
        signaller = Signaller.incomingChatSignaller;

        setContentView(R.layout.activity_incoming_call);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.from_username_calling_text), fromUsername);
        TextView fromUsernameText = (TextView) findViewById(R.id.fromUsernameCallingText);
        fromUsernameText.setText(text);

        findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer(ChatStatus.ACCEPTED);
                openChatDisplayActivity(fromUsername);
                finish();
            }
        });

        findViewById(R.id.declineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer(ChatStatus.DECLINED);
                finish();
            }
        });

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                answer(ChatStatus.DECLINED);
                finish();
            }
        }, 170500);

    }

    public void answer(ChatStatus status) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "answer");
            json.put("from", username);
            json.put("chatStatus", status.name());
            signaller.send("answer", json.toString());
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
