package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;

public class DeviceBlock extends TableRow
{
    private IoTDeviceController controller;
    private EntryValueUITable entryTable;
    private LinearLayout elements;

    protected static final int MAX_COLOR  = 16777215;
    protected  static final float MIN_SATURATION = .7f;

    public DeviceBlock(Context context, IoTDeviceController c) {
        super(context);
        this.controller = c;

        elements = (LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.device_block, elements, false);

        Button displayButton = (Button) elements.findViewById(R.id.deviceButton);
        displayButton.setText(c.getToken());

        final ScrollView sv = (ScrollView) elements.findViewById(R.id.entryScrollView);

        entryTable = new EntryValueUITable(sv.getContext());


        for (SubscriptionDescription d : controller.getSubscriptions()) {
            entryTable.updateRow(IoTVariablesBase.getInstance().get(d.key));
        }

        displayButton.setBackgroundColor(colorFromName(displayButton.getText().toString(),1));
        sv.setBackgroundColor(colorFromName(displayButton.getText().toString(),.3f));

        sv.addView(entryTable);

        this.addView(elements);

        displayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sv.getHeight() != 0) {
                    sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                } else {
                    sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        });

    }


    public static int colorFromName(String name, float saturationMultiplier)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(-(name.hashCode() % MAX_COLOR),hsv);

        hsv[1] = hsv[1] < MIN_SATURATION ? MIN_SATURATION*saturationMultiplier : hsv[1]*saturationMultiplier;

        return Color.HSVToColor(hsv);
    }
}
