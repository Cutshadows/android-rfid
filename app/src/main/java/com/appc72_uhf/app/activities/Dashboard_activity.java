package com.appc72_uhf.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.reception.Dashboard_reception_activity;
import com.appc72_uhf.app.helpers.DialogOptionsHelpers;
import com.appc72_uhf.app.helpers.GetParametersHelper;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.tools.UIHelper;

public class Dashboard_activity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibtn_takeInventory, ibtn_Labelled, ibtn_reception;
    private Button btn_logout, btn_syncronousParameters;
    private TextView tv_welkomen_user;
    private Button btn_create_recovery;
    private String token_access, android_id;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
     Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int companyId;
    String code_enterprise, USER_NOW;
    RelativeLayout Relative_layout_Makelabel, Relative_layout_takeInventory;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_activity);
        initComponent();
        verifyStoragePermissions(this);
        //devicePermissions();

    }
    private void initComponent(){
        ibtn_takeInventory=(ImageButton) findViewById(R.id.ibtn_takeInventory);
        ibtn_reception=(ImageButton) findViewById(R.id.ibtn_reception);
        ibtn_Labelled=(ImageButton) findViewById(R.id.ibtn_Labelled);
        tv_welkomen_user=(TextView) findViewById(R.id.tv_welkomen_user);
        btn_logout=(Button) findViewById(R.id.btn_logout);
        btn_syncronousParameters=(Button) findViewById(R.id.btn_syncronousParameters);

        btn_logout.setOnClickListener(this);
        ibtn_Labelled.setOnClickListener(this);
        ibtn_takeInventory.setOnClickListener(this);
        ibtn_reception.setOnClickListener(this);
        btn_syncronousParameters.setOnClickListener(this);


        Relative_layout_Makelabel=(RelativeLayout) findViewById(R.id.Relative_layout_Makelabel);
        Relative_layout_takeInventory=(RelativeLayout) findViewById(R.id.Relative_layout_takeInventory);
       // btn_create_recovery=(Button) findViewById(R.id.btn_create_recovery);
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        code_enterprise=getCompany();


        SharedPreferences preferencesGetUsername=getSharedPreferences("username", Context.MODE_PRIVATE);
        String Username=preferencesGetUsername.getString("username", "");
        if(Username.length()==0){
            UIHelper.ToastMessage(this, "Debe volver a iniciar sesión!!");
            Intent goToMain=new Intent(Dashboard_activity.this, LoginActivity.class);
            startActivity(goToMain);
        }else{
            tv_welkomen_user.setText(" "+Username);
            USER_NOW=Username;
        }
        SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");
        if(access_token.length()==0){
            UIHelper.ToastMessage(this, "Debe volver a iniciar sesión!!");
            Intent goToMain=new Intent(Dashboard_activity.this, LoginActivity.class);
            startActivity(goToMain);
        }else{
            token_access=access_token;
        }
       // btn_create_recovery.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_takeInventory:
                Intent goToMain=new Intent(Dashboard_activity.this, MainActivity.class);
                goToMain.putExtra("EntryType", "Inventory");
                startActivity(goToMain);
                break;
            case R.id.ibtn_Labelled:
                Intent goToMain2=new Intent(Dashboard_activity.this, MainActivity.class);
                goToMain2.putExtra("EntryType", "MakeLabel");
                startActivity(goToMain2);
                break;
            case R.id.ibtn_reception:
                Intent goToMainReception=new Intent(Dashboard_activity.this, Dashboard_reception_activity.class);
                goToMainReception.putExtra("EntryType", "ReceptionDocuments");
                startActivity(goToMainReception);
                break;
            case R.id.btn_logout:
                DialogOptionsHelpers dialogOptionsHelpers=new DialogOptionsHelpers(this);
                dialogOptionsHelpers.showDialog();
               break;
            case R.id.btn_syncronousParameters:
                getParameters();
                break;
        }
    }

    public static void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onBackPressed() {
    }
    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(this);
        SharedPreferences preferenceCodeActive=getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;
            Log.e("code_result", ""+code_result);
            companyId=companyRepository.getCompanieId(code_result.toLowerCase());
            Log.e("companyId", ""+companyId);
        }
        return code_result;
    }

    public void getParameters(){
        ConnectivityManager connMgr=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : connMgr.getAllNetworks()) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            }
        }
        if(isMobileConn || isWifiConn){
            UIHelper.ToastMessage(this, "Sincronizando...", 3);
            final String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID;
            GetParametersHelper getParametersHelper=new GetParametersHelper(this);
            Log.e("GET PARAM HELPER", "ESTOY ACA"+USER_NOW);

                getParametersHelper.SaveLocationByUser(URL_COMPLETE, USER_NOW, companyId);
        }else{
            UIHelper.ToastMessage(this, "Necesita Internet para sincronizar", 3);
        }
    }

}
