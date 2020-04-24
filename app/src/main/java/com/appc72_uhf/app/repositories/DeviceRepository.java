package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

public class DeviceRepository {
    private Context context;
    public DeviceRepository(Context context) {
        this.context = context;
    }
    public boolean DeviceInsert(
            int Id,
            String Name,
            String Description,
            String IsActive,
            String IsAssigned,
            int CompanyId,
            String  HardwareId,
            String TakingInventory,
            String MakeLabel
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean result;
        try {
            ContentValues reg = new ContentValues();

            reg.put("Id", Id);
            reg.put("Name", Name);
            reg.put("Description", Description);
            reg.put("IsActive", IsActive);
            reg.put("IsAssigned", IsAssigned);
            reg.put("CompanyId", CompanyId);
            reg.put("HardwareId", HardwareId);
            reg.put("TakingInventory", TakingInventory);
            reg.put("MakeLabel", MakeLabel);

            db.insert("Device", null, reg);

            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }
    public boolean FindCode(int companyId){
            AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
            SQLiteDatabase db = admin.getWritableDatabase();
            boolean result_code;
            try{
                Cursor selectCode=db.rawQuery("SELECT CompanyId FROM Device WHERE CompanyId="+companyId, null);
                if(selectCode.moveToFirst()){
                    result_code=true;
                }else{
                    result_code=false;
                }
            }catch (Exception e){
                result_code=false;
            }
            return result_code;
    }
}
