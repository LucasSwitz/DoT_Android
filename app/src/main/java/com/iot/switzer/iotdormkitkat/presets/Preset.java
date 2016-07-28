package com.iot.switzer.iotdormkitkat.presets;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;

import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class Preset extends ArrayList<Preset.PresetEntry> {

    private String name = "";
    public static final char PRESET_DELIM = '~';
    public static final char PRESET_NAME_DELIM = '/';


    public Preset(String presetString) {
        loadFromString(presetString);
    }

    private void loadFromString(String inString)
    {
        String currentValue = "";
        SubscriptionDescription.SubscriptionType type = null;
        String entryName ="";
        byte[] value  = null;
        for(char c : inString.toCharArray())
        {
            switch (c) {
                case PRESET_NAME_DELIM:
                    if (name.isEmpty()) {
                        name = currentValue;
                        currentValue = "";
                        break;
                    }
                case PresetEntry.PRESET_ENTRY_TYPE_DELIM:
                    if (type == null) {
                        type = SubscriptionDescription.SubscriptionType.fromInt(Integer.parseInt(currentValue));
                        currentValue = "";
                        break;
                    }
                case PresetEntry.PRESET_ENTRY_VALUE_DELIM:
                    if(entryName.isEmpty()) {
                        entryName = currentValue;
                        currentValue = "";
                        break;
                    }
                case PresetEntry.PRESET_ENTRY_DELIM:
                    if(value == null) {
                        value = IoTSubscriptionEntry.bytePtrFromString(currentValue);
                        /**
                         * This should ask the variables base for this SubscriptionEntry (create it if
                         * it doest exists), then return it to our PresetEntry.
                         */
                        PresetEntry e = new PresetEntry(IoTVariablesBase.getInstance().get(entryName), value);
                        e.entry.updateType(type);
                        Log.d("PRESET","Added new entry to Preset: "+name + ","+entryName);
                        add(e);
                        currentValue = "";

                        value = null;
                        type = null;
                        entryName = "";
                        break;
                    }
                case '\r':
                case '\n':
                    break;
                default:
                    currentValue+=c;
            }
        }
    }

    public final String getName() {
        return name;
    }

    public static class PresetEntry {
        public IoTSubscriptionEntry entry;
        public byte[] value;
        public static final char PRESET_ENTRY_TYPE_DELIM = '|';
        public static final char PRESET_ENTRY_VALUE_DELIM = '=';
        public static final char PRESET_ENTRY_DELIM = '$';

        public PresetEntry(IoTSubscriptionEntry e, byte[] value) {
            this.entry = e;
            this.value = value;
        }

        public String toExportString()
        {
            String out = String.valueOf(SubscriptionDescription.SubscriptionType.asInt(entry.getDescription().type)) + PRESET_ENTRY_TYPE_DELIM
                    + entry.getKey() + PRESET_ENTRY_VALUE_DELIM
                    + String.valueOf(entry.getValueAsType())
                    + PRESET_ENTRY_DELIM;
            return out;
        }

        public static String toExportString(String key, SubscriptionDescription.SubscriptionType type,String val)
        {
            String out = String.valueOf(SubscriptionDescription.SubscriptionType.asInt(type)) + PRESET_ENTRY_TYPE_DELIM
                    + key + PRESET_ENTRY_VALUE_DELIM
                    + val
                    + PRESET_ENTRY_DELIM;

            return out;
        }
    }

}