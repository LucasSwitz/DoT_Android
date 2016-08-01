package com.iot.switzer.iotdormkitkat.network;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceListener;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTManager implements IoTDeviceListener {
    public static final int STATE_OFF = 0;
    public static final int STATE_DISCOVERY = 1;
    public static final int STATE_LIVE = 2;
    static private IoTManager instance = null;
    private HashMap<String, IoTDeviceController> devices;
    private ArrayList<IoTNetworkListener> listeners;
    private int currentState;


    private IoTManager() {
        devices = new HashMap<>();
        listeners = new ArrayList<>();
    }

    static public IoTManager getInstance() {
        if (instance == null) {
            instance = new IoTManager();
        }
        return instance;
    }

    public void addListener(IoTNetworkListener listener) {
        listeners.add(listener);
    }

    public void addDevice(IoTDeviceController device) {
        _addDevice(device);
        signalDeviceAdd(device);

        if (currentState != STATE_LIVE && currentState != STATE_DISCOVERY)
            changeState(STATE_LIVE);
    }

    private void _addDevice(IoTDeviceController device) {
        devices.put(device.getToken(), device);
        device.addListener(this);
        IoTVariablesBase.getInstance().addSubscriber(device);
    }

    protected void signalDeviceAdd(IoTDeviceController d) {
        for (IoTNetworkListener l : listeners) {
            l.onDeviceAdd(d);
        }
    }

    public Collection<IoTDeviceController> getLiveDevices() {
        return devices.values();
    }

    protected void signalNetworkStateChange(IoTNetworkStateData data) {
        for (IoTNetworkListener l : listeners) {
            l.onNetworkStateChange(data);
        }
    }

    public void searchForDevices(Activity a) {
        if (currentState != STATE_DISCOVERY) {
            startDiscoveryService(a);
            changeState(STATE_DISCOVERY);
            startDiscoveryProgressUpdate();
        }
    }

    private void startDiscoveryProgressUpdate() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                long duration = 0;
                while (duration < DeviceDiscoveryService.discoveryPeriod) {
                    try {
                        //update at 10hz
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    duration = (System.currentTimeMillis() - start) / 1000;

                    int progress = (int) (((double) duration / (double) DeviceDiscoveryService.discoveryPeriod) * 100);
                    postDiscoverState(progress);
                }
                postState();
            }
        })).start();
    }

    public void postState() {
        if (devices.size() > 0) {
            changeState(STATE_LIVE);
        } else {
            changeState(STATE_OFF);
        }
    }

    private void postDiscoverState(int progress) {
        signalNetworkStateChange(new IoTNetworkStateData(1, progress, "Discovering..."));
    }

    private void postLiveState() {
        Log.d("NETWORK", "Network state changed to: STATE_LIVE");
        signalNetworkStateChange(new IoTNetworkStateData(2, 100, "Network Live"));
    }

    private void postOffState() {
        Log.d("NETWORK", "Network state changed to: STATE_OFF");
        signalNetworkStateChange(new IoTNetworkStateData(0, 0, "Network Off"));
    }

    private void changeState(int state) {
        if (currentState != state) {
            switch (state) {
                case STATE_OFF:
                    postOffState();
                    break;
                case STATE_DISCOVERY:
                    postDiscoverState(0);
                    break;
                case STATE_LIVE:
                    postLiveState();
                    break;
                default:
            }
            currentState = state;
        }
    }

    private void startDiscoveryService(Activity a) {
        Intent msgIntent = new Intent(a.getApplicationContext(), DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        a.startService(msgIntent);
    }

    public void destroy() {
        for (IoTDeviceController deviceController : devices.values()) {
            deviceController.stop();
        }
    }

    @Override
    public void onDisconnect(IoTDeviceController d) {

    }

    @Override
    public void onConnect(IoTDeviceController d) {

    }


    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry e) {
        IoTVariablesBase.getInstance().get(e.getKey()).update(e);
    }
}
