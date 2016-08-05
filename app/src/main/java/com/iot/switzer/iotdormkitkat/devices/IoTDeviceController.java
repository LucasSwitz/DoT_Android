package com.iot.switzer.iotdormkitkat.devices;

import com.iot.switzer.iotdormkitkat.communication.DoTPacket;
import com.iot.switzer.iotdormkitkat.communication.DoTSubscriptionUpdatePacket;
import com.iot.switzer.iotdormkitkat.data.IoTContributor;
import com.iot.switzer.iotdormkitkat.data.IoTContributorListener;
import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;
import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 6/20/2016.
 */
public abstract class IoTDeviceController implements IoTSubscriber, IoTContributor {
    private ArrayList<IoTContributorListener> listeners;
    private DeviceDescription description;

    public IoTDeviceController(String identifer, String token, int heartbeatInterval, List<SubscriptionDescription> subscriptions) {
        this(new DeviceDescription(identifer, token, heartbeatInterval, subscriptions));
    }

    public IoTDeviceController(DeviceDescription d) {
        description = d;
        listeners = new ArrayList<>();
    }

    public void addListener(IoTContributorListener listener) {
        listeners.add(listener);
    }

    private void write(DoTPacket packet) throws IOException {
        write(packet.asBytes());
    }

    protected void writeSubscriptionUpdateToDevice(String key, byte[] val) throws IOException {

        DoTSubscriptionUpdatePacket packet = new DoTSubscriptionUpdatePacket(key,val);
        packet.build();
        write(packet);
    }

    protected void writeSubscriptionUpdateToDevice(IoTSubscriptionEntry e) throws IOException {
        writeSubscriptionUpdateToDevice(e.getKey(), e.getVal());
    }

    public DeviceDescription getDeviceDescription() {
        return description;
    }

    public String getToken() {
        return description.token;
    }

    public abstract void write(byte[] out) throws IOException;


    public void signalSubscriptionChange(IoTSubscriptionEntry entry) {
        for (IoTContributorListener listener : listeners) {
            listener.onSubscriptionUpdate(entry);
        }
    }

    protected abstract void stopDevice();

    public void stop() {
        stopDevice();
    }

    public static class DeviceDescription {
        public String identifer;
        public String token;
        public int heartbeatInterval;
        public ArrayList<SubscriptionDescription> subscriptionDescriptions;

        public DeviceDescription() {
            subscriptionDescriptions = new ArrayList<>();
        }

        public DeviceDescription(String identifer, String token, int heartbeatInterval, List<SubscriptionDescription> subscriptionDescriptions) {
            this();
            this.identifer = identifer;
            this.token = token;
            this.heartbeatInterval = heartbeatInterval;
            this.subscriptionDescriptions = (ArrayList<SubscriptionDescription>) subscriptionDescriptions;

        }
    }
}
