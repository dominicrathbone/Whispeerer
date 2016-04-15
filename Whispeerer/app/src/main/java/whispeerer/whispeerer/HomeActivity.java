package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dominic on 11/02/2016.
 */
public class HomeActivity extends AppCompatActivity implements Observer {

    public static final String SIGNALLER = "whispeerer.whispeerer.SIGNALlER";
    public static final String USERNAME = "whispeerer.whispeerer.USERNAME";
    public static final String FROM_USERNAME = "whispeerer.whispeerer.FROM_USERNAME";
    public static final String CHAT_TYPE = "whispeerer.whispeerer.CHAT_TYPE";
    private String username;
    private Signaller signaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        username = intent.getStringExtra(SignInActivity.USERNAME);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.welcome_text), username);
        setContentView(R.layout.activity_home);
        TextView welcomeText = (TextView)findViewById(R.id.welcomeText);
        welcomeText.setText(text);
        connectToNetwork();
        findViewById(R.id.startChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStartChatActivity(view);
            }
        });

        findViewById(R.id.chatRequests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChatRequestsActivity(view);
            }
        });

        findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareActivity(view);
            }
        });


    }

    public void connectToNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new CreateNewUserTask().execute(username);
            signaller = new Signaller(Signaller.BASE_URI + username);
            signaller.setObserver(this);
        } else {
            createNetworkConnectionErrorDialog();
        }
    }

    public void createNetworkConnectionErrorDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Network Connection Error")
            .setMessage("Failed to connect to the internet")
            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    public void openStartChatActivity(View view) {
        Intent intent = new Intent(this, StartChatActivity.class);
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }

    public void openChatRequestsActivity(View view) {
        Intent intent = new Intent(this, ChatRequestsActivity.class);
        startActivity(intent);
    }

    public void openShareActivity(View view) {
        String mimeType = "text/plain";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        if(username != null) {
            intent.putExtra(Intent.EXTRA_TEXT, "Contact me on WhisPeerer! My username is: " + username);
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, "Contact me on WhisPeerer!");
        }
        startActivity(Intent.createChooser(intent, "Share Your Chat"));
    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            JSONObject json = new JSONObject((String) data);
            if(json.has("from")) {
                Intent intent = new Intent(this, IncomingChatActivity.class);
                Gson gson = new Gson();
                intent.putExtra(SIGNALLER, gson.toJson(signaller));
                intent.putExtra(CHAT_TYPE, json.getString("chatType"));
                intent.putExtra(FROM_USERNAME, json.get("from").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
