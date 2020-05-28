package com.appc72_uhf.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
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
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DataAdapterInventories extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {

    Context mContext;
    ArrayList<DatamodelInventories> datalist;
    ProgressDialog mypDialog;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com/";
    String code_enterprise;
    private String android_id;



    public DataAdapterInventories(@NonNull Context context, ArrayList<DatamodelInventories> datalist ) {
        super(context, R.layout.simple_list_inventories_1, datalist);
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
            case R.id.item_info:
                Intent goToMain=new Intent(getContext(), Detail_product_activity.class);
                goToMain.putExtra("Id",  datamodelInventories.getId());
                goToMain.putExtra("Name",  datamodelInventories.getName());
                mContext.startActivity(goToMain);
                break;

            case R.id.item_sync:
                ConnectivityManager connMgr=(ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                if(isMobileConn || isWifiConn) {
                    try {
                        TagsRepository tagRepo = new TagsRepository(getContext());
                        ArrayList Tags = tagRepo.ViewAllTags(datamodelInventories.getId());
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        JSONObject jsonBody;
                        String URL = PROTOCOL_URLRFID + code_enterprise.toLowerCase()+ DOMAIN_URLRFID + "api/inventory/SaveTagReaded";
                        JSONArray data = new JSONArray();
                        mypDialog = new ProgressDialog((Activity) getContext());
                        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mypDialog.setMessage("Enviando codigos leidos del inventario '" + datamodelInventories.getName() + "'...");
                        mypDialog.setCanceledOnTouchOutside(false);
                        mypDialog.show();

                        if (Tags.size() > 0) {
                            for (int i = 0; i <= Tags.size() - 1; i++) {
                                jsonBody = new JSONObject();
                                String etags = String.valueOf(Tags.get(i));
                                String[] spliTags = etags.split("@");
                                String RFIDtagsString = spliTags[0];
                                String TIDtagsString = spliTags[1];

                                jsonBody.put("InventoryId", String.valueOf(datamodelInventories.getId()));
                                jsonBody.put("TId", TIDtagsString);
                                jsonBody.put("IdHardware", android_id);
                                jsonBody.put("RFID", RFIDtagsString);
                                data.put(jsonBody);
                            }
                            Log.e("JSONBODY", "" + data.toString());
                            Log.e("URL", "" + URL);

                            BooleanRequest booleanRequest = new BooleanRequest(1, URL, data, new Response.Listener<Boolean>() {
                                @Override
                                public void onResponse(Boolean response) {
                                    mypDialog.dismiss();
                                    UIHelper.ToastMessage(getContext(), "Envio de tags con exito!!", 3);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof NetworkError) {
                                    } else if (error instanceof ServerError) {
                                    } else if (error instanceof AuthFailureError) {
                                    } else if (error instanceof ParseError) {
                                    } else if (error instanceof NoConnectionError) {
                                    } else if (error instanceof TimeoutError) {
                                        mypDialog.dismiss();
                                        UIHelper.ToastMessage(getContext(), "Error con el servidor, intente mas tarde!!!", 3);
                                    }
                                    //UIHelper.ToastMessage(getContext(), "Error con el servidor, intente mas tarde!!!", 3);
                                }
                            });
                            int socketTimeout = 30000;//30 seconds - change to what you want
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            booleanRequest.setRetryPolicy(policy);
                            requestQueue.add(booleanRequest);
                        } else {
                            mypDialog.dismiss();
                            UIHelper.ToastMessage(getContext(), "No tiene codigos en este inventario!!!");
                        }
                    } catch (Exception ex) {
                        Log.e("Error Exception", "" + ex.getLocalizedMessage());
                    }
                }else{
                    UIHelper.ToastMessage(getContext(), "No esta conectado a una red en estos momentos!!!");
                }
                break;
            case R.id.item_void:
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
                            mypDialog = new ProgressDialog((Activity) getContext());
                            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mypDialog.setMessage("Vaciando codigos disponibles...");
                            mypDialog.setCanceledOnTouchOutside(false);
                            mypDialog.show();
                            try{
                                boolean res= tagsRepository.DeleteAllTags(datamodelInventories.getId());
                                if(res){
                                    mypDialog.dismiss();
                                    UIHelper.ToastMessage(getContext(), "Codigos elimandos exitosamente!!", 10);
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
            case R.id.item_take_inventory:
                Intent fragment=new Intent(getContext(), MainActivity.class);
                fragment.putExtra("inventoryBool", true);
                fragment.putExtra("inventoryID", datamodelInventories.getId());
                fragment.putExtra("inventoryName", datamodelInventories.getName());
                mContext.startActivity(fragment);
                break;
        }
    }
    private class ViewHolder{
        TextView title;
        ImageView info, item_sync, item_void, item_take_inventory;
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
            holder.info=(ImageView)convertView.findViewById(R.id.item_info);
            holder.item_sync=(ImageView)convertView.findViewById(R.id.item_sync);
            holder.item_void=(ImageView)convertView.findViewById(R.id.item_void);
            holder.item_take_inventory=(ImageView) convertView.findViewById(R.id.item_take_inventory);

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
        holder.item_take_inventory.setOnClickListener(this);
        holder.item_void.setOnClickListener(this);
        holder.item_sync.setOnClickListener(this);
        holder.info.setOnClickListener(this);
        holder.item_take_inventory.setTag(position);
        holder.item_void.setTag(position);
        holder.item_sync.setTag(position);
        holder.info.setTag(position);

        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
            holder.info.setVisibility(View.INVISIBLE);
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
