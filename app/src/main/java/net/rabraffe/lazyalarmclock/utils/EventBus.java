package net.rabraffe.lazyalarmclock.utils;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * 的
 * Created by Neo on 2015/10/28 0028.
 */
public class EventBus {
    private static Bus ourInstance = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return ourInstance;
    }

    private EventBus() {
    }
}
