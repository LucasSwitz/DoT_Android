package com.iot.switzer.iotdormkitkat.activities;

import android.util.Log;
import android.widget.TableLayout;

import java.util.ArrayList;

/**
 * Created by Lucas Switzer on 7/2/2016.
 */
public class AddPresetTable extends ArrayList<AddPresetRow>{
    private TableLayout layout;
    public AddPresetTable(TableLayout layout) {
        this.layout = layout;
        addEntryRow();
    }

    public void addEntryRow() {
        Log.d("ADDPRESET", "Adding row...");
        AddPresetRow newRow = new AddPresetRow(layout.getContext());
        add(newRow);
        layout.addView(newRow.getRowView(),layout.getChildCount() - 1);
    }
}