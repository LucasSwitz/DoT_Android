package com.iot.switzer.iotdormkitkat.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;

import com.iot.switzer.iotdormkitkat.R;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;

/**
 * Created by Lucas Switzer on 7/2/2016.
 */
public class AddPresetRow{

    private EditText entryNameEditText;
    private Spinner entryTypeSpinner;
    private EditText entryValueEditText;
    private TableRow rowView;
    public AddPresetRow(Context context)
    {
        rowView = (TableRow) LayoutInflater.from(context).inflate(R.layout.preset_row, rowView, false);

        entryNameEditText = (EditText) rowView.findViewById(R.id.entryName);
        entryTypeSpinner = (Spinner) rowView.findViewById(R.id.entryType);
        entryValueEditText = (EditText) rowView.findViewById(R.id.entryValue);

        populateSpinner();
    }

    public TableRow getRowView()
    {
        return rowView;
    }

    public void populateSpinner()
    {
            SubscriptionDescription.SubscriptionType values[] = SubscriptionDescription.SubscriptionType.values();
            String stringValues[] = new String[values.length];

            for(int i =0; i < SubscriptionDescription.SubscriptionType.values().length; i++)
            {
                stringValues[i] = values[i].name();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(rowView.getContext(),
                    android.R.layout.simple_spinner_item, stringValues);

            entryTypeSpinner.setAdapter(adapter);
    }

    public String getEntryName()
    {
        return entryNameEditText.getText().toString();
    }
    public SubscriptionDescription.SubscriptionType getEntryType()
    {
        int itemPosition = entryTypeSpinner.getSelectedItemPosition();
        return SubscriptionDescription.SubscriptionType.fromInt(itemPosition);
    }
    public String getEntryValue()
    {
        return entryValueEditText.getText().toString();
    }

    public void warn()
    {
        rowView.setBackgroundColor(Color.parseColor("#F44336"));
    }

    public void good()
    {
        rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
}
