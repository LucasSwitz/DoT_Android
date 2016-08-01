package com.iot.switzer.iotdormkitkat.presets;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 7/31/2016.
 */
public class PresetParser {

    public Preset parseFromSring(String s) {
        Preset preset = new Preset();

        String currentValue = "";
        SubscriptionDescription.SubscriptionType type = null;
        String entryName = "";
        byte[] value = null;

        for (char c : s.toCharArray()) {
            switch (c) {
                case Preset.PRESET_NAME_DELIM:
                    if (preset.getName().isEmpty()) {
                        preset.setName(currentValue);
                        currentValue = "";
                        break;
                    }
                case Preset.PresetEntry.PRESET_ENTRY_TYPE_DELIM:
                    if (type == null) {
                        type = SubscriptionDescription.SubscriptionType.fromInt(Integer.parseInt(currentValue));
                        currentValue = "";
                        break;
                    }
                case Preset.PresetEntry.PRESET_ENTRY_VALUE_DELIM:
                    if (entryName.isEmpty()) {
                        entryName = currentValue;
                        currentValue = "";
                        break;
                    }
                case Preset.PresetEntry.PRESET_ENTRY_DELIM:
                    if (value == null) {
                        value = IoTSubscriptionEntry.bytePtrFromString(currentValue);
                        /**
                         * This should ask the variables base for this SubscriptionEntry (create it if
                         * it doest exists), then return it to our PresetEntry.
                         */
                        Preset.PresetEntry e = new Preset.PresetEntry(IoTVariablesBase.getInstance().get(entryName), value);
                        e.entry.updateType(type);
                        Log.d("PRESET", "Added new entry to Preset: " + preset.getName() + "," + entryName);
                        preset.add(e);
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
                    currentValue += c;
            }
        }
        return preset;
    }

    public Preset parseFromBuffer(char[] in, int size) {
        return (parseFromSring(String.copyValueOf(in, 0, size)));
    }

    public List<Preset> parseMany(char[] in, int size) {
        ArrayList<Preset> presets = new ArrayList<>();
        int index = 0;
        String s = "";
        while (index < size) {
            if (in[index] == Preset.PRESET_DELIM) {
                Preset presetNew = parseFromSring(s);
                presets.add(presetNew);
                s = "";
            } else {
                s += in[index];
            }
            index++;
        }
        return presets;
    }

}
