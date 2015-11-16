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

    private String uuid;                            //UUID
    private String name = "闹钟";                   //闹钟名称
    private int type = TYPE_ONCE;                   //闹钟类型
    private boolean isVibrateOn;                    //是否震动
    private boolean[] weekAlarm = new boolean[7];   //每周几重响,从周日开始
    private boolean isEnabled;                      //是否启用
    private Date alarmTime;                         //响铃的时间，注意不包含日期

    public AlarmClock() {

    }

    public Date getAlarmTime() {
        //重写获取alarmTime根据下次要响铃的时间获取
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTime(alarmTime);
        Date dtNow = new Date();            //现在的时间
        calendarNow.setTime(dtNow);
        int dayOfWeek = convertDayOfWeek(calendarNow.get(Calendar.DAY_OF_WEEK));     //确定今天星期几
        if (type == TYPE_ONCE && dtNow.getTime() >= alarmTime.getTime()) {
            //单次响铃
            calendar.add(Calendar.DATE, 1); //加一天
        } else if (type != TYPE_ONCE) {
            if ((!weekAlarm[dayOfWeek]) ||
                    (weekAlarm[dayOfWeek] && dtNow.getTime() >= alarmTime.getTime())) {
                //今天不响铃
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
        //判断响铃模式
        if (weekAlarm[0] && weekAlarm[1] && weekAlarm[2] && weekAlarm[3] && weekAlarm[4] &&
                weekAlarm[5] && weekAlarm[6]) {
            //每天响铃
            this.setType(AlarmClock.TYPE_EVERYDAY);
        } else if (weekAlarm[1] && weekAlarm[2] && weekAlarm[3] && weekAlarm[4] &&
                weekAlarm[5]) {
            //工作日响铃
            this.setType(AlarmClock.TYPE_WORKDAY);
        } else if (!weekAlarm[0] && !weekAlarm[1] && !weekAlarm[2] && !weekAlarm[3] && !weekAlarm[4] &&
                !weekAlarm[5] && !weekAlarm[6]) {
            //单次响铃
            this.setType(AlarmClock.TYPE_ONCE);
        } else {
            //自定义响铃
            this.setType(AlarmClock.TYPE_CUSTOM);
        }
    }

    /**
     * 转换系统的DayOffWeek
     *
     * @param l_int_dayOfWeek
     * @return
     */
    private int convertDayOfWeek(int l_int_dayOfWeek) {
        switch (l_int_dayOfWeek) {
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
        }
        return -1;
    }
}
