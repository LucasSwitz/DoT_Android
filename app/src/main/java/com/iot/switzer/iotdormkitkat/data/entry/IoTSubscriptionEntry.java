package com.iot.switzer.iotdormkitkat.data.entry;

import com.iot.switzer.iotdormkitkat.data.IoTEntryListener;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;

import java.util.Arrays;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTSubscriptionEntry {
    private SubscriptionDescription description;
    private byte[] val;
    boolean locked;
    private IoTEntryListener listener;

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

    public void setListener(IoTEntryListener listener){this.listener = listener;}

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

    public boolean getValAsBool()
    {
        return (getValAsInt() != 0);
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
            case BOOLEAN:
                return getValAsBool();
            default:
                return rawBytePtrToString(val);
        }
    }


    public void update(IoTSubscriptionEntry e)
    {
        if(!locked) {
            if (getVal() == null || !(Arrays.equals(e.getVal(), getVal()))) {
                setVal(e.getVal());
            }
        }
    }

    private void signalListener()
    {
        if(listener != null)
        {
            listener.onEntryChange(this);
        }
    }

    protected void update(SubscriptionDescription d)
    {
        if(d.type != getDescription().type)
        {
            getDescription().type = d.type;
        }
    }

    public static String rawBytePtrToString(byte[] ptr)
    {
        String s = "";
        for(byte n : ptr)
        {
            s+=String.valueOf(n);
        }
        return s;
    }


    public static byte[] bytePtrFromInteger(int i )
    {
        byte convert[] = new byte[4];

        convert[0] = (byte)((i >> 24) & 0xFF);
        convert[1] =  (byte)((i >> 16) & 0xFF);
        convert[2] =  (byte)((i >> 8) & 0xFF);
        convert[3] =  (byte)(i & 0xFF);

        return convert;


    }

    public final SubscriptionDescription getDescription()
    {
        return description;
    }

    protected void setVal(byte[] val) {
            this.val = val;
            signalListener();
    }
}