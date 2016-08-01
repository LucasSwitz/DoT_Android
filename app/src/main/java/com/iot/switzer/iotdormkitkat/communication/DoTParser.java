package com.iot.switzer.iotdormkitkat.communication;

/**
 * Created by Lucas on 7/31/2016.
 */
public interface DoTParser {
    DoTPacket parse(byte[] data);
}
