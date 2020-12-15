package com.appc72_uhf.app.activities.reception;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.LoginActivity;
import com.appc72_uhf.app.activities.inventory.Server_inventory_activity;
import com.appc72_uhf.app.repositories.ReceptionRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class FormularyReceptionActivity extends AppCompatActivity implements View.OnClickListener{
    Spinner sp_location;
    EditText et_comentary;
    Button btn_add_reception, btn_back_reception;
    ArrayList dataSelect;
    String USER_NOW;
    ArrayAdapter<String> adapterSelectLocation;
    int item_selected_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulary_reception);
        initComponents();
    }
    public void initComponents(){
        sp_location=(Spinner) findViewById(R.id.sp_location);
        et_comentary=(EditText)findViewById(R.id.et_comentary);
        btn_add_reception=(Button)findViewById(R.id.btn_add_reception);
        btn_back_reception=(Button)findViewById(R.id.btn_back_reception);
        SharedPreferences preferencesGetUsername=getSharedPreferences("username", Context.MODE_PRIVATE);
        String Username=preferencesGetUsername.getString("username", "");
        if(Username.length()==0){
            UIHelper.ToastMessage(this, "Debe volver a iniciar sesión!!");
            Intent goToMain=new Intent(this, LoginActivity.class);
            startActivity(goToMain);
        }else{
            USER_NOW=Username;
        }
        dataSelect=new ArrayList();
        loadLocations();
        sp_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ReceptionRepository receptionRepository=new ReceptionRepository(FormularyReceptionActivity.this);
                String selectedItemLocation=parent.getItemAtPosition(position).toString().toLowerCase();
                    item_selected_location=receptionRepository.locationSelected(selectedItemLocation, USER_NOW.toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_add_reception.setOnClickListener(this);
        btn_back_reception.setOnClickListener(new BtnBackButton());
    }
    public void loadLocations(){
        final ReceptionRepository receptionRepository=new ReceptionRepository(this);
        try{
            dataSelect=receptionRepository.LoadLocations(USER_NOW);
            adapterSelectLocation=new ArrayAdapter<String>(this, R.layout.spinner_item_reception_locations, dataSelect);
            sp_location.setAdapter(adapterSelectLocation);
            adapterSelectLocation.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_reception:
                addNewReception();
                break;
            case R.id.btn_back_reception:

                break;
        }
    }
    public void  addNewReception(){
       ReceptionRepository receptionRepository=new ReceptionRepository(this);
       String ReceptionComentaryText=et_comentary.getText().toString().trim();
        boolean respInsertReception=receptionRepository.insertReceptionAutomatic(ReceptionComentaryText, item_selected_location, USER_NOW);
        if(sp_location.getSelectedItemPosition()!=0){
            if(respInsertReception){
                UIHelper.ToastMessage(this, "Recepcion creada exitosamente");
                cleanInputs();
            }else{
                UIHelper.ToastMessage(this, "Error al crear la recepción");
                cleanInputs();
            }
        }else {
            UIHelper.ToastMessage(this, "Debe seleccionar una ubicación", 2);
        }
    }

    public void cleanInputs(){
        et_comentary.setText("");
        sp_location.setSelection(0);
    }
    public class BtnBackButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent goToMain=new Intent(FormularyReceptionActivity.this, MainActivity.class);
            goToMain.putExtra("EntryType", "ReceptionAutomatics");
            FormularyReceptionActivity.this.finish();
        }
    }
}
