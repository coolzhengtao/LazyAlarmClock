package net.rabraffe.lazyalarmclock.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.rabraffe.lazyalarmclock.R;

/**
 * 编辑闹钟界面
 */
public class EditAlarmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
    }
}
