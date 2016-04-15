package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

public class StartChatActivity extends AppCompatActivity {

    public static final String TO_USERNAME = "TO_USERNAME";
    public static final String PEER_CONNECTION_FACTORY = "whispeerer.whispeerer.PEER_CONNECTION_FACTORY";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);
        EditText toUsernameInput = (EditText) findViewById(R.id.toUsernameInput);
        final String toUsername = toUsernameInput.getText().toString();
        username = getIntent().getStringExtra(HomeActivity.USERNAME);
        findViewById(R.id.voiceChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(username, toUsername, ChatType.VOICE_CHAT);
            }
        });

        findViewById(R.id.videoChatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat(username, toUsername, ChatType.VIDEO_CHAT);
            }
        });
    }

    public void startChat(String username, String toUsername, ChatType chatType) {
        Intent intent = new Intent(this, OutgoingChatActivity.class);
        Gson gson = new Gson();
        Signaller signaller = new Signaller(Signaller.BASE_URI + toUsername);
        intent.putExtra(HomeActivity.SIGNALLER, gson.toJson(signaller));
        intent.putExtra(HomeActivity.CHAT_TYPE, chatType.name());
        intent.putExtra(HomeActivity.USERNAME, username);
        intent.putExtra(TO_USERNAME, toUsername);
        startActivity(intent);
    }
}

