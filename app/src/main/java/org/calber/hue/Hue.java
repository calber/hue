package org.calber.hue;

import android.app.Application;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

import api.Api;
import io.fabric.sdk.android.Fabric;
import models.AllData;

/**
 * Created by calber on 28/2/16.
 */
public class Hue extends Application {
    public static final String TAG = "HUE";
    public static String androidId;
    public static String TOKEN;
    public static Api api;
    public static String URL;

    public static AllData hueConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();

        if(!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
