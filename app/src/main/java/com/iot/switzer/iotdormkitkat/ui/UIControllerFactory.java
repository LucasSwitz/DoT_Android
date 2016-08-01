package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntryBooleanController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntryEnumController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntryIntegerController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntryStringController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTUIController;

/**
 * Created by Lucas on 8/1/2016.
 */
public class UIControllerFactory {

    public IoTUIController getController(Context context, IoTSubscriptionEntry entry) {
        IoTUIController controller = null;
        switch (entry.getType()) {
            case INT:
                controller =
                        new IoTSubscriptionEntryIntegerController(context, entry);
                break;
            case CHAR:
                break;
            case STRING:
                controller =
                        new IoTSubscriptionEntryStringController(context, entry);
                break;
            case BOOLEAN:
                controller =
                        new IoTSubscriptionEntryBooleanController(context, entry);
                break;

            case ENUM:
                controller =
                        new IoTSubscriptionEntryEnumController(context, entry);
                break;
            case BYTE_PTR:
                controller =
                        new IoTSubscriptionEntryStringController(context, entry);
                break;
        }
        return controller;
    }

}
