package com.iot.switzer.iotdormkitkat.data.entry;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.IoTEntryListener;
import com.iot.switzer.iotdormkitkat.data.IoTObserver;
import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas Switzer on 6/20/2016.
 */
public class IoTVariablesBase extends IoTTableModel implements IoTEntryListener {
    public static final byte DEFAULT_VALUE = 0;
    static private IoTVariablesBase instance;
    private HashMap<String, ArrayList<IoTSubscriber>> subscriptions;
    private ArrayList<IoTObserver> observers;

    private IoTVariablesBase() {

        subscriptions = new HashMap<>();
        observers = new ArrayList<>();
    }

    static public IoTVariablesBase getInstance() {
        if (instance == null) {
            instance = new IoTVariablesBase();
        }
        return instance;
    }

    @Override
    public IoTSubscriptionEntry get(Object o) {
        if (super.get(o) == null) {
            addEntry(new IoTSubscriptionEntry((String) o, new byte[]{}));
        }
        return super.get(o);
    }

    private void addEntry(IoTSubscriptionEntry entry) {
        Log.d("DATABASE", "Entry Added: " + entry.getKey() + "," + entry.getVal() + "," + entry.getType().name());
        entry.setListener(this);
        put(entry.getKey(), entry);
    }

    public void removeSubscriber(String key, IoTSubscriber t) {
        for (int i = 0; i < subscriptions.get(key).size(); i++) {
            if (subscriptions.get(key).get(i) == t) {
                subscriptions.get(key).remove(i);
            }
        }
    }

    public void addSubscriber(IoTSubscriber subscriber) {

        Log.d("DATABASE", "Adding Subscriber");
        for (SubscriptionDescription d : subscriber.getSubscriptions()) {

            /**
             * If subscription doesn't exist init its index
             * */
            if (subscriptions.get(d.getKey()) == null) {
                subscriptions.put(d.getKey(), new ArrayList<IoTSubscriber>());
            }

            Log.d("DATABASE", "Adding subscriber to: " + d.getKey());
            subscriptions.get(d.getKey()).add(subscriber);

            /**
             *  If subscription entry does not exists yet, add it with default value.
             */
            if (super.get(d.getKey()) == null) {
                Log.d("DATABASE", "Adding New Entry based on Sub: " + d.getKey());
                addEntry(new IoTSubscriptionEntry(d, new byte[]{DEFAULT_VALUE}));
            } else {
                /**
                 * If a subscriber further describes and entry  past the default, update the entry.
                 */
                get(d.getKey()).update(d);
                updateObservers(d.getKey());
            }
            /**
             * Update the subscriber with the current value of the entry
             * */
            subscriber.onSubscriptionUpdate(get(d.getKey()));
        }
    }

    public void updateSubscription(String key, IoTSubscriber s) {
        if (subscriptions.get(key) != null) {
            subscriptions.get(key).add(s);
        }
    }

    public void addObserver(IoTObserver observer) {
        Log.d("DATABASE", "Added Observer.");
        observers.add(observer);

        /**
         * Update observers with all the current entry values
         * */
        for (IoTSubscriptionEntry e : values()) {
            observer.onSubscriptionUpdate(e);
        }
    }

    protected void updateAll(String key) {

        Log.d("DATABASE", "Updating all Subscribers of: " + key);
        updateRegularSubscribers(key);
        updateObservers(key);
    }

    private void updateRegularSubscribers(String key) {
        if (subscriptions.get(key) != null) {
            for (IoTSubscriber subscriber : subscriptions.get(key))
                subscriber.onSubscriptionUpdate(get(key));
        }
    }

    private void updateObservers(String key) {
        for (IoTObserver subscriber : observers)
            subscriber.onSubscriptionUpdate(get(key));
    }

    @Override
    public void onEntryChange(IoTSubscriptionEntry e) {
        Log.d("DATABASE:", "onEntryChange() " + e.getKey());
        updateAll(e.getKey());
    }
}

/**
 * This Class needs rethought in general.
 */
/*class InterruptManager extends HashMap<String,InterruptTicket> implements IoTEntryMaster
{
    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        //If the value equals true
        if(entry.getVal()[0] == 1)
        {
            InterruptTicket t = get(entry.getKey());
           // IoTVariablesBase.getInstance().get(t.entryKey).unlock();
            IoTVariablesBase.getInstance().get(t.entryKey).setVal(t.lastValue);
            IoTVariablesBase.getInstance().updateAll(t.entryKey);
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

    @Override
    public void enablePreset() {

    }

    @Override
    public void disablePreset() {

    }
}*/

class IoTTableModel extends HashMap<String, IoTSubscriptionEntry> {
    public byte[] asPacket() {
        return null;
    }

    ;
}

class InterruptTicket {
    public String resumeKey;
    public String entryKey;
    public byte[] lastValue;

    InterruptTicket(String entryKey, byte[] newValue, String resumeKey) {
        this.lastValue = IoTVariablesBase.getInstance().get(entryKey).getVal();
        this.entryKey = entryKey;
        this.resumeKey = resumeKey;

        IoTVariablesBase.getInstance().get(entryKey).setVal(newValue);
        //IoTVariablesBase.getInstance().get(entryKey).lock();

    }
}
