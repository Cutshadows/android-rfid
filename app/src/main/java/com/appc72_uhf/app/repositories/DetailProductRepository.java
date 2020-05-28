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
            int ProductMasterId,
            int InventoryId
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
            reg.put("InventoryId", InventoryId);

            result = db.insert("DetailForDevice", null, reg)>0;

        } catch (Exception ex) {
            result = false;
        }
        db.close();
        return result;
    }
    public ArrayList OrderProductMasterId(int inventoryId){
        ArrayList<String> masterProductCount= new ArrayList<>();
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();

        Cursor queryProducts=db.rawQuery("SELECT COUNT(ProductMasterId) as contador, Code, ProductMasterId, Name FROM DetailForDevice WHERE InventoryId="+inventoryId+" GROUP BY ProductMasterId ORDER BY Found=\"true\" ASC", null);
        if(queryProducts.moveToFirst()){
            do{
                masterProductCount.add(queryProducts.getInt(queryProducts.getColumnIndex("contador"))+"@"+queryProducts.getInt(queryProducts.getColumnIndex("ProductMasterId"))+"@"+queryProducts.getString(queryProducts.getColumnIndex("Name"))+"@"+queryProducts.getString(queryProducts.getColumnIndex("Code")));
            }while (queryProducts.moveToNext());
        }
        db.close();
        return masterProductCount;
    }

    public int CountProductFoundTrue(int inventoryId, int masterProductId){
        int ProductFoundTrue=0;
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        Cursor queryCountProduct=db.rawQuery("SELECT COUNT(ProductMasterId) as product FROM DetailForDevice WHERE ProductMasterId="+masterProductId+" AND InventoryId="+inventoryId+" AND Found='true'", null);
        if(queryCountProduct.moveToFirst()){
            ProductFoundTrue=queryCountProduct.getInt(queryCountProduct.getColumnIndex("product"));
        }
        db.close();
        return ProductFoundTrue;
    }

    public ArrayList ProductListEPC(int inventoryId, int productMasterId){
        ArrayList<String> productFields= new ArrayList<>();
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();

        Cursor queryProducts=db.rawQuery("SELECT EPC, Found, Name, Code FROM DetailForDevice WHERE InventoryId="+inventoryId+" AND ProductMasterId="+productMasterId+" ORDER BY Found='false' ASC", null);
        if(queryProducts.moveToFirst()){
            do{
                productFields.add(queryProducts.getString(queryProducts.getColumnIndex("EPC"))+"@"+queryProducts.getString(queryProducts.getColumnIndex("Found"))+"@"+queryProducts.getString(queryProducts.getColumnIndex("Name"))+"@"+queryProducts.getString(queryProducts.getColumnIndex("Code")));
            }while (queryProducts.moveToNext());
        }
        db.close();
        return productFields;
    }

    public int OrderProductTotalEPC(int inventoryId){
        int productCount=0;
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();

        Cursor queryProducts=db.rawQuery("SELECT COUNT(EPC) as contadorEPC, EPC FROM DetailForDevice WHERE InventoryId="+inventoryId, null);
        if(queryProducts.moveToFirst()){
            productCount=queryProducts.getInt(queryProducts.getColumnIndex("contadorEPC"));
        }
        db.close();
        return productCount;
    }

    public int OrderProductTotalEPCFOUND(int inventoryId){
        int productCountFound=0;
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();

        Cursor queryProducts=db.rawQuery("SELECT COUNT(EPC) as contadorFound, EPC FROM DetailForDevice WHERE InventoryId="+inventoryId+" AND Found='true'", null);
        if(queryProducts.moveToFirst()){
            productCountFound=queryProducts.getInt(queryProducts.getColumnIndex("contadorFound"));
        }
        db.close();
        return productCountFound;
    }
}
