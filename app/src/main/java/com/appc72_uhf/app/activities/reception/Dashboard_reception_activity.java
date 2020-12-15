package com.appc72_uhf.app.activities.reception;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;

public class Dashboard_reception_activity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibtn_reception_document, ibtn_reception_automatic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_reception_activity);

        initComponents();
    }
    private void initComponents(){
        ibtn_reception_document=(ImageButton) findViewById(R.id.ibtn_reception_document);
        ibtn_reception_automatic=(ImageButton) findViewById(R.id.ibtn_reception_automatic);

        ibtn_reception_document.setOnClickListener(this);
        ibtn_reception_automatic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_reception_automatic:
                Intent goToMainReception=new Intent(Dashboard_reception_activity.this, MainActivity.class);
                goToMainReception.putExtra("EntryType", "ReceptionAutomatics");
                startActivity(goToMainReception);
                break;
            case R.id.ibtn_reception_document:
                Intent goToMainReception2=new Intent(Dashboard_reception_activity.this, MainActivity.class);
                goToMainReception2.putExtra("EntryType", "ReceptionDocuments");
                startActivity(goToMainReception2);
                break;
        };
    }
}
