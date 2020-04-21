package com.appc72_uhf.app.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.domain.Application;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HhSyncActivity extends AppCompatActivity implements View.OnClickListener {
    private String android_id;
    private TextView tv_idDevice;
    private EditText et_syncCode;
    private Button btn_syncronousDevice;
    ProgressDialog mypDialog;
    public static final String PROTOCOL="http://";
    public static final String URL=".izyrfid.com";
    private static final String TAG="HhSyncActivity";
    int codeCompany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hh_sync);
        tv_idDevice=(TextView) findViewById(R.id.tv_idDevice);
        et_syncCode=(EditText) findViewById(R.id.et_syncCode);
        btn_syncronousDevice=(Button) findViewById(R.id.btn_syncronousDevice);
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.e("ID DEVICE", android_id);

        tv_idDevice.setText(android_id.toUpperCase());
        btn_syncronousDevice.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        SyncHandleCompanyId();
    }

    private void SyncHandleCompanyId() {
            final String code = et_syncCode.getText().toString();
            String URL_COMPLETE = PROTOCOL + code + URL;
            /*mypDialog = new ProgressDialog(HhSyncActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Verificando Codigo...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();*/
            //CONSULTA AL ENDPOINT /api/document/GetCompanies
            HttpHelpers http = new HttpHelpers(this, URL_COMPLETE, "");
            http.client(Request.Method.GET, "/api/document/GetCompanies", "application/json; charset=utf-8", null, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Gson gson = new Gson();
                        Application[] apps = gson.fromJson(response, Application[].class);
                        for (Application app : apps) {
                            codeCompany = app.getCompanyId();
                            Log.e("CODECOMPANY", String.valueOf(codeCompany));
                        }
                        setViewEnabled(false);
                        if(codeCompany>0){
                            try {
                                //CONSULTA AL ENDPOINT de  /api/devices/GetInfoDevice
                               // Gson gson = new Gson();

                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("HardwareId", android_id);
                                jsonBody.put("CompanyId", codeCompany);

                                //jsonBody.put("", "{´HardwareId´:´f2e47736ce53306c´, ´CompanyId´:1}");

                                String code = et_syncCode.getText().toString();
                                String URL_COMPLETE = PROTOCOL + code + URL;
                                Log.e("URLCOMPLETE", URL_COMPLETE);


                                HttpHelpers http2 = new HttpHelpers(HhSyncActivity.this, URL_COMPLETE, "");
                                http2.client(Request.Method.POST, "/api/devices/GetInfoDevice", "application/json; charset=utf-8", jsonBody, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("onResponse", response);
                                        /*try{
                                            Gson gson = new Gson();
                                            //Application[] apps = gson.fromJson(response, Application[].class);
                                            for (Application app: apps) {
                                                codeCompany=app.getCompanyId();
                                            }
                                            setViewEnabled(true);
                                        }catch (Exception ehttp){
                                            setViewEnabled(true);
                                            Log.e("Exception 2", ehttp.toString());
                                            //Toast.makeText(HhSyncActivity.this, "Error : "+ehttp.getMessage(), Toast.LENGTH_SHORT).show();
                                        }*/
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        setViewEnabled(true);
                                        Log.d(TAG, "onErrorResponse " + error.getMessage());
                                    }
                                });
                            }catch (JSONException ex){
                                Log.e("ERROR EJECUTION", ex.getMessage());
                                ex.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        Log.e("ERROR EJECUTION", e.getMessage());
                        setViewEnabled(true);
                        Toast.makeText(HhSyncActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse " + error.networkResponse);
                   // mypDialog.dismiss();
                }
            });

    }
    private void setViewEnabled(boolean enabled) {
        btn_syncronousDevice.setEnabled(enabled);

    }
}
