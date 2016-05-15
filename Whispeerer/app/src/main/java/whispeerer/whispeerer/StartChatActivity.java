package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

public class StartChatActivity extends AppCompatActivity {

    public static final String TO_USERNAME = "TO_USERNAME";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        username = getIntent().getStringExtra(SignInActivity.USERNAME);

        findViewById(R.id.voiceChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText toUsernameInput = (EditText) findViewById(R.id.toUsernameInput);
                final String toUsername = toUsernameInput.getText().toString();
                startChat(username, toUsername, ChatType.VOICE_CHAT);
            }
        });
        findViewById(R.id.videoChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText toUsernameInput = (EditText) findViewById(R.id.toUsernameInput);
                final String toUsername = toUsernameInput.getText().toString();
                startChat(username, toUsername, ChatType.VIDEO_CHAT);
            }
        });
    }

    public void startChat(String username, String toUsername, ChatType chatType) {
        Intent intent = new Intent(this, OutgoingChatActivity.class);
        intent.putExtra(HomeActivity.CHAT_TYPE, chatType.name());
        intent.putExtra(SignInActivity.USERNAME, username);
        intent.putExtra(TO_USERNAME, toUsername);
        startActivity(intent);
    }
}

