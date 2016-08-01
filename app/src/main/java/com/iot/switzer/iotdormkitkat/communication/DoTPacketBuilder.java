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

    private void appendEelement(byte[] data)
    {
        packet.appendBytes(data);
    }

    private DoTPacket pack()
    {
        packet.close();
        return packet;
    }
}
