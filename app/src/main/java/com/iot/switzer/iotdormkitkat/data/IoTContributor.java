package com.iot.switzer.iotdormkitkat.data;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 6/26/2016.
 */
public interface IoTContributor {
    void signalSubscriptionChange(IoTSubscriptionEntry entry);
}
