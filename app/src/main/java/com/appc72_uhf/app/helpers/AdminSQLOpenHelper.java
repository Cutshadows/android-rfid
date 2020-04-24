package com.appc72_uhf.app.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "IZYSEARCH";
    private static final int DATABASE_VERSION=3;


    public AdminSQLOpenHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public AdminSQLOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF  NOT EXISTS Tags(" +
                "RFID TEXT UNIQUE, " +
                "InventoryId TEXT, " +
                "IdHardware TEXT, " +
                "TID TEXT, " +
                "TagStatus " +
                "INTEGER);"); //String, String, Float
        db.execSQL("CREATE TABLE IF  NOT EXISTS Inventory(" +
                "Id TEXT UNIQUE, " +
                "CompanyId INTEGER, " +
                "Name TEXT, " +
                "DetailForDevice TEXT, " +
                "InventoryStatus INTEGER);"); //String, String, Float
        db.execSQL("CREATE TABLE IF  NOT EXISTS Device(" +
                "Id INTEGER UNIQUE, " +
                "Name TEXT, " +
                "Description TEXT, " +
                "IsActive TEXT, " +
                "IsAssigned TEXT, " +
                "CompanyId INTEGER, " +
                "HardwareId TEXT, " +
                "TakingInventory TEXT, " +
                "MakeLabel TEXT);"); //String, String, Float

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Tags");
        db.execSQL("DROP TABLE IF EXISTS Inventory");
        db.execSQL("DROP TABLE IF EXISTS Device");
    }

}
