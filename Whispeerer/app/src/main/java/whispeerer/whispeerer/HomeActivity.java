package whispeerer.whispeerer;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Dominic on 11/02/2016.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "197158447313911";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String username = intent.getStringExtra(SignInActivity.USERNAME);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.welcome_text), username);
        setContentView(R.layout.activity_home);
        TextView welcomeText = (TextView)findViewById(R.id.welcomeText);
        welcomeText.setText(text);
    }

    public void openStartChatActivity(View view) {
        Intent intent = new Intent(this, StartChatActivity.class);
        startActivity(intent);
    }

    public void openChatRequestsActivity(View view) {
        Intent intent = new Intent(this, ChatRequestsActivity.class);
        startActivity(intent);
    }

    public void openShareIntent(View view) {
        String mimeType = "text/*";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage("com.facebook.orca");
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("Call me on WhisPeerer! My username is" + SignInActivity.USERNAME));
        intent.putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION);
        intent.putExtra(EXTRA_APP_ID, YOUR_APP_ID);

        this.startActivityForResult(intent, SHARE_TO_MESSENGER_REQUEST_CODE);

    }
}
