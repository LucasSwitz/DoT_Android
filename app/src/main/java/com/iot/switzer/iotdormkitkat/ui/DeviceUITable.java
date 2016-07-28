package com.iot.switzer.iotdormkitkat.ui;

import android.app.Activity;
import android.content.Context;

import android.widget.TableLayout;


import java.util.HashMap;

/**
 * Created by Lucas Switzer on 7/7/2016.
 */
public class DeviceUITable extends TableLayout{
    private HashMap<String, DeviceBlock> deviceBlocks;
    private Activity mainThreadActivity;

    public DeviceUITable(Context context, Activity a)
    {
        super(context);
        this.setColumnStretchable(0,true);
        deviceBlocks = new HashMap<>();
        mainThreadActivity = a;
    }

    public void add(final DeviceBlock db)
    {
        mainThreadActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addView(db);
            }
        });
    }
}

