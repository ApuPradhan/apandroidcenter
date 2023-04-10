package com.apandroidcenter.helpers;

import android.os.Handler;

import com.cdac.hrmis.base.interfaces.DateTimeListener;

import java.util.Date;

public class DateTimeUpdater implements Runnable {
    private static final int UPDATE_INTERVAL = 1000; // update time every 1 second

    private final Handler handler;
    private final DateTimeListener listener;

    public DateTimeUpdater(DateTimeListener listener) {
        handler = new Handler();
        this.listener = listener;
    }

    public void start() {
        handler.postDelayed(this, UPDATE_INTERVAL);
    }

    public void stop() {
        handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        Date date = new Date();
        listener.onDateTimeUpdated(date);
        handler.postDelayed(this, UPDATE_INTERVAL);
    }
}
