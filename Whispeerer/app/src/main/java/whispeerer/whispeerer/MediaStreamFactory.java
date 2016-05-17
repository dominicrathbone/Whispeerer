package whispeerer.whispeerer;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
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

    PeerConnectionFactory peerConnectionFactory;

    public MediaStreamFactory(PeerConnectionFactory peerConnectionFactory) {
        this.peerConnectionFactory = peerConnectionFactory;
    }

    public MediaStream getMediaStream(boolean videoEnabled) {
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream("OUTGOING");

        AudioSource audioSource;
        AudioTrack outgoingAudioTrack;
        MediaConstraints audioConstraints = new MediaConstraints();
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        outgoingAudioTrack = peerConnectionFactory.createAudioTrack("OUTGOING_AUDIO", audioSource);
        mediaStream.addTrack(outgoingAudioTrack);

        if(videoEnabled) {
            VideoCapturerAndroid videoCapturerAndroid = getCamera();
            if(videoCapturerAndroid != null) {
                VideoSource videoSource;
                VideoTrack outgoingVideoTrack;
                MediaConstraints videoConstraints = new MediaConstraints();
                videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid, videoConstraints);
                outgoingVideoTrack = peerConnectionFactory.createVideoTrack("OUTGOING_VIDEO", videoSource);
                mediaStream.addTrack(outgoingVideoTrack);
            }
        }
        return mediaStream;
    }

    private VideoCapturerAndroid getCamera() {
        if(VideoCapturerAndroid.getDeviceCount() >= 1) {
            String cameraName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
            if(cameraName == null) {
                cameraName = VideoCapturerAndroid.getNameOfBackFacingDevice();
            }
            return VideoCapturerAndroid.create(cameraName);
        }
        return null;
    }
}
