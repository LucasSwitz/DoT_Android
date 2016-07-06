package com.iot.switzer.iotdormkitkat.devices;

import com.iot.switzer.iotdormkitkat.data.IoTContributorListener;

/**
 * Created by Administrator on 6/20/2016.
 */
public interface IoTDeviceListener extends IoTContributorListener {
    void onDisconnect(IoTDeviceController d);

    void onConnect(IoTDeviceController d);
}


