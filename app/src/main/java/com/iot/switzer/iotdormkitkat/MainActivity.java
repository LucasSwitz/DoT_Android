package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.iot.switzer.iotdormkitkat.activities.AddPresetActivity;
import com.iot.switzer.iotdormkitkat.data.entry.IoTPresetButton;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.network.IoTManager;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;
import com.iot.switzer.iotdormkitkat.ui.EntryValueUITable;


public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        stopService(msgIntent);
        IoTManager.getInstance().destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discover_devices_menu_item:
                startDiscoveryService();
                return true;
            case R.id.add_preset_menu_item:
                launchAddPresetActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("START", "Start of program!");


        startDiscoveryService();
        setContentView(R.layout.activity_main);

        ScrollView scrollingView = new ScrollView(getApplicationContext());
        EntryValueUITable table = new EntryValueUITable(getApplicationContext());
        IoTVariablesBase.getInstance().addObserver(table);
        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);
        scrollingView.addView(table);


        layout.addView(new IoTPresetButton(getApplicationContext(), new Preset("All Red", new Preset.PresetEntry[]{
                new Preset.PresetEntry(IoTVariablesBase.getInstance().get("R"), IoTSubscriptionEntry.bytePtrFromInteger(255))})));

        layout.addView(scrollingView);
    }

    private void startDiscoveryService() {
        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        startService(msgIntent);
    }

    private void launchAddPresetActivity()
    {
        Intent intent = new Intent(this, AddPresetActivity.class);
        startActivity(intent);
    }
}

