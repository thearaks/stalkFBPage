package aracne.name.stalk4temps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    public static AccessToken accessToken;

    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.start)
    Button startButton;
    @BindView(R.id.syncTextView)
    TextView syncTextView;
    @BindView(R.id.statusTextView)
    TextView statusTextView;
    @BindView(R.id.errorTextView)
    TextView errorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        accessToken = AccessToken.getCurrentAccessToken();

        callbackManager = CallbackManager.Factory.create();

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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateViews();
                    }
                }, 1000);
            }
        });

        if (accessToken != null) {
            startButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void updateViews() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final Date lastSyncDate = new Date(prefs.getLong(StalkService.KEY_LAST_SYNC, 0L));
        syncTextView.setText(String.format(Locale.getDefault(), "Last sync: %1$s",
                SimpleDateFormat.getInstance().format(lastSyncDate)));

        final boolean isServiceRunning = StalkService.isServiceRunning(this);
        if (isServiceRunning) {
            statusTextView.setText("Service running");
            errorTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            statusTextView.setText("Service NOT running");
            errorTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }

        final String lastError = prefs.getString(StalkService.KEY_LAST_ERROR, null);
        if (lastError != null) {
            errorTextView.setText(String.format(Locale.getDefault(), "Last error: %1$s", lastError));
            errorTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            errorTextView.setText("No errors! :D");
            errorTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }

    }
}
