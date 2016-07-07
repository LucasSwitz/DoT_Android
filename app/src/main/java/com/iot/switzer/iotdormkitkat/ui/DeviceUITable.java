package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTUIController;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.iot.switzer.iotdormkitkat.network.IoTNetworkListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lucas Switzer on 7/7/2016.
 */
public class DeviceUITable extends TableLayout implements IoTNetworkListener{
    private HashMap<String, DeviceBlock> deviceBlocks;

    public DeviceUITable(Context context)
    {
        super(context);
        this.setColumnStretchable(0,true);
        deviceBlocks = new HashMap<>();
    }

    public void add(DeviceBlock db)
    {
        this.addView(db);
    }

    @Override
    public void onDeviceAdd(IoTDeviceController d) {
        add(new DeviceBlock(getContext(),d));
    }
}

class DeviceBlock extends TableRow
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