package com.heaven7.android.log_server.demo;

import com.heaven7.android.log_server.demo.sample.LogLookOverActivity;

import java.util.List;

/**
 * Created by heaven7 on 2016/5/25.
 */
public class MainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(LogLookOverActivity.class, "test Read Log with filter"));
    }
}
