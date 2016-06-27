package com.iot.switzer.iotdormkitkat.devices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTBluetoothDeviceController extends IoTDeviceController implements AsyncBluetoothReaderListener {
    private BluetoothDevice bthDevice;
    private BluetoothSocket socket;
    private OutputStream os;
    AsyncBluetoothReader reader;
    private boolean ready = false;

    public static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public IoTBluetoothDeviceController(IoTDeviceController.DeviceDescription desc, BluetoothDevice bthDevice,BluetoothSocket socket) {
        super(desc);
        Log.d("CHECK","HERE:"+getDeviceDescription().identifer);
        this.socket = socket;
        this.bthDevice = bthDevice;

        try {
            if (!socket.isConnected()) {

                socket.connect();
                Log.d(getDeviceDescription().token,"Connected:"+getDeviceDescription().identifer);
            }
            else
            {
                Log.d(getDeviceDescription().token,"Already Connected:"+getDeviceDescription().identifer);
            }
            os = socket.getOutputStream();
            reader = new AsyncBluetoothReader(socket.getInputStream());
            reader.setListener(this);
            (new Thread(reader)).start();
        }
                catch (IOException e1) {
                    Log.d("DEVICE","Not Connected:"+getDeviceDescription().identifer);
                e1.printStackTrace();
            }
    }

    @Override
    public void write(byte[] out) throws IOException {
        if (socket.isConnected())
            os.write(out);
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


    public boolean isReady()
    {
        return ready;
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        try {
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

        switch(data[0] / 16)
        {
            case HEARTBEAT_HEADER:
                break;
            case SUBSCRIPTION_HEADER:
                switch(data[0]) {
                    case SUBSCRIPTION_UPDATE:
                        Log.d(getDeviceDescription().identifer, "Subscription Update!");
                        signalSubscriptionChange(parseSubscriptionUpdate(data));
                        break;
                }
                break;
        }
    }
    IoTSubscriptionEntry parseSubscriptionUpdate(byte[] data) {
        String key = "";
        byte val[] = null;
        byte buf[] = new byte[256];
        int buf_index = 0;
        int elementIndex = 0;

        Log.d("PACKET", "Data length: " + String.valueOf(data.length));
        for (int i = 1; i < data.length; i++) {
            byte c = data[i];
            Log.d("PACKET", "Data byte: " + String.valueOf(c));
            switch (c) {
                case UNI_DELIM:
                    switch (elementIndex) {
                        case 0:
                            //ignore header delim
                            break;
                        case 1:
                            for (int k = 0; k < buf_index; k++) {
                                key += (char) buf[k];
                            }
                            Log.d("PACKET", "key Value: " + key);
                            break;
                    }
                    elementIndex++;
                    buf_index = 0;
                    break;
                case (char) 10:
                    val = new byte[buf_index];
                    for (int k = 0; k < buf_index; k++)
                    {
                        val[k] = buf[k];
                    }

                    for (byte b : val) {
                        Log.d("PACKET", "Val: " + String.valueOf(b));
                    }
                    break;
                default:
                    buf[buf_index] = c;
                    buf_index++;
            }
            if ((char) c == (char) 10)
                break;
        }
        return (new IoTSubscriptionEntry(key, val));
    }
}

interface AsyncBluetoothReaderListener
{
    void onPacket(byte[] data);
}

class AsyncBluetoothReader implements Runnable
{
    InputStream is;
    byte packet[];
    int packetSize = 0;
    boolean reading = true;
    AsyncBluetoothReaderListener listener;

    public AsyncBluetoothReader(InputStream is)
    {
        this.is = is;
        packet = new byte[512];
    }

    public void setListener(AsyncBluetoothReaderListener listener)
    {
        this.listener = listener;
    }

    public void stopRead()
    {
        reading = false;
    }

    @Override
    public void run() {
        if (reading) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("DEVICE", "Started Listen...");
            byte[] in = new byte[512];
            int numOfBytes = 0;
            try {
                numOfBytes = is.read(in);

                for (int i = 0; i < numOfBytes; i++) {
                    packet[packetSize] = in[i];
                    packetSize++;
                }
                String s = "";
                for (int i = 0; i < packetSize; i++) {
                    s += (char) packet[i];
                }
                Log.d("PACKET", s);
                if (in[numOfBytes - 1] == (char) 13) {
                    Log.d("PACKET", "Sending packet to be parsed..");
                    listener.onPacket(packet);
                    packet = new byte[256];
                    packetSize = 0;

                }
                run();
            } catch (IOException e) {
                Log.d("BAD", "Something has gone horribly wrong..");
                e.printStackTrace();
            }
        }
    }
}