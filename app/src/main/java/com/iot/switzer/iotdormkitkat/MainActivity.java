package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("START","Start of program!");
        setContentView(R.layout.activity_main);
        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        startService(msgIntent);
        Log.d("START","After Service Start!");
    }
}
