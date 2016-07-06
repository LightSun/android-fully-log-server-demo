package com.heaven7.android.log_server.demo;

import android.app.Application;

import com.heaven7.android.log.LogServer;

/**
 * Created by heaven7 on 2016/7/6.
 */
public class ServerApplication extends Application {

    private LogServer mServer;

    @Override
    public void onCreate() {
        super.onCreate();

        mServer = new LogServer(this);
    }
}
