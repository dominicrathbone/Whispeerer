package whispeerer.whispeerer;

import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Dominic on 08/04/2016.
 */
public class Signaller extends Observable {

    public static final String BASE_URI = "http://192.168.1.101";
    private Socket socket;
    private Gson gson;
    private Signaller signaller = this;
    private Observer signallerObserver;

    public Signaller(String uri) {
        this.gson = new Gson();
        try {
            this.socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("foo", "hi");
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });
        socket.connect();
    }

    public void send(String event, String signal) {
        socket.emit(event, signal);
    }

    public void setObserver(final Observer observer) {
        this.signallerObserver = observer;
        socket.on("caller", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String callStatus = gson.fromJson((String) args[0], String.class);
                signallerObserver.update(signaller, callStatus);
            }
        });
    }

    public void setPeerConnection(final PeerConnection peerConnection, final SdpObserver sdpObserver) {
        socket.on("candidate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                IceCandidate iceCandidate = gson.fromJson((String) args[0], IceCandidate.class);
                peerConnection.addIceCandidate(iceCandidate);
            }
        }).on("sdp", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SessionDescription sessionDescription = gson.fromJson((String) args[0], SessionDescription.class);
                peerConnection.setRemoteDescription(sdpObserver, sessionDescription);
                if(sessionDescription.type == SessionDescription.Type.OFFER) {
                    peerConnection.createAnswer(sdpObserver, new MediaConstraints());
                }
            }
        });
    }

}
