package org.calber.hue;

import android.app.Application;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by calber on 28/2/16.
 */
public class Hue extends Application {
    public static final String TAG = "HUE";
    public static String androidId;
    public static String TOKEN;

    @Override
    public void onCreate() {
        super.onCreate();

        if(!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
