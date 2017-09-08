package aracne.name.stalk4temps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class StalkService extends Service {

    private static final String KEY_LAST_POST_ID = "LAST_POST_ID";

    private String lastPostId;
    private Handler handler = new Handler();
    private Runnable checkLastPost = new CheckPostRunnable();


    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        lastPostId = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_LAST_POST_ID, null);

        startForeground(100, new Notification.Builder(this)
                .setContentTitle("STALKER RUNNING")
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build());

        handler.removeCallbacks(checkLastPost);
        handler.post(checkLastPost);
        return START_STICKY;
    }

    private class CheckPostRunnable implements Runnable {

        public CheckPostRunnable() {}

        @Override public void run() {
            GraphRequest.newGraphPathRequest(MainActivity.accessToken, "lesquatretemps/posts?limit=1", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError() == null) {
                        try {
                            JSONObject responseObject = response.getJSONObject();
                            JSONObject lastPost = (JSONObject) ((JSONArray) responseObject.get("data")).get(0);

                            if (!lastPost.get("id").equals(lastPostId)) {
                                // New post! Save the ID and notify the user!
                                lastPostId = (String) lastPost.get("id");
                                PreferenceManager.getDefaultSharedPreferences(StalkService.this)
                                        .edit().putString(KEY_LAST_POST_ID, lastPostId).apply();

                                Intent openFacebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/lesquatretemps/"));
                                PendingIntent pendingFacebookIntent = PendingIntent.getActivity(StalkService.this, 0, openFacebookIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(lastPostId.hashCode(), new NotificationCompat.Builder(StalkService.this)
                                        .setSmallIcon(R.drawable.com_facebook_favicon_blue)
                                        .setContentTitle("NEW POST!")
                                        .setContentText((CharSequence) lastPost.get("message"))
                                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText((CharSequence) lastPost.get("message")))
                                        .setContentIntent(pendingFacebookIntent)
                                        .setSound(defaultSoundUri)
                                        .setLights(Color.rgb(255, 0, 0), 500, 500)
                                        .build());

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    handler.postDelayed(checkLastPost, 5 * 60 * 1000);
                }
            }).executeAsync();
        }
    }

}
