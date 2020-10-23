package com.appc72_uhf.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.helpers.AdminSQLOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

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
            resultInsert=false;
        }
        db.close();
        return resultInsert;
    }


    public boolean insertVirtualTag(
            JSONArray documentDetailVirtual
           // int id,
           // String productMaster,
           // String productVirtualId,
           // int documentId,
           // String codebar
    ){
        final DatamodelDocumentsMakeLabel dModelMakeLabel=new DatamodelDocumentsMakeLabel();
        dModelMakeLabel.setDocumentDetailsVirtual(documentDetailVirtual);
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        boolean resultInsertTags=false;
        try {

        /*try {
            for(int indexVirtual=0; indexVirtual < dModelMakeLabel.getDocumentDetailsVirtual().length(); indexVirtual++){
                JSONObject jsonObject=dModelMakeLabel.getDocumentDetailsVirtual().getJSONObject(indexVirtual);
                int id=jsonObject.getInt("Id");
                JSONObject productMasterArray=jsonObject.getJSONObject("ProductMaster");
                String productVirtualId=jsonObject.getString("ProductVirtualId");
                int documentId=jsonObject.getInt("DocumentId");
                String codeBar=jsonObject.getString("CodeBar");

                ContentValues valContentVirtualTags= new ContentValues();
                valContentVirtualTags.put("Id", id);
                valContentVirtualTags.put("ProductMaster", productMasterArray.toString());
                valContentVirtualTags.put("ProductVirtualId", productVirtualId);
                valContentVirtualTags.put("DocumentId", documentId);
                valContentVirtualTags.put("Status", 0);
                valContentVirtualTags.put("CodeBar", codeBar);

                resultInsertTags=db.insert("DocumentDetailsVirtual", null,  valContentVirtualTags)>0;
            }
        }catch (SQLiteException | JSONException sqliEx){
            Log.e("SQLIEX", ""+sqliEx.getLocalizedMessage());
            resultInsertTags=false;
        }*/
            String sqlSecuences="INSERT INTO DocumentDetailsVirtual VALUES(?, ?, ?, ?, ?, ?, ?)";
            db.beginTransactionNonExclusive();
            SQLiteStatement stmt=db.compileStatement(sqlSecuences);
            for(int indexVirtual=0; indexVirtual < dModelMakeLabel.getDocumentDetailsVirtual().length(); indexVirtual++){
                JSONObject jsonObject=dModelMakeLabel.getDocumentDetailsVirtual().getJSONObject(indexVirtual);
                int id=jsonObject.getInt("Id");
                JSONObject productMasterArray=jsonObject.getJSONObject("ProductMaster");
                String productVirtualId=jsonObject.getString("ProductVirtualId");
                int documentId=jsonObject.getInt("DocumentId");
                String codeBar=jsonObject.getString("CodeBar");

                stmt.bindString(1, String.valueOf(id));
                stmt.bindString(2, String.valueOf(productMasterArray));
                stmt.bindString(3, productVirtualId);
                stmt.bindLong(4, documentId);
                stmt.bindLong(5, 0);
                stmt.bindString(6, " ");
                stmt.bindString(7, codeBar);
                stmt.execute();
            }
            db.setTransactionSuccessful();
            resultInsertTags=true;
        }catch (Exception ex){
            ex.printStackTrace();
            resultInsertTags = false;
        }finally {
            db.endTransaction();
        }
        db.close();
        return resultInsertTags;
    }
//, int virtualId
    public boolean deleteDocument(int documentId){
        AdminSQLOpenHelper admin=new AdminSQLOpenHelper(context);
        SQLiteDatabase db=admin.getWritableDatabase();
        final String MY_TABLE_DOCUMENT="Documents";
        final String MY_TABLE_VIRTUAL="DocumentDetailsVirtual";
        try {
            db.beginTransaction();
            boolean result1=db.delete(MY_TABLE_DOCUMENT, "DocumentId="+documentId, null )>0;
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
        Cursor read=db.rawQuery("SELECT DISTINCT ProductMaster, Id, ProductVirtualId, DocumentId, CodeBar FROM DocumentDetailsVirtual WHERE DocumentId="+documentId+" AND Status=0", null);
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
        Cursor countRead=db.rawQuery("SELECT COUNT(Id) as counterVirtualDoc FROM DocumentDetailsVirtual WHERE DocumentId="+documentId+" AND Status=0", null); //+" AND ProductVirtualId='0'"
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
        Cursor read=db.rawQuery("SELECT COUNT(ProductMaster) as countProductMaster FROM DocumentDetailsVirtual WHERE ProductMaster LIKE '%"+productMste+"%' AND Status=0 AND DocumentId="+documentId+" AND Status=0", null);
        if (read.moveToFirst()) {
                dataMakeLabelTag=read.getInt(read.getColumnIndex("countProductMaster"));
        }
        db.close();
        return dataMakeLabelTag;
    }


    public boolean UpdateVirtualMakeLabel(String EPCString, String productMster, int DocumentId){
        final String NAME_TABLE="DocumentDetailsVirtual";
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        final ContentValues cv=new ContentValues();
        int idVirtual=0;
        try{
            db.beginTransaction();
            Cursor selectId=db.rawQuery("SELECT Id FROM DocumentDetailsVirtual WHERE ProductMaster LIKE '%"+productMster+"' AND Status=0 AND DocumentId="+DocumentId+" ORDER BY Id DESC LIMIT 1 ", null);
            if(selectId.moveToFirst()){
                idVirtual=selectId.getInt(selectId.getColumnIndex("Id"));
            }
            if(idVirtual!=0){
                cv.put("Status", 1);
                cv.put("EPCString", EPCString);
                final boolean resultUpdateVirtualTags=db.update(NAME_TABLE, cv, "Id="+idVirtual+" AND DocumentId="+DocumentId+"", null)>0;
                db.setTransactionSuccessful();
                return resultUpdateVirtualTags;
            }else{
                return false;
            }
        }catch (SQLException sqlex){
            throw sqlex;
        }finally {
            db.endTransaction();
            db.close();
        }
    }


    public ArrayList GetReadyMakelabel(int DocumentId){
        ArrayList<String> datos=new ArrayList<>();
        AdminSQLOpenHelper admin = new AdminSQLOpenHelper(context);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor read= db.rawQuery("SELECT Id, EPCString FROM DocumentDetailsVirtual WHERE DocumentId="+DocumentId, null); //ORDER BY RFID DESC LIMIT 100
        if (read.moveToFirst()) {
            do {
                    datos.add(
                            read.getString(
                                    read.getColumnIndex("Id")
                            )+"@"+read.getString(
                                    read.getColumnIndex("EPCString")
                            )
                    );

            } while (read.moveToNext());

        }
        return datos;
    }
}
