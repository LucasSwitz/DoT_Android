package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public class IoTSubscriptionEntryIntegerController extends IoTUIController implements SeekBar.OnSeekBarChangeListener {
    int value = 0;
    private IoTSubscriptionEntry entry;
    private SeekBar seekBar;
    private TextView textView;

    public IoTSubscriptionEntryIntegerController(Context context, IoTSubscriptionEntry entry) {
        super(context, entry);
        this.entry = entry;
        seekBar = new SeekBar(context);
        textView = new TextView(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }

        seekBar.setOnSeekBarChangeListener(this);


        new UpdateProgressBarTask(seekBar, entry.getDescription().highLimit).execute();
        seekBar.setBackgroundColor(Color.TRANSPARENT);

        disable();
    }

    @Override
    public void onValueUpdate(IoTSubscriptionEntry entry) {
        value = entry.getValAsInt();
    }

    public void postValue() {
        seekBar.setProgress(value);
        seekBar.refreshDrawableState();
        textView.setText(String.valueOf(value));
    }

    @Override
    public View getView() {
        return seekBar;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        entry.setVal(IoTSubscriptionEntry.bytePtrFromInteger(seekBar.getProgress()));
    }
}

class UpdateProgressBarTask extends AsyncTask<Void, Void, Void> {
    SeekBar s;
    int max;

    UpdateProgressBarTask(SeekBar s, int max) {
        this.s = s;
        this.max = max;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        s.setMax(max);
        s.setProgress(0);
        s.refreshDrawableState();
    }
}
