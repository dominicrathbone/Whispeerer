package whispeerer.whispeerer;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dominic on 12/05/2016.
 */
public class ServerApiClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void createNewUser(AsyncHttpResponseHandler asyncHttpResponseHandler) {
        try {
            URL url = new URL(ServerInfo.BASE_URL.getUri() + ":" + ServerInfo.PORT.getUri() + ServerInfo.USER.getUri());
            client.post(url.toExternalForm(),asyncHttpResponseHandler);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void checkUserExists(String username, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        try {
            URL url = new URL(ServerInfo.BASE_URL.getUri() + ":" + ServerInfo.PORT.getUri() + ServerInfo.USERS.getUri() + username);
            client.get(url.toExternalForm(),asyncHttpResponseHandler);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }



}



