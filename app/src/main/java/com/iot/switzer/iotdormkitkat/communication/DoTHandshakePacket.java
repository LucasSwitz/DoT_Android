package com.iot.switzer.iotdormkitkat.communication;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;

import java.util.ArrayList;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTHandshakePacket extends DoTPacket {

    String token;
    int heartbeatInterval;
    ArrayList<SubscriptionDescription> subscriptionDescriptions;

    @Override
    protected void _build() {
        /*
        * TODO: Implement this if ever have need of an Android network device.
        * */
    }

    public DoTHandshakePacket() {
        subscriptionDescriptions = new ArrayList<>();
    }

    public ArrayList<SubscriptionDescription> getSubscriptionDescriptions() {
        return subscriptionDescriptions;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public String getToken() {
        return token;
    }

    protected void setToken(String token) {
        this.token = token;
    }

    protected void setHeartBeatInterval(int i) {
        heartbeatInterval = i;
    }

    protected void addSubscription(SubscriptionDescription subscriptionDescription) {
        subscriptionDescriptions.add(subscriptionDescription);
    }

}
