package aracne.name.stalk4temps;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    public static AccessToken accessToken;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessToken = AccessToken.getCurrentAccessToken();
        startButton = (Button) findViewById(R.id.start);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                startButton.setEnabled(true);
                accessToken = loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("TEST", exception.getMessage());
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, StalkService.class);
                startService(i);
            }
        });

        if (accessToken != null) {
            startButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((TextView) findViewById(R.id.syncTextView)).setText(String.format(Locale.getDefault(),
                "Last sync: %1$s", new Date(PreferenceManager.getDefaultSharedPreferences(this).getLong(StalkService.KEY_LAST_SYNC, 0l))));

        ((TextView) findViewById(R.id.statusTextView)).setText(StalkService.isServiceRunning(this) ?
                "Service running" : "Service NOT running");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
