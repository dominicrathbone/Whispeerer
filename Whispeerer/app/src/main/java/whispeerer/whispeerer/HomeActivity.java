package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Dominic on 11/02/2016.
 */
public class HomeActivity extends AppCompatActivity implements Observer {

    public static final String FROM_USERNAME = "whispeerer.whispeerer.FROM_USERNAME";
    public static final String CHAT_TYPE = "whispeerer.whispeerer.CHAT_TYPE";
    public static final String USERNAME = "whispeerer.whispeerer.USERNAME";
    private String username;
    private ProgressDialog progress;
    private HomeActivity homeActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progress = ProgressDialog.show(homeActivity, "","Connecting to Signalling Server...", true);
        if(connectToNetwork()) {
            progress.setMessage("Creating new session username...");
            createNewUser();
        } else {
            progress.dismiss();
            displayAlertDialog("Network Connection Failure", "Try checking to see if your wi-fi is enabled");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Signaller.incomingChatSignaller != null) {
            Signaller.incomingChatSignaller.setObserver(this);
        }
    }

    private boolean connectToNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void createNewUser() {
        ServerApiClient.createNewUser(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progress.setMessage("Joining signalling room...");

                    ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
                    responseBytes.write(responseBody);
                    JSONObject response = new JSONObject(responseBytes.toString());
                    username = response.getString("username");

                    new Signaller(username, true);
                    Signaller.incomingChatSignaller.setObserver(homeActivity);

                    Resources res = getResources();
                    String text = String.format(res.getString(R.string.username), username);
                    TextView usernameText = (TextView) findViewById(R.id.username);
                    usernameText.setText(text);

                    findViewById(R.id.voiceChatButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openStartChatActivity(ChatType.VOICE_CHAT);
                        }
                    });

                    findViewById(R.id.videoChatButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openStartChatActivity(ChatType.VIDEO_CHAT);
                        }
                    });

                    findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openShareActivity();
                        }
                    });

                    progress.dismiss();
                    Log.v(username, "NEW_USER_REQUEST_STATUS: " + Integer.toString(statusCode) + " - " + responseBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v(username, "NEW_USER_REQUEST_STATUS: " + Integer.toString(statusCode) + " - " + responseBody);
                displayAlertDialog("Server Connection Failure", "The signalling server might be down");
                Resources res = getResources();
                TextView usernameText = (TextView) findViewById(R.id.username);
                usernameText.setText("");
                progress.dismiss();
            }
        });
    }

    public void openStartChatActivity(ChatType chatType) {
        Intent intent = new Intent(this, StartChatActivity.class);
        intent.putExtra(USERNAME, username);
        intent.putExtra(CHAT_TYPE, chatType.name());
        startActivity(intent);
    }

    public void openShareActivity() {
        String mimeType = "text/plain";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TEXT, "Contact me on WhisPeerer! My username is: " + username);
        startActivity(Intent.createChooser(intent, "Share Your Chat"));
    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            JSONObject json = new JSONObject((String) data);
            if(json.getString("type").equals("offer")) {
                Intent intent = new Intent(this, IncomingChatActivity.class);
                intent.putExtra(USERNAME, username);
                intent.putExtra(CHAT_TYPE, json.getString("chatType"));
                intent.putExtra(FROM_USERNAME, json.getString("from"));
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    connectToNetwork();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
