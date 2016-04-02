package com.bignerdranch.android.tingle.Model.Database;

public class ThingDBSchema {
    //Our database table schema.
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