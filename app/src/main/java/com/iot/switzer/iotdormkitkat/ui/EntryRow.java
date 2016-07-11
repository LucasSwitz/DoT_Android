package com.iot.switzer.iotdormkitkat.ui;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.data.entry.IoTUIController;

/**
 * Created by Lucas Switzer on 6/25/2016.
 */

public class EntryRow extends TableRow implements View.OnLongClickListener {
    private TextView title;
    private IoTUIController controller;
    private boolean userControlled = false;

    public EntryRow(Context context, String title, IoTUIController controller) {
        super(context);

        final float scale = getContext().getResources().getDisplayMetrics().density;


        this.setMinimumHeight(dp(scale, 50));

        this.title = new TextView(context);
        this.title.setText(title);
        this.title.setPadding(0, 0, dp(scale, 10), dp(scale, 40));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.title.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        }
        this.addView(this.title);

        this.controller = controller;

        this.addView(controller.getView());

        this.setLongClickable(true);
        this.setOnLongClickListener(this);

        disableUserControls();
    }

    private static int dp(float scale, int pixels) {
        return (int) (pixels * scale + 0.5f);
    }

    private void enableUserControls() {
        userControlled = true;
        controller.enable();
    }

    private void disableUserControls() {
        userControlled = false;
        Toast.makeText(getContext(), title.getText() + ": Restored Network Control", Toast.LENGTH_SHORT).show();
        controller.disable();
    }

    @Override
    public boolean onLongClick(View v) {
        if (userControlled) {

            disableUserControls();
        } else {
            enableUserControls();
        }

        return false;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void drawController() {
        controller.postValue();
    }
}