package net.rabraffe.lazyalarmclock.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 闹钟计划实体类
 * Created by coolz on 2015/10/15 0015.
 */
public class AlarmClock implements Serializable {
    public static final int TYPE_ONCE = 0x1;        //单次
    public static final int TYPE_EVERYDAY = 0x2;    //每日
    public static final int TYPE_WORKDAY = 0x3;     //工作日
    public static final int TYPE_CUSTOM = 0x4;      //自定义

    private String uuid;                  //UUID
    private String name = "闹钟";                //闹钟名称
    private int type = TYPE_ONCE;                   //闹钟类型
    private boolean isVibrateOn;        //是否震动
    private boolean[] weekAlarm = new boolean[7];           //每周几重响
    private boolean isEnabled;          //是否启用
    private Date alarmTime;             //响铃的时间，注意不包含日期

    public AlarmClock() {

    }

    public Date getAlarmTime() {
        //重写获取alarmTime根据下次要响铃的时间获取
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmTime);
        Date dtNow = new Date();            //现在的时间
        switch (type) {
            case TYPE_ONCE:
                break;
            case TYPE_EVERYDAY:
                //每天响铃
                if (dtNow.getTime() >= alarmTime.getTime()) {
                    calendar.add(Calendar.DATE, 1); //加一天
                }
                break;
            case TYPE_WORKDAY:
                //工作日响铃
                if (dtNow.getTime() >= alarmTime.getTime()) {
                    Calendar calendar1Now = Calendar.getInstance();
                    calendar1Now.setTime(dtNow);
                    switch (calendar1Now.get(Calendar.DAY_OF_WEEK)) {
                        case Calendar.MONDAY:
                        case Calendar.TUESDAY:
                        case Calendar.WEDNESDAY:
                        case Calendar.THURSDAY:
                            calendar.add(Calendar.DATE, 1); //加一天
                            break;
                        case Calendar.FRIDAY:
                            calendar.add(Calendar.DATE, 3); //加三天
                            break;
                        case Calendar.SATURDAY:
                            calendar.add(Calendar.DATE, 2); //加两天
                            break;
                        case Calendar.SUNDAY:
                            calendar.add(Calendar.DATE, 1); //加一天
                            break;
                    }
                }
                break;
            case TYPE_CUSTOM:
                //自定义响铃
                if (dtNow.getTime() >= alarmTime.getTime()) {
                    Calendar calendar1Now = Calendar.getInstance();
                    calendar1Now.setTime(dtNow);
                    int dayOfWeek = calendar1Now.get(Calendar.DAY_OF_WEEK) - 1;     //确定今天星期几
                    boolean isAddDay = false;
                    for (int i = dayOfWeek + 1; i < 7; i++) {
                        if (weekAlarm[i]) {
                            calendar.add(Calendar.DATE, i - dayOfWeek);
                            isAddDay = true;            //已经加过
                            break;
                        }
                    }
                    if (!isAddDay) {
                        for (int i = 0; i <= dayOfWeek; i++) {
                            if (weekAlarm[i]) {
                                calendar.add(Calendar.DATE, i + 7 - dayOfWeek);
                                break;
                            }
                        }
                    }
                }
                break;
        }
        alarmTime = calendar.getTime();
        return alarmTime;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isVibrateOn() {
        return isVibrateOn;
    }

    public void setIsVibrateOn(boolean isVibrateOn) {
        this.isVibrateOn = isVibrateOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean[] getWeekAlarm() {
        return weekAlarm;
    }

    public void setWeekAlarm(boolean[] weekAlarm) {
        this.weekAlarm = weekAlarm;
    }
}
