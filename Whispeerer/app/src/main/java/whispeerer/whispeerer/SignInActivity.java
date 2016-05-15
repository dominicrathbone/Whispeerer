package whispeerer.whispeerer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class SignInActivity extends AppCompatActivity {

    public static final String USERNAME = "whispeerer.whispeerer.USERNAME";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViewById(R.id.goButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToNetwork();
                findViewById(R.id.goButton).setEnabled(false);
            }
        });
    }

    private void connectToNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            createNewUser();
        } else {
            createErrorDialog("Network Connection Failure", "Try checking to see if your wi-fi is enabled");
        }
    }

    private void createNewUser() {
        ServerApiClient.createNewUser(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
                    responseBytes.write(responseBody);
                    JSONObject response = new JSONObject(responseBytes.toString());
                    username = response.getString("username");
                    new Signaller(username, true);
                    openHomeActivity(username);
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
                createErrorDialog("Server Connection Failure", "The signalling server might be down");
            }
        });
    }

    public void openHomeActivity(String username) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(SignInActivity.USERNAME, username);
        startActivity(intent);
    }

    public void createErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}
