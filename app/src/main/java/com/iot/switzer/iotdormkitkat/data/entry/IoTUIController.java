package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Switzer on 6/27/2016.
 */
public abstract class IoTUIController implements IoTSubscriber {
    private IoTSubscriptionEntry entry;
    private Context context;
    private IoTUIControllerListener listener = null;

    public IoTUIController(Context context,IoTSubscriptionEntry entry)
    {
        this.entry = entry;
        this.context = context;
    }
    public abstract void postValue();
    public abstract View getView();
    public abstract void onValueUpdate(IoTSubscriptionEntry e);

    protected Context getContext()
    {
        return  context;
    }
    public boolean enable() {
        if (!entry.isLocked()) {
            lockEntry();
            enableView();

            Toast.makeText(getContext(), entry.getKey() + ": Enabled User Control", Toast.LENGTH_SHORT).show();
            return true;
        }

        Toast.makeText(getContext(), entry.getKey() + " is Locked!", Toast.LENGTH_SHORT).show();
        return  false;
    }

    public boolean disable() {
            unlockEntry();
            disableView();
            return true;
    }

    private void lockEntry()
    {
        entry.lock();
    }

    private void unlockEntry()
    {
        entry.unlock();
    }
    private void disableView()
    {
        getView().setEnabled(false);

    }
    private void enableView() {
        getView().setEnabled(true);

    }

    public IoTSubscriptionEntry getEntry()
    {
        return  entry;
    }

    public interface IoTUIControllerListener
    {
        void onUIDrawRequest(IoTUIController controller);
    }

    public void setListener(IoTUIControllerListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry e) {
        onValueUpdate(e);
        signalDrawRequest();
    }

    private void signalDrawRequest()
    {
        if(listener != null) {
            listener.onUIDrawRequest(this);
        }
    }
    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> d = new ArrayList<>();
        d.add(getEntry().getDescription());
        return d;

    }
}
