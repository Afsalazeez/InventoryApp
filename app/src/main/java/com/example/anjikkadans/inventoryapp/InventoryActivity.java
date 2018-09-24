package com.example.anjikkadans.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.anjikkadans.inventoryapp.data.InventoryContract;
import com.example.anjikkadans.inventoryapp.data.InventoryDBHelper;

/**
 * This class in future may list all the items in the
 * inventory with details
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // declares the InventoryDBHelper class to access from
    private InventoryDBHelper inventoryDBHelper;

    // declares the SQLiteDatabase instance for the inventory database
    private SQLiteDatabase inventoryDatabase;

    // tag name for messages through logs
    private static final String TAG_NAME = InventoryActivity.class.getSimpleName();

    /**
     * Identifier for the inventory data loader
     */
    private static final int INVENTORY_LOADER = 1;

    /**
     * Adapter for the listView
     */
    private InventoryCursorAdapter inventoryCursorAdapter;

    /**
     * Declaring the ListView to list the items in the inventory database
     */
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // initializes the inventoryDBHelper class
        inventoryDBHelper = new InventoryDBHelper(this);

        // initializing the items listView
        listView = (ListView) findViewById(R.id.inventory_list_view);

        // initializing empty view which should be shown when list view is empty
        View emptyView = (View) findViewById(R.id.empty_text_view);

        // hooking the emptyView to the listView
        listView.setEmptyView(emptyView);

        // initializing the inventoryCursorAdapter to null
        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);

        // setting the inventoryCursorAdapter to the  inventory listView
        listView.setAdapter(inventoryCursorAdapter);

        // calling the loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

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
        contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY, quantity);
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
                InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY};

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


    // Called when a new Loader is to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Projection string array describes the columns which should be
        // fetched from the query
        String[] projection = {InventoryContract.InventoryFeedEntry._ID, InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryFeedEntry.COLUMN_PRICE, InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed
        return new CursorLoader(this, InventoryContract.InventoryFeedEntry.CONTENT_URI, projection,
                null, null, null);
    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new Cursor in. ( The framework will take care of closing the
        // old cursor once we return.)
        inventoryCursorAdapter.swapCursor(data);
    }

    // called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer use it.
        inventoryCursorAdapter.swapCursor(null);

    }
}
