package com.bignerdranch.android.tingle;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Pierre on 07-02-2016.
 */
public class Thing{
    private UUID mId = null;
    private String mWhat = null;
    private String mWhere = null;
    private String mDate = null;
    private String mQRcode = null;
    private String mBarcode = null;

    //Thing constructor
    public Thing(UUID id, String what, String where) {
        mId = id;
        mWhat = what;
        mWhere = where;
        mDate = new Date().toString();
    }

    //Getters, Setters and toString
    public UUID getId() { return mId; }
    public String getWhat() { return mWhat; }
    public void setWhat(String what) { mWhat = what; }
    public String getWhere() { return mWhere; }
    public void setWhere(String where) { mWhere = where; }
    public String getDate() { return mDate; }
    public void setDate(String Date) { mDate = Date; }
    public String getQRcode() { return mQRcode; }
    public void setQRcode(String mQRcode) { this.mQRcode = mQRcode; }
    public String getBarcode() { return mBarcode; }
    public void setBarcode(String mBarcode) { this.mBarcode = mBarcode; }
}
