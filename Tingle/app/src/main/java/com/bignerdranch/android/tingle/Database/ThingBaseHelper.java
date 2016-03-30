package com.bignerdranch.android.tingle.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.tingle.Database.ThingDBSchema.ThingTable;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "thingBase.db";

    public ThingBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ThingTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        ThingTable.Cols.UUID + ", " +
                        ThingTable.Cols.WHAT + ", " +
                        ThingTable.Cols.WHERE + ", " +
                        ThingTable.Cols.DATE + ", " +
                        ThingTable.Cols.BARCODE +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
