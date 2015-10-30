package net.rabraffe.lazyalarmclock.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.rabraffe.lazyalarmclock.entities.AlarmScheme;

public class SystemStartReceiver extends BroadcastReceiver {
    public SystemStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //开机读取闹钟计划
        AlarmScheme.getInstance().setNextAlarm();
    }
}
