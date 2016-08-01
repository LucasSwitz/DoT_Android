package com.iot.switzer.iotdormkitkat.data;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;

import java.util.List;

/**
 * Created by Administrator on 6/20/2016.
 */

//put a listener on this class
public interface IoTSubscriber {
    void onSubscriptionUpdate(IoTSubscriptionEntry entry);

    List<SubscriptionDescription> getSubscriptions();
}