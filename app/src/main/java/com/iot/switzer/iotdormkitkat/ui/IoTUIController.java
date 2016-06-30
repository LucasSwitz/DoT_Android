package com.iot.switzer.iotdormkitkat.ui;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;

/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public interface IoTUIController extends IoTSubscriber{
    void postValue();
    void enable();
    void disable();
}
