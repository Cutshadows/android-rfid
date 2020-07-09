package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

public class MakeLabelRepository {
    private Context context;

    public MakeLabelRepository(Context context) {
        this.context = context;
    }


    public boolean InsertDocuments(
            String documentName,
            int documentId,
            int deviceId,
            String locationOriginName,
            int status,
            boolean hashVirtualItems,
            int isSelected
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resultInsert;
        try {
            ContentValues valContentDocs= new ContentValues();
            valContentDocs.put("DocumentName", documentName);
            valContentDocs.put("DocumentId", documentId);
            valContentDocs.put("DeviceId", deviceId);
            valContentDocs.put("LocationOriginName", locationOriginName);
            valContentDocs.put("Status", status);
            valContentDocs.put("HasVirtualItems", String.valueOf(hashVirtualItems));
            valContentDocs.put("isSelected", isSelected);

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
    String associatedDocNumber,
    int status,
    String createdDate,
    String readDate,
    int productMasterId,
    int productVirtualId,
    int documentId,
    int productMaster,
    String document,
    int typeDocumentVirtual,
    String cost,
    String wasMoved,
    String labelAssociated,
    int productId,
    String product
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resultInsertTags;

        try {
            ContentValues valContentVirtualTags= new ContentValues();
            valContentVirtualTags.put("Id", id);
            valContentVirtualTags.put("AssociatedDocNumber", associatedDocNumber);
            valContentVirtualTags.put("Status", status);
            valContentVirtualTags.put("CreatedDate", createdDate);
            valContentVirtualTags.put("ReadDate", readDate);
            valContentVirtualTags.put("ProductMasterId", productMasterId);
            valContentVirtualTags.put("ProductVirtualId", productVirtualId);
            valContentVirtualTags.put("DocumentId", documentId);
            valContentVirtualTags.put("ProductMaster", productMaster);
            valContentVirtualTags.put("Document",document);
            valContentVirtualTags.put("TypeDocumentVirtual", typeDocumentVirtual);
            valContentVirtualTags.put("Cost", cost);
            valContentVirtualTags.put("wasMoved", wasMoved);
            valContentVirtualTags.put("LabelAssociated", labelAssociated);
            valContentVirtualTags.put("ProductId", productId);
            valContentVirtualTags.put("Product", product);
            resultInsertTags=db.insert("DocumentDetailsVirtual", null,  valContentVirtualTags)>0;
        }catch (SQLiteException sqliEx){
            resultInsertTags=false;
        }
        db.close();
        return resultInsertTags;
    }

    public boolean deleteDocument(int documentId, int virtualId){
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        final String MY_TABLE_DOCUMENT="Documents";
        final String MY_TABLE_VIRTUAL="Documents";
        try {
            db.beginTransaction();
            boolean result1=db.delete(MY_TABLE_DOCUMENT, "Id="+documentId, null )>0;
            boolean result2=false;
            if(result1){
                result2=db.delete(MY_TABLE_VIRTUAL, "Id"+virtualId+" AND DocumentId"+documentId, null)>0;
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
}
