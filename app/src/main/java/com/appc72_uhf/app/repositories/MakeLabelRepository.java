package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import java.util.ArrayList;

public class MakeLabelRepository {
    private Context context;

    public MakeLabelRepository(Context context) {
        this.context = context;
    }


    public boolean InsertDocuments(
            String documentName,
            int documentId,
            int deviceId,
            String fechaAsignacion,
            boolean allowLabeling,
            int associatedDocumentId,
            String associatedDocNumber,
            String locationOriginName,
            int destinationLocationId,
            String client,
            int status,
            boolean hashVirtualItems,
            int isSelected,
            int companyId
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resultInsert;
        try {
            ContentValues valContentDocs= new ContentValues();
            valContentDocs.put("DocumentName", documentName.trim());
            valContentDocs.put("DocumentId", documentId);
            valContentDocs.put("DeviceId", deviceId);
            valContentDocs.put("FechaAsignacion", fechaAsignacion);
            valContentDocs.put("AllowLabeling", String.valueOf(allowLabeling));
            valContentDocs.put("AssociatedDocumentId", associatedDocumentId);
            valContentDocs.put("AssociatedDocNumber", associatedDocNumber);
            valContentDocs.put("LocationOriginName", locationOriginName.trim());
            valContentDocs.put("DestinationLocationId", destinationLocationId);
            valContentDocs.put("Client", client);
            valContentDocs.put("Status", status);
            valContentDocs.put("HasVirtualItems", String.valueOf(hashVirtualItems));
            valContentDocs.put("isSelected", isSelected);
            valContentDocs.put("CompanyId", companyId);

            resultInsert=db.insert("Documents", null, valContentDocs)>0;
        }catch (SQLiteException sqlEx){
            Log.e("SQLIMSG", ""+sqlEx.getLocalizedMessage());
            resultInsert=false;
        }
        db.close();
        return resultInsert;
    }


    public boolean insertVirtualTag(
    int id,
    String productMaster,
    String productVirtualId,
    int documentId,
    String codebar
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resultInsertTags;

        try {
            ContentValues valContentVirtualTags= new ContentValues();
            valContentVirtualTags.put("Id", id);
            valContentVirtualTags.put("ProductMaster", productMaster);
            valContentVirtualTags.put("ProductVirtualId", productVirtualId);
            valContentVirtualTags.put("DocumentId", documentId);
            valContentVirtualTags.put("CodeBar", codebar);

            resultInsertTags=db.insert("DocumentDetailsVirtual", null,  valContentVirtualTags)>0;
        }catch (SQLiteException sqliEx){
            Log.e("SQLIEX", ""+sqliEx.getLocalizedMessage());
            resultInsertTags=false;
        }
        db.close();
        return resultInsertTags;
    }
//, int virtualId
    public boolean deleteDocument(int documentId){
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        final String MY_TABLE_DOCUMENT="Documents";
        final String MY_TABLE_VIRTUAL="Documents";
        try {
            db.beginTransaction();
            boolean result1=db.delete(MY_TABLE_DOCUMENT, "Id="+documentId, null )>0;
            boolean result2=false;
            if(result1){
                result2=db.delete(MY_TABLE_VIRTUAL, "DocumentId="+documentId, null)>0;
            }
            db.setTransactionSuccessful();
            return result2;
        }catch (SQLException sqlex){
            throw  sqlex;
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    public int ViewDocument(int documentId){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor read= db.rawQuery("SELECT * FROM Documents WHERE DocumentId="+documentId, null);
        int datos=0;
        if (read.moveToFirst()) {
            datos = 1;
        }
        db.close();
        return datos;
    }

    public ArrayList<String> ViewDocumentsMakeLabel(int companyId){
        ArrayList<String> datosInventory=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read=db.rawQuery("SELECT DocumentName, DocumentId, DeviceId, LocationOriginName, Status, HasVirtualItems, isSelected FROM Documents WHERE CompanyId="+companyId+" AND isSelected=1", null);
        if (read.moveToFirst()) {
           // Cursor countRead=db.rawQuery("SELECT COUNT(Id) as counterVirtualDoc FROM DocumentDetailsVirtual WHERE DocumentId="+read.getInt(read.getColumnIndex("DocumentId"))+" AND ProductVirtualId='0'", null); //+" AND ProductVirtualId='0'"
            //if(countRead.moveToNext() ){
                do {
                    datosInventory.add(
                            read.getString(
                                    read.getColumnIndex("DocumentName")
                            )+"@"+read.getInt(
                                    read.getColumnIndex("DocumentId")
                            )+"@"+read.getString(
                                    read.getColumnIndex("HasVirtualItems")
                            )+"@"+read.getInt(
                                    read.getColumnIndex("Status")
                            )+"@"+read.getInt(
                                    read.getColumnIndex("isSelected")
                            )+"@"+read.getString(
                                    read.getColumnIndex("LocationOriginName")
                            )
                            /*+"@"+countRead.getInt(
                                    countRead.getColumnIndex("counterVirtualDoc")
                            )*/
                    );
                } while (read.moveToNext());
            //}
        }
        db.close();
        return datosInventory;

    }
    public ArrayList<String> ViewVirtualTagsEnabled(int documentId){
        ArrayList<String> dataMakeLabelTag=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read=db.rawQuery("SELECT DISTINCT ProductMaster, Id, ProductVirtualId, DocumentId, CodeBar FROM DocumentDetailsVirtual WHERE DocumentId="+documentId, null);
        if (read.moveToFirst()) {
                do {
                    dataMakeLabelTag.add(
                            read.getString(
                                    read.getColumnIndex("ProductMaster")
                            )+"@"+read.getInt(
                                    read.getColumnIndex("Id")
                            )+"@"+read.getString(
                                    read.getColumnIndex("ProductVirtualId")
                            )+"@"+read.getInt(
                                    read.getColumnIndex("DocumentId")
                            )+"@"+read.getString(
                                    read.getColumnIndex("CodeBar")
                            )
                    );
                } while (read.moveToNext());
        }
        db.close();
        return dataMakeLabelTag;

    }
    public int countTagsVirtualEnabled(int documentId){
        int dataMakeLabelTag=0;
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor countRead=db.rawQuery("SELECT COUNT(Id) as counterVirtualDoc FROM DocumentDetailsVirtual WHERE DocumentId="+documentId+" AND ProductVirtualId='0'", null); //+" AND ProductVirtualId='0'"
        if (countRead.moveToFirst()) {
            dataMakeLabelTag=countRead.getInt(countRead.getColumnIndex("counterVirtualDoc"));
        }
        db.close();
        return dataMakeLabelTag;
    }

    public int ViewVirtualCount(int productMste, int documentId){
        int dataMakeLabelTag=0;
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read=db.rawQuery("SELECT COUNT(ProductMaster) as countProductMaster FROM DocumentDetailsVirtual WHERE ProductMaster LIKE '%"+productMste+"%' AND DocumentId="+documentId, null);
        if (read.moveToFirst()) {
                dataMakeLabelTag=read.getInt(read.getColumnIndex("countProductMaster"));
        }
        db.close();
        return dataMakeLabelTag;
    }
}
