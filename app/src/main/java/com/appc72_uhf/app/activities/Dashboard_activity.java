package com.appc72_uhf.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Dashboard_activity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibtn_takeInventory, ibtn_Labelled;
    private TextView tv_welkomen_user;
    private Button btn_create_recovery;
    private String token_access, android_id;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
     Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int companyId;
    String takingInventory, takingMakeLabel;
    RelativeLayout Relative_layout_Makelabel, Relative_layout_takeInventory;
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
        ibtn_Labelled=(ImageButton) findViewById(R.id.ibtn_Labelled);
        tv_welkomen_user=(TextView) findViewById(R.id.tv_welkomen_user);
        ibtn_Labelled.setOnClickListener(this);
        ibtn_takeInventory.setOnClickListener(this);
        Relative_layout_Makelabel=(RelativeLayout) findViewById(R.id.Relative_layout_Makelabel);
        Relative_layout_takeInventory=(RelativeLayout) findViewById(R.id.Relative_layout_takeInventory);
        btn_create_recovery=(Button) findViewById(R.id.btn_create_recovery);
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        companyId=getCompany();
        SharedPreferences preferencesGetUsername=getSharedPreferences("username", Context.MODE_PRIVATE);
        String Username=preferencesGetUsername.getString("username", "");
        if(Username.length()==0){
            UIHelper.ToastMessage(this, "Debe volver a iniciar sesión!!");
            Intent goToMain=new Intent(Dashboard_activity.this, LoginActivity.class);
            startActivity(goToMain);
        }else{
            tv_welkomen_user.setText(" "+Username);
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
        btn_create_recovery.setOnClickListener(this);
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
            case R.id.btn_create_recovery:
                String strSrc = "/data/data/com.appc72_uhf.app/databases/IZYRFID.db";
                String strDst = "/sdcard/IZYRFID.db";

                File fSrc = new File(strSrc);
                File fDst = new File(strDst);

                try{
                    copy(fSrc, fDst);
                }catch(IOException e){
                    Log.e("IOException", e.toString());
                }
                break;
        }
    }

    public static void verifyStoragePermissions(Activity activity){
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }


    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        //out.flush();
        in.close();
        out.close();
    }

    private int getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(this);
        SharedPreferences preferenceCodeActive=getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        int IdComp=0;
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;
            IdComp=companyRepository.getCompanieId(code_result);
        }
        return IdComp;
    }

}
