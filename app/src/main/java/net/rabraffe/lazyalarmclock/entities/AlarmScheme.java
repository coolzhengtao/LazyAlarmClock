package net.rabraffe.lazyalarmclock.entities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import net.rabraffe.lazyalarmclock.Application.AlarmApplication;
import net.rabraffe.lazyalarmclock.activities.AlarmActivity;
import net.rabraffe.lazyalarmclock.utils.FileUtil;
import net.rabraffe.lazyalarmclock.utils.StaticValues;

import java.util.ArrayList;
import java.util.Date;

/**
 * 闹钟管理类
 * Created by Neo on 2015/10/29 0029.
 */
public class AlarmScheme {
    private static AlarmScheme ourInstance = new AlarmScheme();

    public static AlarmScheme getInstance() {
        return ourInstance;
    }

    public ArrayList<AlarmClock> listAlarm = new ArrayList<>();          //闹钟列表

    private AlarmScheme() {
        //尝试从硬盘中获取数据
        Object objList = FileUtil.getObjectFromFile(StaticValues.FILE_PATH, "alarm.dat");
        if (objList != null) {
            this.listAlarm = (ArrayList<AlarmClock>) objList;
            setNextAlarm();
        }
    }

    /**
     * 保存闹钟到磁盘
     */
    public void SaveAlarms() {
        FileUtil.saveObjectToFile(StaticValues.FILE_PATH, "alarm.dat", this.listAlarm);
    }

    /**
     * 新增一个闹钟
     *
     * @param scheme
     */
    public void addAlarm(AlarmClock scheme) {
        listAlarm.add(scheme);
    }

    /**
     * 取消一个闹钟
     *
     * @param strUUID
     */
    public void disableAlarm(String strUUID) {
        for (AlarmClock scheme :
                listAlarm) {
            if (scheme.getUUID().equals(strUUID)) {
                scheme.setIsEnabled(false);
            }
        }
    }

    /**
     * 获取下一个闹钟
     *
     * @return
     */
    private AlarmClock getNextAlarm() {
        AlarmClock schemeResult = null;
        for (AlarmClock scheme :
                listAlarm) {
            //保证下个闹钟的时间比现在的时间晚
            if (scheme.isEnabled() && scheme.getAlarmTime().getTime() > new Date().getTime()) {
                if (schemeResult == null) {
                    schemeResult = scheme;
                    continue;
                }
                if (scheme.getAlarmTime().getTime() < schemeResult.getAlarmTime().getTime()) {
                    schemeResult = scheme;
                }
            }
        }
        return schemeResult;
    }

    /**
     * 设置下一个闹钟
     */
    public void setNextAlarm() {
        AlarmClock scheme = getNextAlarm();
        if (scheme == null) return;
        Intent intent = new Intent(AlarmApplication.appContext, AlarmActivity.class);
        intent.putExtra("uuid", scheme.getUUID());
        PendingIntent pendingIntent = PendingIntent.getActivity(AlarmApplication.appContext, 0, intent, Intent.FILL_IN_ACTION);
        android.app.AlarmManager am = (android.app.AlarmManager) AlarmApplication.appContext.getSystemService(Context.ALARM_SERVICE);
        am.set(android.app.AlarmManager.RTC_WAKEUP, scheme.getAlarmTime().getTime(), pendingIntent);
    }

}
