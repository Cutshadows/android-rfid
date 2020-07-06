package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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

public class Server_inventory_activity extends AppCompatActivity {
    private ListView lv_server_inventories;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";
    private RelativeLayout layout_server_inventory_load, layout_no_data;
    ArrayList<DatamodelInventories> dataArrayList;
    DataAdapterInventoryServer dataAdapterInventoryServer;
    String token_access;
    String code_enterprise;
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
        layout_no_data=(RelativeLayout)findViewById(R.id.layout_no_data);
        lv_server_inventories = (ListView) findViewById(R.id.lv_server_inventories);
        dataArrayList=new ArrayList<DatamodelInventories>();
        dataAdapterInventoryServer=new DataAdapterInventoryServer(Server_inventory_activity.this, dataArrayList);
        lv_server_inventories.setAdapter(dataAdapterInventoryServer);
        layout_server_inventory_load.setVisibility(View.VISIBLE);
        lv_server_inventories.setVisibility(View.INVISIBLE);
        layout_no_data.setVisibility(View.INVISIBLE);


        code_enterprise=getCompany();
        SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");
        if(access_token.length()==0){
            Log.e("No data preferences", " Error data empty "+access_token);
        }else{
            token_access=access_token;
        }

        sincronizar();
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
                final String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID;
                final InventaryRespository inventoryRepo = new InventaryRespository(Server_inventory_activity.this);
                dataArrayList.clear();
                HttpHelpers http = new HttpHelpers(Server_inventory_activity.this, URL_COMPLETE, "");
                http.addHeader("Authorization", "Bearer "+token_access);
                http.client(Request.Method.GET, "/api/inventory/GetAllInventories", "application/json; charset=utf-8", null, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onResponse UBICACION", ""+response);
                        try{
                            Gson gson = new Gson();
                            Application[] apps = gson.fromJson(response, Application[].class);
                            layout_server_inventory_load.removeAllViews();
                            layout_no_data.removeAllViews();
                            lv_server_inventories.setVisibility(View.VISIBLE);
                            for( int i=0; i< apps.length; i++){
                                Log.e("DATA FOR", ""+apps[i].getId()+""+apps[i].getName()+" "+Boolean.valueOf(apps[i].getDetailForDevice())+" INVENTORY STATUS: "+apps[i].getInventoryStatus()+" INCLUDETID: "+apps[i].getIncludeTID());
                                int inventoryFound=inventoryRepo.ViewInventory(apps[i].getId());
                                if(apps[i].getInventoryStatus()==0 && inventoryFound==0){
                                    dataArrayList.add(
                                            new DatamodelInventories(
                                                    apps[i].getId(),
                                                    String.valueOf(apps[i].getName()),
                                                    Boolean.parseBoolean(apps[i].getDetailForDevice()),
                                                    apps[i].getInventoryStatus(),
                                                    apps[i].getIncludeTID(),
                                                    apps[i].getIsSelect(),
                                                    1,
                                                    codeCompany
                                            ));
                                }
                            }
                        }catch (Exception e){
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error : "+e.getMessage(), 1);
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, no hay conexion a internet", 3);
                        } else if (error instanceof ServerError) {
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, credenciales invalidas", 3);
                        } else if (error instanceof AuthFailureError) {
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, intente mas tarde.", 3);
                        } else if (error instanceof ParseError) {
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error desconocido, intente mas tarde", 3);
                        } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            UIHelper.ToastMessage(Server_inventory_activity.this, "Error con el servidor, intente mas tarde!!!", 3);
                            Server_inventory_activity.super.onBackPressed();
                        }
                    }
                });
            /**
             * Cargar inventario por documento
             */
            HttpHelpers http2 = new HttpHelpers(Server_inventory_activity.this, URL_COMPLETE, "");
            http.addHeader("Authorization", "Bearer "+token_access);
            http.client(Request.Method.GET, "/api/inventoryDoc/GetAllInventories", "application/json; charset=utf-8", null, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("onResponse DOCUMENT", ""+response);
                    try{
                        Gson gson = new Gson();
                        Application[] inventaryDoc = gson.fromJson(response, Application[].class);
                        /*layout_server_inventory_load.removeAllViews();
                        layout_no_data.removeAllViews();
                        lv_server_inventories.setVisibility(View.VISIBLE);*/
                        for (Application application : inventaryDoc) {
                            Log.e("DATA FOR", "" + application.getId() + "" + application.getName() + " " + Boolean.valueOf(application.getDetailForDevice()) + " INVENTORY STATUS: " + application.getInventoryStatus() + " INCLUDETID: " + application.getIncludeTID());
                            int inventoryFound = inventoryRepo.ViewInventory(application.getId());
                            Log.e("InventoryStatus", "InventoryStatus: " + application.getInventoryStatus());
                            if (application.getInventoryStatus() == 0 && inventoryFound == 0) {
                                dataArrayList.add(
                                        new DatamodelInventories(
                                                application.getId(),
                                                application.getName(),
                                                Boolean.parseBoolean(application.getDetailForDevice()),
                                                application.getInventoryStatus(),
                                                application.getIncludeTID(),
                                                application.getIsSelect(),
                                                2,
                                                codeCompany
                                        ));
                            }
                        }
                        dataAdapterInventoryServer.notifyDataSetChanged();
                    }catch (Exception ex){
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error : "+ex.getLocalizedMessage(), 5);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NetworkError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, no hay conexion a internet", 3);
                    } else if (error instanceof ServerError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, credenciales invalidas", 3);
                    } else if (error instanceof AuthFailureError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, intente mas tarde.", 3);
                    } else if (error instanceof ParseError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error desconocido, intente mas tarde", 3);
                    } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error con el servidor, intente mas tarde!!!", 3);
                        Server_inventory_activity.super.onBackPressed();
                    }
                }
            });
            /**
             * FIN DE CARGA POR DOCUMENTO
             * INICIO DE INVENTARIO PLANTILLA
             */
            http.addHeader("Authorization", "Bearer "+token_access);
            http.client(Request.Method.GET, "/api/inventoryTemplate/GetAllInventories", "application/json; charset=utf-8", null, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("onResponse DOCUMENT", ""+response);
                    try{
                        Gson gson = new Gson();
                        Application[] inventaryDoc = gson.fromJson(response, Application[].class);
                        /*layout_server_inventory_load.removeAllViews();
                        layout_no_data.removeAllViews();
                        lv_server_inventories.setVisibility(View.VISIBLE);*/
                        for (Application application : inventaryDoc) {
                            Log.e("DATA FOR", "" + application.getId() + "" + application.getName() + " " + Boolean.valueOf(application.getDetailForDevice()) + " INVENTORY STATUS: " + application.getInventoryStatus() + " INCLUDETID: " + application.getIncludeTID());
                            int inventoryFound = inventoryRepo.ViewInventory(application.getId());
                            Log.e("InventoryStatus", "InventoryStatus: " + application.getInventoryStatus());
                            if (application.getInventoryStatus() == 0 && inventoryFound == 0) {
                                dataArrayList.add(
                                        new DatamodelInventories(
                                                application.getId(),
                                                application.getName(),
                                                Boolean.parseBoolean(application.getDetailForDevice()),
                                                application.getInventoryStatus(),
                                                application.getIncludeTID(),
                                                application.getIsSelect(),
                                                3,
                                                codeCompany
                                        ));
                            }
                        }
                        dataAdapterInventoryServer.notifyDataSetChanged();
                    }catch (Exception ex){
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error : "+ex.getLocalizedMessage(), 5);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NetworkError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, no hay conexion a internet", 3);
                    } else if (error instanceof ServerError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, credenciales invalidas", 3);
                    } else if (error instanceof AuthFailureError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, intente mas tarde.", 3);
                    } else if (error instanceof ParseError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error desconocido, intente mas tarde", 3);
                    } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error con el servidor, intente mas tarde!!!", 3);
                        Server_inventory_activity.super.onBackPressed();
                    }
                }
            });
            /**
             * FIN DE CARGA POR PLANTILLA
             * INICIO DE INVENTARIO PRODUCTO
             */
            http.addHeader("Authorization", "Bearer "+token_access);
            http.client(Request.Method.GET, "/api/inventoryProduct/GetAllInventories", "application/json; charset=utf-8", null, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("onResponse DOCUMENT", ""+response);
                    try{
                        Gson gson = new Gson();
                        Application[] inventaryDoc = gson.fromJson(response, Application[].class);
                        /*layout_server_inventory_load.removeAllViews();
                        layout_no_data.removeAllViews();
                        lv_server_inventories.setVisibility(View.VISIBLE);*/
                        for (Application application : inventaryDoc) {
                            Log.e("DATA FOR", "" + application.getId() + "" + application.getName() + " " + Boolean.valueOf(application.getDetailForDevice()) + " INVENTORY STATUS: " + application.getInventoryStatus() + " INCLUDETID: " + application.getIncludeTID());
                            int inventoryFound = inventoryRepo.ViewInventory(application.getId());
                            Log.e("InventoryStatus", "InventoryStatus: " + application.getInventoryStatus());
                            if (application.getInventoryStatus() == 0 && inventoryFound == 0) {
                                dataArrayList.add(
                                        new DatamodelInventories(
                                                application.getId(),
                                                application.getName(),
                                                Boolean.parseBoolean(application.getDetailForDevice()),
                                                application.getInventoryStatus(),
                                                application.getIncludeTID(),
                                                application.getIsSelect(),
                                                4,
                                                codeCompany
                                        ));
                            }
                        }
                        dataAdapterInventoryServer.notifyDataSetChanged();
                    }catch (Exception ex){
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error : "+ex.getLocalizedMessage(), 5);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NetworkError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, no hay conexion a internet", 3);
                    } else if (error instanceof ServerError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, credenciales invalidas", 3);
                    } else if (error instanceof AuthFailureError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error de conexion, intente mas tarde.", 3);
                    } else if (error instanceof ParseError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error desconocido, intente mas tarde", 3);
                    } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        UIHelper.ToastMessage(Server_inventory_activity.this, "Error con el servidor, intente mas tarde!!!", 3);
                        Server_inventory_activity.super.onBackPressed();
                    }
                }
            });
            /**
             * FIN DE CARGA POR PRODUCTO
             */
            dataAdapterInventoryServer.notifyDataSetChanged();

        }else{
            UIHelper.ToastMessage(this, "Necesita Internet para sincronizar", 3);
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
