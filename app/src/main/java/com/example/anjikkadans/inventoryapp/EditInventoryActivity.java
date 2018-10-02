package com.example.anjikkadans.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EditInventoryActivity extends AppCompatActivity {

    /**
     * Spinner field to enter item type
     */
    private Spinner mItemTypeSpinner;

    /**
     * Type of the item. The possible values are:
     * 0 for sold per kg, 1 for sold per item/piece
     */
    private int mItemType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Find all relevant views that we will need to read user input from
        mItemTypeSpinner = (Spinner) findViewById(R.id.item_type_spinner);

        setupSpinner();
    }

    /**
     * Setup the drop-down spinner that allows user to select the type of the item
     */
    public void setupSpinner() {
        // Create adapter for spinner. The string options are from the String array
        // the spinner will use the default layout
        ArrayAdapter itemTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_item_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        itemTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mItemTypeSpinner.setAdapter(itemTypeSpinnerAdapter);

        // Set the integer mItemType to the constant values
        mItemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.kilo))) {
                        mItemType = 0; // Item is sold per kg
                    } else if (selection.equals(getString(R.string.count))) {
                        mItemType = 1; // Item is sold per count
                    }
                } else {
                    mItemType = 0; // sold per kg
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mItemType = 0; // sold per kg
            }
        });
    }
}
