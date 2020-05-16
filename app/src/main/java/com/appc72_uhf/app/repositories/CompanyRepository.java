package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;

public class CompanyRepository {
    private Context context;

    public CompanyRepository(Context context) {
        this.context = context;
    }

    public boolean CompanyInsert(
            int Id,
            String Name,
            String Code,
            String isActive
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("Id", Id);
            reg.put("Name", Name);
            reg.put("IsActive", isActive);
            reg.put("Code", Code);

            db.insert("Company", null, reg);

            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    public ArrayList LoadCompanies(){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT Code FROM Company WHERE IsActive='true' ORDER BY Id ASC", null);
        if (read.moveToFirst()) {
            do {
                datos.add(read.getString(read.getColumnIndex("Code")));

            } while (read.moveToNext());
        }
        return datos;

    }

    public int getCompanieId(String companyName){
        int datos=0;
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT Id FROM Company WHERE Code=\""+companyName+"\"", null);
        if (read.moveToFirst()) {
            datos=read.getInt(read.getColumnIndex("Id"));
        }
        return datos;

    }
}