package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class TagsRepository {

    private Context context;

    public TagsRepository(Context context) {
        this.context = context;
    }

    public boolean UpdateTagsFound(String RFID, String inventoryId){
        final String NAME_TABLE="DetailForDevice";
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        final ContentValues cv=new ContentValues();
        try{
            db.beginTransaction();
            cv.put("Found", "true");
            final boolean result=db.update(NAME_TABLE, cv, "InventoryId='"+inventoryId+"' AND EPC='"+RFID+"'", null)>0;
            db.setTransactionSuccessful();
            return result;
        }catch (SQLException sqlex){
            throw sqlex;
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    public boolean InsertTag(ArrayList<HashMap<String, String>> maestroTagList, String idInventory, String IdHardware, int TagStatus) { //, String TID Struing RFID
        String lastItem=null;
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try {
        String sqlSentence = "INSERT INTO Tags values(?, ?, ?, ?, ?);";
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt=db.compileStatement(sqlSentence);
        for (int index = 0; index < maestroTagList.size(); index++) {
            //ContentValues reg = new ContentValues();
            String RFID = maestroTagList.get(index).get("tagUii");
            String TID = maestroTagList.get(index).get("tagRssi");
            stmt.bindString(1, RFID);
            stmt.bindString(2, idInventory);
            stmt.bindString(3, IdHardware);
            stmt.bindString(4, TID);
            stmt.bindLong(5, TagStatus);
            stmt.execute();
            if(index==(maestroTagList.size()-1)){
                lastItem=maestroTagList.get(index).get("tagUii");
            }
        }
        db.setTransactionSuccessful();
        /*
            try{
                for (int index = 0; index < maestroTagList.size(); index++) {
                    ContentValues reg = new ContentValues();
                    String RFID = maestroTagList.get(index).get("tagUii");
                    String TID = maestroTagList.get(index).get("tagRssi");
                    reg.put("RFID", RFID);
                    reg.put("InventoryId", idInventory);
                    reg.put("IdHardware", IdHardware);
                    reg.put("TID", TID);
                    reg.put("TagStatus", TagStatus);
                    result = db.insert("Tags", null, reg) > 0;
                    Log.e("result", ""+result);

                }
            }catch (Exception ex){
                result = false;
            }
        */
            result=true;
        }catch (Exception ex){
            result = false;
        }finally {
            db.endTransaction();
        }
        db.close();
        return result;
    }
    public Boolean ClearTags(String idInventory){
        AdminSQLOpenHelper admin= new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resp=false;
        try{

            Cursor delete=db.rawQuery("DELETE FROM Tags WHERE InventoryId='"+idInventory+"'", null);
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

    public boolean DeleteAllTags(String inventoryId){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        boolean resp = false;
        try{
            //Cursor read= db.rawQuery("DELETE FROM DetailForDevice WHERE InventoryId="+inventoryId, null);
            Cursor read2= db.rawQuery("DELETE FROM Tags WHERE InventoryId='"+inventoryId+"'", null);
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


    public ArrayList ViewAllTags(String inventoryId, boolean includeTID){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read= db.rawQuery("SELECT RFID, InventoryId, IdHardware, TID, TagStatus FROM Tags WHERE InventoryId='"+inventoryId+"'", null); //ORDER BY RFID DESC LIMIT 100
        if (read.moveToFirst()) {
            //Cursor readCount2=db.rawQuery("SELECT COUNT(RFID) as countRFID FROM Tags WHERE InventoryId='"+inventoryId+"'", null);
            //if(readCount2.moveToFirst()){
                do {
                    if(includeTID){
                        datos.add(
                                read.getString(
                                        read.getColumnIndex("RFID")
                                )+"@"+read.getString(
                                        read.getColumnIndex("TID")
                                )/*+"@"+readCount2.getInt(
                                        readCount2.getColumnIndex("countRFID")
                                )*/
                        );
                    }else{
                        datos.add(
                                read.getString(
                                        read.getColumnIndex("RFID")
                                )/*+"@"+readCount2.getInt(
                                    readCount2.getColumnIndex("countRFID")
                                )*/
                        );
                    }
                } while (read.moveToNext());
            //}

        }
        return datos;
    }

    public ArrayList ViewAllTagsSync(String inventoryId, boolean includeTID){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read= db.rawQuery("SELECT RFID, InventoryId, IdHardware, TID, TagStatus FROM Tags WHERE InventoryId='"+inventoryId+"'", null); //ORDER BY RFID DESC LIMIT 100
        if (read.moveToFirst()) {
                do {
                    if(includeTID){
                        datos.add(
                                read.getString(
                                        read.getColumnIndex("RFID")
                                )+"@"+read.getString(
                                        read.getColumnIndex("TID")
                                )
                        );
                    }else{
                        datos.add(
                                read.getString(
                                        read.getColumnIndex("RFID")
                                )
                        );
                    }
                    //datos.add(read.getString(read.getColumnIndex("TID")));
                    //datos.add(read.getString(2));
                    //datos.add(read.getString(3));

                } while (read.moveToNext());

        }
        return datos;
    }


    public int countTags(String inventoryId){
        int tags=0;
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
            Cursor readCount2=db.rawQuery("SELECT COUNT(RFID) as countRFID FROM Tags WHERE InventoryId='"+inventoryId+"'", null);
            if(readCount2.moveToFirst()){
                    tags=readCount2.getInt(readCount2.getColumnIndex("countRFID"));
            }

        return tags;
    }

}
