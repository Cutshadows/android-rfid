package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;

public class TagsRepository {

    private Context context;

    public TagsRepository(Context context) {
        this.context = context;
    }

    public boolean InsertTag(String RFID, String IdHardware, int idInventory, String TID, Integer TagStatus) {
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
        boolean resp;
        try{
            Cursor delete=db.rawQuery("DELETE FROM Tags WHERE InventoryId="+idInventory, null);
            if(delete.moveToFirst()){
                resp=true;

            }else{
                resp=false;
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


    public ArrayList ViewAllTags(){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT RFID, InventoryId, IdHardware, TID, TagStatus FROM Tags", null);

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
