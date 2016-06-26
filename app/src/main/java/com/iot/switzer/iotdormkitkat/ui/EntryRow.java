package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */
public class EntryRow extends TableRow
{
    TextView title;
    TextView value;
    public EntryRow(Context context, String title) {
        super(context);
        this.title = new TextView(context);
        this.value = new TextView(context);
        this.value.setGravity(Gravity.RIGHT);
        this.title.setText(title);
        this.addView(this.title);
        this.addView(value);
    }
    public void setValue(String value)
    {
        this.value.setText(value);
    }

    public void setTitle(String title)
    {
        this.title.setText(title);
    }
}