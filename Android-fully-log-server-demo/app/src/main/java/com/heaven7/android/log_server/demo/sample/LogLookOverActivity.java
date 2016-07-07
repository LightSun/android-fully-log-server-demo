package com.heaven7.android.log_server.demo.sample;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.heaven7.android.log.LogClient;
import com.heaven7.android.log_server.demo.BaseActivity;
import com.heaven7.android.log_server.demo.R;
import com.heaven7.core.util.TextWatcherAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * look over the log
 * Created by heaven7 on 2016/7/6.
 */
public class LogLookOverActivity extends BaseActivity {

    @InjectView(R.id.spinner_level)
    Spinner spinnerLevel;
    @InjectView(R.id.spinner_lowestLevel)
    Spinner spinnerLowestLevel;

    @InjectView(R.id.et_dir)
    TextInputEditText etDir;

    @InjectView(R.id.et_startTime)
    TextInputEditText etStartTime;
    @InjectView(R.id.et_endTime)
    TextInputEditText etEndTime;

    @InjectView(R.id.et_tag)
    TextInputEditText etTag;
    @InjectView(R.id.et_tag_prefix)
    TextInputEditText etTagPrefix;
    @InjectView(R.id.et_methodTag)
    TextInputEditText etMethodTag;
    @InjectView(R.id.et_methodTag_prefix)
    TextInputEditText etMethodTagPrefix;

    @InjectView(R.id.et_exceptionName)
    TextInputEditText etExceptionName;
    @InjectView(R.id.et_exceptionShortName)
    TextInputEditText etExceptionShortName;

    @InjectView(R.id.et_contains_content)
    TextInputEditText etContainsContent;

    private LogClient mClient;
    private int mLevel ;
    private int mLowestLevel ;

    @Override
    protected int getlayoutId() {
        return R.layout.ac_look_log;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mClient = new LogClient(this);

        setSpinners();
        setListeners();
    }

    private void setListeners() {
        etDir.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final String str = s.toString();
                if(str.length() != 0  && !new File(str).isDirectory()){
                    //TODO
                   // etDir.setError("");
                }
            }
        });
    }

    private void setSpinners() {
        List<LogLevel> list = new ArrayList<>();
        list.add(new LogLevel("Verbose",LogClient.LEVEL_VERBOSE));
        list.add(new LogLevel("Debug",LogClient.LEVEL_DEBUG));
        list.add(new LogLevel("Info",LogClient.LEVEL_INFO));
        list.add(new LogLevel("Warning",LogClient.LEVEL_WARNING));
        list.add(new LogLevel("Error",LogClient.LEVEL_ERROR));

        spinnerLevel.setAdapter(new ArrayAdapter<LogLevel>(this,android.R.layout.simple_spinner_dropdown_item,list));
        spinnerLevel.setSelection(0);
        spinnerLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogLevel item = (LogLevel) spinnerLevel.getAdapter().getItem(position);
                mLevel = item.level;
            }
        });

        spinnerLowestLevel.setAdapter(new ArrayAdapter<LogLevel>(this, android.R.layout.simple_spinner_dropdown_item, list));
        spinnerLowestLevel.setSelection(0);
        spinnerLowestLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogLevel item = (LogLevel) spinnerLevel.getAdapter().getItem(position);
                mLowestLevel = item.level;
            }
        });
    }

    @Override
    protected void onDestroy() {
        mClient.destroy();
        super.onDestroy();
    }

    @OnClick(R.id.bt_query_log)
    public void onClickQuery(View v){
        //query log
    }

    static class LogLevel{
        String name ;
        int level ;

        public LogLevel(String name, int level) {
            this.name = name;
            this.level = level;
        }
    }
}
