package com.example.anjikkadans.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.anjikkadans.inventoryapp.data.InventoryContract;

/**
 * {@link InventoryCursorAdapter} is an adapter for the list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory item data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
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
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView itemNameTextView = (TextView) view.findViewById(R.id.itemNameTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.itemPriceTextView);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryFeedEntry.COLUMN_PRICE);

        // Read the item attributes from the cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = String.valueOf(cursor.getInt(priceColumnIndex)) + " $";

        // Update the with attributes for the current item
        itemNameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
    }
}
