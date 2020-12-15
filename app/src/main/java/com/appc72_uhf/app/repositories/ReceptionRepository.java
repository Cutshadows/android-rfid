package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReceptionRepository {
    private Context mContext;

    public ReceptionRepository(Context mContext) {
        this.mContext = mContext;
    }

    public boolean InserParametersLocation(int IdLocation, String Name, String emailUser){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(mContext);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("IdLocation", IdLocation);
            reg.put("LocationName", Name);
            reg.put("EmailUser", emailUser);

            result=db.insert("Location", null, reg)>0;

        } catch (Exception ex) {
            result = false;
        }
        db.close();
        return result;
    }

    public ArrayList LoadLocations(String UserId){
        ArrayList arrayLocation=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(mContext);
        SQLiteDatabase db = admin.getWritableDatabase();
        arrayLocation.add("Seleccionar");
        Cursor read= db.rawQuery("SELECT IdLocation, LocationName FROM Location WHERE EmailUser='"+UserId+"' ORDER BY LocationName ASC", null);
        if (read.moveToFirst()) {
            do {
                arrayLocation.add(read.getString(read.getColumnIndex("LocationName")).trim());
            } while (read.moveToNext());
        }
        db.close();
        return arrayLocation;
    }
    public int locationSelected(String selected_location, String User){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(mContext);
        SQLiteDatabase db = admin.getWritableDatabase();
        int dataIdLocation=0;
        Cursor read=db.rawQuery("SELECT IdLocation FROM Location WHERE LocationName LIKE '%"+selected_location+"%' AND EmailUser='"+User+"'", null);
        if (read.moveToFirst()) {
            dataIdLocation=read.getInt(read.getColumnIndex("IdLocation"));
        }
        db.close();
        return dataIdLocation;
    }
    public String locationToString(int selected_location){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(mContext);
        SQLiteDatabase db = admin.getWritableDatabase();
        String dataIdLocation="";
        Cursor read=db.rawQuery("SELECT LocationName FROM Location WHERE IdLocation="+selected_location+"", null);
        if (read.moveToFirst()) {
            dataIdLocation=read.getString(read.getColumnIndex("LocationName"));
        }
        db.close();
        return dataIdLocation;
    }

    public boolean insertReceptionAutomatic(String stringComment, int IdLocation, String emailUser){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(mContext);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try {
            ContentValues reg = new ContentValues();
            reg.put("dateReception", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            reg.put("Comment", stringComment);
            reg.put("Fk_IdLocation", IdLocation);
            reg.put("EmailUserCreate", emailUser);

            result=db.insert("Reception", null, reg)>0;

        } catch (Exception ex) {
            result = false;
        }
        db.close();
        return result;
    }

    public ArrayList SelectReceptions(String UserEmail){
        ArrayList arrayReceptions=new ArrayList<>();

        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(mContext);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read= db.rawQuery("SELECT IdReception, dateReception, Comment, Fk_IdLocation, EmailUserCreate FROM Reception WHERE EmailUserCreate='"+UserEmail+"' ORDER BY IdReception DESC", null);
        if (read.moveToFirst()) {
            do {
                arrayReceptions.add(read.getInt(read.getColumnIndex("IdReception"))
                        +"@@"+read.getString(read.getColumnIndex("dateReception")).trim()
                        +"@@"+read.getString(read.getColumnIndex("Comment")).trim()
                        +"@@"+read.getInt(read.getColumnIndex("Fk_IdLocation"))
                        +"@@"+read.getString(read.getColumnIndex("EmailUserCreate")).trim());
            } while (read.moveToNext());
        }
        db.close();
        return arrayReceptions;
    }
}
