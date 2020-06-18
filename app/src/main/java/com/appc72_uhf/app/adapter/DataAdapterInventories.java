package com.appc72_uhf.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.Detail_product_activity;
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.repositories.TagsRepository;
import com.appc72_uhf.app.tools.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DataAdapterInventories extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {

    Context mContext;
    ArrayList<DatamodelInventories> datalist;
    ProgressDialog mypDialog;
    String code_enterprise;
    private String android_id;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com/";

    public DataAdapterInventories(@NonNull Context context, int resource, ArrayList<DatamodelInventories> datalist ) {
        super(context,  resource, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DatamodelInventories datamodelInventories=(DatamodelInventories)object;
        InventaryRespository inventaryRespository=new InventaryRespository(getContext());
        code_enterprise=getCompany();
        android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        switch (v.getId())
        {
            case R.id.item_sync:

                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
                    builder.setTitle(R.string.ap_dialog_inventario_vaciar);
                    builder.setMessage("¿Desea enviar codigos a procesar?");
                    builder.setIcon(R.drawable.button_bg_up);
                    builder.setNegativeButton(R.string.ap_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton(R.string.ap_dialog_acept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            JSONArray data=new JSONArray();
                            TagsRepository tagRepo= new TagsRepository(getContext());
                            ArrayList Tags=tagRepo.ViewAllTags(datamodelInventories.getId());
                            if(Tags.size()>0){
                                try{
                                    RequestQueue requestQueue= Volley.newRequestQueue(getContext());
                                    String URL = PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID+"api/inventory/SaveTagReaded";
                                    JSONArray arregloCodigos = new JSONArray(Tags.toString());
                                    mypDialog = new ProgressDialog(mContext);
                                    mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    mypDialog.setMessage("Enviando codigos...");
                                    mypDialog.setCanceledOnTouchOutside(false);
                                    mypDialog.show();
                                    for(int i=0; i<arregloCodigos.length();i++){
                                        JSONObject jsonBody=new JSONObject();
                                        String etags=String.valueOf(Tags.get(i));
                                        String[] spliTags=etags.split("@");
                                        String RFIDtagsString=spliTags[0];
                                        String TIDtagsString=spliTags[1];
                                        jsonBody.put("InventoryId", String.valueOf(datamodelInventories.getId()));
                                        jsonBody.put("TId", TIDtagsString);
                                        jsonBody.put("IdHardware", android_id);
                                        jsonBody.put("RFID", RFIDtagsString);
                                        data.put(jsonBody);
                                    }
                                    Log.e("jsonBody", data.toString());

                                    BooleanRequest booleanRequest = new BooleanRequest(1, URL, data, new Response.Listener<Boolean>() {
                                        @Override
                                        public void onResponse(Boolean response) {
                                            if(response){
                                                mypDialog.dismiss();
                                                UIHelper.ToastMessage(getContext(), "Envio de codigos exitoso!!", 3);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            mypDialog.dismiss();
                                            if (error instanceof NetworkError) {
                                                UIHelper.ToastMessage(getContext(), "Error de conexion, no hay conexion a internet", 3);
                                            } else if (error instanceof ServerError) {
                                                UIHelper.ToastMessage(getContext(), "Error de conexion, credenciales invalidas", 3);
                                            } else if (error instanceof AuthFailureError) {
                                                UIHelper.ToastMessage(getContext(), "Error de conexion, intente mas tarde.", 3);
                                            } else if (error instanceof ParseError) {
                                                UIHelper.ToastMessage(getContext(), "Error desconocido, intente mas tarde", 3);
                                            } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                UIHelper.ToastMessage(getContext(), "Error con el servidor, intente mas tarde!!!", 3);
                                            }
                                        }
                                    });
                                    int socketTimeout = 30000;//30 seconds - change to what you want
                                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                    booleanRequest.setRetryPolicy(policy);
                                    requestQueue.add(booleanRequest);
                                }catch (JSONException ex){
                                    ex.printStackTrace();
                                }
                            }else{
                                UIHelper.ToastMessage(mContext, "No hay codigos leidos para enviar!!", 2);
                            }
                        }
                    });

                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.item_delete:
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
                    builder.setTitle(R.string.ap_dialog_inventario_vaciar);
                    builder.setMessage("Vaciar codigos para este inventario?");
                    builder.setIcon(R.drawable.button_bg_up);
                    builder.setNegativeButton(R.string.ap_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton(R.string.ap_dialog_acept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            TagsRepository tagsRepository=new TagsRepository(getContext());
                            InventaryRespository inventaryRespository=new InventaryRespository(getContext());
                            mypDialog = new ProgressDialog((Activity) getContext());
                            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mypDialog.setMessage("Vaciando codigos disponibles...");
                            mypDialog.setCanceledOnTouchOutside(false);
                            mypDialog.show();
                            try{
                                boolean res= tagsRepository.DeleteAllTags(datamodelInventories.getId());
                                boolean resultUpdateFalse=inventaryRespository.DeleteInventory(datamodelInventories.getId());
                                if(resultUpdateFalse && res){
                                    mypDialog.dismiss();
                                    UIHelper.ToastMessage(getContext(), "El inventario '"+datamodelInventories.getName()+"' eliminado!!", 5);
                                    //notifyDataSetChanged();
                                    Intent goToMain=new Intent(getContext(), MainActivity.class);
                                    mContext.startActivity(goToMain);

                                    //UIHelper.ToastMessage(getContext(), "Codigos elimandos exitosamente!!", 10);
                                }
                            }catch (Exception e){
                                mypDialog.dismiss();
                                UIHelper.ToastMessage(getContext(), "Error al eliminar codigos!!", 10);
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_global_detail:
                if(datamodelInventories.getDetailForDevice()){
                    Intent goToMain=new Intent(getContext(), Detail_product_activity.class);
                    goToMain.putExtra("Id",  datamodelInventories.getId());
                    goToMain.putExtra("Name",  datamodelInventories.getName());
                    goToMain.putExtra("inventoryType", datamodelInventories.getDetailForDevice());
                    mContext.startActivity(goToMain);

                }else{
                    Intent fragment=new Intent(getContext(), MainActivity.class);
                    fragment.putExtra("inventoryBool", true);
                    fragment.putExtra("inventoryID", datamodelInventories.getId());
                    fragment.putExtra("inventoryName", datamodelInventories.getName());
                    fragment.putExtra("inventoryType", datamodelInventories.getDetailForDevice());
                    mContext.startActivity(fragment);

                }
                break;
        }
    }
    private class ViewHolder{
        TextView title;
        ImageView item_delete, item_sync;
        ImageButton btn_global_detail;
    }
    private int lastPosition = -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DatamodelInventories datamodelInventories=getItem(position);
        ViewHolder holder;
        final View result;

        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_inventories_1, parent, false);


            holder.title=(TextView) convertView.findViewById(R.id.tv_inventory);
            holder.item_delete=(ImageView)convertView.findViewById(R.id.item_delete);
            holder.item_sync=(ImageView) convertView.findViewById(R.id.item_sync);
            holder.btn_global_detail=(ImageButton)convertView.findViewById(R.id.btn_global_detail);

            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition = position;
        holder.title.setText(datamodelInventories.getName());
        holder.btn_global_detail.setOnClickListener(this);
        holder.item_delete.setOnClickListener(this);
        holder.item_sync.setOnClickListener(this);
        holder.btn_global_detail.setTag(position);
        holder.item_delete.setTag(position);
        holder.item_sync.setTag(position);

        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
        }


        return convertView;
    }
    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(getContext());
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;
        }
        return code_result;
    }

    class BooleanRequest extends Request<Boolean> {
        private final Response.Listener<Boolean> mListener;
        private final Response.ErrorListener mErrorListener;
        private final JSONArray mRequestBody;

        private final String PROTOCOL_CHARSET = "utf-8";
        private final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

        public BooleanRequest(int method, String url, JSONArray requestBody, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
            this.mRequestBody = requestBody;
        }

        @Override
        protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {
            Boolean parsed;
            try {
                parsed = Boolean.valueOf(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
            } catch (UnsupportedEncodingException e) {
                parsed = Boolean.valueOf(new String(response.data));
            }
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected VolleyError parseNetworkError(VolleyError volleyError) {
            return super.parseNetworkError(volleyError);
        }

        @Override
        protected void deliverResponse(Boolean response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        @Override
        public String getBodyContentType() {
            return PROTOCOL_CONTENT_TYPE;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            try {
                return mRequestBody == null ? null : mRequestBody.toString().getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        mRequestBody, PROTOCOL_CHARSET);
                return null;
            }
        }
    }

}
