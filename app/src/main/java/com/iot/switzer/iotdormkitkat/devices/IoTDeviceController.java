package com.iot.switzer.iotdormkitkat.devices;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 6/20/2016.
 */
public abstract class IoTDeviceController implements IoTSubscriber {
    public static final byte HEARTBEAT_HEADER = 0;
    public static final byte HEARTBEAT_REQUEST = 0x01;
    public static final byte HEARTBEAT_RETURN = 0x02;

    public static final byte HANDSHAKE_HEADER = 1;
    public static final byte HANDSHAKE_REQUEST = 0x11;
    public static final byte HANDSHAKE_RETURN = 0x12;

    public static final byte SUBSCRIPTION_HEADER = 3;
    public static final byte SUBSCRIPTION_UPDATE = 0x31;

    public static final byte UNI_DELIM = ',';

    private ArrayList<IoTDeviceListener> listeners;
    private DeviceDescription description;

    public IoTDeviceController(String identifer, String token, int heartbeatInterval, List<SubscriptionDescription> subscriptions) {
        this(new DeviceDescription(identifer, token,heartbeatInterval,subscriptions));
    }

    public IoTDeviceController(DeviceDescription d) {
        description = d;
        listeners = new ArrayList<>();
    }

    public void addListener(IoTDeviceListener listener) {
        listeners.add(listener);
    }

    protected void writeSubscriptionUpdate(String key, byte[] val) throws IOException {

        byte out[] = new byte[key.length() + val.length +1];
        int i =0;
        for(i =0; i < key.length(); i++)
        {
            out[i] = (byte)key.charAt(i);
        }

        out[i] = UNI_DELIM;
        i++;

        for(int k = 0; k < val.length; k++)
        {
            out[i+k] = val[k];
        }

        write(packet(SUBSCRIPTION_UPDATE, out));
    }

    protected void writeSubscriptionUpdate(IoTSubscriptionEntry e) throws IOException {
        writeSubscriptionUpdate(e.getKey(), e.getVal());
    }

    static public byte[] packet(byte header, byte[] data) {
        byte[] out = new byte[data.length + 3];
        out[0] = header;

        int packetIndex = 1;
        for (int i = 0; i < data.length;i++) {
            out[packetIndex] = data[i];
            packetIndex++;
        }
        out[packetIndex] = (char)10;
        packetIndex++;
        out[packetIndex] = (char)13;
        return out;
    }

    public DeviceDescription getDeviceDescription()
    {
        return description;
    }

    public String getToken() {
        return description.token;
    }

    public abstract void write(byte[] out) throws IOException;


    public void signalSubscriptionChange(IoTSubscriptionEntry entry)
    {
        for(IoTDeviceListener listener : listeners)
        {
            listener.onVariablesUpdate(entry);
        }
    }

    protected abstract void stopDevice();
    public void stop()
    {
        stopDevice();
    }

    public static class DeviceDescription {
        public String identifer;
        public String token;
        public int heartbeatInterval;
        public ArrayList<SubscriptionDescription> subscriptionDescriptions;

        public DeviceDescription()
        {
            subscriptionDescriptions = new ArrayList<>();
        }
        public DeviceDescription(String identifer, String token, int heartbeatInterval, List<SubscriptionDescription> subscriptionDescriptions)
        {
            this();
            this.identifer = identifer;
            this.token = token;
            this.heartbeatInterval = heartbeatInterval;
            this.subscriptionDescriptions = (ArrayList<SubscriptionDescription>) subscriptionDescriptions;

        }
    }
}
