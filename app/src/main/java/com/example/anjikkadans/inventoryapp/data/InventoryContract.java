package com.example.anjikkadans.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * This class holds all the string constants for the
 * column names and table names in the inventory database
 */

public class InventoryContract {

    // constructor for the InventoryContract class
    private InventoryContract() {
    }

    // class which holds table string value constants of table
    // named 'inventory'
    public static final class InventoryFeedEntry implements BaseColumns {

        public static final String TABLE_NAME_INVENTORY = "inventory_table";

        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITIY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }
}
