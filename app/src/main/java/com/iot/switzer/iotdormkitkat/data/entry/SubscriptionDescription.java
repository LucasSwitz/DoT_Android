package com.iot.switzer.iotdormkitkat.data.entry;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class SubscriptionDescription {
    public int highLimit;
    public int lowLimit;
    private String key;
    private SubscriptionType type;

    public SubscriptionDescription(String key, SubscriptionType type, int lowLimit, int highLimit) {
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.type = type;
        this.key = key;
    }

    public SubscriptionDescription(String key, SubscriptionType type) {
        this(key, type, 0, 255);
    }

    public SubscriptionDescription() {

    }

    public final SubscriptionType getType() {
        return type;
    }

    public final String getKey() {
        return key;
    }

    public final int getHighLimit() {
        return highLimit;
    }

    public final int getLowLimit() {
        return lowLimit;
    }

    public enum SubscriptionType {
        INT,
        CHAR,
        STRING,
        BOOLEAN,
        ENUM,
        BYTE_PTR;

        public static SubscriptionType fromInt(int i) {
            switch (i) {
                case 0:
                    return INT;
                case 1:
                    return CHAR;
                case 2:
                    return STRING;
                case 3:
                    return BOOLEAN;
                case 4:
                    return ENUM;
                default:
                    return BYTE_PTR;
            }
        }

        public static int asInt(SubscriptionType t) {
            switch (t) {

                case INT:
                    return 0;
                case CHAR:
                    return 1;
                case STRING:
                    return 2;
                case BOOLEAN:
                    return 3;
                case ENUM:
                    return 4;
                default:
                    return 5;

            }
        }
    }
    public void setType(SubscriptionType type)
    {
        this.type = type;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
