package net.rabraffe.lazyalarmclock.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import net.rabraffe.lazyalarmclock.R;
import net.rabraffe.lazyalarmclock.entities.AlarmClock;
import net.rabraffe.lazyalarmclock.entities.AlarmScheme;
import net.rabraffe.lazyalarmclock.events.AlarmAddEvent;
import net.rabraffe.lazyalarmclock.utils.EventBus;

import java.util.Calendar;
import java.util.Date;

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

    AlarmClock alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        ButterKnife.bind(this);
        timePicker.setIs24HourView(true);
        switch_vibrate.setChecked(true);
    }

    @OnClick(R.id.btn_save)
    public void btn_saveOnclick(View view){
        //保存闹钟事件
        if(alarm == null){
            //新增闹钟
            alarm = new AlarmClock();
        }
        Date dtAlarm = new Date();
        dtAlarm.setHours(timePicker.getCurrentHour());
        dtAlarm.setMinutes(timePicker.getCurrentMinute());
        dtAlarm.setSeconds(0);
        if (dtAlarm.before(new Date())) {
            //设置为新的一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dtAlarm);
            calendar.add(Calendar.DATE, 1);
            dtAlarm = calendar.getTime();
        }
        alarm.setIsEnabled(true);
        alarm.setAlarmTime(dtAlarm);
        alarm.setType(AlarmClock.TYPE_ONCE);
        alarm.setIsVibrateOn(switch_vibrate.isChecked());
        alarm.setName(tv_name.getText().toString());
        AlarmScheme.getInstance().addAlarm(alarm);
        AlarmScheme.getInstance().setNextAlarm();
        EventBus.getInstance().post(new AlarmAddEvent());
        this.finish();
    }
}
