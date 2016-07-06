package com.iot.switzer.iotdormkitkat.presets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.iot.switzer.iotdormkitkat.data.entry.IoTPresetButton;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Lucas Switzer on 7/5/2016.
 */
public class PresetButtonGroup implements Button.OnClickListener,
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private HashMap<String, IoTPresetButton> buttonMap;
    private static final String KEY_HEADER = "com.switzer.iotdorm.";
    private static final String TABLE_HEADER = "/presets";

    private Context context;

    public Collection<IoTPresetButton> getButtons()
    {
        return buttonMap.values();
    }

    private GoogleApiClient googleApiClient;

    public PresetButtonGroup(Context context)
    {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        buttonMap = new HashMap<>();


        googleApiClient.connect();
        this.context = context;
        reload();
    }

    public void reload()
    {
        PresetManager.getInstance().reload();
        buttonMap.clear();
        for(Preset p : PresetManager.getInstance())
        {
            IoTPresetButton b = new IoTPresetButton(context,p);
            b.setOnClickListener(this);
            add(b);
        }
    }

    public void add(IoTPresetButton b)
    {
        buttonMap.put(toKeySyntax(b.getPreset().getName()),b);
        sendUpdate(b.getPreset().getName(),b.isPresetEnabled());
    }


    @Override
    public void onClick(View v) {
        sendUpdate(((IoTPresetButton)v).getPreset().getName(),!((IoTPresetButton)v).isPresetEnabled());

    }

    public void sendUpdate(String name, boolean enabled)
    {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(TABLE_HEADER);

        putDataMapReq.getDataMap().putBoolean(toKeySyntax(name),enabled);


        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
    }

    private static String toKeySyntax(String s)
    {
        return (KEY_HEADER + s);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d("WEARABLE", "Data Change!");
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                DataItem item = event.getDataItem();
                Log.d("WEARABLE", "URI:"+item.getUri().getPath().toString());
                if (item.getUri().getPath().compareTo(TABLE_HEADER) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    for (String key : dataMap.keySet()) {
                        Log.d("WEARABLE", "Key:"+key);
                        boolean enabled = dataMap.getBoolean(key);
                        if(enabled) {
                            buttonMap.get(key).enable();
                            Log.d("WEARABLE","Enabling");
                        }
                        else {
                            buttonMap.get(key).disable();
                            Log.d("WEARABLE","Disabling");
                        }
                    }

                } else if (event.getType() == DataEvent.TYPE_DELETED) {

                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.d("WEARABLE","Connected!");
        Wearable.DataApi.addListener(googleApiClient,this);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(TABLE_HEADER);
        for(IoTPresetButton b : buttonMap.values())
        {
            putDataMapReq.getDataMap().putBoolean(toKeySyntax(b.getPreset().getName()), b.isPresetEnabled());
        }

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
    }

    public void pause()
    {
        Wearable.DataApi.removeListener(googleApiClient,this);
        googleApiClient.disconnect();
    }

    public void resume()
    {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("WEARABLE","Connection Failed");
    }
}
