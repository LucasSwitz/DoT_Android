package com.iot.switzer.iotdormkitkat.data;

import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceListener;
import com.iot.switzer.iotdormkitkat.devices.IoTSubscriber;

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

    @Override
    public void onVariablesUpdate(IoTEntry entry) {
        IoTVariablesBase.getInstance().update(entry);
    }

    @Override
    public void onDisconnect(IoTDeviceController d) {

    }

    @Override
    public void onConnect(IoTDeviceController d) {

    }
}
