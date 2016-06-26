package com.iot.switzer.iotdormkitkat.ui;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.MainActivity;
import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class TableRowUpdate
{
    public IoTSubscriptionEntry entry;
    public EntryUITable table;

    void handleMessage(int message)
    {
        Log.d("TABLE","Constructing Update");
        MainActivity mainThread = MainActivity.getInstance();
        mainThread.handleUpdate(this,message);
    }
}
