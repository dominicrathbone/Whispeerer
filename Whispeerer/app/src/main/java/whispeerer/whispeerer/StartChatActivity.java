package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class StartChatActivity extends AppCompatActivity implements Observer {

    public static final String TO_USERNAME = "TO_USERNAME";
    private String username;
    private String chatType;
    private ProgressDialog progress;
    private StartChatActivity startChatActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        Intent intent = getIntent();
        chatType = intent.getStringExtra(HomeActivity.CHAT_TYPE);
        username = getIntent().getStringExtra(HomeActivity.USERNAME);

        Signaller.incomingChatSignaller.setObserver(this);

        Resources res = getResources();
        String text = "";
        ImageButton goButton = (ImageButton) findViewById(R.id.goButton);

        if(chatType.equals(ChatType.VOICE_CHAT.name())) {
            text = String.format(res.getString(R.string.chat_type), "VOICE CHAT");
            goButton.setImageResource(R.drawable.ic_call_white_48dp);
        } else if(chatType.equals(ChatType.VIDEO_CHAT.name())) {
            text = String.format(res.getString(R.string.chat_type), "VIDEO CHAT");
            goButton.setImageResource(R.drawable.ic_videocam_white_48dp);
        }

        TextView chatHeaderText = (TextView) findViewById(R.id.chatHeaderText);
        chatHeaderText.setText(text);

        findViewById(R.id.goButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(chatType);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Signaller.incomingChatSignaller.setObserver(this);
    }

    private void startChat(final String chatType) {
        findViewById(R.id.goButton).setEnabled(false);
        final EditText toUsernameInput = (EditText) findViewById(R.id.toUsernameInput);
        toUsernameInput.setError(null);
        final String toUsername = toUsernameInput.getText().toString();
        if(TextUtils.isEmpty(toUsername)) {
            toUsernameInput.setError("Cant be empty");
            findViewById(R.id.goButton).setEnabled(true);
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    toUsernameInput.setError(null);
                }
            }, 5000);
            return;
        }
        progress = ProgressDialog.show(startChatActivity, "","Verifying user exists...", true);
        ServerApiClient.checkUserExists(toUsername, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progress.dismiss();
                openOutgoingChatActivity(username, toUsername, chatType);
                findViewById(R.id.goButton).setEnabled(true);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progress.dismiss();
                if(statusCode == 404) {
                    toUsernameInput.setError("User doesn't exist");
                } else {
                    displayAlertDialog("Server Connection Error", "Couldn't verify if user exists", false);
                }
                findViewById(R.id.goButton).setEnabled(true);
            }
        });
    }

    private void openOutgoingChatActivity(String username, String toUsername, String chatType) {
        Intent intent = new Intent(this, OutgoingChatActivity.class);
        intent.putExtra(HomeActivity.CHAT_TYPE, chatType);
        intent.putExtra(HomeActivity.USERNAME, username);
        intent.putExtra(TO_USERNAME, toUsername);
        startActivity(intent);
    }

    private void displayAlertDialog(String title, String message, final boolean endActivity) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(endActivity) {
                            finish();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            JSONObject json = new JSONObject((String) data);
            if(json.getString("type").equals("offer")) {
                Intent intent = new Intent(this, IncomingChatActivity.class);
                intent.putExtra(HomeActivity.USERNAME, username);
                intent.putExtra(HomeActivity.CHAT_TYPE, json.getString("chatType"));
                intent.putExtra(HomeActivity.FROM_USERNAME, json.getString("from"));
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

