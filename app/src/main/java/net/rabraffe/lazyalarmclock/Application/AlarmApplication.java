package net.rabraffe.lazyalarmclock.Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Neo on 2015/10/30 0030.
 */
public class AlarmApplication extends Application {
    public static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
}
