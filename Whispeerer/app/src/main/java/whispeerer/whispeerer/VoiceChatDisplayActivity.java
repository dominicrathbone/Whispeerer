package whispeerer.whispeerer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import java.io.IOException;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VoiceChatDisplayActivity extends ChatDisplayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);
        Intent intent = getIntent();
        String toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);

        Resources res = getResources();
        String text = String.format(res.getString(R.string.voice_chat_header_text), toUsername);
        TextView welcomeText = (TextView) findViewById(R.id.voiceChatHeaderText);
        welcomeText.setText(text);

        if(getCallingActivity().getClassName().equals(IncomingChatActivity.class.getCanonicalName())) {
            Log.v(username, "INCOMING SIGNALLER ADDED");
            signaller = Signaller.incomingChatSignaller;
            establishChat();
        } else {
            Log.v(username, "OUTGOING SIGNALLER ADDED");
            signaller = Signaller.outgoingChatSignaller;
            establishChat(true);
        }
    }

    public void establishChat(boolean outgoing) {
        super.establishChat();
        if (peerConnection != null && outgoing) {
            peerConnection.createOffer(this, new MediaConstraints());
        } else if (peerConnection != null) {
            signaller.disconnect();
            displayAlertDialog("Call Error", "Failed to establish connection");
        }
    }

    private void displayAlertDialog(String title, String message) {
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
    public void onAddStream(MediaStream mediaStream) {
        if(hasRecordAudioPermission()) {
            Log.v(username, "STREAM ADDED");
            this.mediaStream = mediaStream;
            requestPermissions();
        }
    }

    private void requestPermissions() {
        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, requiredPermission)) {
            displayAlertDialog("Audio Permissions", "Remember, to communicate you must enable audio permissions");
        }

        ActivityCompat.requestPermissions(this, new String[]{requiredPermission}, 2);
    }

    private boolean hasRecordAudioPermission(){
        Boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);

        Log.v(username + " has audio permission? ", hasPermission.toString());
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(hasRecordAudioPermission()) {
            AudioTrack audioTrack = mediaStream.audioTracks.getFirst();
            audioTrack.setEnabled(true);
        }
        else {
            displayAlertDialog("Audio Permissions", "Remember, to communicate you must enable audio permissions");
            finish();
        }
    }
}
