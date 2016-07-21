package com.iot.switzer.iotdormkitkat.network;

import android.app.Activity;
import android.content.Intent;

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
    static private IoTManager instance = null;
    private HashMap<String, IoTDeviceController> devices;
    private ArrayList<IoTNetworkListener> listeners;

    public enum NetworkState
    {
        SEARCHING,
        ALIVE
    }

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

    public void addListener(IoTNetworkListener listener)
    {
        listeners.add(listener);
    }

    public void addDevice(IoTDeviceController device) {
        devices.put(device.getToken(), device);
        device.addListener(this);
        IoTVariablesBase.getInstance().addSubscriber(device);

        signalDeviceAdd(device);
    }

    protected void signalDeviceAdd(IoTDeviceController d)
    {
        for(IoTNetworkListener l : listeners)
        {
            l.onDeviceAdd(d);
        }
    }

    public Collection<IoTDeviceController> getLiveDevices() {
        return devices.values();
    }

    public void searchForDevices(Activity a)
    {
        Intent msgIntent = new Intent(a, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        a.startService(msgIntent);

        setState(NetworkState.SEARCHING);
    }

    protected void setState(NetworkState state)
    {
        for(IoTNetworkListener listener : listeners)
        {
            listener.onNetworkMasterStateChange(state);
        }
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
