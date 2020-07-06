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

    public boolean InventoryInsert(String id, String name, String detailForDevice, int InventoryStatus, int codeCompany, boolean IncludeTID, int isSelect) {
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor readInventories=db.rawQuery("SELECT * FROM Inventory WHERE Id='"+id+"'", null);
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("Id", id);
            reg.put("Name", name);
            reg.put("InventoryStatus", InventoryStatus);
            reg.put("DetailForDevice", detailForDevice);
            reg.put("CompanyId", codeCompany);
            reg.put("IncludeTID", String.valueOf(IncludeTID));
            reg.put("IsSelect", isSelect);

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

    public boolean DeleteInventory(String inventoryID){
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        final String MY_TABLE_NAME="Inventory";
        final String MY_TABLE_NAME2="DetailForDevice";
        final ContentValues cv=new ContentValues();
            try {
                db.beginTransaction();
                boolean result=db.delete(MY_TABLE_NAME, "Id='"+inventoryID+"'", null )>0;
                boolean result2=db.delete(MY_TABLE_NAME2, "InventoryId='"+inventoryID+"'", null )>0;
                db.setTransactionSuccessful();
                return result;
            }catch (SQLException sqlex){
                throw  sqlex;
            }finally {
                db.endTransaction();
                db.close();
            }
    }

    public int ViewInventory(String InventoryID){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT * FROM Inventory WHERE Id='"+InventoryID+"'", null);
        int datos=0;
        if (read.moveToFirst()) {
                datos = 1;
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
            db.close();
        }
    }


    public boolean inventoryDetailForDevice(String inventoryId){
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        boolean result=false;
        try{
            Cursor querydetail=db.rawQuery("SELECT DetailForDevice FROM Inventory WHERE InventoryStatus=0 AND DetailForDevice='true' AND Id='"+inventoryId+"'", null);
            if(querydetail.getCount()>0){
                result=true;
            }
        }catch (SQLException e){
            throw e;
        }
        db.close();
        return result;
    }
    public String inventoryWithTID(String inventoryId){
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        String result="";
            Cursor querydetail=db.rawQuery("SELECT IncludeTID FROM Inventory WHERE InventoryStatus=0 AND Id='"+inventoryId+"'", null);
            if(querydetail.moveToFirst()){
                result=querydetail.getString(querydetail.getColumnIndex("IncludeTID"));
            }
        db.close();
        return result;
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
        db.close();
        return datos;
    }

    public ArrayList<String> ViewInventoriesHH(int CompanyId){
        ArrayList<String> datosInventory=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read=db.rawQuery("SELECT Id, Name, DetailForDevice, InventoryStatus, IsSelect FROM Inventory WHERE CompanyId="+CompanyId+" AND IsSelect=1", null);
        if (read.moveToFirst()) {
            do {
                datosInventory.add(read.getString(read.getColumnIndex("Id"))+"@"+read.getString(read.getColumnIndex("Name"))+"@"+read.getString(read.getColumnIndex("DetailForDevice"))+"@"+read.getInt(read.getColumnIndex("InventoryStatus"))+"@"+read.getInt(read.getColumnIndex("IsSelect")));
            } while (read.moveToNext());
        }
        db.close();
        return datosInventory;

    }
}
