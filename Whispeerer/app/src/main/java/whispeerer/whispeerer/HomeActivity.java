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

    private String username;

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
}
