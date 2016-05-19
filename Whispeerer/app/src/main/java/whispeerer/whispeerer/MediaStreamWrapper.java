package whispeerer.whispeerer;

import org.webrtc.AudioSource;
import org.webrtc.MediaStream;
import org.webrtc.VideoSource;

/**
 * Created by Dominic on 19/05/2016.
 */
public class MediaStreamWrapper {

    private final String id;
    private final MediaStream mediaStream;
    private final VideoSource videoSource;
    private final AudioSource audioSource;

    public MediaStreamWrapper(String id, MediaStream mediaStream, VideoSource videoSource, AudioSource audioSource) {
        this.id = id;
        this.mediaStream = mediaStream;
        this.videoSource = videoSource;
        this.audioSource = audioSource;
    }

    public void dispose() {
        if(videoSource != null) {
            videoSource.stop();
            videoSource.dispose();
        }
        if(audioSource != null) {
            audioSource.dispose();
        }
    }

    public MediaStream getMediaStream() {
        return mediaStream;
    }

}
