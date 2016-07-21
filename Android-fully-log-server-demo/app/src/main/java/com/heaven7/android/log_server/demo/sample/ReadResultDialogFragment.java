package com.heaven7.android.log_server.demo.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.android.log.LogRecord;
import com.heaven7.android.log_server.demo.R;
import com.heaven7.core.util.BundleHelper;
import com.heaven7.core.util.ViewHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heaven7 on 2016/7/10.
 */
public class ReadResultDialogFragment extends DialogFragment {

    private static final String TAG = "ReadResultDialogFragment";
    private static final String KEY_RESULT = "result";
    private RecyclerView mRv;

    public static DialogFragment show(FragmentManager fm, List<LogRecord> records){
        ReadResultDialogFragment df = new ReadResultDialogFragment();
        df.setArguments(new BundleHelper()
                .putParcelableList(KEY_RESULT,records)
                .getBundle());
        df.show(fm);
        return df;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        //需要用android.R.id.content这个view
        View view = inflater.inflate(R.layout.dialog_read_log_result,
                ((ViewGroup) window.findViewById(android.R.id.content)), false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //这2行,和上面的一样,注意顺序就行;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRv = (RecyclerView) view.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        ArrayList<LogRecord> records = getArguments().getParcelableArrayList(KEY_RESULT);
        List<Item> items = new ArrayList<>();
        for(LogRecord record : records){
            items.add(new Item(record));
        }
        mRv.setAdapter(new QuickRecycleViewAdapter<Item>(R.layout.item_read_log_result, items) {
            @Override
            protected void onBindData(Context context, int position, Item item,
                                      int itemLayoutId, ViewHelper helper) {
                TextView tv = helper.getView(R.id.tv_content);
                tv.setText(ENTER);
                tv.append("time: " + FORMAT.format(new Date(item.record.getTime())) + ENTER);
                tv.append("level: " + item.record.getLevel() + ENTER);
                tv.append("tag: " + item.record.getTag() + ENTER);
                tv.append("methodTag: " + item.record.getMethodTag() + ENTER);
                tv.append("exceptionName: " + item.record.getExceptionName() + ENTER);
                tv.append("message: " + item.record.getMessage() + ENTER);
            }
        });
    }

    public void show(FragmentManager fm){
        show(fm,TAG);
    }

    static final String ENTER = "\r\n";
    static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static class Item extends BaseSelector{
        LogRecord record;
        public Item(LogRecord record) {
            this.record = record;
        }
    }

}
