package net.rabraffe.lazyalarmclock.utils;

import com.squareup.otto.Bus;

/**
 * 事件总线
 * Created by Neo on 2015/10/28 0028.
 */
public class EventBus {
    private static Bus ourInstance = new Bus();

    public static Bus getInstance() {
        return ourInstance;
    }

    private EventBus() {
    }
}
