package com.iot.switzer.iotdormkitkat.data.entry;

import com.iot.switzer.iotdormkitkat.data.IoTContributor;
import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;

/**
 * Created by Lucas Switzer on 6/27/2016.
 */

/**
 * The only point of this interface is to give a subscriber/contributor access
 * to the protected methods of an IoTSubscriptionEntry
 */
public interface IoTEntryMaster extends IoTSubscriber {

    void enable();
    void disable();
}
