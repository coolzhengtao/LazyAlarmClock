package net.rabraffe.lazyalarmclock.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AnalogClock;

import com.squareup.otto.Subscribe;

import net.rabraffe.lazyalarmclock.R;
import net.rabraffe.lazyalarmclock.events.AlarmAddEvent;
import net.rabraffe.lazyalarmclock.utils.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
    @Bind(R.id.clock)
    AnalogClock clock;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        EventBus.getInstance().register(this);
    }

    @OnClick(R.id.btn_add)
    public void btn_addOnClick(View view){
        Intent intent = new Intent(context,EditAlarmActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onAddAlarm(AlarmAddEvent event){
        //新增闹钟或修改闹钟时刷新界面事件
    }
}
