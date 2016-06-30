package com.iot.switzer.iotdormkitkat.data.entry;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.MainActivity;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.ui.IoTUIController;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public class IoTSubscriptionEntryIntegerController extends SeekBar implements IoTUIController, SeekBar.OnSeekBarChangeListener {
    private IoTSubscriptionEntry entry;
    int value = 0;

    public IoTSubscriptionEntryIntegerController(Context context,IoTSubscriptionEntry entry) {
        super(context);
        this.entry = entry;
        this.setOnSeekBarChangeListener(this);


        new UpdateProgressBarTask(this);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry)
    {
        Log.d("TABLE","Sub Update");
        value = entry.getValAsInt();
        setProgress(value);
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> d = new ArrayList<>();
        d.add(entry.getDescription());
        return d;
    }

    public void postValue()
    {
        this.setProgress(value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
         entry.setVal(IoTSubscriptionEntry.bytePtrFromInteger(this.getProgress()));
    }

    @Override
    public void enable() {
        if(!entry.isLocked()) {
            Toast.makeText(getContext(), entry.getKey()+": Enabled User Control",Toast.LENGTH_SHORT).show();
            entry.lock();
            setEnabled(true);
        }
        else
        {
            Toast.makeText(getContext(), entry.getKey()+" is Locked!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void disable() {
        entry.unlock();
        setEnabled(false);
    }

}

class UpdateProgressBarTask extends AsyncTask<Void,Void,Void>
{
    SeekBar s;

    UpdateProgressBarTask(SeekBar s)
    {
        this.s = s;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        s.setMax(255);
    }
}
