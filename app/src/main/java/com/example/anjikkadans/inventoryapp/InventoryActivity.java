package com.example.anjikkadans.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.anjikkadans.inventoryapp.data.InventoryContract;
import com.example.anjikkadans.inventoryapp.data.InventoryDBHelper;

/**
 * This class in future may list all the items in the
 * inventory with details
 */
public class InventoryActivity extends AppCompatActivity {

    // declares the InventoryDBHelper class to access from
    private InventoryDBHelper inventoryDBHelper;

    // declares the SQLiteDatabase instance for the inventory database
    private SQLiteDatabase inventoryDatabase;

    // tag name for messages through logs
    private String TAG_NAME = InventoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // initializes the inventoryDBHelper class
        inventoryDBHelper = new InventoryDBHelper(this);

        // calling the function for testing
        insertDummyData();

    }

    // this function inserts some dummy data to the database
    // when called
    private void insertDummyData() {

        // get the writeable type instance of inventory database
        inventoryDatabase = inventoryDBHelper.getWritableDatabase();

        // fake name for the dummy product
        String productName = "Dummy Product";

        // fake price for the dummy product
        int productPrice = 100;

        // fake quanitity for the dummy product
        int quantity = 2;

        // fake supplier name for the dummy product
        String supplierName = "Fake Supplier";

        // fake phone number of the supplier
        String supplierPhoneNumber = "+91 8452******";

        // creates and initializes ContentValues object to
        // save data to the database
        ContentValues contentValues = new ContentValues();

        // putting values to the corresponding columns
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_PRICE, productPrice);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITIY, quantity);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        // save the content values to the databse
        long rowId = inventoryDatabase.insert(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY,
                null, contentValues);

        Log.v(TAG_NAME, "Dummy data added to inventory table to row " + String.valueOf(rowId));

    }

    // this function queries data from the database
    // when called
    private Cursor getData() {

        // get the writeable type instance of inventory database
        inventoryDatabase = inventoryDBHelper.getWritableDatabase();

        // below string array contains the name of the columns to return
        String[] projection = {InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryFeedEntry.COLUMN_QUANTITIY};

        // below string holds conditions to filter the data
        String selection = null;

        // string array contains arguments to the above condition string
        String[] selectionArgs = null;

        // returns a cursor which holds data returned from the query
        return inventoryDatabase.query(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close the DBHelper class when the activity is
        // destroyed
        inventoryDBHelper.close();
    }
}
