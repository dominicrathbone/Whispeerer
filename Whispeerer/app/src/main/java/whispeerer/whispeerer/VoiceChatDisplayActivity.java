package whispeerer.whispeerer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VoiceChatDisplayActivity extends ChatDisplayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);

        if(getCallingActivity().getClassName().equals(IncomingChatActivity.class.getCanonicalName())) {
            Log.v(username, "INCOMING SIGNALLER ADDED");
            signaller = Signaller.incomingChatSignaller;
            establishChat(false, true);
        } else {
            outgoing = true;
            Log.v(username, "OUTGOING SIGNALLER ADDED");
            signaller = Signaller.outgoingChatSignaller;
            establishChat(true, true);
        }

        Resources res = getResources();
        String text = String.format(res.getString(R.string.username), toUsername);
        TextView voiceChatHeaderText = (TextView) findViewById(R.id.voiceChatHeaderText);
        voiceChatHeaderText.setText(text);

        findViewById(R.id.disconnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.disconnectButton).setEnabled(false);
                disconnect();
            }
        });
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        if(hasPermissions()) {
            Log.v(username, "STREAM ADDED");
            this.mediaStream = mediaStream;
            playStreams();
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, requiredPermission)) {
            displayAlertDialog(this, "Audio Permissions", "Remember, to communicate you must enable audio permissions");
        }

        ActivityCompat.requestPermissions(this, new String[]{requiredPermission}, 2);
    }

    private boolean hasPermissions(){
        Boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
        Log.v(username + " has audio permission? ", hasPermission.toString());
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(hasPermissions() && mediaStream.audioTracks.size() > 0) {
            playStreams();
        }
        else {
                displayAlertDialog(this, "Audio Permissions", "Remember, to communicate you must enable audio permissions");
                finish();
        }
    }

    private void playStreams() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AudioTrack audioTrack = mediaStream.audioTracks.getFirst();
                audioTrack.setEnabled(true);
            }
        });
    }
}
