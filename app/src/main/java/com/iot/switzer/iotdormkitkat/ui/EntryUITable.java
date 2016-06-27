package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.IoTObserver;

import java.util.HashMap;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class EntryUITable extends TableLayout implements IoTObserver {
    private HashMap<String, EntryRow> rows;
    private Handler tableUpdateHandler;
    public static final int UPDATE_ROW = 1;
    public static final int ADD_ROW = 0;
    private static EntryUITable instance;

    public EntryUITable(Context context) {
        super(context);
        instance = this;
        this.setColumnStretchable(1,true);
        rows = new HashMap<>();

        this.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry)
    {
        Log.d("TABLE","Sub Update");
        TableRowUpdate update = new TableRowUpdate();
        update.entry = entry;
        update.table = this;
        if(rows.get(entry.getKey()) == null)
        {
            update.handleMessage(ADD_ROW);
        }
        update.handleMessage(UPDATE_ROW);
    }

    public void addRow(String key)
    {
        EntryRow row = new EntryRow(getContext(),key);
        rows.put(key,row);
        Log.d("TABLE","Adding Row: "+key);
        this.addView(row);
    }

    public void updateRow(String key, String val)
    {
        Log.d("TABLE","Updating Row: "+key);
        rows.get(key).setValue(val);
    }
}

