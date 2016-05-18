package whispeerer.whispeerer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VideoChatDisplayActivity extends ChatDisplayActivity {

    MyGLSurfaceView videoView;
    VideoRenderer.Callbacks render;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point dimensions = new Point();
            display.getSize(dimensions);
            videoView = new MyGLSurfaceView(this, dimensions);
            VideoRendererGui.setView(videoView, new Runnable() {
                @Override
                public void run() {
                }
            });
            render = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
            RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ABOVE, R.id.disconnectButton);
            mainLayout.addView(videoView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources res = getResources();
        String text = String.format(res.getString(R.string.username), toUsername);
        TextView videoChatHeaderText = (TextView) findViewById(R.id.videoChatHeaderText);
        videoChatHeaderText.setText(text);
        videoChatHeaderText.bringToFront();
        videoEnabled = true;

        if(getCallingActivity().getClassName().equals(IncomingChatActivity.class.getCanonicalName())) {
            Log.v(username, "INCOMING SIGNALLER ADDED");
            signaller = Signaller.incomingChatSignaller;
            establishChat(false);
        } else {
            outgoing = true;
            Log.v(username, "OUTGOING SIGNALLER ADDED");
            signaller = Signaller.outgoingChatSignaller;
            establishChat(true);
        }

        findViewById(R.id.disconnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaStream != null && peerConnection != null) {
                    peerConnection.removeStream(mediaStream);
                    mediaStream.dispose();
                    peerConnection.close();
                    peerConnection.dispose();
                    mediaStreamFactory.disposeVideoCapturer();
                }
                if(outgoing) {
                    signaller.disconnect();
                }
                finish();
            }
        });
    }

    public void establishChat(boolean outgoing) {
        super.establishChat();
        if (peerConnection != null && outgoing) {
            MediaConstraints mediaConstraints = new MediaConstraints();
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio", "true"));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo", "true"));
            peerConnection.createOffer(this, mediaConstraints);
        } else if (peerConnection == null && outgoing) {
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
        if(hasPermissions()) {
            Log.v(username, "STREAM ADDED");
            this.mediaStream = mediaStream;
            playStreams();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        videoView.updateDisplaySize(displaySize);
        super.onConfigurationChanged(newConfig);
    }

    private void requestPermissions() {
        String requiredAudioPermission = Manifest.permission.RECORD_AUDIO;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, requiredAudioPermission)) {
            displayAlertDialog("Audio Permissions", "Remember, to communicate you must enable audio permissions");
        }

        ActivityCompat.requestPermissions(this, new String[]{requiredAudioPermission}, 2);
    }

    private boolean hasPermissions(){
        Boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
        Log.v(username + " has audio permission? ", hasPermission.toString());
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(hasPermissions()) {
            playStreams();
        }
        else {
            displayAlertDialog("Audio Permissions", "Remember, to communicate you must enable audio permissions");
            finish();
        }
    }

    private void playStreams() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaStream.videoTracks.size() > 0) {
                    mediaStream.videoTracks.getFirst().addRenderer(new VideoRenderer(render));
                } else if(mediaStream.audioTracks.size() > 0) {
                    AudioTrack audioTrack = mediaStream.audioTracks.getFirst();
                    audioTrack.setEnabled(true);
                }
            }
        });

    }
}
