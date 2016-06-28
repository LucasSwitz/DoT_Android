package com.iot.switzer.iotdormkitkat.data;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 6/26/2016.
 */
public interface IoTContributorListener {
    void onSubscriptionUpdate(IoTSubscriptionEntry e);
}
