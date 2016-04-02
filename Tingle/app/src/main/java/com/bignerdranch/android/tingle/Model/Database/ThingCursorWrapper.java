package com.bignerdranch.android.tingle.Model.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.tingle.Model.Thing;

import com.bignerdranch.android.tingle.Model.Database.ThingDBSchema.ThingTable;

import java.util.UUID;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingCursorWrapper extends CursorWrapper {
    public ThingCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Thing getThing() {
        String uuidString = getString(getColumnIndex(ThingTable.Cols.UUID));
        String what = getString(getColumnIndex(ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(ThingTable.Cols.WHERE));
        String date = getString(getColumnIndex(ThingTable.Cols.DATE));
        String Barcode = getString(getColumnIndex(ThingTable.Cols.BARCODE));

        Thing thing = new Thing(UUID.fromString(uuidString), what, where);
        thing.setDate(date);
        thing.setBarcode(Barcode);

        return thing;
    }
}
