package com.example.anjikkadans.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anjikkadans.inventoryapp.data.InventoryContract;

public class EditInventoryActivity extends AppCompatActivity {

    /**
     * Spinner field to enter item type
     */
    private Spinner mItemTypeSpinner;

    /**
     * Button to save the item Data
     */
    private Button mSaveButton;
    /**
     * Button to cancel saving the item Data
     */
    private Button mCancelButton;
    /**
     * EditText to insert the item name
     */
    private EditText mItemNameEditText;
    /**
     * EditText to insert the item price
     */
    private EditText mItemPriceEditText;
    /**
     * EditText to insert the item quantity
     */
    private EditText mItemQuantityEditText;
    /**
     * EditText to insert the supplier name
     */
    private EditText mSupplierName;
    /**
     * EditText to insert the supplier contact
     */
    private EditText mSupplierContact;
    /**
     * EditText to insert the item description
     */
    private EditText mItemDescriptionEditText;
    /**
     * Type of the item. The possible values are:
     * 0 for sold per kg, 1 for sold per item/piece
     */
    private int mItemType = 0;

    private static final String TAG_NAME = EditInventoryActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Find all relevant views that we will need to read user input from
        mItemTypeSpinner = (Spinner) findViewById(R.id.item_type_spinner);
        mItemNameEditText = (EditText) findViewById(R.id.item_name_edit_text);
        mItemPriceEditText = (EditText) findViewById(R.id.item_price_edit_text);
        mItemQuantityEditText = (EditText) findViewById(R.id.item_quantity_edit_text);
        mItemDescriptionEditText = (EditText) findViewById(R.id.item_description_edit_text);
        mSupplierContact = (EditText) findViewById(R.id.supplier_phone_number_edit_text);
        mSupplierName = (EditText) findViewById(R.id.supplier_name_edit_text);

        mSaveButton = (Button) findViewById(R.id.submit_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the data if all fields are filled
                saveItemData();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(EditInventoryActivity.this);
            }
        });

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

    /**
     * This function should add the item data when the user chooses
     * to save the data
     */
    public void saveItemData() {

        // check if any of the fields are not filled before saving
        // to avoid crash
        if (TextUtils.isEmpty(mItemNameEditText.getText()) || TextUtils.isEmpty(mItemPriceEditText.getText())
                || TextUtils.isEmpty(mItemQuantityEditText.getText()) || TextUtils.isEmpty(mSupplierName.getText())
                || TextUtils.isEmpty(mSupplierContact.getText())) {

            Toast.makeText(this, "All fields are not filled", Toast.LENGTH_SHORT).show();
            // don't save the data
            return;
        }

        // get the item name from the {@link mItemNameEditText}
        String itemName = mItemNameEditText.getText().toString();

        // initializing the item price value with 0
        int itemPrice = 0;

        // get the item price from the {@link mItemPriceEditText}
        // check if it's a valid number
        try {
            // if yes save the value
            itemPrice = Integer.parseInt(mItemPriceEditText.getText().toString());
        } catch (Exception e) {
            // else, save with the initialized value
        }

        // initializing the item count value with 0
        int itemQuantity = 0;

        // get the item quantity/count from the (@link mItemQuantityEditText}
        // check if it's a valid number
        try {
            // if yes, save the value
            itemQuantity = Integer.parseInt(mItemQuantityEditText.getText().toString());
        } catch (Exception e) {
            // or save with the initialized value
        }
        // get the item description from {@link mItemDescriptionEditText}
        String itemDescription = mItemDescriptionEditText.getText().toString();

        // get the supplier name from {@link mSupplierName}
        String supplierName = mSupplierName.getText().toString();

        // get the supplier contact number from {@link mSupplierContact}
        // since the phone number is a long value and cannot be stored as int
        // it is always safe to save the value as String itself
        String supplierPhoneNumber = mSupplierContact.getText().toString();

        // create new content value object to enter the item data to the database
        ContentValues contentValues = new ContentValues();
        // put all the data required to the corresponding columns
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME, itemName);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_PRICE, itemPrice);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY, itemQuantity);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_ITEM_TYPE, mItemType);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_DESCRIPTION, itemDescription);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);


        try {

            // getting the content resolver for the content provider to insert data to the database
            Uri uri = getContentResolver().insert(InventoryContract.InventoryFeedEntry.CONTENT_URI, contentValues);

            Log.v(TAG_NAME, "Item added with uri " + uri);
            NavUtils.navigateUpFromSameTask(this);
        } catch (Exception e) {
            // in case some error happens at the first time
            e.printStackTrace();
        }
    }
}
