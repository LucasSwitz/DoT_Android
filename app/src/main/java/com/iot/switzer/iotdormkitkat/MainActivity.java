package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.iot.switzer.iotdormkitkat.activities.AddPresetActivity;
import com.iot.switzer.iotdormkitkat.data.entry.IoTPresetButton;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.network.IoTManager;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;
import com.iot.switzer.iotdormkitkat.ui.EntryValueUITable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ViewGroup presetScrollView;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("START", "Start of program!");

        startDiscoveryService();
        setContentView(R.layout.activity_main);

        presetScrollView = (ViewGroup) findViewById(R.id.presetScrollView).findViewById(R.id.presetLinearLayout);
        loadPresets();


        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);
        ScrollView scrollingView = (ScrollView) layout.findViewById(R.id.tableScrollView);
        EntryValueUITable table = new EntryValueUITable(scrollingView.getContext());
        IoTVariablesBase.getInstance().addObserver(table);
        scrollingView.addView(table);
    }

    private void startDiscoveryService() {
        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        startService(msgIntent);
    }


    void addPreset(Preset p)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(20,0,20,0);

        presetScrollView.addView(new IoTPresetButton(getApplicationContext(),p),layoutParams);
    }

    private void loadPresets()
    {
        presetScrollView.removeAllViews();
        File presetsFile = new File(getExternalFilesDir(null),Constants.PRESETS_FILE_NAME);
        char in[] = new char[4096];
        String s = "";
        int bytesRead;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(presetsFile));
            bytesRead = bf.read(in);

            int index = 0;
            while(index < bytesRead)
            {
                if(in[index] == Preset.PRESET_DELIM)
                {
                    addPreset(new Preset(s));
                    s="";
                }
                else
                {
                    s+=in[index];
                }
                index++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("MAIN","Activity return");
        if(requestCode == AddPresetActivity.CREATE_NEW_PRESET)
        {
            if(resultCode == AddPresetActivity.PRESET_ADDED)
            {
                loadPresets();
            }
            else
            {

            }
        }
    }

    private void launchAddPresetActivity()
    {
        Intent intent = new Intent(this, AddPresetActivity.class);
        startActivityForResult(intent,AddPresetActivity.CREATE_NEW_PRESET);
    }
}

