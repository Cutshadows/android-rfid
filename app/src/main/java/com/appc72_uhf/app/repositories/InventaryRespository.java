package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("Id", id);
            reg.put("Name", name);
            reg.put("InventoryStatus", InventoryStatus);
            reg.put("DetailForDevice", detailForDevice);
            reg.put("CompanyId", codeCompany);


            db.insert("Inventory", null, reg);

            result = true;
        } catch (Exception ex) {
            result = false;
        }

        db.close();

        return result;
    }

    public String ViewInventory(){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT * FROM Inventory", null);
        String datos = "";

        if (read.moveToFirst()) {
            do {
                datos = (read.getString(0));

            } while (read.moveToNext());
        }

        return datos;
    }


    public ArrayList<String> ViewAllInventories(){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT * FROM Inventory", null);

        if (read.moveToFirst()) {
            do {
                datos.add(read.getString(0)+"@"+read.getString(1));
            } while (read.moveToNext());
        }
        return datos;
    }
}
