package com.iot.switzer.iotdormkitkat.data;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTEntry{
    private String key;
    private byte[] val;

   public IoTEntry(String key, byte[] val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public byte[] getVal() {
        return val;
    }
}