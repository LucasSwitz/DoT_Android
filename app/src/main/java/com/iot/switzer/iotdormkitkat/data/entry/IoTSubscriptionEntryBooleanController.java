package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.widget.Switch;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.ui.IoTUIController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public class IoTSubscriptionEntryBooleanController extends Switch implements IoTUIController {

    private IoTSubscriptionEntry entry;
    private boolean value;

    public IoTSubscriptionEntryBooleanController(Context context,IoTSubscriptionEntry entry)
    {
        super(context);
        this.entry = entry;
        this.setChecked(false);
    }

    public IoTSubscriptionEntryBooleanController(Context context) {
        super(context);
    }

    @Override
    public void postValue() {
        this.setChecked(value);
    }

    public void enable()
    {
        entry.lock();
        setEnabled(true);
    }
    public void disable()
    {
        entry.unlock();
        setEnabled(false);
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        this.value = entry.getValAsBool();
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> d = new ArrayList<>();
        d.add(entry.getDescription());
        return d;
    }
}
