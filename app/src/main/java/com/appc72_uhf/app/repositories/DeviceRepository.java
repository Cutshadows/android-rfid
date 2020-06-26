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
            String AssignedResponse,
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
            reg.put("AssignedResponse", AssignedResponse);
            reg.put("MakeLabel", MakeLabel);

            db.insert("Device", null, reg);

            result = true;
        } catch (Exception ex) {
            result = false;
        }
        db.close();
        return result;
    }
    public boolean FindCode(int companyId){
            AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
            SQLiteDatabase db = admin.getWritableDatabase();
            boolean result_code;
                Cursor selectCode=db.rawQuery("SELECT CompanyId FROM Device WHERE CompanyId="+companyId, null);
                if(!selectCode.moveToLast()){
                    result_code=true;
                }else{
                    result_code=false;
                }
            db.close();
            return result_code;
    }

    public String PermissionDevice(String hardwareId, int companyId){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        String response="";
        Cursor queryDevice=db.rawQuery("SELECT TakingInventory, MakeLabel FROM Device WHERE HardwareId='"+hardwareId+"' AND CompanyId="+companyId, null);
        if(queryDevice.moveToFirst()){
            response=queryDevice.getString(queryDevice.getColumnIndex("TakingInventory"))+"@"+queryDevice.getString(queryDevice.getColumnIndex("MakeLabel"));
        }
        return response;
    }



}
