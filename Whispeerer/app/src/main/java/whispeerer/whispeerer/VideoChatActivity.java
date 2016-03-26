package whispeerer.whispeerer;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import org.webrtc.AudioTrack;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoTrack;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VideoChatActivity extends AppCompatActivity {

    String toUsername;
    AudioTrack audioTrack;
    VideoTrack videoTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);
        Intent intent = getIntent();
        Gson gson = new Gson();
        toUsername = intent.getStringExtra(StartChatActivity.TO_USERNAME);
        audioTrack = gson.fromJson(intent.getStringExtra(StartChatActivity.AUDIO_TRACK), AudioTrack.class);
        videoTrack = gson.fromJson(intent.getStringExtra(StartChatActivity.AUDIO_TRACK), VideoTrack.class);

        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                Log.d("info", "VIDEO RENDERING WORKING");
            }
        });
        try {
            VideoRenderer renderer = VideoRendererGui.createGui(0, 0, videoView.getWidth(), videoView.getHeight(), VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
            videoTrack.addRenderer(renderer);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
