package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iot.switzer.iotdormkitkat.data.IoTContributor;
import com.iot.switzer.iotdormkitkat.presets.Preset;

/**
 * Created by Lucas Switzer on 6/29/2016.
 */
public class IoTPresetButton extends Button implements View.OnClickListener {
    private Preset preset;
    private boolean presetEnabled = false;

    public IoTPresetButton(Context context, Preset p) {
        super(context);
        this.preset = p;
        this.setText(preset.getName());
        this.setBackgroundColor(Color.RED);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (!presetEnabled) {
            for (Preset.PresetEntry e : preset) {
                e.entry.setVal(e.value);
                e.entry.lock();
            }
            presetEnabled = true;
            this.setBackgroundColor(Color.GREEN);
        } else {
            for (Preset.PresetEntry e : preset) {
                e.entry.setVal(new byte[]{0, 0, 0, 0});
                e.entry.unlock();
            }
            presetEnabled = false;
            this.setBackgroundColor(Color.RED);
        }
    }
}
