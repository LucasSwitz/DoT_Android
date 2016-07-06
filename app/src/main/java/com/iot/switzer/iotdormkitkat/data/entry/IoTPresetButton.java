package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iot.switzer.iotdormkitkat.data.IoTContributor;
import com.iot.switzer.iotdormkitkat.presets.Preset;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class IoTPresetButton extends Button{
    private Preset preset;
    private boolean presetEnabled = false;
    private int color;
    private static final int MAX_COLOR = 16777215;

    public IoTPresetButton(Context context, Preset p) {
        super(context);
        this.preset = p;
        this.setText(preset.getName());
        color = colorFromName(preset.getName());
        this.setBackgroundColor(color);
        Log.d("COLOR",String.valueOf((Color.rgb(0,0,0))));

    }

    public static int colorFromName(String name)
    {
        return -(name.hashCode() % MAX_COLOR);
    }
    public final Preset getPreset()
    {
        return preset;
    }

    public boolean isPresetEnabled()
    {
        return presetEnabled;
    }

    public void toggle()
    {
        if(presetEnabled)
            disable();
        else
            enable();
    }

    public void enable()
    {
        for (Preset.PresetEntry e : preset) {
            e.entry.setVal(e.value);
            e.entry.lock();
        }
        presetEnabled = true;
        this.setBackgroundColor(Color.GREEN);
    }

    public void disable()
    {
        for (Preset.PresetEntry e : preset) {
            e.entry.setVal(new byte[]{0, 0, 0, 0});
            e.entry.unlock();
        }
        presetEnabled = false;
        this.setBackgroundColor(color);
    }
}
