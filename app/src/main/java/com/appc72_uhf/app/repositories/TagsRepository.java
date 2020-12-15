package com.appc72_uhf.app.repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class TagsRepository {

    private Context context;

    public TagsRepository(Context context) {
        this.context = context;
    }

    //public boolean UpdateTagsFound(String RFID, String inventoryId){
    public int UpdateTagsFound(ArrayList<HashMap<String, String>> listMaster, String inventoryId){
        //final String NAME_TABLE="DetailForDevice";
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        int counterState=0;

        //final ContentValues cv=new ContentValues();
        try{
            db.beginTransactionNonExclusive();

            String sqlSentenceUpdate="UPDATE DetailForDevice SET Found=? WHERE InventoryId=? AND EPC=?";
            SQLiteStatement stmt=db.compileStatement(sqlSentenceUpdate);

            for (int index = 0; index < listMaster.size(); index++) {
                //ContentValues reg = new ContentValues();
                String RFID = listMaster.get(index).get("tagUii");
                Log.e("EPC", ""+RFID);
                stmt.bindString(1, "true");
                stmt.bindString(2, inventoryId);
                stmt.bindString(3, RFID);
                stmt.executeUpdateDelete();
            }
            db.setTransactionSuccessful();

            Cursor readCountRFID=null;
            try {
                readCountRFID = db.rawQuery("SELECT COUNT(EPC) AS counterTrue, InventoryId FROM DetailForDevice WHERE InventoryId='" + inventoryId + "' AND Found='true'", null);
                if (readCountRFID.moveToFirst()) {
                    counterState=readCountRFID.getInt(readCountRFID.getColumnIndex("counterTrue"));
                }
            }finally {
                assert readCountRFID != null;
                readCountRFID.close();
            }

            //db.beginTransaction();

            /*cv.put("Found", "true");
            final boolean result=db.update(NAME_TABLE, cv, "InventoryId='"+inventoryId+"' AND EPC='"+RFID+"'", null)>0;
            db.setTransactionSuccessful();*/
        }catch (SQLException sqlex){
            throw sqlex;
        }finally {
            db.endTransaction();
            db.close();
        }
        return counterState;

    }
    public boolean InsertTag(ArrayList<HashMap<String, String>> maestroTagList, String idInventory, String IdHardware, int TagStatus) { //, String TID Struing RFID
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result=false;
        try {
                ArrayList<HashMap<String, String>> newArraylistTags=checkIsExist(idInventory, maestroTagList);
                String sqlSentence="INSERT INTO Tags VALUES(?, ?, ?, ?, ?)";
                db.beginTransactionNonExclusive();
                SQLiteStatement stmt=db.compileStatement(sqlSentence);
                for (int index = 0; index < newArraylistTags.size(); index++) {
                    //ContentValues reg = new ContentValues();
                    String RFID = newArraylistTags.get(index).get("tagUii");
                    String TID=(newArraylistTags.get(index).get("tagRssi")==null)? " " :newArraylistTags.get(index).get("tagRssi");

                    stmt.bindString(1, RFID);
                    stmt.bindString(2, idInventory);
                    stmt.bindString(3, IdHardware);
                    stmt.bindString(4, TID);
                    stmt.bindLong(5, TagStatus);
                    stmt.execute();
                }
                db.setTransactionSuccessful();

                    /*try{
                        for (int index = 0; index < newArraylistTags.size(); index++) {
                            ContentValues reg = new ContentValues();
                            String RFID = newArraylistTags.get(index).get("tagUii");
                            String TID = newArraylistTags.get(index).get("tagRssi");
                            reg.put("RFID", RFID);
                            reg.put("InventoryId", idInventory);
                            reg.put("IdHardware", IdHardware);
                            reg.put("TID", TID);
                            reg.put("TagStatus", TagStatus);
                            db.insert("Tags", null, reg);
                        }
                        result=true;
                    }catch (SQLException ex){
                         Log.e("ExceptionSQLI", ""+ex.getLocalizedMessage());
                        //result = false;
                    }finally {
                        db.endTransaction();
                        db.close();
                    }*/
                    result=true;
            }catch (SQLiteException ex){
                ex.printStackTrace();
                    result = false;
            }finally {
                db.endTransaction();
            }
        return result;
    }

    private ArrayList<HashMap<String, String>> checkIsExist(String inventoryId, ArrayList<HashMap<String, String>> maestroTagList) {
        ArrayList<HashMap<String, String>> respondNewArray=new ArrayList<HashMap<String, String>>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(int indexTa=0; indexTa<maestroTagList.size(); indexTa++){
            HashMap<String, String> newHash=new HashMap<String, String>();
            String strEPC=maestroTagList.get(indexTa).get("tagUii").toString();
            String strTID=" ";
            if(maestroTagList.get(indexTa).containsKey("tagRssi")){
                strTID=maestroTagList.get(indexTa).get("tagRssi").toString();
            }
            if(strTID.equals(" ")) {
                Cursor readExistTagsOne=null;
                try{
                    readExistTagsOne= db.rawQuery("SELECT RFID, InventoryId, TID FROM Tags WHERE RFID='" + strEPC + "' AND InventoryId='" + inventoryId + "'", null);
                    if (readExistTagsOne.moveToFirst()) {
                    }else{
                        newHash.put("tagUii", strEPC);
                        newHash.put("tagRssi", " ");
                        respondNewArray.add(newHash);
                    }
                }finally {
                    assert readExistTagsOne != null;
                    readExistTagsOne.close();
                }

            }else {
                Cursor readExistTags=null;
                try{
                    readExistTags = db.rawQuery("SELECT RFID, InventoryId, TID FROM Tags WHERE RFID='" + strEPC + "' OR TID='" + strTID + "' AND InventoryId='" + inventoryId + "'", null);
                    if (readExistTags.moveToFirst()) {}else{
                        newHash.put("tagUii", strEPC);
                        newHash.put("tagRssi", strTID);
                        respondNewArray.add(newHash);

                    }
                }finally {
                    assert readExistTags != null;
                    readExistTags.close();
                }

            }
        }
        db.close();
        return respondNewArray;
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
        ArrayList maestroTags=new ArrayList();

        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read= db.rawQuery("SELECT RFID, InventoryId, IdHardware, TID, TagStatus FROM Tags WHERE InventoryId='"+inventoryId+"'", null); //ORDER BY RFID DESC LIMIT 100
        if (read.moveToFirst()) {
            //Cursor readCount2=db.rawQuery("SELECT COUNT(RFID) as countRFID FROM Tags WHERE InventoryId='"+inventoryId+"'", null);
            //if(readCount2.moveToFirst()){
                do {
                    HashMap<String, String> datos=new HashMap<String, String>();
                    if(includeTID){
                        datos.put("tagUii", read.getString(
                                read.getColumnIndex("RFID")
                        ));
                        datos.put("tagRssi", read.getString(
                                read.getColumnIndex("TID")
                                ));
                        /*datos.add(
                                read.getString(
                                        read.getColumnIndex("RFID")
                                )+"@"+read.getString(
                                        read.getColumnIndex("TID")
                                )+"@"+readCount2.getInt(
                                        readCount2.getColumnIndex("countRFID")
                                )
                        );*/
                    }else{
                        datos.put("tagUii",
                                read.getString(
                                        read.getColumnIndex("RFID")
                                )/*+"@"+readCount2.getInt(
                                    readCount2.getColumnIndex("countRFID")
                                )*/
                        );
                    }
                    maestroTags.add(datos);

                } while (read.moveToNext());

            //}

        }
        return maestroTags;
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
