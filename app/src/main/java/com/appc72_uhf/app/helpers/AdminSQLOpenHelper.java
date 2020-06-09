package com.appc72_uhf.app.helpers;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="IZYRFID.db";
    public static final int DATABASE_VERSION=3;


    public AdminSQLOpenHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public AdminSQLOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler){
        //public AdminSQLOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF  NOT EXISTS Tags(" +
                "RFID TEXT UNIQUE, " +
                "InventoryId INTEGER, " +
                "IdHardware TEXT, " +
                "TID TEXT, " +
                "TagStatus INTEGER);"); //String, String, Float

        db.execSQL("CREATE TABLE IF  NOT EXISTS Inventory(" +
                "Id INTEGER UNIQUE, " +
                "CompanyId INTEGER, " +
                "Name TEXT, " +
                "DetailForDevice TEXT, " +
                "InventoryStatus INTEGER,"+
                "IsSelect INTEGER);"); //String, String, Float //isSelect=1 true || 0 false

        db.execSQL("CREATE TABLE IF  NOT EXISTS Device(" +
                "Id INTEGER UNIQUE, " +
                "Name TEXT, " +
                "Description TEXT, " +
                "IsActive TEXT, " +
                "IsAssigned TEXT, " +
                "CompanyId INTEGER, " +
                "HardwareId TEXT, " +
                "TakingInventory TEXT, " +
                "AssignedResponse TEXT, " +
                "MakeLabel TEXT);");

        db.execSQL("CREATE TABLE IF  NOT EXISTS Company(" +
                "Id INTEGER UNIQUE, " +
                "Name TEXT,"+
                "IsActive TEXT,"+
                "Code TEXT);");

        db.execSQL("CREATE TABLE IF  NOT EXISTS DetailForDevice(" +
                "Id INTEGER UNIQUE, " +
                "EPC TEXT,"+
                "Code TEXT,"+
                "Name TEXT,"+
                "Found TEXT,"+
                "ProductMasterId INTEGER, " +
                "InventoryId INTEGER);");//String, String, Float

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Tags");
        db.execSQL("DROP TABLE IF EXISTS Inventory");
        db.execSQL("DROP TABLE IF EXISTS Device");
        db.execSQL("DROP TABLE IF EXISTS Company");
        db.execSQL("DROP TABLE IF EXISTS DetailForDevice");
    }

}
