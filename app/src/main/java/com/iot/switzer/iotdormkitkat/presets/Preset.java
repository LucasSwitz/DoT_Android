package com.iot.switzer.iotdormkitkat.presets;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class Preset extends ArrayList<Preset.PresetEntry> {

    private String name;

    public Preset(String name, String filePath) {
        this.name = name;
        loadFromFile(filePath);
    }

    public Preset(String name, PresetEntry... configuration) {
        this.name = name;
        for (PresetEntry e : configuration) {
            add(e);
        }
    }

    public String getName() {
        return name;
    }


    protected void loadFromFile(String path) {

    }

    public static class PresetEntry {
        public IoTSubscriptionEntry entry;
        public byte[] value;

        public PresetEntry(IoTSubscriptionEntry e, byte[] value) {
            this.entry = e;
            this.value = value;
        }

        public String toExportString()
        {
            String out = entry.getKey()+','
                    + String.valueOf(SubscriptionDescription.SubscriptionType.asInt(entry.getDescription().type)) + ','
                    + String.valueOf(entry.getValueAsType())
                    + "\r\n";

            return out;
        }

        public static String toExportString(String key, SubscriptionDescription.SubscriptionType type,String val)
        {
            String out = key+','
                    + String.valueOf(SubscriptionDescription.SubscriptionType.asInt(type)) + ','
                    + val
                    + "\r\n";

            return out;
        }
    }

}