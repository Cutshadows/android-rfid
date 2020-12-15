package com.appc72_uhf.app.activities.reception;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.LoginActivity;
import com.appc72_uhf.app.adapter.AdapterListAutomaticReception;
import com.appc72_uhf.app.entities.DataModelReceptionAutomatic;
import com.appc72_uhf.app.fragment.KeyDwonFragment;
import com.appc72_uhf.app.repositories.ReceptionRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class Automatic_reception_activity extends KeyDwonFragment implements View.OnClickListener{
    private MainActivity mContext;
    Button btn_sync_epcReaded, btn_reception_automatic;
    ListView lv_data_reception_automatic;
    ImageButton ibtn_add_reception;
    String USER_NOW;
    ArrayList<DataModelReceptionAutomatic> dataArraylistReception;
    AdapterListAutomaticReception adapterListAutomaticReception;
    DataModelReceptionAutomatic dataModelReceptionAutomatic;

    @Override
    public void onResume() {
        Log.i("onResume", "ON RESUME 1 INVENTORY LIST");
        super.onResume();
        dataArraylistReception.clear();
        LoadReceptions();
        adapterListAutomaticReception.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.activity_automatic_reception_activity, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }
    private void initComponents(){
        mContext=(MainActivity) getActivity();
        lv_data_reception_automatic=(ListView) getView().findViewById(R.id.lv_data_reception_automatic);
        ibtn_add_reception=(ImageButton) getView().findViewById(R.id.ibtn_add_reception);
        dataArraylistReception=new ArrayList<DataModelReceptionAutomatic>();
        SharedPreferences preferencesGetUsername=getContext().getSharedPreferences("username", Context.MODE_PRIVATE);
        String Username=preferencesGetUsername.getString("username", "");
        if(Username.length()==0){
            UIHelper.ToastMessage(mContext, "Debe volver a iniciar sesión!!");
            Intent goToMain=new Intent(mContext, LoginActivity.class);
            startActivity(goToMain);
        }else{
            USER_NOW=Username;
        }
        LoadReceptions();

        adapterListAutomaticReception=new AdapterListAutomaticReception(mContext, R.layout.simple_list_reception_automatics, dataArraylistReception);
        lv_data_reception_automatic.setAdapter(adapterListAutomaticReception);
        adapterListAutomaticReception.notifyDataSetChanged();
        //btn_reception_automatic=(Button) getView().findViewById(R.id.btn_reception_automatic);
        //btn_sync_epcReaded=(Button) getView().findViewById(R.id.btn_sync_epcReaded);
        ((BaseAdapter) lv_data_reception_automatic.getAdapter()).notifyDataSetChanged();
        ibtn_add_reception.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_add_reception:
                Intent goToMainReception=new Intent(mContext, FormularyReceptionActivity.class);
                startActivity(goToMainReception);
                break;
        }
    }

    public void LoadReceptions(){
        ReceptionRepository receptionRepository=new ReceptionRepository(mContext);
        ArrayList<String> arrayRecepBD=receptionRepository.SelectReceptions(USER_NOW);
        if(arrayRecepBD.size()!=0){
            for(int indexRecep=0; indexRecep<arrayRecepBD.size();indexRecep++){
                String recip=arrayRecepBD.get(indexRecep);
                String[] recepStrs = recip.split("@@");
                dataArraylistReception.add(new DataModelReceptionAutomatic(Integer.parseInt(recepStrs[3]), recepStrs[2], recepStrs[1], 9999999));
            }
        }

    }
}
