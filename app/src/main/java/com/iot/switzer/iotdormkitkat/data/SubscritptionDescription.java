package com.iot.switzer.iotdormkitkat.data;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class SubscritptionDescription
{
    public enum SubscriptionType {
        INT,
        CHAR,
        STRING,
        BYTE_PTR;

        public static SubscriptionType fromInt(int i)
        {
            switch (i)
            {
                case 0:
                    return INT;
                case 1:
                    return CHAR;
                case 2:
                    return STRING;
                default:
                    return BYTE_PTR;
            }
        }
    }
    public SubscritptionDescription(String key, SubscriptionType type)
    {
        this.type = type;
        this.key = key;
    }

    public String key;
    public SubscriptionType type;
}
