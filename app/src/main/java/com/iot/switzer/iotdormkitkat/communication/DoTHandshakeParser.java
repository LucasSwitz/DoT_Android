package com.iot.switzer.iotdormkitkat.communication;

import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;

/**
 * Created by Lucas on 7/31/2016.
 */
public class DoTHandshakeParser implements DoTParser {

    @Override
    public DoTPacket parse(byte[] data) {
        DoTHandshakePacket handshakePacket = new DoTHandshakePacket();
        /**
         Handshake data indexes:
         0 - Handshake Header
         1 - Token
         2 - HeartbeatInterval
         3..n-1 = subscriptions*/

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

                    break;
                case DoTPacket.UNI_DELIM:
                    switch (descIndex) {
                        case 0:
                            //we wont save the header
                            break;
                        case 1:
                            s = "";
                            for (int k = 0; k < bufIndex; k++) {
                                s += (char) buf[k];
                            }
                            handshakePacket.setToken(s);
                            break;
                        case 2:
                            int heartbeatInterval = buf[0] << 24 | buf[1] << 16 | buf[2] << 8 | buf[3];
                            handshakePacket.setHeartBeatInterval(heartbeatInterval);
                            break;
                        case 3:
                        default:
                            if ((descIndex - 2) % 4 == 1) {
                                s = "";
                                for (int k = 0; k < bufIndex; k++) {
                                    s += (char) buf[k];
                                }
                                subDesc.setKey(s);
                            } else if ((descIndex - 2) % 4 == 2) {
                                subDesc.setType(SubscriptionDescription.SubscriptionType.fromInt(buf[0]));
                            } else if ((descIndex - 2) % 4 == 3) {
                                subDesc.lowLimit = buf[0] & 0xFF;
                            } else if ((descIndex - 2) % 4 == 0) {
                                subDesc.highLimit = buf[0] & 0xFF;

                                handshakePacket.addSubscription(subDesc);
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
        return handshakePacket;
    }
}
