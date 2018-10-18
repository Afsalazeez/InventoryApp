package com.example.anjikkadans.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.anjikkadans.inventoryapp.data.InventoryContract;

/**
 * {@link InventoryCursorAdapter} is an adapter for the list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory item data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {


    private final Context mContext;

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {

        super(context, c, 0);
        mContext = context;
    }

    /**
     * Make a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context   app context
     * @param cursor    The cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param viewGroup The parent to which the new view is attached to
     * @return The newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Inflate a list item view using the layout specified in inventory_list_item_view.xml
        return LayoutInflater.from(context).inflate(R.layout.inventory_list_item_view, viewGroup, false);
    }

    /**
     * This method binds the item data( in the current row pointed by the cursor ) to the given
     * list item layout. For example, the name of the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned early by the newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView itemNameTextView = (TextView) view.findViewById(R.id.itemNameTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price_text_view);
        TextView itemQuantityTextView = (TextView) view.findViewById(R.id.item_quantity_text_view);
        Button sellButton = (Button) view.findViewById(R.id.sellButton);

        // Find the columns of item attributes that we're interested in
        int itemIdIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_PRICE);
        int itemTypeColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_ITEM_TYPE);
        int itemQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY);

        // Read the item attributes from the cursor for the current item
        final int itemId = cursor.getInt(itemIdIndex);
        final String itemName = cursor.getString(nameColumnIndex);
        final String itemPrice = String.valueOf(cursor.getInt(priceColumnIndex)) + " $";
        final int itemType = cursor.getInt(itemTypeColumnIndex);
        final int itemQuantity = cursor.getInt(itemQuantityColumnIndex);

        // Update the with attributes for the current item
        itemNameTextView.setText(itemName);
        priceTextView.setText(itemPrice);

        // initializing a String value to paste the display the quantity message
        String display = "";

        // checks if the item is sold per kilogram or per pieces
        if (itemType == InventoryContract.InventoryFeedEntry.KILO_TYPE) {

            display = String.valueOf(itemQuantity) + " Kg left";
        } else {
            display = String.valueOf(itemQuantity) + " Pieces left";
        }
        // printing the display value to {@link itemQuantityTextView}
        itemQuantityTextView.setText(display);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (itemQuantity > 0) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(InventoryContract.InventoryFeedEntry.COLUMN_QUANTITY, itemQuantity - 1);
                    Uri uri = ContentUris.withAppendedId(InventoryContract.InventoryFeedEntry.CONTENT_URI, itemId);
                    mContext.getContentResolver().update(uri, contentValues, null, null);
                }
            }
        });
    }
}
