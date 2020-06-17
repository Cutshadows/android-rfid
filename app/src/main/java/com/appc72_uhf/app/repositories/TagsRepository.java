package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;

public class TagsRepository {

    private Context context;

    public TagsRepository(Context context) {
        this.context = context;
    }

    public boolean UpdateTagsFound(String RFID, int inventoryId){
        final String NAME_TABLE="DetailForDevice";
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        final ContentValues cv=new ContentValues();
        try{
            db.beginTransaction();
            cv.put("Found", "true");
            final boolean result=db.update(NAME_TABLE, cv, "InventoryId="+inventoryId+" AND EPC=\""+RFID+"\"", null)>0;
            db.setTransactionSuccessful();
            return result;
        }catch (SQLException sqlex){
            throw sqlex;
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    public boolean InsertTag(String RFID, int idInventory, String IdHardware, String TID, int TagStatus) {
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try{
            ContentValues reg = new ContentValues();
            reg.put("RFID", RFID);
            reg.put("InventoryId", idInventory);
            reg.put("IdHardware", IdHardware);
            reg.put("TID", TID);
            reg.put("TagStatus", TagStatus);

            db.insert("Tags", null, reg);
            result=true;
        }catch (Exception ex){
            result = false;
        }
        db.close();
        return result;
    }
    public Boolean ClearTags(int idInventory){
        AdminSQLOpenHelper admin= new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resp=false;
        try{

            Cursor delete=db.rawQuery("DELETE FROM Tags WHERE InventoryId="+idInventory, null);
            if(!delete.moveToFirst()){
                resp=true;
            }
        }catch (Exception ex){
            resp=false;
        }
        db.close();

        return resp;
    }

    public String ViewTags(){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT * FROM Tags", null);
        String datos = "";

        if (read.moveToFirst()) {
            do {
                datos = (read.getString(0));

            } while (read.moveToNext());
        }
        db.close();
        return datos;
    }

    public boolean DeleteAllTags(int inventoryId){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        boolean resp = false;
        try{
            //Cursor read= db.rawQuery("DELETE FROM DetailForDevice WHERE InventoryId="+inventoryId, null);
            Cursor read2= db.rawQuery("DELETE FROM Tags WHERE InventoryId="+inventoryId, null);
            if (!read2.moveToFirst()) {
                resp=true;
            }
        }catch (SQLException ex){
            ex.getLocalizedMessage();
            resp=false;
        }

        db.close();
        return resp;
    }


    public ArrayList ViewAllTags(int inventoryId){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT RFID, InventoryId, IdHardware, TID, TagStatus FROM Tags WHERE InventoryId="+inventoryId, null);

        if (read.moveToFirst()) {
            do {
                datos.add(read.getString(read.getColumnIndex("RFID"))+"@"+read.getString(read.getColumnIndex("TID")));
                //datos.add(read.getString(read.getColumnIndex("TID")));
                //datos.add(read.getString(2));
                //datos.add(read.getString(3));

            } while (read.moveToNext());
        }
        return datos;
    }

}
