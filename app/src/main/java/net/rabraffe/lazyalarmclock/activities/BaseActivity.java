package net.rabraffe.lazyalarmclock.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import net.rabraffe.lazyalarmclock.entities.AlarmClock;
import net.rabraffe.lazyalarmclock.entities.AlarmScheme;

/**
 * Created by Neo on 2015/10/29 0029.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化界面
        requestWindowFeature(Window.FEATURE_NO_TITLE);                          //无标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);    //纵向屏幕
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmScheme.getInstance().setNextAlarm();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmScheme.getInstance().SaveAlarms();
    }
}
