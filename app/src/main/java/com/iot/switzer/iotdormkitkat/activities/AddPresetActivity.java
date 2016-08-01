package com.iot.switzer.iotdormkitkat.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.presets.PresetManager;
import com.iot.switzer.iotdormkitkat.presets.PresetStringBuilder;

/**
 * Created by Lucas Switzer on 7/1/2016.
 */
public class AddPresetActivity extends Activity {

    public static final int CREATE_NEW_PRESET = 1;
    public static final int PRESET_NOT_ADDED = 10;
    public static final int PRESET_ADDED = 11;
    PresetStringBuilder builder;
    private AddPresetTable table;
    private EditText presetNameEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_preset);
        initUI();

    }

    private void initUI() {
        final ScrollView scrollView = (ScrollView) findViewById(R.id.presetScrollView);
        this.table = new AddPresetTable((TableLayout) scrollView.getChildAt(0));

        Button finishButton = (Button) this.findViewById(R.id.exportPresetButton);
        Button cancelButton = (Button) this.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(PRESET_ADDED, new Intent());
                finish();
            }
        });

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
                final ScrollView scrollView = (ScrollView) findViewById(R.id.presetScrollView);
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

    private boolean checkNameValidity(String presetName) {

        if (presetName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Set preset name", Toast.LENGTH_SHORT).show();
            presetNameEditText.setHintTextColor(Color.parseColor("#F44336"));
            return false;
        }
        return true;
    }

    private void buildPresetEntryStrings() {
        for (AddPresetRow row : table) {
            builder.addPreset(new Preset.PresetEntry(row.getEntryName(), row.getEntryValue()));
        }
    }

    private boolean checkRowValidity() {
        boolean verfied = true;

        for (AddPresetRow row : table) {

            String entryName = row.getEntryName();
            byte[] value = row.getEntryValue();

            if (entryName.isEmpty() || value.length == 0) {
                row.warn();
                verfied = false;
            } else {
                row.good();
            }

            if (!verfied) {
                signalFieldInvalidity();
                return false;
            }
        }
        return true;
    }

    void signalFieldInvalidity() {
        Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
    }

    private void exportPresetToFile() {
        builder = new PresetStringBuilder();

        String presetName = presetNameEditText.getText().toString();

        if (checkNameValidity(presetName)) {
            builder.setName(presetName);

            if (checkRowValidity()) {
                buildPresetEntryStrings();

                PresetManager.getInstance().writePreset(builder.build());
                endWithResult(PRESET_ADDED);
            }
        }
    }

    private void endWithResult(int result) {
        this.setResult(result, new Intent());
        this.finish();
    }
}
