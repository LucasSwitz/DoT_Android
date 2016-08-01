package com.iot.switzer.iotdormkitkat.data.entry;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Lucas Switzer on 6/30/2016.
 */
public class IoTSubscriptionEntryEnumController extends IoTUIController implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private int position = 0;
    private IoTSubscriptionEntry entry;

    public IoTSubscriptionEntryEnumController(Context context, IoTSubscriptionEntry entry) {
        super(context, entry);
        spinner = new Spinner(context);
        spinner.setOnItemSelectedListener(this);

        this.entry = entry;

        populateSpinner();
        disable();
    }

    private void populateSpinner() {
        Integer enums[] = new Integer[entry.getDescription().highLimit + 1];

        for (int i = 0; i < enums.length; i++) {
            enums[i] = i;
        }

        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, enums);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void postValue() {
        spinner.setSelection(position);
    }

    @Override
    public View getView() {
        return spinner;
    }

    @Override
    public void onValueUpdate(IoTSubscriptionEntry entry) {
        position = entry.getValAsInt();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        entry.setVal(IoTSubscriptionEntry.bytePtrFromInteger(i));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        entry.setVal(IoTSubscriptionEntry.bytePtrFromInteger(0));
    }
}
