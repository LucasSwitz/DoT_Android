package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.ui.IoTUIController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class IoTSubscriptionEntryStringController extends TextView implements IoTUIController {
    private String value;
    private IoTSubscriptionEntry entry;


    public IoTSubscriptionEntryStringController(Context context,IoTSubscriptionEntry e) {
        super(context);
        this.entry = e;

        disable();
    }

    @Override
    public void enable() {
        if (!entry.isLocked()) {
            Toast.makeText(getContext(), entry.getKey() + ": Enabled User Control", Toast.LENGTH_SHORT).show();
            entry.lock();
            setEnabled(true);
        } else {
            Toast.makeText(getContext(), entry.getKey() + " is Locked!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void disable() {
        entry.unlock();
        setEnabled(false);
    }

    @Override
    public void postValue() {
        setText(value);
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        value = entry.getValAsString();
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> d = new ArrayList<>();
        d.add(entry.getDescription());
        return d;
    }
}
