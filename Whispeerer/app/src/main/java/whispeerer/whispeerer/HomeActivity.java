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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Dominic on 11/02/2016.
 */
public class HomeActivity extends AppCompatActivity implements Observer {

    public static final String FROM_USERNAME = "whispeerer.whispeerer.FROM_USERNAME";
    public static final String CHAT_TYPE = "whispeerer.whispeerer.CHAT_TYPE";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        username = intent.getStringExtra(SignInActivity.USERNAME);

        Resources res = getResources();
        String text = String.format(res.getString(R.string.welcome_text), username);
        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
        welcomeText.setText(text);

        Signaller.incomingChatSignaller.setObserver(this);

        findViewById(R.id.startChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStartChatActivity(view);
            }
        });

        findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareActivity(view);
            }
        });
    }

    public void openStartChatActivity(View view) {
        Intent intent = new Intent(this, StartChatActivity.class);
        intent.putExtra(SignInActivity.USERNAME, username);
        startActivity(intent);
    }

    public void openShareActivity(View view) {
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
                intent.putExtra(SignInActivity.USERNAME, username);
                intent.putExtra(CHAT_TYPE, json.getString("chatType"));
                intent.putExtra(FROM_USERNAME, json.getString("from"));
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
