package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterProductDetails;
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.repositories.DetailProductRepository;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Detail_product_activity extends AppCompatActivity {
    private TextView tv_tittle_detail;
    private ListView lv_detail_product;
    public static final String PROTOCOL_URLRFID="http://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";
    ArrayList<DataModelProductDetails> dataArrayProducts;
    AdapterProductDetails adapterProductDetails;
    String token_access;
    String code_enterprise;
    String val_inventory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_product_activity);
        initComponent();
    }
    private void initComponent(){
        tv_tittle_detail=(TextView)findViewById(R.id.tv_tittle_detail);
        lv_detail_product=(ListView) findViewById(R.id.lv_detail_product);
        dataArrayProducts=new ArrayList<DataModelProductDetails>();
        val_inventory="";
        val_inventory=getIntent().getStringExtra("Id");
        Log.e("ID", ""+val_inventory);
        code_enterprise=getCompany();
        SharedPreferences preferencesAccess_token=getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");

        if(access_token.length()==0){
            Log.e("No data preferences", " Error data no empty "+access_token);
        }else{
            token_access=access_token;
        }
        loadProductDetail();
        adapterProductDetails=new AdapterProductDetails(this, dataArrayProducts);
        lv_detail_product.setAdapter(adapterProductDetails);


    }
    private void loadProductDetail(){
        String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise+DOMAIN_URLRFID;
        final DetailProductRepository detailProductRepository=new DetailProductRepository(Detail_product_activity.this);
        HttpHelpers http = new HttpHelpers(Detail_product_activity.this, URL_COMPLETE, "");
        http.addHeader("Authorization", "Bearer "+token_access);
        http.clientProductDetail(Request.Method.GET, "/api/inventory/GetDetailForDevice?InventoryId="+val_inventory, null,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onRESPONSE PRODUCT", response);
                try{
                    Gson gson=new Gson();
                    DataModelProductDetails[] products=gson.fromJson(response, DataModelProductDetails[].class);
                    for(int index=0; index<=products.length-1; index++){
                       Log.e("DATA FOR", "ID: "+products[index].getId()+" NAME:"+products[index].getName()+" ProductMaster:"+products[index].getProductMasterId()+" FOUNG:"+Boolean.valueOf(products[index].getFound()));
                       boolean resultInsertProduct=detailProductRepository.DetailProductInsert(products[index].getId(), products[index].getEPC(), products[index].getCode(), products[index].getName(), products[index].getFound(), products[index].getProductMasterId());
                       if(resultInsertProduct){
                        ArrayList productList=detailProductRepository.ProductList();
                        Log.e("ProductList", productList.toString());
                       }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", ""+error.getLocalizedMessage());
            }
        });


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
