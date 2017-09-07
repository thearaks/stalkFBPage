package aracne.name.stalk4temps;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;


public class StalkService extends Service {

    private String lastPostId;
    private Handler handler = new Handler();
    private Runnable checkLastPost = new CheckPostRunnable();


    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        lastPostId = PreferenceManager.getDefaultSharedPreferences(this).getString("LAST_POST_ID", null);

        handler.post(checkLastPost);

        return START_STICKY;
    }

    private class CheckPostRunnable implements Runnable {

        public CheckPostRunnable() {}

        @Override public void run() {
            GraphRequest.newGraphPathRequest(MainActivity.accessToken, "lesquatretemps/posts", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError() != null) {
                        response.getJSONObject();
                    }

                    handler.postDelayed(checkLastPost, 3 * 60 * 1000);
                }
            }).executeAsync();
        }
    }

}
