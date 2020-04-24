package com.appc72_uhf.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.domain.Application;
import com.appc72_uhf.app.fragment.KeyDwonFragment;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.google.gson.Gson;

import java.util.ArrayList;


public class inventoryList extends KeyDwonFragment {

    private ListView lstData;
    private MainActivity mContext;
    Button btnInventory;
    private static final String TAG="Inventory_list";

    public static final String URL_RFID="http://demo.izyrfid.com";

    private boolean shouldRefreshOnResume =false;

    ArrayList<String> data;
    ArrayAdapter<String> adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onCreateView ");
        //SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        //String access_token=preferencesAccess_token.getString("access_token", "");
        //if(access_token.length()==0){
         //   Log.e("DATAPREFERENCES", access_token);
        //}else{

        //}

        return inflater
                .inflate(R.layout.activity_inventory_list, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();


        //lisInventory=(TextView) getView().findViewById(R.id.listInventory);
        lstData = (ListView) getView().findViewById(R.id.lstData);
        btnInventory=(Button) getView().findViewById(R.id.btnInventory);
        data = new ArrayList<>();
        getData();
        btnInventory.setOnClickListener(new BtnInvenotryClickListener());
        adapter = new ArrayAdapter<>(this.mContext, android.R.layout.simple_list_item_1, data);
        lstData.setAdapter(adapter);
        lstData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });
        //lstData.setFastScrollEnabled(true);
        //lstData.canScrollVertically(data.size()-1);
        //lstData.smoothScrollToPosition(positionNumber);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
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

    public void sincronizar() {
        final InventaryRespository inventoryRepo = new InventaryRespository(this.mContext);
        data.clear();
        HttpHelpers http = new HttpHelpers(getContext(), URL_RFID, "");
        http.addHeader("Authorization", "Bearer U3vqlSYZcffila-SwBtRdVQoanGIY37FJlYMV9fLkDqCZiHmaWTu55qMPWpMOLKlGB11Z-ZJ345fMJdX8nPgYIw3sO72gxQ-sfZKnCUTYPC5dwXRYFM3gy-8O81-23mDSmogC6oSJzM88zcyj3deoOUV8s1ii3r3-etv2Z5SUgBjNaF3RG-xexMM_9b_TIJvv2pUC5iS7RvDyA_9Y0xUklCCWK5Owhky7NVw9-xkR6u40xbxOgaKStaYVFWPYfWiB2IQECsZ9ZUIBU-5OcRdeOOm2TKPpz_gDKow8IsGYyxHk33Zvx1GdML_2nsKjL-Qk6KgfvthBnExMpLaXCUAOnZC7JxwZVDlINBubyVL4GIOrcqvs4leCC_Bdmw4xqeKNf4hv74xFC4OUQ9ifU08lgreWkChV2PfcFqtAh1uOpKjTJ6YHq_5XjdmDhXxCFG2mfDjeyEmpnMajkmI34jQU_A-uhioVINnH9oRFR6QhvU_rFA6ItVM5D02y4Spt2p1");

        http.client(Request.Method.GET, "/api/inventory/GetAllInventories", "application/json; charset=utf-8", null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.e("JSONRESPONSE", response);
                    Gson gson = new Gson();
                    Application[] apps = gson.fromJson(response, Application[].class);
                    for (Application app: apps) {
                        Log.e("DATA LOOP", app.getId()+""+app.getName()+""+app.getInventoryStatus()+" "+app.getDetailForDevice());
                        boolean res = inventoryRepo.InventoryInsert(app.getId(), app.getName(), app.getInventoryStatus(), app.getDetailForDevice(), 1);
                        if(res && app.getInventoryStatus()==0) {
                            data.add(app.getId()+" "+app.getName()+" "+app.getDetailForDevice());
                            adapter.notifyDataSetChanged();
                        }
                    }
                   // lisInventory.setText("DATA :"+response);
                }catch (Exception e){
                    Log.e(TAG, "Error : "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse " + error);
            }
        });

        Toast.makeText(this.mContext, "CLICK EN EL SINCRONIZAR", Toast.LENGTH_SHORT).show();
    }
    public class BtnInvenotryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sincronizar();
        }
    }

    public void getData() {
        InventaryRespository inv = new InventaryRespository(this.mContext);
        ArrayList<String> invs = inv.ViewAllInventories();
        for (String i: invs) {
            String recip=i;
            String[] strs = recip.split("@");
            String cadena=strs[0]+" "+strs[1];
            data.add(cadena);
        }

    }

    public void ToastMg(String texto) {
        //Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
    }
}
