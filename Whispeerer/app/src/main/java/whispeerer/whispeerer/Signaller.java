package whispeerer.whispeerer;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
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


    public static Signaller outgoingChatSignaller = null;
    public static Signaller incomingChatSignaller = null;
    private String username;
    private String uri;
    private Socket socket;
    private Gson gson;
    private Signaller signaller = this;
    private Observer signallerObserver;

    public Signaller(final String username, boolean isForIncomingChats) {
        if(isForIncomingChats) {
            this.incomingChatSignaller = this;
        } else {
            this.outgoingChatSignaller = this;
        }
        this.username = username;
        this.uri = ServerInfo.BASE_URL.getUri() + ":" + ServerInfo.PORT.getUri() + ServerInfo.USERS.getUri() + username;
        this.gson = new Gson();
        try {
            this.socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v(username, "SOCKET CONNECTED");
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v(username, "SOCKET DISCONNECTED");
            }
        });
        socket.connect();
    }

    public void send(String event, String signal) {
        socket.emit(event, signal);
    }

    public void setObserver(final Observer observer) {
        this.signallerObserver = observer;
        socket.on("offer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String json = args[0].toString();
                ((AppCompatActivity) signallerObserver).runOnUiThread(new Runnable() {
                    public void run() {
                        signallerObserver.update(signaller, json);
                        Log.v(username, "OFFER RECEIVED");
                    }
                });
            }
        }).on("answer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String json = args[0].toString();
                ((AppCompatActivity) signallerObserver).runOnUiThread(new Runnable() {
                    public void run() {
                        signallerObserver.update(signaller, json);
                        Log.v(username, "ANSWER RECEIVED");
                    }
                });
            }
        });
    }

    public void setPeerConnection(final PeerConnection peerConnection, final SdpObserver sdpObserver) {
        socket.on("candidate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                IceCandidate iceCandidate = gson.fromJson((String) args[0], IceCandidate.class);
                peerConnection.addIceCandidate(iceCandidate);
                Log.v(username, "CANDIDATE RECEIVED");
            }
        }).on("sdp", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SessionDescription sessionDescription = gson.fromJson((String) args[0], SessionDescription.class);
                if(sessionDescription != null) {
                    peerConnection.setRemoteDescription(sdpObserver, sessionDescription);
                    Log.v(username, "SDP RECEIVED");
                }
            }
        });
    }

    public void sendInitialChatOffer(String chatType, String fromUsername) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "offer");
            json.put("from", fromUsername);
            json.put("chatType", chatType);
            signaller.send("offer", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendInitialChatCancellation() {
        try {
            JSONObject json = new JSONObject();
            json.put("chatStatus", ChatStatus.CANCELLED.name());
            signaller.send("offer", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendInitialChatAnswer(ChatStatus status) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "answer");
            json.put("from", username);
            json.put("chatStatus", status.name());
            signaller.send("answer", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        socket.disconnect();
        Log.v(username, "SOCKET DISCONNECTED");
    }


}
