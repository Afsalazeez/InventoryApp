package com.example.anjikkadans.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the inventory app
 */

public class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor
    private InventoryContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and it's website. A convenient string to use for the
     * content authority is the package name of the app, which is guaranteed to be unique on the
     * device
     */

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which all apps use to contact
     * the content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to the base URI for possible URI's)
     * For instance, content://com.example.android.inventory/inventory/ is a valid path for
     * looking at pet data. content://com.example.android.inventory/stock will fail,
     * as the ContentProvider hasn't been given any information on what to do with "stock".
     */
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class which defines all constants for the inventory_table database table.
     * Each entry in the table represents an inventory item.
     */
    public static final class InventoryFeedEntry implements BaseColumns {
        /**
         * The content URI  to access the inventory item data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type for the {@link #CONTENT_URI} for a list of inventory items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type for the {@link #CONTENT_URI} for a single inventory item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;


        /**
         * Name of the database table for inventory items.
         */
        public static final String TABLE_NAME_INVENTORY = "inventory_table";

        /**
         * Name of the item.
         * <p>
         * Type : TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "product_name";

        /**
         * Price or the item.
         * <p>
         * Type : INTEGER
         */
        public static final String COLUMN_PRICE = "price";
        /**
         * Type of the item available at store
         * <p>
         * Type : INTEGER
         */
        public static final String COLUMN_ITEM_TYPE = "type";
        /**
         * Quantity of the item available at store
         * <p>
         * Type : INTEGER
         */

        public static final String COLUMN_QUANTITY = "quantity";

        /**
         * Shor description of the item
         * <p>
         * Type : TEXT
         */
        public static final String COLUMN_DESCRIPTION = "description";

        /**
         * Name of the supplier
         * <p>
         * Type : TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number of the supplier
         * <p>
         * Type : TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * Integer values for storing the item type
         */
        public static final int KILO_TYPE = 0;
        public static final int COUNT_TYPE = 1;

    }
}
