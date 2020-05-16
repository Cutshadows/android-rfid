package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.DataAdapterInventories;
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.fragment.KeyDwonFragment;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;

import java.util.ArrayList;


public class inventoryList extends KeyDwonFragment implements View.OnClickListener{
    private ListView lstData;
    private MainActivity mContext;
    ImageButton btnInventory;
    private static final String TAG="Inventory_list";

    public static final String PROTOCOL_URLRFID="http://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";

    ArrayList<DatamodelInventories> dataArrayList;
    DataAdapterInventories dataAdapterInventories;

    //ArrayAdapter<String> adapter;
    String token_access;
    String code_enterprise;
    int codeCompany;
    DatamodelInventories datamodelInventories;


    @Override
    public void onResume() {
        Log.e("onResume", "ON RESUME 1 INVENTORY LIST");
        super.onResume();
        dataArrayList.clear();
        getData();
        dataAdapterInventories.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        Log.e("onPause", "ON PAUSE 1 INVENTORY LIST");
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.activity_inventory_list, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponent();
    }
    private void initComponent(){
        mContext = (MainActivity) getActivity();
        //lisInventory=(TextView) getView().findViewById(R.id.listInventory);
        lstData = (ListView) getView().findViewById(R.id.lstData);
        btnInventory=(ImageButton) getView().findViewById(R.id.btnInventory);
        dataArrayList=new ArrayList<DatamodelInventories>();
        //dataArrayList.add(new DatamodelInventories(9, "Android 4.0", false));*/
        code_enterprise=getCompany();

        getData();

        dataAdapterInventories=new DataAdapterInventories(mContext, dataArrayList);
        lstData.setAdapter(dataAdapterInventories);
        dataAdapterInventories.notifyDataSetChanged();

        btnInventory.setOnClickListener(this);



        //adapter = new ArrayAdapter<>(this.mContext, R.layout.simple_list_inventories_1, data);
        /*
       lstData.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/

        SharedPreferences preferencesAccess_token=this.getActivity().getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");

        if(access_token.length()==0){
            Log.e("No data preferences", " Error data no empty "+access_token);
        }else{
            token_access=access_token;
        }
    }

    @Override
    public void onStop() {
        Log.e("onStop", "ON STOP 1 INVENTORY LIST");
        super.onStop();
    }

    /*** @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sincronizar:
                sincronizar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }***/

    public void onClick(View v) {
            Log.e("CLICK ON", "CLICK EN EL BOTON MAS");
            switch (v.getId()){
                case R.id.btnInventory:
                    Intent goToInventory=new Intent(getContext(), Server_inventory_activity.class);
                    startActivity(goToInventory);
                    break;
            }

        }

    private void getData() {
        InventaryRespository inv = new InventaryRespository(this.mContext);
        ArrayList<String> invs = inv.ViewInventoriesHH(codeCompany);
        Log.e("DATA HH", invs.toString());
        for(int index=0; index<=invs.size()-1;index++){
            String recip=invs.get(index);
            Log.e("RECIP", recip);
            String[] strs = recip.split("@");
            if(strs[3].equals("0")){
                dataArrayList.add(new DatamodelInventories(Integer.parseInt(strs[0]), strs[1], Boolean.parseBoolean(strs[2]), Integer.parseInt(strs[4])));
            }
        }
    }

    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(mContext);
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        int companyId;
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);

        }else{
            code_result=enterprises_code;
            companyId=companyRepository.getCompanieId(code_result);
            codeCompany=companyId;

        }
        return code_result;
    }
}
