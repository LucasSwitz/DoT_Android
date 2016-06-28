package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTEntryMaster;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.ui.IoTUIController;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public class IoTSubscriptionEntryIntegerController extends SeekBar implements IoTUIController,SeekBar.OnSeekBarChangeListener{
    private IoTSubscriptionEntry entry;
    int value = 0;

    public IoTSubscriptionEntryIntegerController(Context context,IoTSubscriptionEntry entry) {
        super(context);
        this.entry = entry;
        this.setOnSeekBarChangeListener(this);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setLongClickable(true);
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry)
    {
        Log.d("TABLE","Sub Update");
        value = entry.getValAsInt();
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> d = new ArrayList<>();
        d.add(entry.getDescription());
        return d;
    }

    public void postValue()
    {
        this.setProgress(value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser)
        {
            entry.setVal(IoTSubscriptionEntry.bytePtrFromInteger(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void enable() {
        setEnabled(true);
    }

    @Override
    public void disable() {
        setEnabled(false);
    }
}
