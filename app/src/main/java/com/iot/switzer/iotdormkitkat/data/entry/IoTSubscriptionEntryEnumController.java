package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.ui.IoTUIController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Switzer on 6/30/2016.
 */
public class IoTSubscriptionEntryEnumController extends Spinner implements IoTUIController {

    private IoTSubscriptionEntry entry;

    public IoTSubscriptionEntryEnumController(Context context, IoTSubscriptionEntry entry) {
        super(context);
        this.entry = entry;

        Integer enums[] = new Integer[entry.getDescription().highLimit];

        for (int i = 0; i < enums.length; i++) {
            enums[i] = i;
        }

        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, enums);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void postValue() {

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
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        entry.setVal(IoTSubscriptionEntry.bytePtrFromInteger((this.getSelectedItemPosition())));
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> d = new ArrayList<>();
        d.add(entry.getDescription());
        return d;
    }
}
