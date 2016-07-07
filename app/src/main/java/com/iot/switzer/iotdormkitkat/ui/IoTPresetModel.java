package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;

import com.iot.switzer.iotdormkitkat.presets.Preset;

/**
 * Created by Lucas Switzer on 7/6/2016.
 */
public class IoTPresetModel extends Button {
    protected static final int MAX_COLOR  = 16777215;
    protected  static final float MIN_SATURATION = .7f;
    private int color;
    protected Preset preset;
    private boolean active = false;

    public IoTPresetModel(Context context,Preset p)
    {
        super(context);
        this.preset = p;
        this.setText(p.getName());
        color = colorFromName(p.getName());
        this.setBackgroundColor(color);
        this.setTextColor(Color.WHITE);
    }


    public final Preset getPreset()
    {
        return preset;
    }

    public void setActiveState(boolean state)
    {
        active = state;
        if(state)
        {
            this.setBackgroundColor(Color.GREEN);
        }
        else
        {
            this.setBackgroundColor(color);

        }
    }

    public final boolean isActive()
    {
        return active;
    }

    public static int colorFromName(String name)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(-(name.hashCode() % MAX_COLOR),hsv);

        hsv[1] = hsv[1] < MIN_SATURATION ? MIN_SATURATION : hsv[1];

        return Color.HSVToColor(hsv);
    }
}
