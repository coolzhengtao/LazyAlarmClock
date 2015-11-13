package net.rabraffe.lazyalarmclock.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import net.rabraffe.lazyalarmclock.R;
import net.rabraffe.lazyalarmclock.entities.AlarmClock;
import net.rabraffe.lazyalarmclock.entities.AlarmScheme;
import net.rabraffe.lazyalarmclock.events.AlarmUpdateEvent;
import net.rabraffe.lazyalarmclock.events.CloseAllActivityEvent;
import net.rabraffe.lazyalarmclock.utils.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑闹钟界面
 */
public class EditAlarmActivity extends BaseActivity {
    @Bind(R.id.timePicker)
    TimePicker timePicker;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.switch_vibrate)
    Switch switch_vibrate;
    @Bind(R.id.check_sun)
    CheckBox check_sun;
    @Bind(R.id.check_mon)
    CheckBox check_mon;
    @Bind(R.id.check_tue)
    CheckBox check_tue;
    @Bind(R.id.check_wed)
    CheckBox check_wed;
    @Bind(R.id.check_thu)
    CheckBox check_thu;
    @Bind(R.id.check_fri)
    CheckBox check_fri;
    @Bind(R.id.check_sat)
    CheckBox check_sat;
    @Bind(R.id.tv_title)
    TextView tv_title;

    AlarmClock alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        EventBus.getInstance().register(this);
        timePicker.setIs24HourView(true);
        switch_vibrate.setChecked(true);
        initValues();
    }

    private void initValues() {
        String strUUID = getIntent().getStringExtra("uuid");
        if (strUUID != null && !strUUID.equals("")) {
            tv_title.setText("编辑闹钟");
            alarm = AlarmScheme.getInstance().getAlarmByUUID(strUUID);
            if (Build.VERSION.SDK_INT > 23) {
                timePicker.setHour(alarm.getAlarmTime().getHours());
                timePicker.setMinute(alarm.getAlarmTime().getMinutes());
            }
            tv_name.setText(alarm.getName());
            switch_vibrate.setChecked(alarm.isVibrateOn());
            check_sun.setChecked(alarm.getWeekAlarm()[0]);
            check_mon.setChecked(alarm.getWeekAlarm()[1]);
            check_tue.setChecked(alarm.getWeekAlarm()[2]);
            check_wed.setChecked(alarm.getWeekAlarm()[3]);
            check_thu.setChecked(alarm.getWeekAlarm()[4]);
            check_fri.setChecked(alarm.getWeekAlarm()[5]);
            check_sat.setChecked(alarm.getWeekAlarm()[6]);
        }
    }

    @OnClick(R.id.btn_save)
    public void btn_saveOnclick(View view) {
        //保存闹钟事件
        if (alarm == null) {
            //新增闹钟
            alarm = new AlarmClock();
            AlarmScheme.getInstance().addAlarm(alarm);
        }
        Date dtAlarm = new Date();
        dtAlarm.setHours(timePicker.getCurrentHour());
        dtAlarm.setMinutes(timePicker.getCurrentMinute());
        dtAlarm.setSeconds(0);
        boolean[] bAlarmDays = new boolean[]{
                check_sun.isChecked(),
                check_mon.isChecked(),
                check_tue.isChecked(),
                check_wed.isChecked(),
                check_thu.isChecked(),
                check_fri.isChecked(),
                check_sat.isChecked()
        };
        alarm.setWeekAlarm(bAlarmDays);
        alarm.setIsEnabled(true);
        alarm.setAlarmTime(dtAlarm);
        alarm.setIsVibrateOn(switch_vibrate.isChecked());
        alarm.setName(tv_name.getText().toString());
        alarm.setUuid(UUID.randomUUID().toString());
        EventBus.getInstance().post(new AlarmUpdateEvent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Toast.makeText(this, String.format("闹钟响铃时间：" + format.format(alarm.getAlarmTime())),
                Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Subscribe
    public void onCloseAllActivity(CloseAllActivityEvent event) {
        //关闭所有窗体
        this.finish();
    }
}
