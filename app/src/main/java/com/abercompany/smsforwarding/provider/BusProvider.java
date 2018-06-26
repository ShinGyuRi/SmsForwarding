package com.abercompany.smsforwarding.provider;

import org.greenrobot.eventbus.EventBus;

public final class BusProvider {
    private static final EventBus BUS = new EventBus();

    public static EventBus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
