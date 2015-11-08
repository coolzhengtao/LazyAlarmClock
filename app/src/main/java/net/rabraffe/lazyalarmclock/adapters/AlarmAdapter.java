package net.rabraffe.lazyalarmclock.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import net.rabraffe.lazyalarmclock.R;
import net.rabraffe.lazyalarmclock.entities.AlarmClock;
import net.rabraffe.lazyalarmclock.entities.AlarmScheme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Neo on 2015/10/29 0029.
 */
public class AlarmAdapter extends BaseAdapter {
    private ArrayList<AlarmClock> listClocks;
    private Context context;

    public AlarmAdapter(Context context, ArrayList<AlarmClock> list) {
        this.context = context;
        this.listClocks = list;
    }

    @Override
    public int getCount() {
        return listClocks.size();
    }

    @Override
    public Object getItem(int position) {
        return listClocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_alarm_info, null);
            vHolder = new ViewHolder();
            vHolder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            vHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            vHolder.switch_enable = (Switch) convertView.findViewById(R.id.switch_enable);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        final AlarmClock alarmClock = listClocks.get(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        vHolder.tv_time.setText(simpleDateFormat.format(alarmClock.getAlarmTime()));
        vHolder.switch_enable.setChecked(alarmClock.isEnabled());
        StringBuilder strInfo = new StringBuilder(alarmClock.getName());
        if (alarmClock.getType() == AlarmClock.TYPE_ONCE) {
            strInfo.append(" 单次");
        } else if (alarmClock.getType() == AlarmClock.TYPE_EVERYDAY) {
            strInfo.append(" 每天");
        } else if (alarmClock.getType() == AlarmClock.TYPE_WORKDAY) {
            strInfo.append(" 工作日");
        } else if (alarmClock.getType() == AlarmClock.TYPE_CUSTOM) {
            strInfo.append(" 周");
            if (alarmClock.getWeekAlarm()[0]) {
                strInfo.append("日");
            }
            if (alarmClock.getWeekAlarm()[1]) {
                strInfo.append("一");
            }
            if (alarmClock.getWeekAlarm()[2]) {
                strInfo.append("二");
            }
            if (alarmClock.getWeekAlarm()[3]) {
                strInfo.append("三");
            }
            if (alarmClock.getWeekAlarm()[4]) {
                strInfo.append("四");
            }
            if (alarmClock.getWeekAlarm()[5]) {
                strInfo.append("五");
            }
            if (alarmClock.getWeekAlarm()[6]) {
                strInfo.append("六");
            }
        }
        strInfo.append(alarmClock.isEnabled() ? " 启用" : " 未启用");
        vHolder.tv_info.setText(strInfo.toString());
        vHolder.switch_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmClock.setIsEnabled(isChecked);
                notifyDataSetChanged();
                AlarmScheme.getInstance().setNextAlarm();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_time;
        private TextView tv_info;
        private Switch switch_enable;
    }
}
