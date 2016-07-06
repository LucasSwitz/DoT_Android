package com.iot.switzer.iotdormkitkat.data.entry;

import com.iot.switzer.iotdormkitkat.data.IoTEntryListener;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;

import java.util.Arrays;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTSubscriptionEntry {
    boolean locked;
    private SubscriptionDescription description;
    private byte[] val;
    private byte[] lastVal;
    private IoTEntryListener listener;

    public IoTSubscriptionEntry(SubscriptionDescription description, byte[] val) {
        this.description = description;
        this.val = val;
    }

    public IoTSubscriptionEntry(String key, byte[] val) {
        description = new SubscriptionDescription(key, SubscriptionDescription.SubscriptionType.BYTE_PTR);
        this.val = val;
    }

    public static String bytePtrToString(byte[] ptr) {
        String s = "";
        for (byte n : ptr) {
            s += String.valueOf(n);
            s += ",";
        }
        /**
         * Removes the last comma
         */
        s = s.substring(0,s.length()-2);
        return s;
    }

    public static byte[] bytePtrFromInteger(int i) {
        byte convert[] = new byte[4];

        convert[0] = (byte) ((i >> 24) & 0xFF);
        convert[1] = (byte) ((i >> 16) & 0xFF);
        convert[2] = (byte) ((i >> 8) & 0xFF);
        convert[3] = (byte) (i & 0xFF);

        return convert;
    }

    public static byte[] bytePtrFromBoolean(boolean b) {
        byte convert[] = new byte[1];

        if(b)
            convert[0] = 1;
        else
            convert[0] = 0;

        return convert;
    }

    public static String stringFromBytesPtr(byte[] bytes)
    {
        String s = "";

        for(byte b : bytes)
        {
            s+=(char)b;
        }
        return s;
    }

    public static byte[] bytePtrFromString(String s)
    {
        byte[] out = new byte[s.length()];
        char[] c = s.toCharArray();
        for(int i =0; i < c.length; i++)
        {
            out[i] = (byte)c[i];
        }
        return out;
    }

    public static byte[] bytePtrFromDeliminatedString(String s)
    {
        byte[] out = new byte[s.length()];
        char[] c = s.toCharArray();
        String sOut = "";
        for(int i =0; i < c.length; i++)
        {
            if(c[i] == ',')
            {
                out[i] = Byte.parseByte(sOut);
            }

            sOut +=c;
        }
        return out;
    }

    protected void lock() {
        locked = true;
    }

    protected void unlock() {
        locked = false;
    }

    public void setListener(IoTEntryListener listener) {
        this.listener = listener;
    }

    public String getKey() {
        return description.key;
    }

    public byte[] getVal() {
        return val;
    }

    protected void setVal(byte[] val) {
        lastVal = this.val;
        this.val = val;
        signalListener();
    }

    public int getValAsInt() {
        switch (val.length) {
            case 0:
                return 0;
            case 1:
                return (val[0] & 0xFF);
            case 2:
                return (val[0] << 8 | val[1]) & 0xFF;
            case 3:
                return (val[0] << 16 | val[1] << 8 | val[2]) & 0xFF;
            default:
                return (val[0] << 24 | val[1] << 16 | val[2] << 8 | val[3]) & 0xFF;
        }
    }

    public char getValAsChar() {
        return (char) val[0];
    }

    public boolean getValAsBool() {
        return (getValAsInt() != 0);
    }

    public String getValAsString() {
        String s = "";

        for(byte b : val)
        {
            s+=(char)b;
        }
        return s;
    }

    public Object getValueAsType() {
        switch (description.type) {
            case INT:
                return getValAsInt();
            case CHAR:
                return getValAsChar();
            case STRING:
                return getValAsString();
            case BOOLEAN:
                return getValAsBool();
            default:
                return bytePtrToString(val);
        }
    }

    public void update(IoTSubscriptionEntry e) {
        if (!locked) {
            if (getVal() == null || !(Arrays.equals(e.getVal(), getVal()))) {
                setVal(e.getVal());
            }
        }
    }

    private void signalListener() {
        if (listener != null) {
            listener.onEntryChange(this);
        }
    }

    public void update(SubscriptionDescription d) {
        if (d.type != getDescription().type) {
            getDescription().type = d.type;
        }
    }

    public void updateType(SubscriptionDescription.SubscriptionType t) {
        if (t != getDescription().type) {
            getDescription().type = t;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public byte[] getLastVal() {
        return lastVal;
    }

    public final SubscriptionDescription getDescription() {
        return description;
    }
}