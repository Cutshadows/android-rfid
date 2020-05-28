package com.appc72_uhf.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_ingresar, btn_nuevaEmpresa;
    private Spinner sp_code;
    private EditText et_email, et_password;
    private Integer codeCompany;
    public static final String PROTOCOL="http://";
    public static final String URL=".izyrfid.com";
    private static final String TAG="Login_activity";
    ProgressDialog mypDialog;
    ArrayList<String> dataSelect = new ArrayList<>();
    ArrayAdapter<String> adapterSelect;
    String et_code="";

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
        //et_code=(EditText)findViewById(R.id.et_code);
        codeCompany=0;
        sp_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final CompanyRepository deviceRepository=new CompanyRepository(LoginActivity.this);
                try{
                    codeCompany=deviceRepository.getCompanieId(parent.getItemAtPosition(position).toString().toLowerCase());
                    et_code=parent.getItemAtPosition(position).toString();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setViewEnabled(true);

        dataSelect=new ArrayList<>();
        /**
         * LLenaddo de los codigos ingresados
         */
        getDataCompany();


        SharedPreferences preferencesGetUsername=getSharedPreferences("username", Context.MODE_PRIVATE);
        String Username=preferencesGetUsername.getString("username", "");
        if(Username.length()==0){
            Log.i("No data preferences", Username);
        }else{
            et_email.setText(Username);
        }

        /**************************************************
                 * Load data preferences of the get code companies

        SharedPreferences preferencesCompanyId=getSharedPreferences("CompanyId", Context.MODE_PRIVATE);
        codeCompany=preferencesCompanyId.getInt("CompanyId", 0);

        SharedPreferences preferencesNameCompany=getSharedPreferences("CompanyName", Context.MODE_PRIVATE);
        String CompanyName=preferencesNameCompany.getString("CompanyName", "");
        if(CompanyName.length()==0){
            Log.i("No data preferences", CompanyName);
        }else{
            dataSelect.add(CompanyName);
            et_code.setText(CompanyName);
            et_code.setEnabled(false);
        }
        Log.e("COdeCompany Login", String.valueOf(codeCompany));
       ***************************************************/
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
     * Metodo para validar la sesion del usuario
     */

    private void login_process() {
        String username= et_email.getText().toString().toLowerCase();
        String password= et_password.getText().toString();
        int companyId=codeCompany;

        if(username.isEmpty() || password.isEmpty()){
            UIHelper.ToastMessage(this, "Los campos usuario y contraseña son obligatorios", 2);
        }else{
            SharedPreferences savePreferencesUsername=getSharedPreferences("username", Context.MODE_PRIVATE);
            SharedPreferences.Editor obj_edite=savePreferencesUsername.edit();
            obj_edite.putString("username", username);
            obj_edite.apply();
            try{
                setViewEnabled(false);
                final String code=et_code;

                String URL_COMPLETE=PROTOCOL+code.toLowerCase()+URL;
                Map<String, String> params = new HashMap<>();
               // params.put("username", "admin@izysearch.cl");
                params.put("username", username);
                params.put("password", password);
                params.put("grant_type", "password");
                params.put("CompanyId", String.valueOf(companyId));


                Log.e("valor 2", URLDecoder.decode(URL_COMPLETE));
                Log.e("valor 3", password);
                Log.e("valor 4", String.valueOf(companyId));

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
                        ArrayList<String> resArray=new ArrayList<String>();
                        resArray.add(response);
                        String[] rest=response.split(",");
                        String quitStringToken=rest[0].replace("{\"access_token\":", "");
                        String access_token=quitStringToken.replace("\"", "");

                        Log.e("TOKEN ACCESS", access_token);

                        SharedPreferences savePreferenceCodeActive=getSharedPreferences("code_activate", Context.MODE_PRIVATE);
                        SharedPreferences.Editor obj_codeActive=savePreferenceCodeActive.edit();
                        obj_codeActive.putString("code_activate", code);
                        obj_codeActive.apply();

                        SharedPreferences savePreferencesToken=getSharedPreferences("access_token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor obj_edite=savePreferencesToken.edit();
                        obj_edite.putString("access_token", access_token);
                        obj_edite.apply();

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
                    if (error instanceof NetworkError) {
                    } else if (error instanceof ServerError) {
                    } else if (error instanceof AuthFailureError) {
                    } else if (error instanceof ParseError) {
                    } else if (error instanceof NoConnectionError) {
                    } else if (error instanceof TimeoutError) {
                        mypDialog.dismiss();
                        UIHelper.ToastMessage(LoginActivity.this, "Error con el servidor, intente mas tarde!!!", 3);
                    }
                }
            });
            }catch (Exception ex){
                setViewEnabled(true);
                ex.printStackTrace();
            }
        }
    }
    public void getDataCompany(){
        final CompanyRepository companyRepository=new CompanyRepository(LoginActivity.this);
        dataSelect.clear();
        try{
            dataSelect=companyRepository.LoadCompanies();
        }catch (Exception e){
            e.printStackTrace();
        }
        adapterSelect=new ArrayAdapter<String>(this, R.layout.spinner_item_companyid, dataSelect);
        sp_code.setAdapter(adapterSelect);
        adapterSelect.notifyDataSetChanged();
    }

    private void setViewEnabled(boolean enabled) {
        btn_ingresar.setEnabled(enabled);
        btn_nuevaEmpresa.setEnabled(enabled);
    }
}
