package com.example.anjikkadans.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class connects app to the data stored in
 * sqLiteDatabase
 */
public class InventoryDBHelper extends SQLiteOpenHelper {

    // private final String for holding the database name
    private static final String DATABASE_NAME = "inventory.db";

    // private final int for holding the database version number
    private static final int DATABASE_VERSION = 1;


    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // this string holds statement for creating the inventory_table
        String CREATE_TABLE_INVENTORY =
                "CREATE TABLE " + InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY + "("
                        + InventoryContract.InventoryFeedEntry._ID + " INTEGER PRIMARY KEY AUTO INCREMENT, "
                        + InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                        + InventoryContract.InventoryFeedEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                        + InventoryContract.InventoryFeedEntry.COLUMN_QUANTITIY + " INT NOT NULL, "
                        + InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_NAME + " TEXT , "
                        + InventoryContract.InventoryFeedEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT "
                        + ");";

        // creates the inventory_table when the database is first created
        sqLiteDatabase.execSQL(CREATE_TABLE_INVENTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        
        // onUpgrade we delete the existing table and creates a new one
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryContract.InventoryFeedEntry.TABLE_NAME_INVENTORY);
        onCreate(sqLiteDatabase);
    }
}
