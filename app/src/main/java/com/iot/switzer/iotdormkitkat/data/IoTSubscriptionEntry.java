package com.iot.switzer.iotdormkitkat.data;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTSubscriptionEntry {
    private SubscriptionDescription description;
    private byte[] val;
    private byte[] oldVal;
    boolean locked;

   public IoTSubscriptionEntry(SubscriptionDescription description, byte[] val) {
        this.description = description;
        this.val = val;
    }

    public IoTSubscriptionEntry(String key, byte[] val) {
        description = new SubscriptionDescription(key, SubscriptionDescription.SubscriptionType.BYTE_PTR);
        this.val = val;
    }

    protected void lock()
    {
        locked = true;
    }

    protected void unlock()
    {
        locked = false;
    }
    public String getKey() {
        return description.key;
    }

    public byte[] getVal() {
        return val;
    }

    public int getValAsInt()
    {
        switch (val.length)
        {
            case 0:
                return 0;
            case 1:
                return val[0];
            case 2:
                return val[0] << 8 | val[1];
            case 3:
                return val[0] << 16 | val[1] << 8 | val[2];
            default:
                return val[0] << 24 | val[1] << 16 | val[2] << 8 | val[3];
        }
    }

    public char getValAsChar()
    {
        return (char)val[0];
    }

    public String getValAsString()
    {
        return "CHANGEME";
    }

    public Object getValueAsType()
    {
        switch(description.type)
        {
            case INT:
                return getValAsInt();
            case CHAR:
                return getValAsChar();
            case STRING:
                return getValAsString();
            default:
                return val;
        }
    }

    public final SubscriptionDescription getDescription()
    {
        return description;
    }

    public void setVal(byte[] val) {
        if(!locked) {
            this.val = val;
        }
    }
}