package com.iot.switzer.iotdormkitkat.presets;

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

    public Preset(String presetString) {
        loadFromString(presetString);
    }


    private void loadFromString(String inString)
    {
        String currentValue = "";
        SubscriptionDescription.SubscriptionType type = SubscriptionDescription.SubscriptionType.BYTE_PTR;
        String entryName ="";
        byte[] value;
        for(char c : inString.toCharArray())
        {
            switch (c)
            {
                case PresetEntry.PRESET_TYPE_DELIM:
                    type = SubscriptionDescription.SubscriptionType.fromInt(Integer.parseInt(currentValue));
                    currentValue = "";
                    break;
                case PresetEntry.PRESET_VALUE_DELIM:
                    entryName = currentValue;
                    currentValue = "";
                    break;
                case '\n':
                    if(name.equals(""))
                    {
                        name = currentValue;
                    }
                    else
                    {
                        value = IoTSubscriptionEntry.bytePtrFromString(currentValue);
                        /**
                         * This should ask the variables base for this SubscriptionEntry (create it if
                         * it doest exists), then return it to our PresetEntry.
                         */
                        PresetEntry e = new PresetEntry(IoTVariablesBase.getInstance().get(entryName), value);
                        e.entry.updateType(type);
                        add(e);
                    }
                    currentValue = "";
                    break;
                case '\r':
                    break;
                default:
                    currentValue+=c;
            }
        }
    }

    public String getName() {
        return name;
    }

    public static class PresetEntry {
        public IoTSubscriptionEntry entry;
        public byte[] value;
        public static final char PRESET_TYPE_DELIM = ':';
        public static final char PRESET_VALUE_DELIM = '=';

        public PresetEntry(IoTSubscriptionEntry e, byte[] value) {
            this.entry = e;
            this.value = value;
        }

        public String toExportString()
        {
            String out = String.valueOf(SubscriptionDescription.SubscriptionType.asInt(entry.getDescription().type)) + PRESET_TYPE_DELIM
                    + entry.getKey() + PRESET_VALUE_DELIM
                    + String.valueOf(entry.getValueAsType())
                    + '\n';
            return out;
        }

        public static String toExportString(String key, SubscriptionDescription.SubscriptionType type,String val)
        {
            String out = String.valueOf(SubscriptionDescription.SubscriptionType.asInt(type)) + PRESET_TYPE_DELIM
                    + key + PRESET_VALUE_DELIM
                    + val
                    + '\n';

            return out;
        }
    }

}