package com.appc72_uhf.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.appc72_uhf.app.repositories.DeviceRepository;
import com.appc72_uhf.app.tools.UIHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HhSyncActivity extends AppCompatActivity implements View.OnClickListener {
    private String android_id;
    private TextView tv_idDevice;
    private EditText et_syncCode;
    private Button btn_syncronousDevice, btn_asynDevice_back_login;
    ProgressDialog mypDialog;
    public static final String PROTOCOL="http://";
    public static final String URL=".izyrfid.com";
    private static final String TAG="HhSyncActivity";
    int codeCompany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hh_sync);
        initComponent();

    }
    private void initComponent(){
        tv_idDevice=(TextView) findViewById(R.id.tv_idDevice);
        et_syncCode=(EditText) findViewById(R.id.et_syncCode);
        btn_asynDevice_back_login=(Button) findViewById(R.id.btn_asynDevice_back_login);
        btn_syncronousDevice=(Button) findViewById(R.id.btn_syncronousDevice);
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        tv_idDevice.setText(android_id.toUpperCase());
        btn_syncronousDevice.setOnClickListener(this);
        btn_asynDevice_back_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_syncronousDevice:
                SyncHandleCompanyId();
                break;
            case R.id.btn_asynDevice_back_login:
                Intent goToLogin=new Intent(HhSyncActivity.this, LoginActivity.class);
                startActivity(goToLogin);
                break;
        }
    }

    private void SyncHandleCompanyId() {
            final String code = et_syncCode.getText().toString();
            if(!code.isEmpty()){

                /**
                 * primero consulta el id del codigo que estoy enviando al endpoint getcompanies
                 * CONSULTA AL ENDPOINT /api/document/GetCompanies
                 */
                String URL_COMPLETE = PROTOCOL + code + URL;
                mypDialog = new ProgressDialog(HhSyncActivity.this);
                mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mypDialog.setMessage("Sincronizando...");
                mypDialog.setCanceledOnTouchOutside(false);
                mypDialog.show();
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

                                obtainAuthorizationHH(codeCompany);
                            }

                        } catch (Exception e) {
                            setViewEnabled(true);
                            Toast.makeText(HhSyncActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse " + error.networkResponse);
                    }
                });
            }else{
                Toast.makeText(HhSyncActivity.this, "El campo codigo es requerido", Toast.LENGTH_SHORT).show();
                et_syncCode.findFocus();
            }
    }
    private void setViewEnabled(boolean enabled) {
        btn_syncronousDevice.setEnabled(enabled);

    }

    private void obtainAuthorizationHH(int codeCompany){
        final DeviceRepository deviceRepository=new DeviceRepository(HhSyncActivity.this);

        boolean validarCodigoLocal=deviceRepository.FindCode(codeCompany);
        if(validarCodigoLocal){
            try {
                /**
                 * CONSULTA AL ENDPOINT de  /api/devices/GetInfoDevice
                 */
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("HardwareId", android_id);
                jsonBody.put("CompanyId", codeCompany);

                //jsonBody.put("", "{´HardwareId´:´f2e47736ce53306c´, ´CompanyId´:1}");
                String code = et_syncCode.getText().toString();
                String URL_COMPLETE = PROTOCOL + code + URL;

                HttpHelpers http2 = new HttpHelpers(HhSyncActivity.this, URL_COMPLETE, "");
                http2.client(Request.Method.POST, "/api/devices/GetInfoDevice", "application/json; charset=utf-8", jsonBody, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onResponse", response);
                        try{
                            mypDialog.dismiss();
                            setViewEnabled(true);
                            Gson gson = new Gson();
                            Application apps = gson.fromJson(response, Application.class);
                            boolean inserT=deviceRepository.DeviceInsert(
                                    apps.getId(),
                                    apps.getName(),
                                    apps.getDescription(),
                                    apps.getIsActive(),
                                    apps.getIsAssigned(),
                                    apps.getCompanyId(),
                                    apps.getHardwareId(),
                                    apps.getTakingInventory(),
                                    apps.getMakeLabel()
                            );
                            if(inserT){
                                SharedPreferences savePreferencesCompanyId=getSharedPreferences("CompanyId", Context.MODE_PRIVATE);
                                SharedPreferences.Editor obj_edite=savePreferencesCompanyId.edit();
                                obj_edite.putInt("CompanyId", apps.getCompanyId());
                                obj_edite.commit();

                                SharedPreferences savePreferencesNameCompany=getSharedPreferences("CompanyName", Context.MODE_PRIVATE);
                                SharedPreferences.Editor obj_editeName=savePreferencesNameCompany.edit();
                                obj_editeName.putString("CompanyName", et_syncCode.getText().toString());
                                obj_editeName.commit();

                                Toast.makeText(HhSyncActivity.this, "Dispositivo Sincronizado", Toast.LENGTH_SHORT).show();
                                Intent goToLogin=new Intent(HhSyncActivity.this, LoginActivity.class);
                                startActivity(goToLogin);
                            }
                        }catch (Exception ehttp){
                            mypDialog.dismiss();
                            Toast.makeText(HhSyncActivity.this, "Error : "+ehttp.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Exception 2", ehttp.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setViewEnabled(true);
                        UIHelper.ToastMessage(HhSyncActivity.this, "Error al sincronizar intente mas tarde", 3);
                        finish();
                    }
                });
            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }else{
            UIHelper.ToastMessage(this, "Codigo ya se encuentra registrado", 3);
            Intent goToLogin=new Intent(HhSyncActivity.this, LoginActivity.class);
            startActivity(goToLogin);
        }

    }

}
