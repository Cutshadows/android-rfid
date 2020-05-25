package com.appc72_uhf.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.DataAdapterInventoryServer;
import com.appc72_uhf.app.domain.Application;
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.tools.UIHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Server_inventory_activity extends AppCompatActivity implements View.OnClickListener {
    private ListView lv_server_inventories;
    public static final String PROTOCOL_URLRFID="http://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";
    private Button btn_syncInventoryServer;
    private RelativeLayout layout_server_inventory_load;
    ProgressDialog mypDialog;
    ArrayList<DatamodelInventories> dataArrayList;
    DataAdapterInventoryServer dataAdapterInventoryServer;
    String token_access;
    String code_enterprise;
    DatamodelInventories datamodelInventories;
    private boolean shouldRequestOffline =false;
    int codeCompany;
    private static final String DEBUG_TAG="NetworkStatusExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_inventory_activity);
        initComponent();

    }
    private void initComponent(){
        layout_server_inventory_load=(RelativeLayout)findViewById(R.id.layout_server_inventory_load);
        lv_server_inventories = (ListView) findViewById(R.id.lv_server_inventories);
        btn_syncInventoryServer=(Button) findViewById(R.id.btn_syncInventoryServer);
        btn_syncInventoryServer.setOnClickListener(this);
        dataArrayList=new ArrayList<DatamodelInventories>();
        getData();
        dataAdapterInventoryServer=new DataAdapterInventoryServer(Server_inventory_activity.this, dataArrayList);
        lv_server_inventories.setAdapter(dataAdapterInventoryServer);
        layout_server_inventory_load.setVisibility(View.VISIBLE);
        lv_server_inventories.setVisibility(View.INVISIBLE);
        code_enterprise=getCompany();
        SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");
        if(access_token.length()==0){
            Log.e("No data preferences", " Error data no empty "+access_token);
        }else{
            token_access=access_token;
        }

        sincronizar();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_syncInventoryServer:
                UIHelper.ToastMessage(Server_inventory_activity.this, "Funcion descargar", 3);
                break;
        }
    }

    public void sincronizar() {
        /**
         * Verify connection to internet
         */
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
            /**
             * Function to get inventories from web
             */
            /*mypDialog = new ProgressDialog(Server_inventory_activity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Sincronizando inventarios disponibles...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();*/
                String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise+DOMAIN_URLRFID;
                final InventaryRespository inventoryRepo = new InventaryRespository(Server_inventory_activity.this);
                dataArrayList.clear();
                HttpHelpers http = new HttpHelpers(Server_inventory_activity.this, URL_COMPLETE, "");
                http.addHeader("Authorization", "Bearer "+token_access);
                http.client(Request.Method.GET, "/api/inventory/GetAllInventories", "application/json; charset=utf-8", null, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Gson gson = new Gson();
                            Application[] apps = gson.fromJson(response, Application[].class);
                            layout_server_inventory_load.removeAllViews();
                            //layout_server_inventory_load.setVisibility(View.INVISIBLE);
                            lv_server_inventories.setVisibility(View.VISIBLE);
                            dataArrayList.clear();
                            for( int i=0; i<=apps.length-1; i++){
                                //boolean res = inventoryRepo.InventoryInsert(apps[i].getId(), apps[i].getName(), apps[i].getInventoryStatus(), apps[i].getDetailForDevice(), codeCompany);
                                Log.e("DATA FOR", ""+apps[i].getId()+""+apps[i].getName()+" "+Boolean.valueOf(apps[i].getDetailForDevice())+" INVENTORY STATUS: "+apps[i].getInventoryStatus());

                                //if(res && apps[i].getInventoryStatus()==0) {
                                if(apps[i].getInventoryStatus()==0){
                                    dataArrayList.add(new DatamodelInventories(apps[i].getId(), String.valueOf(apps[i].getName()),  Boolean.parseBoolean(apps[i].getDetailForDevice()), apps[i].getInventoryStatus(), apps[i].getIsSelect(), codeCompany));
                                }
                                //}
                            }
                            shouldRequestOffline=true;
                            //mypDialog.dismiss();
                            dataAdapterInventoryServer.notifyDataSetChanged();
                        }catch (Exception e){
                            shouldRequestOffline=false;
                           // mypDialog.dismiss();
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error : "+e.getMessage(), 1);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //mypDialog.dismiss();
                        shouldRequestOffline=false;
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error en servidor, intente mas tarde", 1);
                    }
                });

        }else{
            UIHelper.ToastMessage(this, "Necesita Internet para sincronizar", 3);
        }

    }
    public void getData() {
        try{
            InventaryRespository inv = new InventaryRespository(Server_inventory_activity.this);
            ArrayList<String> invs = inv.ViewAllInventories();
            Log.e("INVS", invs.toString());
            for (int index=0; index<=invs.size()-1;index++){
                String recip=invs.get(index);
                String[] strs = recip.split("@");
                if(strs[3].equals("0")){
                    Log.e("RECIP", recip);
                   //dataArrayList.add(new DatamodelInventories(Integer.parseInt(strs[0]), strs[1], Boolean.parseBoolean(strs[2]), Integer.parseInt(strs[4])));
                }
            }

           // mypDialog.dismiss();
            shouldRequestOffline=true;
        }catch (Exception e){
            Log.e("Exception", ""+e.getMessage());
            shouldRequestOffline=false;
        }

     }

    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(this);
        SharedPreferences preferenceCodeActive=getSharedPreferences("code_activate", Context.MODE_PRIVATE);
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
