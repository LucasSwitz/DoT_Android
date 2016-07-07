package com.iot.switzer.iotdormkitkat.ui.contollers;

import android.content.Context;
import android.view.View;
import android.widget.Switch;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTUIController;

/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public class IoTSubscriptionEntryBooleanController extends IoTUIController {

    private boolean value;
    private Switch aSwitch;

    public IoTSubscriptionEntryBooleanController(Context context, IoTSubscriptionEntry entry) {
        super(context,entry);
        aSwitch = new Switch(context);
        aSwitch.setChecked(false);

        disable();
    }
    @Override
    public void postValue() {
        aSwitch.setChecked(value);
    }

    @Override
    public View getView() {
        return aSwitch;
    }

    @Override
    public void onValueUpdate(IoTSubscriptionEntry entry) {
        this.value = entry.getValAsBool();
    }
}
