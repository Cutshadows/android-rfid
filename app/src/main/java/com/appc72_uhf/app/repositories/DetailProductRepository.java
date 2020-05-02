package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;

public class DetailProductRepository {
    private Context context;

    public DetailProductRepository(Context context) {
        this.context = context;
    }

    public boolean DetailProductInsert(
            int Id,
            String EPC,
            String Code,
            String Name,
            String Found,
            int ProductMasterId
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("Id", Id);
            reg.put("EPC", EPC);
            reg.put("Code", Code);
            reg.put("Name", Name);
            reg.put("Found", Found);
            reg.put("ProductMasterId", ProductMasterId);

            db.insert("DetailForDevice", null, reg);

            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }
    public ArrayList ProductList(){
        ArrayList<String> productFields= new ArrayList<>();
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();

        Cursor queryProducts=db.rawQuery("SELECT COUNT(ProductMasterId) as CountMaster, Name FROM DetailForDevice", null);
        if(queryProducts.moveToFirst()){
            do{
                productFields.add(queryProducts.getString(queryProducts.getColumnIndex("CountMaster")));
                productFields.add(queryProducts.getString(queryProducts.getColumnIndex("Name")));
            }while (queryProducts.moveToNext());
        }
        return productFields;
    }
}
