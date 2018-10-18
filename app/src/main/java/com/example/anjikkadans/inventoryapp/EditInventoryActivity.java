package com.example.anjikkadans.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anjikkadans.inventoryapp.data.InventoryContract;

public class EditInventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_PERMISSIONS_REQUEST_CALL = 200;
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

    /**
     * Content URI of the selected pet from the {@link InventoryActivity}
     */
    Uri mCurrentItemUri = null;

    /**
     * Private loader id
     */
    private static final int ITEM_LOADER_ID = 1;
    /**
     * boolean value which changes to true when changes were made on the
     * item selected
     */
    private boolean mItemHasChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Examine the intent that was used to launch this activity
        // in order to figure out if we're creating a new item or editing an existing one
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // if the intent DOES NOT contain an item content URI, then we know that we're
        // creating a new item
        if (mCurrentItemUri == null) {
            // This is a new Item, so change the App bar to say "Add an item"
            setTitle("Add an item");

        } else {
            // otherwise this an existing item, so change the app bar to say "Edit Item"
            setTitle("Edit item");
            // Initializing the loader for calling data on the passed URI
            getSupportLoaderManager().initLoader(ITEM_LOADER_ID, null, this);

        }

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

        // setting onTouchListeners to all edit fields
        mItemTypeSpinner.setOnTouchListener(mTouchListener);
        mItemNameEditText.setOnTouchListener(mTouchListener);
        mItemPriceEditText.setOnTouchListener(mTouchListener);
        mItemQuantityEditText.setOnTouchListener(mTouchListener);
        mItemDescriptionEditText.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierContact.setOnTouchListener(mTouchListener);

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


        if (mCurrentItemUri == null) {

            try {

                // getting the content resolver for the content provider to insert data to the database
                Uri uri = getContentResolver().insert(InventoryContract.InventoryFeedEntry.CONTENT_URI, contentValues);

                Log.v(TAG_NAME, "Item added with uri " + uri);
                NavUtils.navigateUpFromSameTask(this);
            } catch (Exception e) {
                // in case some error happens at the first time
                e.printStackTrace();
            }
        } else {

            // Otherwise this is an EXISTING item, so update the item with the content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify
            int rowsAffected = getContentResolver().update(mCurrentItemUri, contentValues, null, null);

            NavUtils.navigateUpFromSameTask(this);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // projection string array describes the columns which should be
        // fetched from the querry
        String[] projection = {InventoryContract.InventoryFeedEntry._ID, InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryFeedEntry.COLUMN_ITEM_TYPE, InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryFeedEntry.COLUMN_PRICE, InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                InventoryContract.InventoryFeedEntry.COLUMN_DESCRIPTION};

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed
        return new CursorLoader(this, mCurrentItemUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null) {
            Toast.makeText(EditInventoryActivity.this, "Toast", Toast.LENGTH_SHORT).show();
        }

        // move the cursor pointer to the first result
        data.moveToFirst();

        // getting data from the cursor
        String itemName = data.getString(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME));
        int itemPrice = data.getInt(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_PRICE));
        int itemQuantity = data.getInt(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY));
        String supplierName = data.getString(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME));
        String supplierContact = data.getString(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
        int itemType = data.getInt(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_ITEM_TYPE));
        String description = data.getString(data.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_DESCRIPTION));

        // filling the input view with the data from the cursor
        mItemNameEditText.setText(itemName);
        mItemPriceEditText.setText(String.valueOf(itemPrice));
        mItemQuantityEditText.setText(String.valueOf(itemQuantity));
        mSupplierName.setText(supplierName);
        mSupplierContact.setText(supplierContact);
        mItemTypeSpinner.setId(itemType);
        mItemDescriptionEditText.setText(description);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Clear all fields if this happen
        mItemNameEditText.setText("");
        mItemPriceEditText.setText("");
        mItemQuantityEditText.setText("");
        mSupplierContact.setText("");
        mSupplierName.setText("");
        mItemTypeSpinner.setId(0);
    }

    /**
     * Perform the deletion of the item in the database
     */
    public void deleteItem() {

        // Call the ContentResolver to delete the item at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentItemUri
        // content URI already identifies the pet that we want.
        int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were not deleted, then there was an error with the delete.
            Toast.makeText(this, "Error deleting this item", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();

    }


    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mItemHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.homeAsUp:
                // If the item hasn't changed, continue with navigation up to parent activity
                // which is the {@link InventoryActivity}
                if (!mItemHasChanged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditInventoryActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (mCurrentItemUri == null) {
            return super.onCreateOptionsMenu(menu);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    public void decreaseQuantity(View view) {

        String quantityString = mItemQuantityEditText.getText().toString();

        try {
            int quantity = Integer.parseInt(quantityString);
            if (quantity > 0) {
                mItemQuantityEditText.setText(String.valueOf(quantity - 1));
            }
        } catch (Exception e) {

        }

    }

    public void increaseQuantity(View view) {

        String quantityString = mItemQuantityEditText.getText().toString();

        try {
            int quantity = Integer.parseInt(quantityString);

            mItemQuantityEditText.setText(String.valueOf(quantity + 1));
        } catch (Exception e) {

        }
    }

    public void startPhoneIntent(View view) {

        if (mCurrentItemUri == null) {
            return;
        }

        String phoneNumber = mSupplierContact.getText().toString();

        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);

        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (phoneIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(phoneIntent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }

        } else {
            startActivity(phoneIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String phoneNumber = mSupplierContact.getText().toString();

                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);

                    phoneIntent.setData(Uri.parse("tel:" + phoneNumber));

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(phoneIntent);
                    }
                }
        }
    }
}
