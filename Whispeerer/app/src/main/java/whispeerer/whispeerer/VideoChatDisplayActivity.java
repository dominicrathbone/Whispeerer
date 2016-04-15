package whispeerer.whispeerer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

/**
 * Created by Dominic on 26/03/2016.
 */
public class VideoChatDisplayActivity extends ChatDisplayActivity {

//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_video_chat);
//
//        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
//        VideoRendererGui.setView(videoView, new Runnable() {
//            @Override
//            public void run() {
//                Log.d("info", "VIDEO RENDERING WORKING");
//            }
//        });
//        try {
//            VideoRenderer renderer = VideoRendererGui.createGui(0, 0, videoView.getWidth(), videoView.getHeight(), VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
//            videoTrack.addRenderer(renderer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
