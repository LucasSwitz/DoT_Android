package com.iot.switzer.iotdormkitkat.data;

import android.util.Log;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class SubscriptionDescription
{
    public enum SubscriptionType {
        INT,
        CHAR,
        STRING,
        BOOLEAN,
        BYTE_PTR;

        public static SubscriptionType fromInt(int i)
        {
            Log.d("FROMINT",String.valueOf(i));
            switch (i)
            {
                case 0:
                    return INT;
                case 1:
                    return CHAR;
                case 2:
                    return STRING;
                case 3:
                    return BOOLEAN;
                default:
                    return BYTE_PTR;
            }
        }
    }
    public SubscriptionDescription(String key, SubscriptionType type)
    {
        this.type = type;
        this.key = key;
    }

    public String key;
    public SubscriptionType type;
}
