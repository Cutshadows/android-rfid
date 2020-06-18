package com.appc72_uhf.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.helpers.HttpHelpers;
import com.appc72_uhf.app.repositories.DetailProductRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.tools.UIHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DataAdapterInventoryServer extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {
    Context mContext;
    ArrayList<DatamodelInventories> datalist;
    ProgressDialog mypDialog;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com";
    String token_access;
    String code_enterprise;

    public DataAdapterInventoryServer(@NonNull Context context, ArrayList<DatamodelInventories> datalist) {
        super(context, R.layout.smple_list_inventory_server, datalist);
        this.datalist = datalist;
        this.mContext = context;
    }

    @Override
    public void onClick(View v){
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DatamodelInventories datamodelInventories=(DatamodelInventories)object;
        CheckBox chb_takeInventory= (CheckBox) v.findViewById(R.id.chbx_takeInventory);
        //ImageView item_delete=(ImageView) v.findViewById(R.id.item_delete);
        final InventaryRespository inventaryRespository=new InventaryRespository(getContext());
        switch (v.getId()){
            case R.id.chbx_takeInventory:
                if(chb_takeInventory.isChecked()){
                    boolean  resultInventoryInsert=inventaryRespository.InventoryInsert(datamodelInventories.getId(), datamodelInventories.getName(), String.valueOf(datamodelInventories.getDetailForDevice()), datamodelInventories.getInventoryStatus(), datamodelInventories.getCodeCompany(), 1);
                    if(datamodelInventories.getDetailForDevice()){
                        if(resultInventoryInsert){
                                mypDialog = new ProgressDialog(getContext());
                                mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                mypDialog.setMessage("Habilitando inventario '"+datamodelInventories.getName()+"'...");
                                mypDialog.setCanceledOnTouchOutside(false);
                                mypDialog.show();
                                //UIHelper.ToastMessage(getContext(), "Obteniendo inventario '"+datamodelInventories.getName()+"' y los detalles para habilitar!!", 5);
                                String URL_COMPLETE=PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID;
                                final DetailProductRepository detailProductRepository=new DetailProductRepository(getContext());
                                HttpHelpers http = new HttpHelpers(getContext(), URL_COMPLETE, "");
                                http.addHeader("Authorization", "Bearer "+token_access);
                                Log.e("INVENTARIO INT", URL_COMPLETE+"/api/inventory/GetDetailForDevice?InventoryId="+datamodelInventories.getId());
                                http.clientProductDetail(Request.Method.GET, "/api/inventory/GetDetailForDevice?InventoryId="+datamodelInventories.getId(), null,  new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("onRESPONSE PRODUCT", response);
                                        try{
                                            Gson gson=new Gson();
                                            DataModelProductDetails[] products=gson.fromJson(response, DataModelProductDetails[].class);
                                            for(int index=0; index<=products.length-1; index++){
                                                Log.e("DATA FOR", "ID: "+products[index].getId()+" NAME:"+products[index].getName()+" ProductMaster:"+products[index].getProductMasterId()+" FOUNG:"+Boolean.valueOf(products[index].getFound())+ "INTEGER INVENTARIO: "+datamodelInventories.getId());
                                                boolean resultInsertProduct=detailProductRepository.DetailProductInsert(products[index].getId(), products[index].getEPC(), products[index].getCode(), products[index].getName(), products[index].getFound(), products[index].getProductMasterId(), datamodelInventories.getId());
                                                if(resultInsertProduct){

                                                }
                                            }
                                            mypDialog.dismiss();
                                            UIHelper.ToastMessage(getContext(), "Inventario habilitado exitosamente", 5);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (error instanceof NetworkError) {
                                            mypDialog.dismiss();
                                            UIHelper.ToastMessage(mContext, "Error de conexion, no hay conexion a internet", 3);
                                        } else if (error instanceof ServerError) {
                                            mypDialog.dismiss();
                                            UIHelper.ToastMessage(mContext, "Error de conexion, credenciales invalidas", 3);
                                        } else if (error instanceof AuthFailureError) {
                                            mypDialog.dismiss();
                                            UIHelper.ToastMessage(mContext, "Error de conexion, intente mas tarde.", 3);
                                        } else if (error instanceof ParseError) {
                                            mypDialog.dismiss();
                                            UIHelper.ToastMessage(mContext, "Error desconocido, intente mas tarde", 3);
                                        } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                            mypDialog.dismiss();
                                            UIHelper.ToastMessage(mContext, "Tiempo agotado, intente mas tarde!!!", 3);
                                        }
                                    }
                                });
                        }
                    }else {
                        if(resultInventoryInsert){
                            UIHelper.ToastMessage(getContext(), "Obteniendo inventario '"+datamodelInventories.getName()+"' para habilitar!!", 5);
                        }
                    }

                }else {
                   boolean resultUpdateFalse=inventaryRespository.DeleteInventory(datamodelInventories.getId());
                    if(resultUpdateFalse){
                        UIHelper.ToastMessage(getContext(), "El inventario '"+datamodelInventories.getName()+"' esta deshabilitado!!", 5);
                    }
                }
                break;
        }
    }

    private class ViewHolder{
        CheckBox chbx_takeInventory;
        TextView tv_inventory;
        //ImageView item_delete;

    }
    private int lastPosition= -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        //Get the data item for this position
        DatamodelInventories datamodelInventories=getItem(position);
        code_enterprise=getCompany();
        SharedPreferences preferencesAccess_token=getContext().getSharedPreferences("access_token", Context.MODE_PRIVATE);
        String access_token=preferencesAccess_token.getString("access_token", "");
        if(access_token.length()==0){
            Log.e("No data preferences", " Error data no empty "+access_token);
        }else{
            token_access=access_token;
        }

        ViewHolder holder;

        final View result;

        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.smple_list_inventory_server, parent, false);

            holder.tv_inventory=(TextView) convertView.findViewById(R.id.tv_inventory);
            holder.chbx_takeInventory=(CheckBox) convertView.findViewById(R.id.chbx_takeInventory);
            //holder.item_delete=(ImageView) convertView.findViewById(R.id.item_delete);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        // final DatamodelInventories datamodelInventories=datalist.get(position);

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition = position;
        holder.tv_inventory.setText(datamodelInventories.getName());
        holder.chbx_takeInventory.setOnClickListener(this);
        //holder.item_delete.setOnClickListener(this);

        if(datamodelInventories.getisSelect()==1){
            holder.chbx_takeInventory.setChecked(true);
        }else{
            holder.chbx_takeInventory.setChecked(false);
        }
        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
        }
        holder.chbx_takeInventory.setTag(position);
        //holder.item_delete.setTag(position);

        return convertView;
    }
    private String getCompany(){
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        if(enterprises_code.isEmpty()){
            Log.i("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;
        }
        return code_result;
    }

}
