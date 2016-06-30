package com.iot.switzer.iotdormkitkat.presets;

import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class Preset extends ArrayList<Preset.PresetEntry>{

    private String name;
    public Preset(String name, String filePath)
    {
        this.name = name;
        loadFromFile(filePath);
    }

    public String getName()
    {
        return name;
    }
    public Preset(String name,PresetEntry... configuration)
    {
        this.name = name;
        for(PresetEntry e : configuration)
        {
            add(e);
        }
    }

    protected void loadFromFile(String path)
    {

    }

    public static class PresetEntry
    {
        public IoTSubscriptionEntry entry;
        public byte[] value;

        public PresetEntry(IoTSubscriptionEntry e, byte[] value)
        {
            this.entry = e;
            this.value = value;
        }
    }

}