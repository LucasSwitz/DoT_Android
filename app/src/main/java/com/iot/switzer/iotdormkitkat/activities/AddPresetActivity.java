package com.iot.switzer.iotdormkitkat.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;

/**
 * Created by Lucas Switzer on 7/1/2016.
 */
public class AddPresetActivity extends Activity {

    private AddPresetTable table;
    private EditText presetNameEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_preset);
        final ScrollView scrollView =  (ScrollView) findViewById(R.id.presetScrollView);

        this.table = new AddPresetTable((TableLayout) scrollView.getChildAt(0));

        Button finishButton = (Button) this.findViewById(R.id.exportPresetButton);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportPresetToFile();
            }
        });

        Button addNewPresetEntryButton = (Button) this.findViewById(R.id.addEntryButton);

        addNewPresetEntryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ScrollView scrollView =  (ScrollView) findViewById(R.id.presetScrollView);
                table.addEntryRow();

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        presetNameEditText = (EditText) findViewById(R.id.presetName);

    }

    private void exportPresetToFile()
    {
        File presetFile = new File(getExternalFilesDir(null),"Presets.txt");
        String presetName = presetNameEditText.getText().toString();

        if(presetName.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Set preset name",Toast.LENGTH_SHORT).show();
            presetNameEditText.setHintTextColor(Color.parseColor("#F44336"));
            return;
        }

        String out = presetName;
        out+="\r\n";
        boolean verfied = true;

        for(AddPresetRow row: table) {

            String entryName = row.getEntryName();
            SubscriptionDescription.SubscriptionType type = row.getEntryType();
            String value = row.getEntryValue();

            if(entryName.isEmpty() || value.isEmpty())
            {
                row.warn();
                verfied = false;
            }
            else
                row.good();
            {   if(verfied)
                    out += Preset.PresetEntry.toExportString(row.getEntryName(),row.getEntryType(),row.getEntryValue());
            }
        }
        if(!verfied)
        {
            Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_SHORT).show();
            return;
        }
        out+="===";
        try {

            BufferedWriter fw = new BufferedWriter(new FileWriter(presetFile));

            fw.write(out);
            fw.close();
            Log.d("ADDPRESET","File successfully updated:"+(Environment.getExternalStorageDirectory().getPath())+"\\Presets.txt");
        }catch (IOException e)
        {
            Log.d("ADDPRESET","An error occured when writing preset to file");
        }

        this.finish();
    }
}
