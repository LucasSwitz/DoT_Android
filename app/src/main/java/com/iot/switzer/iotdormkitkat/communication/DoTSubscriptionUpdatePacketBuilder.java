package com.iot.switzer.iotdormkitkat.communication;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTSubscriptionUpdatePacketBuilder extends DoTPacketBuilder {

    public DoTSubscriptionUpdatePacket build(String key,byte[] val)
    {
        setHeader(DoTPacket.SUBSCRIPTION_UPDATE);
        appendEelement(key.getBytes());
        appendEelement(val);
        return (DoTSubscriptionUpdatePacket)pack();
    }
}
