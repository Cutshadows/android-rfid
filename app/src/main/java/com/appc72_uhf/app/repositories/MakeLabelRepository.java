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

    ){
        return false;
    }
}
