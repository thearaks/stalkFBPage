package aracne.name.stalk4temps;

import android.app.Service;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import javax.net.ssl.HttpsURLConnection;


public class StalkService extends Service {

    private String lastPostId;
    private Handler handler = new Handler();
    private Runnable checkLastPost = new Runnable() {
        @Override public void run() {
            HttpRequest httpRequest = new HttpRequest() {
            };


            handler.post(checkLastPost);
        }
    };

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        lastPostId = PreferenceManager.getDefaultSharedPreferences(this).getString("LAST_POST_ID", null);

        handler.post(checkLastPost);

        return START_STICKY;
    }

    private class CheckPostRunnable implements Runnable {
        private Gson gson;
        private HttpClient client;
        private HttpUriRequest request;

        public CheckPostRunnable() {
            gson = new Gson();
            client = new AndroidHttpClient();

            request = new HttpGet("https://graph.facebook.com/lesquatretemps/posts");
            request.addHeader(new BasicHeader("Authentication", "Bearer " + "EAACEdEose0cBAOtZBtSWiHoI15DsaguVh5j6mxMGHAxEZBYDysrZCrx7yZBapkPPKy8yj6eAgeIPDPPmgKi1DqcY9BUkrkPZCaafCMhoSTrmUhVeUlAQrntU0RJtxofOT5WN5ZBzF6ZBT57pJhxVzeG7WHWVnQM4qa8hHUCnw5ZBxmWOa3RfreNvQxz5IoZCtcBUZD"));
        }

        @Override public void run() {
            String result = client.execute(request);

            gson.fromJson(result);

            handler.postDelayed(checkLastPost, 60 * 1000);
        }
    }
}
