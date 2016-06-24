package com.iot.switzer.iotdormkitkat.data;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.devices.IoTSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas Switzer on 6/20/2016.
 */
public class IoTVariablesBase extends IoTTableModel {
    static private IoTVariablesBase instance;
    public static final byte DEFAULT_VALUE = 0;
    private HashMap<String, ArrayList<IoTSubscriber>> subscriptions;
    private ArrayList<IoTSubscriber> universal_subscribers;

    private IoTVariablesBase() {

        subscriptions = new HashMap<>();
        universal_subscribers = new ArrayList<>();
    }

    static public IoTVariablesBase getInstance() {
        if (instance == null) {
            instance = new IoTVariablesBase();
        }
        return instance;

    }

    public void update(IoTEntry entry) {
        Log.d("DATABASE","Entry updated: "+entry.getKey() + "," +entry.getVal());
        put(entry.getKey(), entry);
        updateSubscribers(entry.getKey());
    }

    public IoTEntry get(String key) {
        return super.get(key);
    }

    public void addSubscriber(IoTSubscriber subscriber) {

        /**
         * Insert subscriber into the map that contains all of the subscriptions
         * and their subscribers.
         */
        for (String key : subscriber.getSubscritpions()) {
            if(subscriptions.get(key) == null)
            {
                subscriptions.put(key, new ArrayList<IoTSubscriber>());
            }
            Log.d("DATABASE","Adding subscriber to: "+key);
            subscriptions.get(key).add(subscriber);

            /**
             *  If subscription entry does not exists yet, add it with default value.
             */
            if(get(key) == null)
            {
                update(new IoTEntry(key,new byte[]{DEFAULT_VALUE}));
            }
        }
    }

    public void addUniversalSubscriber(IoTSubscriber subscriber)
    {
        Log.d("DATABASE","Added universal subscriber.");
        universal_subscribers.add(subscriber);
    }

    private void updateSubscribers(String key) {

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


class IoTTableModel extends HashMap<String, IoTEntry>
{
    public byte[] asPacket(){return null;};
}
