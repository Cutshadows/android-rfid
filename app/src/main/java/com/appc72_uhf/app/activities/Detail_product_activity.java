package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterProductDetails;
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.repositories.DetailProductRepository;

import java.util.ArrayList;

public class Detail_product_activity extends AppCompatActivity {
    private TextView tv_detail_inventory;
    private ListView lv_detail_product;
    ArrayList<DataModelProductDetails> dataArrayProducts;
    AdapterProductDetails adapterProductDetails;
    String val_inventory;
    boolean chargeFirstTimeProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_product_activity);
        initComponent();
    }

    @Override
    protected void onResume() {
        Log.e("onResume", "ON RESUME 1");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("onPause", "ON PAUSE 1");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("onStop", "ON STTOP 1");
        super.onStop();
    }

    private void initComponent(){
        chargeFirstTimeProduct=true;
        tv_detail_inventory=(TextView)findViewById(R.id.tv_detail_inventory);
        lv_detail_product=(ListView) findViewById(R.id.lv_detail_product);
        dataArrayProducts=new ArrayList<DataModelProductDetails>();
        val_inventory="";
        val_inventory=getIntent().getStringExtra("Id");
        //code_enterprise=getCompany();
        //SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        //String access_token=preferencesAccess_token.getString("access_token", "");

        /*if(access_token.length()==0){
            Log.e("No data preferences", " Error data no empty "+access_token);
        }else{
            token_access=access_token;
        }*/
        getDataProductMaster();
        adapterProductDetails=new AdapterProductDetails(Detail_product_activity.this, dataArrayProducts);
        lv_detail_product.setAdapter(adapterProductDetails);
        tv_detail_inventory.setText(getIntent().getStringExtra("Name"));
        adapterProductDetails.notifyDataSetChanged();
    }
    private void loadProductDetail(){
        /*if(chargeFirstTimeProduct){
            Log.e("chargeFirstTimeProduct", "ESTOY CARGANDO LOS DATOS DEL ENDPOINT");

            String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise+DOMAIN_URLRFID;
            final DetailProductRepository detailProductRepository=new DetailProductRepository(Detail_product_activity.this);
            HttpHelpers http = new HttpHelpers(Detail_product_activity.this, URL_COMPLETE, "");
            http.addHeader("Authorization", "Bearer "+token_access);
            Log.e("INVENTARIO INT", "/api/inventory/GetDetailForDevice?InventoryId="+val_inventory);
            http.clientProductDetail(Request.Method.GET, "/api/inventory/GetDetailForDevice?InventoryId="+val_inventory, null,  new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("onRESPONSE PRODUCT", response);
                    try{
                        Gson gson=new Gson();
                        DataModelProductDetails[] products=gson.fromJson(response, DataModelProductDetails[].class);
                        for(int index=0; index<=products.length-1; index++){
                            Log.e("DATA FOR", "ID: "+products[index].getId()+" NAME:"+products[index].getName()+" ProductMaster:"+products[index].getProductMasterId()+" FOUNG:"+Boolean.valueOf(products[index].getFound())+ "INTEGER INVENTARIO: "+Integer.parseInt(val_inventory));
                            boolean resultInsertProduct=detailProductRepository.DetailProductInsert(products[index].getId(), products[index].getEPC(), products[index].getCode(), products[index].getName(), products[index].getFound(), products[index].getProductMasterId(), Integer.parseInt(val_inventory));
                            if(resultInsertProduct){
                                dataArrayProducts.clear();
                                getDataProductMaster();
                            }
                        }
                        adapterProductDetails.notifyDataSetChanged();
                        chargeFirstTimeProduct=false;
                    }catch (Exception e){
                        chargeFirstTimeProduct=true;
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    chargeFirstTimeProduct=true;
                    Log.e("onErrorResponse", ""+error.getLocalizedMessage());
                }
            });
        }else{*/
            dataArrayProducts.clear();
            getDataProductMaster();
        //}

    }
    public void getDataProductMaster(){
        try{
            DetailProductRepository detailProductRepository=new DetailProductRepository(this);
            ArrayList<String> products=detailProductRepository.OrderProductMasterId(Integer.parseInt(val_inventory));
            for(int indexProd=0; indexProd<=products.size(); indexProd++){
                String receptProducts=products.get(indexProd);
                String[] strProds=receptProducts.split("@");
                dataArrayProducts.add(new DataModelProductDetails(Integer.parseInt(strProds[0]), Integer.parseInt(strProds[1]), strProds[2], Integer.parseInt(val_inventory), strProds[3]));
            }
        }catch (Exception e){
            Log.e("Exception", ""+e.getMessage());
        }
    }
    private String getCompany(){
        SharedPreferences preferenceCodeActive=getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;

        }
        return code_result;
    }
}
