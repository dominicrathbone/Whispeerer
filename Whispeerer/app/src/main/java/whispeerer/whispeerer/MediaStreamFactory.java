package whispeerer.whispeerer;


import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

/**
 * Created by Dominic on 10/04/2016.
 */
public class MediaStreamFactory {

    private PeerConnectionFactory peerConnectionFactory;
    private VideoCapturerAndroid videoCapturerAndroid = null;

    public MediaStreamFactory(PeerConnectionFactory peerConnectionFactory) {
        this.peerConnectionFactory = peerConnectionFactory;
    }

    public MediaStreamWrapper create(boolean videoEnabled) {
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream("OUTGOING");

        AudioSource audioSource;
        AudioTrack outgoingAudioTrack;
        MediaConstraints audioConstraints = new MediaConstraints();
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        outgoingAudioTrack = peerConnectionFactory.createAudioTrack("OUTGOING_AUDIO", audioSource);
        mediaStream.addTrack(outgoingAudioTrack);

        VideoSource videoSource = null;
        if(videoEnabled) {
            videoCapturerAndroid = getCamera();
            if(videoCapturerAndroid != null) {
                MediaConstraints videoConstraints = new MediaConstraints();
                videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid, videoConstraints);
                VideoTrack outgoingVideoTrack = peerConnectionFactory.createVideoTrack("OUTGOING_VIDEO", videoSource);
                mediaStream.addTrack(outgoingVideoTrack);
            }
        }
        return new MediaStreamWrapper("OUTGOING", mediaStream, videoSource, audioSource);
    }

    private VideoCapturerAndroid getCamera() {

        if(CameraEnumerationAndroid.getDeviceCount() >= 1) {
            String cameraName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
            if(cameraName == null) {
                cameraName = CameraEnumerationAndroid.getNameOfBackFacingDevice();
            }
            videoCapturerAndroid = VideoCapturerAndroid.create(cameraName, new VideoCapturerAndroid.CameraEventsHandler() {
                @Override
                public void onCameraError(String s) {
                    Log.v("CAMERA ERROR", s);
                }

                @Override
                public void onCameraFreezed(String s) {
                    Log.v("CAMERA FROZEN", s);
                }

                @Override
                public void onCameraOpening(int i) {
                    Log.v("CAMERA OPENING", ((Integer) i).toString());
                }

                @Override
                public void onFirstFrameAvailable() {
                    Log.v("CAMERA UPDATE", "FIRST FRAME AVAILABLE");
                }

                @Override
                public void onCameraClosed() {
                    Log.v("CAMERA CLOSED", "");

                }
            });
            return videoCapturerAndroid;
        }
        return null;
    }

}
