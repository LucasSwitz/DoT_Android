package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.iot.switzer.iotdormkitkat.data.entry.IoTPresetButton;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.network.IoTManager;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;
import com.iot.switzer.iotdormkitkat.ui.EntryValueUITable;


public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        stopService(msgIntent);
        IoTManager.getInstance().destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("START","Start of program!");
        setContentView(R.layout.activity_main);

        ScrollView scrollingView = new ScrollView(getApplicationContext());

        EntryValueUITable table = new EntryValueUITable(getApplicationContext());
        IoTVariablesBase.getInstance().addObserver(table);

        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);
        scrollingView.addView(table);

        layout.addView(new IoTPresetButton(getApplicationContext(),new Preset("All Red",new Preset.PresetEntry[]{
                new Preset.PresetEntry(IoTVariablesBase.getInstance().get("R"), IoTSubscriptionEntry.bytePtrFromInteger(255))})));

        layout.addView(scrollingView);

        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        startService(msgIntent);
        Log.d("START","After Service Start!");
    }
    public static MainActivity getInstance(){return instance;}
}

