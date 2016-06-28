package com.iot.switzer.iotdormkitkat.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.data.entry.IoTEntryMaster;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */

public class EntryRow<T extends View &IoTUIController> extends TableRow implements View.OnLongClickListener {
    private TextView title;
    private T controller;
    private boolean userControlled = false;
    public EntryRow(Context context, String title, T controller) {
        super(context);

        final float scale = getContext().getResources().getDisplayMetrics().density;


        this.setMinimumHeight(dp(scale,50));

        this.title = new TextView(context);
        this.title.setText(title);
        this.title.setPadding(0,0,dp(scale,10),dp(scale,40));
        this.title.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        this.addView(this.title);

        this.controller = controller;
        this.controller.setMinimumWidth(200);

        this.addView(controller);

        this.setLongClickable(true);
        this.setOnLongClickListener(this);

        disableUserControls();
    }

    private static int dp(float scale, int pixels)
    {
        return (int)(pixels*scale + 0.5f);
    }

    private void enableUserControls()
    {
        userControlled = true;
        Toast.makeText(getContext(), title.getText()+": Enabled User Control",Toast.LENGTH_SHORT).show();
        controller.enable();
    }

    private void disableUserControls()
    {
        userControlled = false;
        Toast.makeText(getContext(), title.getText()+": Restored Network Control",Toast.LENGTH_SHORT).show();
        controller.disable();
    }
    @Override
    public boolean onLongClick(View v) {
        if(userControlled)
        {

            disableUserControls();
        }
        else
        {
            enableUserControls();
        }

        return false;
    }

    public void setTitle(String title)
    {
        this.title.setText(title);
    }

    public void drawController()
    {
        controller.postValue();
    }
}