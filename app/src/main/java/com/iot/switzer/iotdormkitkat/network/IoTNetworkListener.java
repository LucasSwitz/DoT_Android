package com.iot.switzer.iotdormkitkat.network;

import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;

/**
 * Created by Lucas Switzer on 7/7/2016.
 */
public interface IoTNetworkListener {

    void onDeviceAdd(IoTDeviceController d);

    void onNetworkStateChange(IoTNetworkStateData data);
}


