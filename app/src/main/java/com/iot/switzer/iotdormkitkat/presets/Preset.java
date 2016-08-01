package com.iot.switzer.iotdormkitkat.presets;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;

import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class Preset extends ArrayList<Preset.PresetEntry> {

    public static final char PRESET_DELIM = '~';
    public static final char PRESET_NAME_DELIM = '/';
    private String name = "";

    public Preset() {

    }

    public final String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public static class PresetEntry {
        public static final char PRESET_ENTRY_TYPE_DELIM = '|';
        public static final char PRESET_ENTRY_VALUE_DELIM = '=';
        public static final char PRESET_ENTRY_DELIM = '$';
        public IoTSubscriptionEntry entry;
        public byte[] value;

        public PresetEntry(IoTSubscriptionEntry e, byte[] value) {
            this.entry = e;
            this.value = value;
        }

        public PresetEntry(String s, byte[] value) {
            this.entry = IoTVariablesBase.getInstance().get(s);
            this.value = value;
        }
    }

}