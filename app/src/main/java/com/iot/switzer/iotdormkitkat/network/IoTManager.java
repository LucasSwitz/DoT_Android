package com.iot.switzer.iotdormkitkat.network;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceListener;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTManager implements IoTDeviceListener {
    static private IoTManager instance = null;
    private HashMap<String, IoTDeviceController> devices;

    private IoTManager() {
        devices = new HashMap<>();
    }

    static public IoTManager getInstance() {
        if (instance == null) {
            instance = new IoTManager();
        }
        return instance;
    }

    public void addDevice(IoTDeviceController device) {
        devices.put(device.getToken(), device);
        device.addListener(this);
        IoTVariablesBase.getInstance().addSubscriber(device);
    }

    public Collection<IoTDeviceController> getLiveDevices() {
        return devices.values();
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
