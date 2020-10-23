package com.appc72_uhf.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
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
import com.appc72_uhf.app.activities.BarcodeActivity;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.MakeLabelRepository;
import com.appc72_uhf.app.tools.UIHelper;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AdapterDocumentsListMakeLabel extends ArrayAdapter<DatamodelDocumentsMakeLabel> implements View.OnClickListener{
    Context mContext;
    ArrayList<DatamodelDocumentsMakeLabel> datadocuments;
    ProgressDialog mypDialog, myPdialogSingle;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com/";
    String code_enterprise;


    public AdapterDocumentsListMakeLabel(@NonNull Context mContext, ArrayList<DatamodelDocumentsMakeLabel> datadocuments) {
        super(mContext, R.layout.simple_list_documents, datadocuments);
        this.mContext = mContext;
        this.datadocuments = datadocuments;
    }

    private class ViewHolder{
        TextView tv_doument_name, tv_location_document, tv_document_id, tv_resumen_document;
        ImageView async_item_makeLabel, item_delete;
        ImageButton btn_make_label_global;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        final DatamodelDocumentsMakeLabel datamodelDocumentsMakeLabel=getItem(position);
        code_enterprise=getCompany();
        MakeLabelRepository makeLabelRepository=new MakeLabelRepository(getContext());

        switch (v.getId()){
            case R.id.btn_make_label_global:
                Intent goToMain=new Intent(getContext(), BarcodeActivity.class);
                goToMain.putExtra("DocumentId", datamodelDocumentsMakeLabel.getDocumentId());
                mContext.startActivity(goToMain);
                break;
            case R.id.async_item_makeLabel:
                UIHelper.ToastMessage(getContext(), "Sincronizando etiquetas", 5);
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
                    builder.setTitle(R.string.ap_dialog_sync_inventory);
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
                            final Handler handle = new Handler() {
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    mypDialog.incrementProgressBy(10); // Incremented By Value 2
                                }
                            };
                            ArrayList data = new ArrayList();
                            MakeLabelRepository makelabelRepositoryGet=new MakeLabelRepository(getContext());
                            ArrayList tagMakelabel=makelabelRepositoryGet.GetReadyMakelabel(datamodelDocumentsMakeLabel.getDocumentId());
                            //String inventory_include_tid=inventaryRespository.inventoryWithTID(datamodelInventories.getId());
                            if(tagMakelabel.size()>0){
                                try{
                                    //String IsTypeInventory=datamodelInventories.getId().substring(0, 1);
                                    JSONArray arregloCodigos = new JSONArray(tagMakelabel.toString());

                                    RequestQueue requestQueueMakelabel= Volley.newRequestQueue(getContext());
                                    String URL = PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID+"api/test/labelingHH";
                                    Log.e("URL", URL);
                                    if(tagMakelabel.size()>999){
                                        // UIHelper.ToastMessage(getContext(), "aqui entran los millares", 6);
                                        int millares=1000;
                                        mypDialog = new ProgressDialog(mContext);
                                        mypDialog.setMax(millares);
                                        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        mypDialog.setMessage("Enviando codigos empaquetados...");
                                        mypDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        mypDialog.setCanceledOnTouchOutside(false);
                                        mypDialog.setCancelable(false);
                                        mypDialog.show();
                                        JSONObject PrincipalObject=new JSONObject();
                                        for(int i=0; i< tagMakelabel.size();i++){
                                            JSONObject jsonBody=new JSONObject();
                                            String makeTagLabeled=String.valueOf(tagMakelabel.get(i));
                                            String[] spliTags=makeTagLabeled.split("@");
                                            String Id=spliTags[0];
                                            String EpcCreated=spliTags[1];
                                            String DocumentId=String.valueOf(datamodelDocumentsMakeLabel.getDocumentId());
                                            jsonBody.put("Id", Id);
                                            jsonBody.put("EpcAssociated", EpcCreated);
                                            //jsonBody.put("DocumentId", DocumentId);
                                            data.add(jsonBody);
                                        }

                                        List<List<String>> smallerLists= Lists.partition(data, 1000);
                                        Log.e("smallerLists", smallerLists.toString());
                                        int indexSmallArreglos=0;
                                        while(indexSmallArreglos<smallerLists.size()){
                                            PrincipalObject.put("DocumentId", String.valueOf(datamodelDocumentsMakeLabel.getDocumentId()));
                                            PrincipalObject.put("Products", smallerLists.get(indexSmallArreglos));

                                            BooleanRequest booleanRequest = new BooleanRequest(Request.Method.POST, URL, PrincipalObject, new Response.Listener<Boolean>() {
                                                @Override
                                                public void onResponse(Boolean response) {
                                                    //if(response){
                                                        Log.e("response", ""+response);
                                                        mypDialog.dismiss();
                                                        UIHelper.ToastMessage(getContext(), "Envio de codigos exitoso!!", 3);
                                                        handle.sendMessage(handle.obtainMessage());
                                                    //}
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("VolleyError", ""+error.getMessage());
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
                                            requestQueueMakelabel.add(booleanRequest);
                                            indexSmallArreglos++;
                                        }
                                    }else{
                                        mypDialog = new ProgressDialog(mContext);
                                        mypDialog.setMax(tagMakelabel.size());
                                        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        mypDialog.setMessage("Enviando codigos empaquetados...");
                                        mypDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        mypDialog.setCanceledOnTouchOutside(false);
                                        mypDialog.setCancelable(false);
                                        mypDialog.show();
                                        JSONObject PrincipalObject=new JSONObject();
                                        for(int i=0; i< tagMakelabel.size();i++){
                                            JSONObject jsonBody=new JSONObject();
                                            String makeTagLabeled=String.valueOf(tagMakelabel.get(i));
                                            String[] spliTags=makeTagLabeled.split("@");
                                            String Id=spliTags[0];
                                            String EpcCreated=spliTags[1];
                                            String DocumentId=String.valueOf(datamodelDocumentsMakeLabel.getDocumentId());
                                            jsonBody.put("Id", Id);
                                            jsonBody.put("EpcAssociated", EpcCreated);
                                            //jsonBody.put("DocumentId", DocumentId);
                                            data.add(jsonBody);
                                        }
                                        PrincipalObject.put("DocumentId", String.valueOf(datamodelDocumentsMakeLabel.getDocumentId()));
                                        PrincipalObject.put("Products", data);

                                        Log.e("JSONOBJECT", PrincipalObject.toString());
                                        BooleanRequest booleanRequest = new BooleanRequest(Request.Method.POST, URL, PrincipalObject, new Response.Listener<Boolean>() {
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
                                        requestQueueMakelabel.add(booleanRequest);
                                    }
                                }catch (JSONException ex){
                                    Log.e("JSONException", ""+ex.getLocalizedMessage());
                                    ex.printStackTrace();
                                }
                            }else{
                                UIHelper.ToastMessage(mContext, "No hay etiquetas para enviar!!", 2);
                           }
                        }
                    });

                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.item_delete:
                UIHelper.ToastMessage(getContext(), "Estoy en borrar", 6);
                UIHelper.ToastMessage(mContext, "Se elimina los documentos para makelabel "+datamodelDocumentsMakeLabel.getDocumentId(), 4);
                boolean deleteDocument=makeLabelRepository.deleteDocument(datamodelDocumentsMakeLabel.getDocumentId());
                if(deleteDocument){
                    UIHelper.ToastMessage(getContext(), "El documento '"+datamodelDocumentsMakeLabel.getDocumentName()+"' esta deshabilitado!!", 5);
                    Intent toMainAfterDelete=new Intent(getContext(), MainActivity.class);
                    toMainAfterDelete.putExtra("EntryType", "MakeLabel");
                    toMainAfterDelete.putExtra("makeLabelBool", false);
                    mContext.startActivity(toMainAfterDelete);
                    ((Activity) getContext()).onBackPressed();
                }
                break;
        }
    }

    private int lastPosition=-1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DatamodelDocumentsMakeLabel datamodelDocuments=getItem(position);
        ViewHolder holder;
        final View result;
        if (convertView == null) {
            holder=new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_documents, parent, false);
            holder.tv_doument_name=(TextView) convertView.findViewById(R.id.tv_doument_name);
            holder.tv_location_document=(TextView) convertView.findViewById(R.id.tv_location_document);
            holder.tv_document_id=(TextView) convertView.findViewById(R.id.tv_document_id);
            holder.tv_resumen_document=(TextView) convertView.findViewById(R.id.tv_resumen_document);
            holder.async_item_makeLabel=(ImageView) convertView.findViewById(R.id.async_item_makeLabel);
            holder.item_delete=(ImageView) convertView.findViewById(R.id.item_delete);
            holder.btn_make_label_global=(ImageButton) convertView.findViewById(R.id.btn_make_label_global);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition=position;
        holder.tv_doument_name.setText(datamodelDocuments.getDocumentName().toUpperCase());
        holder.tv_location_document.setText(" "+datamodelDocuments.getLocationOriginName());
        holder.tv_document_id.setText("N° Doc: "+datamodelDocuments.getDocumentId());
        holder.tv_resumen_document.setText(""+datamodelDocuments.getCounterEnabled());
        holder.async_item_makeLabel.setOnClickListener(this);
        holder.item_delete.setOnClickListener(this);
        holder.btn_make_label_global.setOnClickListener(this);
        //holder.tv_doument_name.setText("DOCUMENTO HH");
        holder.tv_doument_name.setTag(position);
        holder.tv_location_document.setTag(position);
        holder.tv_document_id.setTag(position);
        holder.tv_resumen_document.setTag(position);
        holder.async_item_makeLabel.setTag(position);
        holder.item_delete.setTag(position);
        holder.btn_make_label_global.setTag(position);
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
    private final JSONObject mRequestBody;

    private final String PROTOCOL_CHARSET = "utf-8";
    private final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    public BooleanRequest(int method, String url, JSONObject requestBody, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
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
