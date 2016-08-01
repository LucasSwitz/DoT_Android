package com.iot.switzer.iotdormkitkat.communication;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTPacketBuilder {
    private DoTPacket packet;
    public DoTPacketBuilder()
    {
        packet = new DoTPacket();
    }

    public void setHeader(byte header)
    {
        packet.appendByte(0,header);
    }

    public void appendEelement(byte[] data)
    {
        packet.appendBytes(data);
    }

    public DoTPacket pack()
    {
        packet.close();
        return packet;
    }
}
