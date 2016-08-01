package com.iot.switzer.iotdormkitkat.communication;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTSubscriptionUpdateParser implements DoTParser {

    public DoTSubscriptionUpdatePacket parse(byte[] data) {
        String key = "";
        byte val[] = null;
        byte buf[] = new byte[256];
        int buf_index = 0;
        int elementIndex = 0;
        for (int i = 1; i < data.length; i++) {
            byte c = data[i];
            switch (c) {
                case DoTPacket.UNI_DELIM:
                    switch (elementIndex) {
                        case 0:
                            //ignore header delim
                            break;
                        case 1:
                            for (int k = 0; k < buf_index; k++) {
                                key += (char) buf[k];
                            }
                            break;
                    }
                    elementIndex++;
                    buf_index = 0;
                    break;
                case (char) 10:
                    val = new byte[buf_index];
                    for (int k = 0; k < buf_index; k++) {
                        val[k] = buf[k];
                    }
                    break;
                default:
                    buf[buf_index] = c;
                    buf_index++;
            }
            if ((char) c == (char) 10)
                break;
        }
        return (new DoTSubscriptionUpdatePacket(key, val));
    }
}
