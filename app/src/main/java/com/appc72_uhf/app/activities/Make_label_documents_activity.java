package com.appc72_uhf.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterMakeLabelDocuments;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.tools.UIHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Make_label_documents_activity extends AppCompatActivity{
    //private Button btn_syncDocumentServer;
    private ListView lv_server_documents;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";
    ArrayList<DatamodelDocumentsMakeLabel> datamodelDocumentsMakeLabelArrayList;
    AdapterMakeLabelDocuments adapterMakeLabelDocuments;
    DatamodelDocumentsMakeLabel datamodelDocumentsMakeLabel;
    String token_access;
    String code_enterprise;
    String android_id;
    int codeCompany;
    ProgressDialog mypDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_label_documents_activity);
        initComponent();
    }
    private void initComponent(){
        //btn_syncDocumentServer=(Button) findViewById(R.id.btn_syncDocumentServer);
        lv_server_documents=(ListView) findViewById(R.id.lv_server_documents);

        //btn_syncDocumentServer.setOnClickListener(this);

        datamodelDocumentsMakeLabelArrayList=new ArrayList<DatamodelDocumentsMakeLabel>();
        SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");
        if(access_token.length()==0){
            Log.e("No data preferences", " Error data no empty "+access_token);
        }else{
            token_access=access_token;
        }
        code_enterprise=getCompany();
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        syncronizedDocuments();
        adapterMakeLabelDocuments=new AdapterMakeLabelDocuments(Make_label_documents_activity.this, datamodelDocumentsMakeLabelArrayList);
        lv_server_documents.setAdapter(adapterMakeLabelDocuments);

        adapterMakeLabelDocuments.notifyDataSetChanged();

    }

    public void syncronizedDocuments(){
        final String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID;
        mypDialog = new ProgressDialog(this);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Verificando documentos en servidor...");
        mypDialog.setCanceledOnTouchOutside(false);
        mypDialog.show();
        HttpHelpers http= new HttpHelpers(Make_label_documents_activity.this, URL_COMPLETE, "");
        http.addHeader("Authorization", "Bearer "+token_access);
        Log.e("INVENTARIO INT", PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID+"/api/devicedocument/GetAllDocumentsDeviceId?HardwareId="+android_id);
        http.clientProductDetail(Request.Method.GET, "/api/devicedocument/GetAllDocumentsDeviceId?HardwareId="+android_id, null,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onRESPONSE PRODUCT", response);
                try{
                    Gson gson=new Gson();
                    DatamodelDocumentsMakeLabel[] docsMakelabels=gson.fromJson(response, DatamodelDocumentsMakeLabel[].class);
                    for(int index=0; index<=docsMakelabels.length-1; index++){
                        Log.e("DATA FOR", "DocumentsId: "+docsMakelabels[index].getDocumentId()+"\n DeviceId:"+docsMakelabels[index].getDeviceId()+" \n FechaAsignacion:"+docsMakelabels[index].getFechaAsignacion()+" \n Document Details Virtual "+docsMakelabels[index].getDocumentDetailsVirtual());
                        datamodelDocumentsMakeLabelArrayList.add(
                                new DatamodelDocumentsMakeLabel(
                                    docsMakelabels[index].getDocumentId(),
                                    docsMakelabels[index].getDeviceId(),
                                    docsMakelabels[index].getLocationOriginName(),
                                    docsMakelabels[index].getDocumentName(),
                                    docsMakelabels[index].isHasVirtualItems(),
                                    docsMakelabels[index].getDocumentDetailsVirtual()
                                )
                        );
                    }
                    adapterMakeLabelDocuments.notifyDataSetChanged();
                    mypDialog.dismiss();
                }catch (Exception e){
                    mypDialog.dismiss();
                    e.printStackTrace();
                }

                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    mypDialog.dismiss();
                    if (error instanceof NetworkError) {
                        UIHelper.ToastMessage(Make_label_documents_activity.this, "Error de conexion, no hay conexion a internet", 3);
                    } else if (error instanceof ServerError) {
                        UIHelper.ToastMessage(Make_label_documents_activity.this, "Error de conexion, 1 incorrecta ", 3);
                    } else if (error instanceof AuthFailureError) {
                        UIHelper.ToastMessage(Make_label_documents_activity.this, "Error de conexion, intente mas tarde.", 3);
                    } else if (error instanceof ParseError) {
                        UIHelper.ToastMessage(Make_label_documents_activity.this, "Error desconocido, intente mas tarde", 3);
                    } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        UIHelper.ToastMessage(Make_label_documents_activity.this, "Tiempo agotado, intente mas tarde!!!", 3);
                    }
                    Log.e("onErrorResponse", ""+error.getLocalizedMessage());
                }
            });
    }

    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(this);
        SharedPreferences preferenceCodeActive=getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        int companyId;
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;
            companyId=companyRepository.getCompanieId(code_result);
            codeCompany=companyId;
        }
        return code_result;
    }
}
