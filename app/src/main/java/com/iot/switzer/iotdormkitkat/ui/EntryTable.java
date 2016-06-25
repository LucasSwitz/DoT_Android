package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.SubscritptionDescription;
import com.iot.switzer.iotdormkitkat.devices.IoTSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class EntryTable extends TableLayout implements IoTSubscriber {
    private HashMap<String, EntryRow> rows;

    public EntryTable(Context context) {
        super(context);
        rows = new HashMap<>();
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        if(rows.get(entry.getKey()) == null)
        {

        }
    }

    public void addRow(IoTSubscriptionEntry e)
    {
        switch(e.getDescription().type)
        {
            case INT:
                rows.put(e.getKey(),new EntryRow(this));
                break;
            case CHAR:
                break;
            case STRING:
                break;
            case BYTE_PTR:
                break;
        }
    }
    @Override
    public List<SubscritptionDescription> getSubscriptions() {
        return null;
    }
}

class EntryRow<T> extends TableRow
{
    IoTSubscriptionEntry e;
    TextView title;
    TextView value;
    public EntryRow(Context context) {
        super(context);
        this.e = e;
    }

    public void setTitle()
    {

    }

    public void setValue(T value)
    {


    }
}