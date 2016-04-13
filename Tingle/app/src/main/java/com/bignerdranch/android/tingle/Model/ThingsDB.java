package com.bignerdranch.android.tingle.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.tingle.Model.Database.ThingBaseHelper;
import com.bignerdranch.android.tingle.Model.Database.ThingCursorWrapper;
import com.bignerdranch.android.tingle.Model.Database.ThingDBSchema.ThingTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

/**
 * Created by Pierre on 13-02-2016.
 */
public class ThingsDB extends Observable {
    private static ThingsDB sThingsDB;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    //return singleton if one has already ben initialized, else initialize a new one and return it.
    public static ThingsDB get(Context context) {
        if(sThingsDB == null) {
            sThingsDB = new ThingsDB(context);
        }
        return sThingsDB;
    }

    private ThingsDB(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ThingBaseHelper(mContext).getWritableDatabase();
    }

    //Wraps our values in a ContentValues and returns it.
    private static ContentValues getContentValues(Thing thing) {
        ContentValues values = new ContentValues();
        values.put(ThingTable.Cols.UUID, thing.getId().toString());
        values.put(ThingTable.Cols.WHAT, thing.getWhat());
        values.put(ThingTable.Cols.WHERE, thing.getWhere());
        values.put(ThingTable.Cols.DATE, thing.getDate().toString());
        values.put(ThingTable.Cols.BARCODE, thing.getBarcode());

        return values;
    }

    //Method being used to query for things.
    private ThingCursorWrapper queryThings(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = mDatabase.query(
                ThingTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                orderBy  // orderBy
        );

        return new ThingCursorWrapper(cursor);
    }

    //Add method.
    public void addThing(Thing thing) {
        ContentValues values = getContentValues(thing);
        mDatabase.insert(ThingTable.NAME, null, values);

        setChanged();
        notifyObservers();
    }

    //Get method.
    public Thing get(UUID uuid) {
        ThingCursorWrapper cursor = queryThings(ThingTable.Cols.UUID + " = ?", new String[]{uuid.toString()}, null);

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getThing();
        } finally {
            cursor.close();
        }
    }

    //Get photo of item
    public File getPhotoFile(Thing thing) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, thing.getPhotoFilename());
    }

    //Get every item with the specific what.
    public List<Thing> getThings(String what) {
        ThingCursorWrapper cursor = queryThings(ThingTable.Cols.WHAT + " LIKE ?", new String[]{what+"%"}, ThingTable.Cols.WHAT);

        List<Thing> things = new ArrayList<>();

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            things.add(cursor.getThing());
            cursor.moveToNext();
        }

        return things;
    }

    //Remove method.
    public void remove(UUID uuid) {
        String uuidString = uuid.toString();

        mDatabase.delete(ThingTable.NAME, ThingTable.Cols.UUID + " = ?", new String[]{uuidString});

        setChanged();
        notifyObservers();
    }

    //Update method.
    public void update(Thing thing) {
        String uuidString = thing.getId().toString();
        ContentValues values = getContentValues(thing);

        mDatabase.update(ThingTable.NAME, values, ThingTable.Cols.UUID + " = ?", new String[]{uuidString});

        setChanged();
        notifyObservers();
    }

    //Get every item in our database table.
    public List<Thing> getThingsDB() {
        List<Thing> things = new ArrayList<>();

        ThingCursorWrapper cursor = queryThings(null, null, ThingTable.Cols.WHAT);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            things.add(cursor.getThing());
            cursor.moveToNext();
        }
        cursor.close();

        return things;
    }
}
