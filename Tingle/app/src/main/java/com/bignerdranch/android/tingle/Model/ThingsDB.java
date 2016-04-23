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
    //Singleton database
    private static ThingsDB sThingsDB;

    //Context and database.
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //Return singleton if one has already been initialized, else initialize a new one and return it.
    public static ThingsDB get(Context context) {
        if(sThingsDB == null) {
            sThingsDB = new ThingsDB(context);
        }
        return sThingsDB;
    }

    //Creates the database if no database has been made. Used by the static get method.
    private ThingsDB(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ThingBaseHelper(mContext).getWritableDatabase();
    }

    /**
     * Wraps our values in a ContentValues and returns it. This ContentValue will be used
     * to put or update stuff in our database.
     *
     * @param thing - The thing to wrap in a ContentValues type.
     * @return - Return the wrapped thing.
     */
    private static ContentValues getContentValues(Thing thing) {
        ContentValues values = new ContentValues();
        values.put(ThingTable.Cols.UUID, thing.getId().toString());
        values.put(ThingTable.Cols.WHAT, thing.getWhat());
        values.put(ThingTable.Cols.WHERE, thing.getWhere());
        values.put(ThingTable.Cols.DATE, thing.getDate().toString());
        values.put(ThingTable.Cols.BARCODE, thing.getBarcode());

        return values;
    }

    /**
     * Method being used to query for things.
     *
     * @param whereClause - The where clause.
     * @param whereArgs - Arguments to our where clause.
     * @param orderBy - order the results by for example date added.
     * @return - Returns our cursor in our own cursor wrapper.
     */
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

    /**
     * Add method.
     *
     * @param thing - Thing to add to our database.
     */
    public void addThing(Thing thing) {
        ContentValues values = getContentValues(thing);
        mDatabase.insert(ThingTable.NAME, null, values);

        setChanged();
        notifyObservers();
    }

    /**
     * Get method.
     *
     * @param uuid - UUID of the thing we want from our database.
     * @return - Returns the thing found, with the given UUID.
     */
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

    /**
     * Get photo of thing.
     *
     * @param thing - The thing we want to get the photo of.
     * @return - Returns a file pointing to our photo resource.
     */
    public File getPhotoFile(Thing thing) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, thing.getPhotoFilename());
    }

    /**
     * Get every thing with the specific what.
     *
     * @param what - What of the things to be searched for.
     * @return - Returns all found things with the given what value.
     */
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

    /**
     * Remove method.
     *
     * @param uuid - UUID of the thing we want to remove.
     */
    public void remove(UUID uuid) {
        String uuidString = uuid.toString();

        mDatabase.delete(ThingTable.NAME, ThingTable.Cols.UUID + " = ?", new String[]{uuidString});

        setChanged();
        notifyObservers();
    }

    /**
     * Update method.
     *
     * @param thing - UUID of the thing we want to update.
     */
    public void update(Thing thing) {
        String uuidString = thing.getId().toString();
        ContentValues values = getContentValues(thing);

        mDatabase.update(ThingTable.NAME, values, ThingTable.Cols.UUID + " = ?", new String[]{uuidString});

        setChanged();
        notifyObservers();
    }

    /**
     * Get every item in our database table.
     *
     * @return - Returns a list containing every thing that is stored in our database.
     */
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
