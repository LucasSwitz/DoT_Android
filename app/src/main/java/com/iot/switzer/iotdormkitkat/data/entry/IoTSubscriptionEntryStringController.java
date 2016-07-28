package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTUIController;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class IoTSubscriptionEntryStringController extends IoTUIController {
    private String value;
    private EditText editText;


    public IoTSubscriptionEntryStringController(Context context,IoTSubscriptionEntry e) {
        super(context,e);
        editText = new EditText(context);
        disable();
    }

    @Override
    public void postValue() {
        editText.setText(value);
    }

    @Override
    public View getView() {
        return editText;
    }

    @Override
    public void onValueUpdate(IoTSubscriptionEntry entry) {
        value = entry.getValAsString();
    }

}
