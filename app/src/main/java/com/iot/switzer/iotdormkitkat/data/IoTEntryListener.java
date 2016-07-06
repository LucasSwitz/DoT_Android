package com.iot.switzer.iotdormkitkat.data;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public interface IoTEntryListener {
    void onEntryChange(IoTSubscriptionEntry e);
}
