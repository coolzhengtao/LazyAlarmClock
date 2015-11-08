package net.rabraffe.lazyalarmclock.activities;

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
    }

    @OnClick(R.id.btn_save)
    public void btn_saveOnclick(View view) {
        //保存闹钟事件
        if (alarm == null) {
            //新增闹钟
            alarm = new AlarmClock();
        }
        Date dtAlarm = new Date();
        dtAlarm.setHours(timePicker.getCurrentHour());
        dtAlarm.setMinutes(timePicker.getCurrentMinute());
        dtAlarm.setSeconds(0);
        //判断响铃模式
        if (check_mon.isChecked() && check_tue.isChecked() &&
                check_wed.isChecked() && check_thu.isChecked() &&
                check_fri.isChecked() && check_sat.isChecked() && check_sun.isChecked()) {
            alarm.setType(AlarmClock.TYPE_EVERYDAY);
        } else if (check_mon.isChecked() && check_tue.isChecked() && check_wed.isChecked() && check_thu.isChecked() && check_fri.isChecked()) {
            alarm.setType(AlarmClock.TYPE_WORKDAY);
        } else if (!check_mon.isChecked() && !check_tue.isChecked() &&
                !check_wed.isChecked() && !check_thu.isChecked() &&
                !check_fri.isChecked() && !check_sat.isChecked() && !check_sun.isChecked()) {
            if (dtAlarm.before(new Date())) {
                //设置为新的一天
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtAlarm);
                calendar.add(Calendar.DATE, 1);
                dtAlarm = calendar.getTime();
            }
            alarm.setType(AlarmClock.TYPE_ONCE);
        } else {
            alarm.setType(AlarmClock.TYPE_CUSTOM);
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
        }
        alarm.setIsEnabled(true);
        alarm.setAlarmTime(dtAlarm);
        alarm.getAlarmTime();               //刷新计划时间
        alarm.setIsVibrateOn(switch_vibrate.isChecked());
        alarm.setName(tv_name.getText().toString());
        AlarmScheme.getInstance().addAlarm(alarm);
        AlarmScheme.getInstance().setNextAlarm();
        EventBus.getInstance().post(new AlarmUpdateEvent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Toast.makeText(this, String.format("闹钟响铃时间：" + format.format(alarm.getAlarmTime())), Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Subscribe
    public void onCloseAllActivity(CloseAllActivityEvent event) {
        //关闭所有窗体
        this.finish();
    }
}
