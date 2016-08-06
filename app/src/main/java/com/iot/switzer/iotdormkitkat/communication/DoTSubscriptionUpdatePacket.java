package com.iot.switzer.iotdormkitkat.communication;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTSubscriptionUpdatePacket extends DoTPacket {
    private IoTSubscriptionEntry entry;
    private DoTPacket packet;

    public DoTSubscriptionUpdatePacket(String key, byte[] value) {
        entry = new IoTSubscriptionEntry(key, value);
    }

    public IoTSubscriptionEntry getEntry() {
        return entry;
    }
    @Override
    protected void _build() {
        setHeader(DoTPacket.SUBSCRIPTION_UPDATE);
        appendEelement(entry.getKey().getBytes());
        delim();
        appendEelement(entry.getVal());
    }
}