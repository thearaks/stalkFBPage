package aracne.name.stalk4temps;

import android.app.Application;

import com.facebook.FacebookSdk;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
