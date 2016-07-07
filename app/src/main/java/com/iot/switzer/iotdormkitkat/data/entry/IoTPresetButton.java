package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;

import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.ui.IoTPresetModel;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class IoTPresetButton extends IoTPresetModel{
    private boolean presetEnabled = false;

    public IoTPresetButton(Context context, Preset p) {
        super(context,p);

    }

    public boolean isPresetEnabled()
    {
        return presetEnabled;
    }

    public void toggle()
    {
        if(presetEnabled)
            disablePreset();
        else
            enablePreset();
    }

    public void enablePreset()
    {
        for (Preset.PresetEntry e : preset) {
            e.entry.setVal(e.value);
            e.entry.lock();
        }
        presetEnabled = true;
        setActiveState(true);

    }

    public void disablePreset()
    {
        for (Preset.PresetEntry e : preset) {
            e.entry.setVal(new byte[]{0, 0, 0, 0});
            e.entry.unlock();
        }
        presetEnabled = false;
        setActiveState(false);
    }
}
