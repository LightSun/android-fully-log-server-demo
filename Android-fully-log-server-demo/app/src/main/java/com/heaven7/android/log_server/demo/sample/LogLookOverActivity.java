package com.heaven7.android.log_server.demo.sample;

import android.Manifest;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.heaven7.android.log.IReadCallback;
import com.heaven7.android.log.LogClient;
import com.heaven7.android.log.LogFilterOptions;
import com.heaven7.android.log.LogRecord;
import com.heaven7.android.log_server.demo.BaseActivity;
import com.heaven7.android.log_server.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;
import com.heaven7.core.util.TextWatcherAdapter;
import com.heaven7.core.util.Toaster;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * look over the log
 * Created by heaven7 on 2016/7/6.
 */
public class LogLookOverActivity extends BaseActivity {

    private static final String TAG = "LogLookOverActivity";
    static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");

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
    @InjectView(R.id.bt_query_log)
    Button bt_query_log;

    private LogClient mClient;

    private boolean mDirAllowed = true;
    private LogFilterOptions mFilterOps;
    private DialogFragment mDialogFragment;

    private final IReadCallback mReadCallback = new IReadCallback.Stub() {
        @Override
        public void onResult(List<LogRecord> records) throws RemoteException {
            int size = records.size();
            Logger.i(TAG, "readLog_onResult", "size = " + size);
            if(size > 0) {
                mDialogFragment = ReadResultDialogFragment.show(getSupportFragmentManager(), records);
            }else{
                showToast("no log record.");
            }
        }
    };
    private PermissionHelper mPermissionHelper;

    @Override
    protected int getlayoutId() {
        return R.layout.ac_look_log;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mPermissionHelper = new PermissionHelper(this);
        mClient = new LogClient(this);
        mFilterOps = new LogFilterOptions();

        setSpinners();
        setListeners();
        mPermissionHelper.startRequestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{1}, new PermissionHelper.ICallback() {
            @Override
            public void onRequestPermissionResult(String requestPermission, int requestCode, boolean success) {
                bt_query_log.setEnabled(success);
                Logger.i(TAG, "mPermissionHelper_onRequestPermissionResult", "success = " + success);
                if(!success){
                    showToast(R.string.notice_request_sd_permission_failed);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setListeners() {
        etDir.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final String str = s.toString();
                if(str.length() > 0 ){
                    if(!new File(str).isDirectory()) {
                        showToast(R.string.notice_not_directory);
                        mDirAllowed = false;
                        mFilterOps.dir = null;
                    }else{
                        mDirAllowed = true;
                        mFilterOps.dir = str;
                    }
                }else{
                    mDirAllowed = true;
                    mFilterOps.dir = null;
                }
            }
        });
        etStartTime.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final String str = s.toString();
                if(s.length() > 0 ){
                    try {
                        final Date date = sFormat.parse(str);
                        mFilterOps.startTime = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mFilterOps.startTime = 0;
                        showToast(R.string.notice_invalid_start_time);
                    }
                }else{
                    mFilterOps.startTime = 0;
                }
            }
        });
        etEndTime.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final String str = s.toString();
                if(s.length() > 0 ){
                    try {
                        final Date date = sFormat.parse(str);
                        mFilterOps.endTime = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mFilterOps.endTime = 0;
                        showToast(R.string.notice_invalid_end_time);
                    }
                }else{
                    mFilterOps.endTime = 0;
                }
            }
        });
    }

    private void setSpinners() {
        List<LogLevel> list = new ArrayList<>();
        list.add(new LogLevel("None",0));
        list.add(new LogLevel("Verbose",LogClient.LEVEL_VERBOSE));
        list.add(new LogLevel("Debug",LogClient.LEVEL_DEBUG));
        list.add(new LogLevel("Info",LogClient.LEVEL_INFO));
        list.add(new LogLevel("Warning",LogClient.LEVEL_WARNING));
        list.add(new LogLevel("Error",LogClient.LEVEL_ERROR));

        final ArrayAdapter<LogLevel> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogLevel item = (LogLevel) spinnerLevel.getAdapter().getItem(position);
                mFilterOps.level = item.level;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFilterOps.level = 0;
            }
        });
        spinnerLevel.setSelection(0);

        final ArrayAdapter<LogLevel> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLowestLevel.setAdapter(adapter2);
        spinnerLowestLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogLevel item = (LogLevel) spinnerLowestLevel.getAdapter().getItem(position);
                mFilterOps.lowestLevel = item.level;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFilterOps.lowestLevel = 0;
            }
        });
        spinnerLowestLevel.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        mClient.destroy();
        if(mDialogFragment!=null) {
            mDialogFragment.dismiss();
            mDialogFragment = null;
        }
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    public void onClickAddLog(View v){
        mClient.i(TAG, "onClickAddLog", "this is a test! --->"+ new Random().nextDouble());
        Toaster.show(this, R.string.notice_log_add_ok, Gravity.CENTER);
    }

    @OnClick(R.id.bt_query_log)
    public void onClickQuery(View v){
        if(mDirAllowed){
            mFilterOps.tag = etTag.getText().toString();
            mFilterOps.tagPrefix = etTagPrefix.getText().toString();
            mFilterOps.methodTag = etMethodTag.getText().toString();
            mFilterOps.methodTagPrefix = etMethodTagPrefix.getText().toString();
            mFilterOps.exceptionName = etExceptionName.getText().toString();
            mFilterOps.exceptionShortName = etExceptionShortName.getText().toString();
            mFilterOps.content = etContainsContent.getText().toString();
            mClient.readLog(mFilterOps, mReadCallback);
        }else{
            showToast(R.string.notice_not_directory);
        }
    }


    static class LogLevel{
        String name ;
        int level ;

        public LogLevel(String name, int level) {
            this.name = name;
            this.level = level;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
