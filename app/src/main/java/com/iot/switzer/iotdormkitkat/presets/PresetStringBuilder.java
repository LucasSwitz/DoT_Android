package com.iot.switzer.iotdormkitkat.presets;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 8/1/2016.
 */
public class PresetStringBuilder {
    private String name;
    private ArrayList<Preset.PresetEntry> entries;

    public PresetStringBuilder() {
        entries = new ArrayList<>();
    }

    public void clear() {
        name = "";
        entries.clear();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPreset(Preset.PresetEntry entry) {
        entries.add(entry);
    }

    private String getEntryString(Preset.PresetEntry entry) {
        SubscriptionDescription.SubscriptionType type = entry.entry.getType();

        String out = String.valueOf(SubscriptionDescription.SubscriptionType.asInt(type))
                + Preset.PresetEntry.PRESET_ENTRY_TYPE_DELIM
                + entry.entry.getKey()
                + Preset.PresetEntry.PRESET_ENTRY_VALUE_DELIM
                + IoTSubscriptionEntry.stringFromBytesPtr(entry.value) //change this to string
                + Preset.PresetEntry.PRESET_ENTRY_DELIM;

        return out;
    }

    public String build() {
        String out = "";
        out += getNameString(name);
        out += getEntriesString(entries);
        out += getCloseString();

        return out;
    }

    private String getCloseString() {
        String out = "";
        out += Preset.PRESET_DELIM;
        out += "\r\n";
        return out;
    }

    private String getEntriesString(List<Preset.PresetEntry> entries) {
        String out = "";
        for (Preset.PresetEntry e : entries) {
            out += getEntryString(e);
        }

        return out;
    }

    private String getNameString(String name) {
        String out = "";
        out += name;
        out += Preset.PRESET_NAME_DELIM;

        return out;
    }

}
