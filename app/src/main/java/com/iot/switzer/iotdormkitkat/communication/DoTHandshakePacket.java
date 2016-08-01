package com.iot.switzer.iotdormkitkat.communication;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;

import java.util.ArrayList;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTHandshakePacket extends DoTPacket {

    String token;
    int heartbeatInterval;
    ArrayList<SubscriptionDescription> subscriptionDescriptions;

    public DoTHandshakePacket()
    {
        subscriptionDescriptions = new ArrayList<>();
    }

    public ArrayList<SubscriptionDescription> getSubscriptionDescriptions()
    {
        return subscriptionDescriptions;
    }

    public int getHeartbeatInterval()
    {
        return heartbeatInterval;
    }

    public String getToken()
    {
        return token;
    }

    protected void setToken(String token)
    {
        this.token = token;
    }

    protected void setHeartBeatInterval(int i)
    {
        heartbeatInterval = i;
    }

    protected void addSubscription(SubscriptionDescription subscriptionDescription)
    {
        subscriptionDescriptions.add(subscriptionDescription);
    }

}
