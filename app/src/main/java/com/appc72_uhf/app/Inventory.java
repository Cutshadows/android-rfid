package com.appc72_uhf.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.interfaceApi.LecturaServicio;
import com.appc72_uhf.app.models.Codigo;
import com.appc72_uhf.app.models.LectorRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Inventory extends AppCompatActivity {
    private final static String TAG ="InventoryActivity";
    private Retrofit retrofit;
    private TextView tvitem;
    private ListView lvInventory;

    private String titleInventory []={"Inventario 1", "Iventario 2"};
    private String detailInventory []={"Cantidad 20/10", "Cantidad 10/09" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        tvitem=(TextView)findViewById(R.id.tv_titleInventory);
        lvInventory=(ListView) findViewById(R.id.lv_inventory);

        retrofit =new Retrofit.Builder()
                .baseUrl("http://rw.izyrfid.com/api/inventory/GetAllInventories")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getData();
    }
    private void getData(){
        LecturaServicio servicio = retrofit.create(LecturaServicio.class);
        Call<LectorRespuesta> responseCall = servicio.obtenerListaEtiqueta();

        responseCall.enqueue(new Callback<LectorRespuesta>() {
            @Override
            public void onResponse(Call<LectorRespuesta> call, Response<LectorRespuesta> response) {
                if(response.isSuccessful()){
                    Log.e(TAG, "onResponse: Respuesta al conectarse exitosa"+response.body());
                    LectorRespuesta responseRead=response.body();
                    ArrayList<Codigo> listBarcodeRead= responseRead.getResults();

                    for(int i=0; i< listBarcodeRead.size(); i++){
                        Codigo p=listBarcodeRead.get(i);
                        Log.e(TAG, "Id Hardware :"+p.getIdHardware());
                        Log.e(TAG, "RFID :"+p.getIdRFID());
                        Log.e(TAG, "Id Inventario :"+p.getInventoryId());
                        Log.e(TAG, "TID :"+p.getTId());
                    }
                }else{
                    Log.e(TAG, "onResponse: Respuesta no tan exitosa"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LectorRespuesta> call, Throwable t) {
                Log.e(TAG, "onFailure:"+t.getMessage());
            }
        });


    }
}
