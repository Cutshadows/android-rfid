package com.appc72_uhf.app.activities.inventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private MainActivity mContext;
    ImageButton btnInventory, btn_exit_productview;
    private static final String TAG="Inventory_list";


    ArrayList<DatamodelInventories> dataArrayList;
    DataAdapterInventories dataAdapterInventories;

    String code_enterprise;
    int codeCompany;
    public ListView lstData;
    DatamodelInventories datamodelInventories;

    @Override
    public void onResume() {
        Log.i("onResume", "ON RESUME 1 INVENTORY LIST");
        super.onResume();
        dataArrayList.clear();
        getData();
        dataAdapterInventories.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        Log.i("onPause", "ON PAUSE 1 INVENTORY LIST");
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
        code_enterprise=getCompany();
        getData();

        btnInventory.setOnClickListener(this);
        dataAdapterInventories=new DataAdapterInventories(getContext(), R.layout.simple_list_inventories_1, dataArrayList);
        lstData.setAdapter(dataAdapterInventories);
        dataAdapterInventories.notifyDataSetChanged();

        ((BaseAdapter) lstData.getAdapter()).notifyDataSetChanged();

    }

    public void onClick(View v) {
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
        for(int index=0; index<=invs.size()-1;index++){
            String recip=invs.get(index);
            String[] strs = recip.split("@");
            if(strs[3].equals("0")){
                dataArrayList.add(new DatamodelInventories(strs[0], strs[1], Boolean.parseBoolean(strs[2]), Integer.parseInt(strs[4])));
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
