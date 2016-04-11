package whispeerer.whispeerer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Dominic on 11/04/2016.
 */
public class IncomingCallActivity extends AppCompatActivity {

    String fromUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        fromUsername = intent.getStringExtra(HomeActivity.USERNAME);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.from_username_text), fromUsername);
        TextView fromUsernameText = (TextView) findViewById(R.id.fromUsernameText);
        setContentView(R.layout.activity_incoming_call);
        fromUsernameText.setText(text);

        findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        findViewById(R.id.declineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void openAcceptActivity(View view) {
        Intent intent = new Intent(this, StartChatActivity.class);
        startActivity(intent);
    }
}
