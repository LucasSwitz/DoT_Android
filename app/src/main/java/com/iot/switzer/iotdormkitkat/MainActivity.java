package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.devices.IoTManager;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;
import com.iot.switzer.iotdormkitkat.ui.EntryRow;
import com.iot.switzer.iotdormkitkat.ui.EntryUITable;
import com.iot.switzer.iotdormkitkat.ui.TableRowUpdate;


public class MainActivity extends AppCompatActivity {


    private Handler tableUpdateHandler;
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

        Log.d("START","Start of program!");
        setContentView(R.layout.activity_main);
        EntryUITable table = new EntryUITable(getApplicationContext());
        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);

        tableUpdateHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage) {
                Log.d("TABLE","Handling Message");

                TableRowUpdate updateTohandle = (TableRowUpdate) inputMessage.obj;

                switch (inputMessage.what)
                {
                    case EntryUITable.UPDATE_ROW:
                        updateTohandle.table.updateRow(updateTohandle.entry.getKey(),String.valueOf(updateTohandle.entry.getValueAsType()));
                        break;

                    case EntryUITable.ADD_ROW:
                        updateTohandle.table.addRow(updateTohandle.entry.getKey());
                        break;
                }
            }
        };

        IoTVariablesBase.getInstance().addSubscriber(table);
        layout.addView(table);

        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        startService(msgIntent);
        Log.d("START","After Service Start!");
    }

    public void handleUpdate(TableRowUpdate up, int state)
    {
        Log.d("TABLE","Handleing Update");
        int out = 0;
        switch(state)
        {
            case EntryUITable.ADD_ROW:
                out = EntryUITable.ADD_ROW;
                break;
            case EntryUITable.UPDATE_ROW:
                out = EntryUITable.UPDATE_ROW;
                break;
        }
        Message updateMessage = tableUpdateHandler.obtainMessage(out,up);
        updateMessage.sendToTarget();
    }

    public static MainActivity getInstance(){return instance;}
}
