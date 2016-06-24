package com.iot.switzer.iotdormkitkat.devices;

import com.iot.switzer.iotdormkitkat.data.IoTEntry;

import java.util.List;

/**
 * Created by Administrator on 6/20/2016.
 */
public interface IoTSubscriber {
    void onSubscriptionUpdate(IoTEntry entry);

    List<String> getSubscritpions();
}
