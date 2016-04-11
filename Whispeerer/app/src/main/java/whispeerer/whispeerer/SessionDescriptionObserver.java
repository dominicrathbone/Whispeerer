package whispeerer.whispeerer;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Created by Dominic on 10/04/2016.
 */
public class SessionDescriptionObserver implements SdpObserver {

    private PeerConnection peerConnection;

    public SessionDescriptionObserver(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }
    
    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {

    }

    @Override
    public void onSetSuccess() {

    }

    @Override
    public void onCreateFailure(String s) {

    }

    @Override
    public void onSetFailure(String s) {

    }
}
