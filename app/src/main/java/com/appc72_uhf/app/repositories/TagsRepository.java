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

    public boolean InsertTag(String RFID, int idInventory, String IdHardware, String TID, Integer TagStatus) {
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
    public Boolean ClearTags(String idInventory){
        AdminSQLOpenHelper admin= new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resp=false;
        try{

            Cursor delete=db.rawQuery("DELETE FROM Tags WHERE InventoryId=\""+idInventory+"\"", null);
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

        return datos;
    }

    public boolean DeleteAllTags(int inventoryId){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        boolean resp = false;

        try{
            Cursor read= db.rawQuery("DELETE FROM DetailForDevice WHERE InventoryId="+inventoryId, null);
            Cursor read2= db.rawQuery("DELETE FROM Tags WHERE InventoryId=\""+inventoryId+"\"", null);

            if (read.moveToFirst() && read2.moveToFirst()) {
                resp=true;
            }
        }catch (SQLException ex){
            ex.getLocalizedMessage();
            resp=false;
        }


        return resp;
    }


    public ArrayList ViewAllTags(String inventoryId){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT RFID, InventoryId, IdHardware, TID, TagStatus FROM Tags WHERE InventoryId='"+inventoryId+"'", null);

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
