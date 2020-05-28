package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;

public class Dashboard_activity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibtn_takeInventory, ibtn_Labelled;
    private TextView tv_welkomen_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_activity);
        initComponent();


    }
    private void initComponent(){
        ibtn_takeInventory=(ImageButton) findViewById(R.id.ibtn_takeInventory);
        ibtn_Labelled=(ImageButton) findViewById(R.id.ibtn_Labelled);
        tv_welkomen_user=(TextView) findViewById(R.id.tv_welkomen_user);
        ibtn_Labelled.setOnClickListener(this);
        ibtn_takeInventory.setOnClickListener(this);



        SharedPreferences preferencesGetUsername=getSharedPreferences("username", Context.MODE_PRIVATE);
        String Username=preferencesGetUsername.getString("username", "");
        if(Username.length()==0){
            Log.i("No data preferences", Username);
        }else{
            tv_welkomen_user.setText(" "+Username);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_takeInventory:
                Intent goToMain=new Intent(Dashboard_activity.this, MainActivity.class);
                startActivity(goToMain);
                break;
            case R.id.ibtn_Labelled:
                //Intent goToMain2=new Intent(Dashboard_activity.this, Make_label_documents_activity.class);
                //startActivity(goToMain2);
                break;
        }
    }
}
