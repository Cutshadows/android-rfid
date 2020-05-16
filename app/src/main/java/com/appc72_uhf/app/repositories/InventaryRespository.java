package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;

public class InventaryRespository {
    private Context context;

    public InventaryRespository(Context context) {
        this.context = context;
    }

    public boolean InventoryInsert(int id, String name, int InventoryStatus, String detailForDevice, int codeCompany) {
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor readInventories=db.rawQuery("SELECT * FROM Inventory WHERE Id="+id, null);
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("Id", id);
            reg.put("Name", name);
            reg.put("InventoryStatus", InventoryStatus);
            reg.put("DetailForDevice", detailForDevice);
            reg.put("CompanyId", codeCompany);
            reg.put("IsSelect", 0);

            if(readInventories.getCount()>0){
                result = false;
            }else{
                db.insert("Inventory", null, reg);
                result = true;
            }

        } catch (Exception ex) {
            result = false;
        }

        db.close();

        return result;
    }

    public int ViewInventory(String NameInventory){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT Id FROM Inventory WHERE Name=\""+NameInventory+"\"", null);
        int datos=0;
        if (read.moveToFirst()) {
                datos = read.getInt(read.getColumnIndex("Id"));
        }
        db.close();
        return datos;
    }

    public boolean UpdateSelect(int inventoryId, int Status){
        final String MY_TABLE_NAME="Inventory";
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        final ContentValues cv=new ContentValues();
        try{
            cv.put("IsSelect", Status);
            db.beginTransaction();
           final boolean result=db.update(MY_TABLE_NAME, cv, "Id="+inventoryId, null)>0;
            db.setTransactionSuccessful();
            return result;
        }catch (SQLException e){
            throw e;
        }finally {
            db.endTransaction();
        }
    }


    public ArrayList<String> ViewAllInventories(){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT Id, CompanyId, Name, DetailForDevice, InventoryStatus, IsSelect FROM Inventory ORDER BY DetailForDevice DESC", null);

        if (read.moveToFirst()) {
            do {
                datos.add(read.getString(read.getColumnIndex("Id"))+"@"+read.getString(read.getColumnIndex("Name"))+"@"+read.getString(read.getColumnIndex("DetailForDevice"))+"@"+read.getInt(read.getColumnIndex("InventoryStatus"))+"@"+read.getInt(read.getColumnIndex("IsSelect")));
            } while (read.moveToNext());
        }
        return datos;
    }

    public ArrayList<String> ViewInventoriesHH(int CompanyId){
        ArrayList<String> datosInventory=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read= db.rawQuery("SELECT Id, CompanyId, Name, DetailForDevice, InventoryStatus, IsSelect FROM Inventory WHERE CompanyId="+CompanyId+" AND IsSelect=1 ORDER BY DetailForDevice  DESC", null);
        if (read.moveToFirst()) {
            do {
                datosInventory.add(read.getString(read.getColumnIndex("Id"))+"@"+read.getString(read.getColumnIndex("Name"))+"@"+read.getString(read.getColumnIndex("DetailForDevice"))+"@"+read.getInt(read.getColumnIndex("InventoryStatus"))+"@"+read.getInt(read.getColumnIndex("IsSelect")));
            } while (read.moveToNext());
        }
        return datosInventory;
    }
}
