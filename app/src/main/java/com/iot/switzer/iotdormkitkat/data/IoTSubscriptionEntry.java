package com.iot.switzer.iotdormkitkat.data;

/**
 * Created by Administrator on 6/20/2016.
 */
public class IoTSubscriptionEntry {
    private SubscritptionDescription description;
    private byte[] val;
    private byte[] oldVal;
    boolean locked;

   public IoTSubscriptionEntry(SubscritptionDescription description, byte[] val) {
        this.description = description;
        this.val = val;
    }

    public IoTSubscriptionEntry(String key, byte[] val) {
        description = new SubscritptionDescription(key,SubscritptionDescription.SubscriptionType.BYTE_PTR);
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

    public final SubscritptionDescription getDescription()
    {
        return description;
    }

    public void setVal(byte[] val) {
        if(!locked) {
            this.val = val;
        }
    }
}

interface IoTEntryListener
{

}