package net.rabraffe.lazyalarmclock.entities;

import java.util.ArrayList;

/**
 * 闹钟管理类
 * Created by Neo on 2015/10/29 0029.
 */
public class AlarmManager {
    private static AlarmManager ourInstance = new AlarmManager();

    public static AlarmManager getInstance() {
        return ourInstance;
    }

    private ArrayList<AlarmScheme> listScheme = new ArrayList<>();          //闹钟列表

    private AlarmManager() {
    }


}
