package com.iot.switzer.iotdormkitkat.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.devices.IoTBluetoothDeviceController;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.iot.switzer.iotdormkitkat.network.IoTManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

interface HandshakeListener {
    void onHandshakeData(byte[] data);
}

/**
 * Created by Administrator on 6/20/2016.
 */
public class DeviceDiscoveryService extends Service implements Runnable {
    public static final String PARAM_IN_MSG = "com.iot.switzer.dormiot.param_n_msg";
    private static final int HANDSHAKE_TIMEOUT = 5;
    private boolean finding = false;
    private int discoveryPeriod = 30;

    private BluetoothAdapter bthAdapter;

    public DeviceDiscoveryService() {
        Log.d("BLUETOOTH", "Constructed Service!");
        bthAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bthAdapter != null)
            Log.d("BLUETOOTH", "Adapter is alive");
        else
            Log.d("BLUETOOTH", "Adapter is nonexistent");
    }

    public void find() {
        long start = System.currentTimeMillis();
        Log.d("DISCOVERY", "Started find for: " + String.valueOf(discoveryPeriod) + " seconds.");
        finding = true;
        while (finding && ((System.currentTimeMillis() - start) / 1000) < discoveryPeriod) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Set<BluetoothDevice> pairedDevices = bthAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    boolean match = false;
                    for (IoTDeviceController controller : IoTManager.getInstance().getLiveDevices()) {
                        match = controller.getDeviceDescription().identifer.equals(device.getAddress());
                        if (match) {
                            break;
                        }
                    }
                    if (!match) {
                        HandshakeService s = new HandshakeService(device, HANDSHAKE_TIMEOUT);
                        new Thread(s).start();
                    }
                }
            }
        }
        this.stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DISCOVERY", "On Create!");
    }

    @Override
    public void onDestroy() {
        Log.d("DISCOVERY", "On Destroy!");
        super.onDestroy();
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DISCOVERY", "On Start Command.");
        new Thread(this).start();
        return START_STICKY;
    }

    private void stop() {
        finding = false;
    }

    @Override
    public void run() {
        Log.d("DISCOVERY", "Starting find!");
        if (!bthAdapter.isEnabled()) {
            bthAdapter.enable();
        }

        if (bthAdapter.isEnabled()) {
            Log.d("DISCOVERY", "Adapter is enabled!");
        }
        find();
    }

}

class HandshakeService implements Runnable, HandshakeListener {
    int timeout;
    private BluetoothDevice device;
    private BluetoothSocket socket;

    public HandshakeService(BluetoothDevice device, int timeout) {
        this.timeout = timeout;
        this.device = device;
    }

    @Override
    public void run() {
        attemptHandshake();
    }

    private void attemptHandshake() {
        try {
            Log.d("DISCOVERY", "Attemping to Handshake: " + device.getAddress());
            socket = device.createRfcommSocketToServiceRecord(IoTBluetoothDeviceController.DEFAULT_UUID);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                if (!socket.isConnected())
                    Log.d("DISCOVERY", device.getAddress() + "Socket was not connected..connecting...");
                socket.connect();
                Log.d("DISCOVERY", device.getName() + " :Device successfully opened socket!");
            } catch (IOException e) {
                Log.d(device.getAddress(), "Error Connecting!");
            }
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            HandshakeServiceReciever handshakeService = new HandshakeServiceReciever(is);
            handshakeService.addListener(this);

            Thread handshakeThread = new Thread(handshakeService);
            handshakeThread.start();

            try {
                os.write(IoTDeviceController.HANDSHAKE_REQUEST);
            } catch (IOException e) {
                Log.d("DISCOVERY", device.getAddress() + ": Unabled to write handshake.");
            }

            double start = System.currentTimeMillis();
            double duration;
            while (!handshakeService.success()) {
                duration = (System.currentTimeMillis() - start) / 1000;

                if (duration > timeout) {
                    handshakeThread.interrupt();
                    break;
                }
            }
            if (!handshakeService.success()) {
                Log.d("DISCOVERY", device.getAddress() + " :Device did not handshake in given time");
                socket.close();
                handshakeThread.interrupt();
            } else {
                Log.d("DISCOVERY", device.getAddress() + " :Device hand shook!");
                if (handshakeThread.isAlive())
                    handshakeThread.interrupt();
            }
        } catch (IOException e) {
            Log.d("DISCOVERY", device.getAddress() + " :Device hand shake failed with error!");
        }
    }

    @Override
    public void onHandshakeData(byte[] data) {
        IoTDeviceController.DeviceDescription desc = new IoTDeviceController.DeviceDescription();
        desc.identifer = device.getAddress();

        /**
         Handshake data indexes:
         0 - Handshake Header
         1 - Token
         2 - HeartbeatInterval
         3..n-1 = subscription keys*/

        int descIndex = 0;
        int bufIndex = 0;
        byte buf[] = new byte[1024];
        String s = "";
        SubscriptionDescription subDesc = new SubscriptionDescription();

        for (int i = 0; i < data.length; i++) {
            byte c = data[i];
            switch (c) {
                case 10:
                    break;
                case 13:
                    Log.d("DISCOVERY", "Adding Device with token,address: " + desc.token + ',' + desc.identifer);
                    try {
                        OutputStream os = socket.getOutputStream();
                        os.write(IoTDeviceController.HANDSHAKE_RETURN);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    IoTManager.getInstance().addDevice(new IoTBluetoothDeviceController(desc, device, socket));
                    break;
                case IoTDeviceController.UNI_DELIM:
                    switch (descIndex) {
                        case 0:
                            //we wont save the header
                            break;
                        case 1:
                            s = "";
                            for (int k = 0; k < bufIndex; k++) {
                                s += (char) buf[k];
                            }
                            desc.token = s;
                            break;
                        case 2:
                            desc.heartbeatInterval = buf[0] << 24 | buf[1] << 16 | buf[2] << 8 | buf[3];
                            break;
                        case 3:
                        default:
                            if ((descIndex - 2) % 4 == 1) {
                                s = "";
                                for (int k = 0; k < bufIndex; k++) {
                                    s += (char) buf[k];
                                }
                                subDesc.key = s;
                                Log.d("DEVICE", "Sub Desc Key: " + s);
                            } else if ((descIndex - 2) % 4 == 2) {
                                subDesc.type = SubscriptionDescription.SubscriptionType.fromInt(buf[0]);
                                Log.d("DEVICE", "Type: " + subDesc.type.name());
                            } else if ((descIndex - 2) % 4 == 3) {
                                subDesc.lowLimit = buf[0] & 0xFF;
                                Log.d("DEVICE", "Low: " + String.valueOf(subDesc.lowLimit));
                            } else if ((descIndex - 2) % 4 == 0) {
                                subDesc.highLimit = buf[0] & 0xFF;
                                Log.d("DEVICE", "High: " + String.valueOf(subDesc.highLimit));

                                desc.subscriptionDescriptions.add(subDesc);
                                subDesc = new SubscriptionDescription();
                            }
                    }
                    bufIndex = 0;
                    descIndex++;
                    break;
                default:
                    buf[bufIndex] = c;
                    bufIndex++;
            }
        }
    }

    class HandshakeServiceReciever implements Runnable {
        boolean hasHeader = false;
        boolean hasTail = false;
        byte packet[];
        int packetSize = 0;
        private InputStream is;
        private HandshakeListener listener;

        HandshakeServiceReciever(InputStream is) {
            this.is = new BufferedInputStream(is);
            packet = new byte[1024];
        }

        void addListener(HandshakeListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] in = new byte[1024];
            int numOfBytes = 0;
            try {
                numOfBytes = is.read(in);
                String test = "";
                for (int i = 0; i < numOfBytes; i++) {
                    packet[packetSize] = in[i];
                    packetSize++;
                }
                for (int i = 0; i < packetSize; i++) {
                    test += (char) packet[i];
                }
                Log.d("PACKET", "Packet: " + test);

                if (in[0] == IoTDeviceController.HANDSHAKE_RETURN) {
                    hasHeader = true;
                }
                if (in[numOfBytes - 1] != (char) 13) {
                    run();
                } else {
                    hasTail = true;
                    listener.onHandshakeData(packet);
                }
            } catch (IOException e) {
                Log.d("DISCOVERY", "Failure Connecting to: " + device.getAddress());
            }
        }

        public boolean success() {
            return hasHeader && hasTail;
        }
    }
}
