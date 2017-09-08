package aracne.name.stalk4temps;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;


public class StalkService extends Service {

    private String lastPostId;
    private Handler handler = new Handler();
    private Runnable checkLastPost = new CheckPostRunnable();


    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        lastPostId = PreferenceManager.getDefaultSharedPreferences(this).getString("LAST_POST_ID", null);

        startForeground(100, new Notification.Builder(this)
                .setContentTitle("STALKER RUNINNG")
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build());

        handler.post(checkLastPost);
        return START_STICKY;
    }

    private class CheckPostRunnable implements Runnable {

        public CheckPostRunnable() {}

        @Override public void run() {
            GraphRequest.newGraphPathRequest(MainActivity.accessToken, "lesquatretemps/posts", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    JSONObject responseObject;
                    if (response.getError() != null) {
                        responseObject = response.getJSONObject();
                    }

                    handler.postDelayed(checkLastPost, 3 * 60 * 1000);
                }
            }).executeAsync();
        }
    }

}
