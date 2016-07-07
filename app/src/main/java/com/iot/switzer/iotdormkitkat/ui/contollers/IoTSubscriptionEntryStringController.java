package com.iot.switzer.iotdormkitkat.ui.contollers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTUIController;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class IoTSubscriptionEntryStringController extends IoTUIController {
    private String value;
    private TextView textView;


    public IoTSubscriptionEntryStringController(Context context,IoTSubscriptionEntry e) {
        super(context,e);
        textView = new TextView(context);
        disable();
    }

    @Override
    public void postValue() {
        textView.setText(value);
    }

    @Override
    public View getView() {
        return textView;
    }

    @Override
    public void onValueUpdate(IoTSubscriptionEntry entry) {
        value = entry.getValAsString();
    }

}
