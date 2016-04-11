package whispeerer.whispeerer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dominic on 10/04/2016.
 */

public class CreateNewUserTask extends AsyncTask<String, Integer, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String username = params[0];

        try {
            URL url = new URL(Signaller.BASE_URI);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            String postData = "username=" + username;
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(postData.getBytes());
            urlConnection.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
