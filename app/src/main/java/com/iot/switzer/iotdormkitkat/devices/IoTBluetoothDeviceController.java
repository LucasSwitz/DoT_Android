package com.iot.switzer.iotdormkitkat.devices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.iot.switzer.iotdormkitkat.communication.DoTPacket;
import com.iot.switzer.iotdormkitkat.communication.DoTPacketParser;
import com.iot.switzer.iotdormkitkat.communication.DoTParser;
import com.iot.switzer.iotdormkitkat.communication.DoTSubscriptionUpdatePacket;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

interface AsyncBluetoothReaderListener {
    void onPacket(byte[] data);
}

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTBluetoothDeviceController extends IoTDeviceController implements AsyncBluetoothReaderListener {
    public static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    AsyncBluetoothReader reader;
    private BluetoothDevice bthDevice;
    private BluetoothSocket socket;
    private OutputStream os;
    private boolean ready = false;

    public IoTBluetoothDeviceController(IoTDeviceController.DeviceDescription desc, BluetoothDevice bthDevice, BluetoothSocket socket) {
        super(desc);
        this.socket = socket;
        this.bthDevice = bthDevice;

        try {
            connectSocket();
            startReader();
            os = socket.getOutputStream();

        } catch (IOException e) {
            Log.d("DEVICE", "Not Connected:" + getDeviceDescription().identifer,e);
        }
    }

    private void startReader() throws IOException {
        reader = new AsyncBluetoothReader(socket.getInputStream());
        reader.setListener(this);
        (new Thread(reader)).start();
    }
    private void connectSocket() throws IOException {
        if (!socket.isConnected()) {
            socket.connect();
            Log.d(getDeviceDescription().token, "Connected:" + getDeviceDescription().identifer);
        } else {
            Log.d(getDeviceDescription().token, "Already Connected:" + getDeviceDescription().identifer);
        }
    }

    @Override
    public void write(byte[] out) throws IOException {
        if (socket.isConnected())
            os.write(out);

        os.flush();
    }

    @Override
    protected void stopDevice() {
        try {
            reader.stopRead();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isReady() {
        return ready;
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        try {
            Log.d(getToken(), "Writing to device: " + entry.getKey() + "," + entry.getValAsInt());
            writeSubscriptionUpdateToDevice(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        return getDeviceDescription().subscriptionDescriptions;
    }

    @Override
    public void onPacket(byte[] data) {

        DoTParser parser = new DoTPacketParser();
        switch (data[0] / 16) {
            case HEARTBEAT_HEADER:
                break;
            case SUBSCRIPTION_HEADER:
                switch (data[0]) {
                    case SUBSCRIPTION_UPDATE:
                        Log.d(getDeviceDescription().identifer, "Subscription Update!");
                        DoTSubscriptionUpdatePacket packet = (DoTSubscriptionUpdatePacket) parser.parse(data);
                        signalSubscriptionChange(packet.getEntry());
                        break;
                }
                break;
        }
    }
}

class AsyncBluetoothReader implements Runnable {
    InputStream is;
    byte packet[];
    int packetSize = 0;
    boolean reading = true;
    AsyncBluetoothReaderListener listener;

    public AsyncBluetoothReader(InputStream is) {
        this.is = is;
        packet = new byte[1024];
    }

    public void setListener(AsyncBluetoothReaderListener listener) {
        this.listener = listener;
    }

    public void stopRead() {
        reading = false;
    }

    private void resetPacket()
    {
        packet = new byte[1024];
        packetSize = 0;
    }

    private void fillPacket(byte[] in, int numOfBytes)
    {
        for (int i = 0; i < numOfBytes; i++) {
            packet[packetSize] = in[i];
            packetSize++;
        }
    }
    private void read() {
        Log.d("DEVICE", "Started Listen...");
        byte[] in = new byte[1024];
        int numOfBytes = 0;

        try {
            numOfBytes = is.read(in);
            fillPacket(in,numOfBytes);

            if (in[numOfBytes - 1] == DoTPacket.PACKET_DELIM) {
                Log.d("PACKET", "Sending packet to be parsed..");
                listener.onPacket(packet);
                resetPacket();
            }
            run();

        } catch (IOException e) {
            Log.d("DEVICE", "Failed to read from Bluetooth device..", e);
        }
    }
    @Override
    public void run() {
        if (reading) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            read();
        }
    }
}