package whispeerer.whispeerer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Dominic on 12/04/2016.
 */
public abstract class ChatActivity extends AppCompatActivity {

    Intent intent;
    String username;
    String chatType;
    Signaller signaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        chatType = intent.getStringExtra(HomeActivity.CHAT_TYPE);
        username = intent.getStringExtra(SignInActivity.USERNAME);

    }

    void openChatDisplayActivity(String username) {
        Intent intent = null;
        if(chatType.equals(ChatType.VOICE_CHAT.name())) {
            intent = new Intent(this, VoiceChatDisplayActivity.class);
        } else if(chatType.equals(ChatType.VIDEO_CHAT.name())) {
            intent = new Intent(this, VideoChatDisplayActivity.class);
        }
        intent.putExtra(SignInActivity.USERNAME, username);
        Log.v(username, "STARTING CHAT DISPLAY ACTIVITY");
        startActivityForResult(intent, 1);
    }
}
