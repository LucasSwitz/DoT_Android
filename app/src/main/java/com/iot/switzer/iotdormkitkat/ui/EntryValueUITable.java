package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntryBooleanController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntryIntegerController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.data.entry.IoTEntryMaster;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.IoTObserver;

import java.util.HashMap;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class EntryValueUITable extends TableLayout implements IoTObserver{
    private HashMap<String, EntryRow> rows;

    public static final int ADD_ROW = 0;
    public static final int UPDATE_ROW = 1;

    private static EntryValueUITable instance;
    private Handler tableUpdateHandler;

    public EntryValueUITable(Context context) {
        super(context);
        instance = this;
        this.setColumnStretchable(1,true);
        rows = new HashMap<>();

        this.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        tableUpdateHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage) {
                Log.d("TABLE","Handling Message");

                IoTSubscriptionEntry entry = (IoTSubscriptionEntry) inputMessage.obj;

                switch (inputMessage.what)
                {
                    case EntryValueUITable.ADD_ROW:
                        Log.d("TABLE","Adding Row: "+entry.getKey());
                        switch(entry.getDescription().type)
                        {
                            case INT:
                                addRow(entry.getKey(),
                                        new IoTSubscriptionEntryIntegerController(getContext(),entry));
                                break;
                            case CHAR:
                                break;
                            case STRING:
                                break;
                            case BOOLEAN:
                                addRow(entry.getKey(),
                                        new IoTSubscriptionEntryBooleanController(getContext(),entry));
                                break;
                            case BYTE_PTR:
                                //print this as a string?
                                break;
                        }
                        break;
                    case EntryValueUITable.UPDATE_ROW:
                        Log.d("TABLE","Updating Row: "+entry.getKey());
                        rows.get(entry.getKey()).drawController();
                        break;
                }
            }
        };

    }

    public void handleUpdate(int state, IoTSubscriptionEntry e)
    {
        Log.d("TABLE","Handleing Update");
        int out = 0;
        switch(state)
        {
            case ADD_ROW:
                out = ADD_ROW;
                break;
            case UPDATE_ROW:
                out = UPDATE_ROW;
                break;
        }
        Message updateMessage = tableUpdateHandler.obtainMessage(out,e);
        updateMessage.sendToTarget();
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry)
    {
        Log.d("TABLE","UI table onSubscriptionUpdate(): "+entry.getKey());
        /*Add new row if row doesn't exist*/
        if(rows.get(entry.getKey()) == null)
        {
            this.handleUpdate(ADD_ROW,entry);
        }

        this.handleUpdate(UPDATE_ROW,entry);
    }

    public<T extends View&IoTEntryMaster>void addRow(String key,T controller)
    {
        EntryRow row = new EntryRow(getContext(),key,controller);
        IoTVariablesBase.getInstance().addSubscriber(controller);
        rows.put(key,row);
        Log.d("TABLE","Adding Row: "+key);
        this.addView(row);
    }

    public static EntryValueUITable getInstance()
    {
        return instance;
    }
}

