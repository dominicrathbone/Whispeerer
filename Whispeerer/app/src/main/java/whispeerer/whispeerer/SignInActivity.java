package whispeerer.whispeerer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {

    public static final String USERNAME = "whispeerer.whispeerer.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.goButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomeActivity(view);
            }
        });
    }

    public void openHomeActivity(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        EditText usernameInput = (EditText) findViewById(R.id.usernameInput);
        String username = usernameInput.getText().toString();
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }

    
}
