package com.appc72_uhf.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.domain.Application;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.tools.UIHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_ingresar, btn_nuevaEmpresa;
    private Spinner sp_code;
    private EditText et_email, et_password, et_code;
    private Integer codeCompany;
    public static final String PROTOCOL="http://";
    public static final String URL=".izyrfid.com";
    private static final String TAG="Login_activity";
    ProgressDialog mypDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
    }

    private void initComponent(){
        btn_ingresar=(Button)findViewById(R.id.btn_ingresar);
        btn_ingresar.setOnClickListener(this);
        btn_nuevaEmpresa=(Button) findViewById(R.id.btn_nuevaEmpresa);
        btn_nuevaEmpresa.setOnClickListener(this);
        sp_code=(Spinner) findViewById(R.id.sp_code);

        et_email=(EditText)findViewById(R.id.et_email);
        et_password=(EditText)findViewById(R.id.et_password);
        et_code=(EditText)findViewById(R.id.et_code);

        setViewEnabled(true);

        /**************************************************
         * Load data preferences of the get code companies
         ***************************************************/
        SharedPreferences preferencesCompanyId=getSharedPreferences("CompanyId", Context.MODE_PRIVATE);
        codeCompany=preferencesCompanyId.getInt("CompanyId", 0);

        SharedPreferences preferencesNameCompany=getSharedPreferences("CompanyName", Context.MODE_PRIVATE);
        String CompanyName=preferencesNameCompany.getString("CompanyName", "");
        if(CompanyName.length()==0){
            Log.i("No data preferences", CompanyName);
        }else{
            et_code.setText(CompanyName);
            et_code.setEnabled(false);
        }
        Log.e("COdeCompany Login", String.valueOf(codeCompany));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ingresar:
                login_process();
                break;
            case R.id.btn_nuevaEmpresa:
                Intent goToMain=new Intent(LoginActivity.this, HhSyncActivity.class);
                startActivity(goToMain);
                break;
        }

    }

    /**
     * Por el momento no estoy utilizando este metodo para conseguir el codigo de la compañia
     */
    private  void getCompanyId(){
        final String code=et_code.getText().toString();
        String URL_COMPLETE=PROTOCOL+code+URL;

        mypDialog = new ProgressDialog(LoginActivity.this);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Verificando Codigo...");
        mypDialog.setCanceledOnTouchOutside(false);
        mypDialog.show();
        HttpHelpers http = new HttpHelpers(this, URL_COMPLETE, "");
        http.client(Request.Method.GET, "/api/document/GetCompanies", "application/json; charset=utf-8", null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onResponseGetEmpresaID", response);
                try{
                    Gson gson = new Gson();
                    Application[] apps = gson.fromJson(response, Application[].class);
                    for (Application app: apps) {
                        codeCompany=app.getCompanyId();
                    }
                    setViewEnabled(true);
                    mypDialog.dismiss();
                }catch (Exception e){
                    mypDialog.dismiss();
                    setViewEnabled(true);
                    Toast.makeText(LoginActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse " + error);
                mypDialog.dismiss();
            }
        });
    }

    /**
     * Metodo para validar la sesion del usuario
     */

    private void login_process() {
        String username= et_email.getText().toString();
        String password= et_password.getText().toString();
        if(username.isEmpty() || password.isEmpty()){
            UIHelper.ToastMessage(this, "Los campos usuario y contraseña son obligatorios", 2);
        }else{
            try{
                setViewEnabled(false);
                final String code=et_code.getText().toString();

                String URL_COMPLETE=PROTOCOL+code+URL;
                Map<String, String> params = new HashMap<>();
               // params.put("username", "admin@izysearch.cl");
                params.put("username", username);
                //params.put("password", "@Mipassword123");
                params.put("password", password);
                params.put("grant_type", "password");
                params.put("CompanyId", "1");
                mypDialog = new ProgressDialog(LoginActivity.this);
                mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mypDialog.setMessage("Iniciando sesion...");
                mypDialog.setCanceledOnTouchOutside(false);
                mypDialog.show();
                HttpHelpers http = new HttpHelpers(this, URL_COMPLETE, "");
                http.clientArray(Request.Method.POST, "/token", "application/x-www-form-urlencoded; charset=UTF-8", params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        Log.e("onResponse Get Empresa", response);
                        ArrayList<String> resÁrray=new ArrayList<String>();
                        resÁrray.add(response);
                        String[] rest=response.split(",");
                        String quitStringToken=rest[0].replace("{\"access_token\":", "");
                        String access_token=quitStringToken.replace("\"", "");

                        Log.e("TOKEN ACCESS", access_token);

                        SharedPreferences savePreferencesToken=getSharedPreferences("access_token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor obj_edite=savePreferencesToken.edit();
                        obj_edite.putString("access_token", access_token);
                        obj_edite.commit();

                        Intent goToMain=new Intent(LoginActivity.this, Dashboard_activity.class);
                        startActivity(goToMain);
                        mypDialog.dismiss();
                        setViewEnabled(true);
                    }catch (Exception e){
                        setViewEnabled(true);
                        e.printStackTrace();
                    }
                 }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse " + error.getMessage());
                    setViewEnabled(true);
                    mypDialog.dismiss();
                }
            });
            }catch (Exception ex){
                setViewEnabled(true);
                ex.printStackTrace();
            }
        }
    }
    public void getDataCompany(){


    }



    private void setViewEnabled(boolean enabled) {
        btn_ingresar.setEnabled(enabled);
        btn_nuevaEmpresa.setEnabled(enabled);
    }

}
