package com.iot.switzer.iotdormkitkat.activities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;

/**
 * Created by Lucas Switzer on 7/2/2016.
 */
public class AddPresetRow {

    private EditText entryNameEditText;
    private Spinner entryTypeSpinner;
    private EditText entryValueEditText;
    private TableRow rowView;

    public AddPresetRow(Context context) {
        rowView = (TableRow) LayoutInflater.from(context).inflate(R.layout.preset_row, rowView, false);

        entryNameEditText = (EditText) rowView.findViewById(R.id.entryName);
        entryTypeSpinner = (Spinner) rowView.findViewById(R.id.entryType);
        entryValueEditText = (EditText) rowView.findViewById(R.id.entryValue);

        populateSpinner();
    }

    public TableRow getRowView() {
        return rowView;
    }

    public void populateSpinner() {
        SubscriptionDescription.SubscriptionType values[] = SubscriptionDescription.SubscriptionType.values();
        String stringValues[] = new String[values.length];

        for (int i = 0; i < SubscriptionDescription.SubscriptionType.values().length; i++) {
            stringValues[i] = values[i].name();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(rowView.getContext(),
                android.R.layout.simple_spinner_item, stringValues);

        entryTypeSpinner.setAdapter(adapter);
    }

    public String getEntryName() {
        return entryNameEditText.getText().toString();
    }

    public SubscriptionDescription.SubscriptionType getEntryType() {
        int itemPosition = entryTypeSpinner.getSelectedItemPosition();
        return SubscriptionDescription.SubscriptionType.fromInt(itemPosition);
    }

    public byte[] getEntryValue() {
        String val = entryValueEditText.getText().toString();
        byte[] outbytes = null;

        Log.d("ADDPRESET", "Type: " + getEntryType().name());
        switch (getEntryType()) {
            case INT:
                outbytes = IoTSubscriptionEntry.bytePtrFromInteger(Integer.parseInt(val));
                break;
            case CHAR:
                break;
            case STRING:
                outbytes = IoTSubscriptionEntry.bytePtrFromString(val);
                break;
            case BOOLEAN:
                outbytes = IoTSubscriptionEntry.bytePtrFromBoolean(Boolean.parseBoolean(val));
                break;
            case ENUM:
                outbytes = IoTSubscriptionEntry.bytePtrFromInteger(Integer.parseInt(val));
                break;
        }
        Log.d("ADDPRESET", "Size: " + String.valueOf(outbytes.length));

        return outbytes;
    }

    public void warn() {
        rowView.setBackgroundColor(Color.parseColor("#F44336"));
    }

    public void good() {
        rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
}
