package com.iot.switzer.iotdormkitkat.data;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.devices.IoTSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lucas Switzer on 6/20/2016.
 */
public class IoTVariablesBase extends IoTTableModel {
    static private IoTVariablesBase instance;
    public static final byte DEFAULT_VALUE = 0;
    private HashMap<String, ArrayList<IoTSubscriber>> subscriptions;
    private ArrayList<IoTSubscriber> universal_subscribers;
    private InterruptManager interruptManager;

    private IoTVariablesBase() {

        subscriptions = new HashMap<>();
        universal_subscribers = new ArrayList<>();
        interruptManager = new InterruptManager();
    }

    public void interruptEntry(String key, byte[] newValue, String resumeKey) {
        IoTSubscriptionEntry entry = get(key);
        if (entry != null) {
            interruptManager.put(resumeKey, new InterruptTicket(entry.getKey(), newValue, resumeKey));
        }
    }

    static public IoTVariablesBase getInstance() {
        if (instance == null) {
            instance = new IoTVariablesBase();
        }
        return instance;

    }

    public void update(IoTSubscriptionEntry entry) {

        /*Once a value is added,TYPE cannot change*/
        if (get(entry.getKey()) == null)
            addEntry(entry);
        else
            updateEntryValue(entry.getKey(), entry.getVal());
    }

    public void updateEntryValue(String key, byte[] value) {
        Log.d("DATABASE", "Entry updated: " + key + "," + value);
        get(key).setVal(value);
        updateSubscribers(key);
    }

    private void addEntry(IoTSubscriptionEntry entry) {
        Log.d("DATABASE", "Entry Added: " + entry.getKey() + "," + entry.getVal() + "," + entry.getDescription().type.name());
        put(entry.getKey(), entry);
        updateSubscribers(entry.getKey());
    }

    public IoTSubscriptionEntry get(String key) {
        return super.get(key);
    }

    public void removeSubscriber(String key, IoTSubscriber t)
    {
        for(int i =0; i < subscriptions.get(key).size(); i++)
        {
            if(subscriptions.get(key).get(i) == t)
            {
                subscriptions.get(key).remove(i);
            }
        }
    }

    public void addSubscriber(IoTSubscriber subscriber) {

        /**
         * Insert subscriber into the map that contains all of the subscriptions
         * and their subscribers.
         */
        if(subscriber.getSubscriptions().get(0).type == SubscriptionDescription.SubscriptionType.UNIVERSAL)
        {
            addUniversalSubscriber(subscriber);
        }
        else {
            for (SubscriptionDescription d : subscriber.getSubscriptions()) {
                if (subscriptions.get(d.key) == null) {
                    subscriptions.put(d.key, new ArrayList<IoTSubscriber>());
                }

                Log.d("DATABASE", "Adding subscriber to: " + d.key);
                subscriptions.get(d.key).add(subscriber);


                /**
                 *  If subscription entry does not exists yet, add it with default value.
                 */
                if (get(d.key) == null) {
                    update(new IoTSubscriptionEntry(d, new byte[]{DEFAULT_VALUE}));
                }

                /**
                 * Update the subscriber with the current value of the entry
                 * */
                subscriber.onSubscriptionUpdate(get(d.key));
            }
        }
    }

    public void updateSubscription(String key,IoTSubscriber s)
    {
        if(subscriptions.get(key) != null)
        {
            subscriptions.get(key).add(s);
        }
    }

    public void addUniversalSubscriber(IoTSubscriber subscriber)
    {
        Log.d("DATABASE","Added universal subscriber.");
        universal_subscribers.add(subscriber);

        /**
         * Update universal subscriber with all the current entry values
         * */
        for(IoTSubscriptionEntry e: values())
        {
            subscriber.onSubscriptionUpdate(e);
        }
    }

    protected void updateSubscribers(String key) {

        updateRegularSubscribers(key);
        updateUniversalSubscribers(key);
    }

    private void updateRegularSubscribers(String key)
    {
        if (subscriptions.get(key) != null) {
            for (IoTSubscriber subscriber : subscriptions.get(key))
                subscriber.onSubscriptionUpdate(get(key));
        }
    }
    private void updateUniversalSubscribers(String key)
    {
        for(IoTSubscriber subscriber: universal_subscribers)
            subscriber.onSubscriptionUpdate(get(key));
    }
}

class InterruptManager extends HashMap<String,InterruptTicket> implements IoTSubscriber
{
    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        //If the value equals true
        if(entry.getVal()[0] == 1)
        {
            InterruptTicket t = get(entry.getKey());
            IoTVariablesBase.getInstance().get(t.entryKey).unlock();
            IoTVariablesBase.getInstance().updateEntryValue(t.entryKey,t.lastValue);
            IoTVariablesBase.getInstance().updateSubscribers(t.entryKey);
            IoTVariablesBase.getInstance().removeSubscriber(t.resumeKey,this);
            remove(t.resumeKey);
        }
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        return new ArrayList<SubscriptionDescription>(){};
    }

    @Override
    public InterruptTicket put(String s,InterruptTicket t)
    {
        IoTVariablesBase.getInstance().updateSubscription(t.resumeKey,this);
        return super.put(s,t);
    }
}

class IoTTableModel extends HashMap<String, IoTSubscriptionEntry>
{
    public byte[] asPacket(){return null;};
}

class InterruptTicket
{
    public String resumeKey;
    public String entryKey;
    public byte[] lastValue;
    InterruptTicket(String entryKey,byte[] newValue,String resumeKey)
    {
        this.lastValue = IoTVariablesBase.getInstance().get(entryKey).getVal();
        this.entryKey = entryKey;
        this.resumeKey = resumeKey;

        IoTVariablesBase.getInstance().updateEntryValue(entryKey,newValue);
        IoTVariablesBase.getInstance().get(entryKey).lock();

    }
}
