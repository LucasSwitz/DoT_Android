package com.iot.switzer.iotdormkitkat.devices;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;

/**
 * Created by Administrator on 6/20/2016.
 */
public interface IoTDeviceListener {
    void onVariablesUpdate(IoTSubscriptionEntry entry);

    void onDisconnect(IoTDeviceController d);

    void onConnect(IoTDeviceController d);
}


