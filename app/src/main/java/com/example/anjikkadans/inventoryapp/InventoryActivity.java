package com.example.anjikkadans.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
     * Floating Action Button to add new items
     */
    private FloatingActionButton actionButton;

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                // creates an intent to go to {@link EditInventoryActivity}
                Intent EditInventoryIntent = new Intent(InventoryActivity.this, EditInventoryActivity.class);

                // from the CONTENT_URI that represents the specific item that was clicked on,
                // by appending the "id" ( passed as input to this method ) onto the
                // {@link InventoryFeedEntry#CONTENT_URI}
                Uri dataUri = ContentUris.withAppendedId(InventoryContract.InventoryFeedEntry.CONTENT_URI, l);

                // set the URI on the data field of the intent
                EditInventoryIntent.setData(dataUri);

                // Launch the (@link EditInventoryActivity} to display the data for the current item
                startActivity(EditInventoryIntent);
            }
        });


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

        // initializing action button
        actionButton = (FloatingActionButton) findViewById(R.id.add_button);

        // adding an action when action button is clicked
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to Insert a new item to the inventory database
                Intent newItemIntent = new Intent(InventoryActivity.this, EditInventoryActivity.class);
                startActivity(newItemIntent);
            }
        });

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
                InventoryContract.InventoryFeedEntry.COLUMN_PRICE, InventoryContract.InventoryFeedEntry.COLUMN_ITEM_TYPE,
                InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY};

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                deleteAllItems();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all items in the database
     */
    public void deleteAllItems() {

        int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryFeedEntry.CONTENT_URI,
                null, null);

    }
}
