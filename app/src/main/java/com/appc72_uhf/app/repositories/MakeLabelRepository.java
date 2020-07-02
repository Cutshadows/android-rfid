package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
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
            String fechaAsignacion,
            String asignadoPor,
            boolean isAllowLabeling,
            int associatedDocumentId,
            String associatedDocNumber,
            int documentTypeId,
            String description,
            String createdDate,
            int locationOriginId,
            String locationOriginName,
            String destinationLocationId,
            String aux1,
            String aux2,
            String aux3,
            String client,
            int status,
            boolean hashVirtualItems,
            String readerId
    ){
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resultInsert;
        try {
            ContentValues valContentDocs= new ContentValues();
            valContentDocs.put("DocumentName", documentName);
            valContentDocs.put("DocumentId", documentId);
            valContentDocs.put("DeviceId", deviceId);
            valContentDocs.put("FechaAsignacion", fechaAsignacion);
            valContentDocs.put("AsignadoPor", asignadoPor);
            valContentDocs.put("AllowLabeling", String.valueOf(isAllowLabeling));
            valContentDocs.put("AssociatedDocumentId", associatedDocumentId);
            valContentDocs.put("AssociatedDocNumber", associatedDocNumber);
            valContentDocs.put("DocumentTypeId", documentTypeId);
            valContentDocs.put("Description", description);
            valContentDocs.put("CreatedDate", createdDate);
            valContentDocs.put("LocationOriginId", locationOriginId);
            valContentDocs.put("LocationOriginName", locationOriginName);
            valContentDocs.put("DestinationLocationId", destinationLocationId);
            valContentDocs.put("Aux1", aux1);
            valContentDocs.put("Aux2", aux2);
            valContentDocs.put("Aux3", aux3);
            valContentDocs.put("Client", client);
            valContentDocs.put("Status", status);
            valContentDocs.put("HasVirtualItems", String.valueOf(hashVirtualItems));
            valContentDocs.put("isSelected", readerId);

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
}
