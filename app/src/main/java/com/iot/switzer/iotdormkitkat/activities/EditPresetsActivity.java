package com.iot.switzer.iotdormkitkat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.presets.PresetManager;
import com.iot.switzer.iotdormkitkat.ui.IoTPresetModel;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lucas Switzer on 7/6/2016.
 */
public class EditPresetsActivity extends AppCompatActivity implements Button.OnClickListener {
    public static final int EDIT_PRESETS = 0;

    public static final int PRESETS_EDITED = 1;
    private HashMap<String,IoTPresetModel> modelMap;

    private HashMap<String,IoTPresetModel> selectedModels;


    @Override
    protected void onCreate(Bundle savedInstances)
    {
        super.onCreate(savedInstances);
        modelMap = new HashMap<>();
        selectedModels = new HashMap<>();

        setContentView(R.layout.activity_edit_presets);
        LinearLayout l = (LinearLayout) findViewById(R.id.editPresetsScrollLinearLayout);

        Button finishButton = (Button) findViewById(R.id.finishButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(PRESETS_EDITED);
                finish();
            }
        });

        for(Preset p: PresetManager.getInstance())
        {
            IoTPresetModel b = new IoTPresetModel(l.getContext(),p);
            b.setOnClickListener(this);
            l.addView(b);
            modelMap.put(p.getName(),b);
        }
    }

    @Override
    public void onClick(View v) {
        IoTPresetModel m = (IoTPresetModel)v;
        m.setActiveState(!(m.isActive()));

        if(m.isActive())
        {
            if(selectedModels.get(m.getText().toString()) == null)
            {
                selectedModels.put(m.getText().toString(),m);
            }
        }
        else
        {
            if(selectedModels.get(m.getText().toString()) != null)
            {
                selectedModels.remove(m.getText().toString());
            }
        }
    }

    public void deleteSelectedValues()
    {
        LinearLayout l = (LinearLayout) findViewById(R.id.editPresetsScrollLinearLayout);
        for(IoTPresetModel m : selectedModels.values())
        {
            l.removeView(m);
            try {
                PresetManager.getInstance().deletePreset(m.getPreset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteSelectedValues();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_presets_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
