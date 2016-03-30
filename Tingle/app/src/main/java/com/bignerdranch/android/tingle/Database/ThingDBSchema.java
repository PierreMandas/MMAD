package com.bignerdranch.android.tingle.Database;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingDBSchema {
    public static final class ThingTable {
        public static final String NAME = "things";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String WHAT = "what";
            public static final String WHERE = "whereC";
            public static final String DATE = "date";
            public static final String BARCODE = "Barcode";
        }
    }
}