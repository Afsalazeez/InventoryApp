package com.example.anjikkadans.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * {@link android.content.ContentProvider} for inventory app.
 */
public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * URI matcher code content URI for the inventory items table.
     */
    private static final int INVENTORY_ITEMS = 200;

    /**
     * URI matcher code content URI for a single item in the inventory table.
     */
    private static final int INVENTORY_ITEM_ID = 201;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed to the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This runs first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all the content URI patterns that the provider
        // should recognize. All the paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.inventory/inventory" will map to the
        // integer code {@link #INVENTORY_ITEMS}. This URI is used to provide access to MULTIPLE rows
        // of the inventory table.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY_ITEMS);

        // The content URI of the form "content://com.example.android.inventory/inventory/#" will map to the
        // integer code {@link #INVENTORY_ITEM_ID}. This URI is used to provide access ONE single row
        // of the inventory table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.inventory/inventory/2" matches, but
        // "content://com.example.android.inventory/inventory" (without a number at the end) doesn't match.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ITEM_ID);
    }

    /**
     * Database helper object
     */
    private InventoryDBHelper inventoryDBHelper;

    @Override
    public boolean onCreate() {
        // creates an instance of the {@link InventoryDBHelper} class.
        inventoryDBHelper = new InventoryDBHelper(getContext());
        return true;
    }

    /**
     * @param uri           URI for fetching data from database
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return cursor with data loaded from the database
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = inventoryDBHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                // For the INVENTORY_ITEMS code, query the inventory table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the inventory table.
                cursor = database.query(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case INVENTORY_ITEM_ID:
                // For the INVENTORY_ITEM_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventory/inventory/2",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 2 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have only one question mark in the
                // selection, we have one String in the selection arguments String array.

                selection = InventoryContract.InventoryFeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the inventory table where the _ID equals to 2 to return a
                // Cursor containing that row of the table.

                cursor = database.query(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                return InventoryContract.InventoryFeedEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ITEM_ID:
                return InventoryContract.InventoryFeedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknow URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        // Check the match condition with INVENTORY_ITEMS code
        switch (match) {
            case INVENTORY_ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    /**
     * Insert an inventory item into the database with the given content values.
     *
     * @param uri
     * @param contentValues
     * @return the new content URI
     */
    private Uri insertItem(Uri uri, ContentValues contentValues) {

        // Check that the item name is not null
        String name = contentValues.getAsString(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        // If the price is provided, check that the it's greater than or equal to 0
        Integer price = contentValues.getAsInteger(InventoryContract.InventoryFeedEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Item requires a valid price");
        }
        // If the quantity is provided, check that it's value is greater than or equal to 0
        Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Item requires a valid quantity");
        }

        // If the supplier name is provided, check that value is not null
        String supplierName = contentValues.getAsString(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Item requires a valid supplier name");
        }

        // If the supplier phone number is provided, check if it's not null
        String supplierPhoneNumber = contentValues.getAsString(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Item requires a valid supplier phone number");
        }

        // Get write able database
        SQLiteDatabase database = inventoryDBHelper.getWritableDatabase();

        // Insert the new item with given values
        Long id = database.insert(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY, null, contentValues);

        // If the id is -1, then the insertion is failed. Log an error and return null
        if (id == -1) {
            Log.e(LOG_TAG, "failed to insert row for " + uri);
            return null;
        }

        // Notify all the listeners that the data has changed for the item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID ( of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get write able database
        SQLiteDatabase database = inventoryDBHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                // Delete all the rows match the selection and selection arguments
                rowsDeleted = database.delete(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY, selection, selectionArgs);
                break;
            case INVENTORY_ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContract.InventoryFeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // get the number of rows deleted
                rowsDeleted = database.delete(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all the listeners the data of the
        // given URI has changed
        if (rowsDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ITEM_ID:
                // For the INVENTORY_ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a string array containing the actual ID.
                selection = InventoryContract.InventoryFeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update inventory items in the database with the given content values. Apply changes to the rows
     * specified in the selection and selection arguments (which would be 0 or 1 or more items).
     *
     * @param uri           URI where data should be updated
     * @param contentValues these values are inserted to the uri
     * @param selection     string for storing the condition of rows to be updated
     * @param selectionArgs contains values to be filled in the selection string
     * @return number of rows updated
     */
    private int updateInventory(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // If the {@link InventoryFeedEntry#COLUMN_PRODUCT_NAME} key is present
        // check that the name value is not null
        if (contentValues.containsKey(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        // If the {@link InventoryFeedEntry#COLUMN_PRICE} key is present
        // check that value is greater than 0
        if (contentValues.containsKey(InventoryContract.InventoryFeedEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(InventoryContract.InventoryFeedEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Item requires a valid price");
            }
        }

        // If the {@link InventoryFeedEntry#COLUMN_QUANTITY} key is present
        // check that value equal or greater than zero
        if (contentValues.containsKey(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Item requires a valid quantity");
            }
        }

        // If the {@link InventoryFeedEntry#COLUMN_SUPPLIER_NAME} value is present
        // check that value is not null
        if (contentValues.containsKey(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = contentValues.getAsString(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Item requires a valid supplier name");
            }
        }

        // If the {@link InventoryFeedEntry#COLUMN_SUPPLIER_PHONE_NUMBER} value is present
        // check that value is not null
        if (contentValues.containsKey(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = contentValues.getAsString(InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Item requires a valid supplier phone number");
            }

        }

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        // Otherwise, get the write able database
        SQLiteDatabase database = inventoryDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY, contentValues, selection, selectionArgs);

        // If one or more rows are updated, then notify all the listeners that the data of the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }


}
