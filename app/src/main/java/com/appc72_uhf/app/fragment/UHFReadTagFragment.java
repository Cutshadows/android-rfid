package com.appc72_uhf.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.repositories.TagsRepository;
import com.appc72_uhf.app.tools.StringUtils;
import com.appc72_uhf.app.tools.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class UHFReadTagFragment extends KeyDwonFragment {

    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    Handler handler;
    private ArrayList<HashMap<String, String>> tagList;
    SimpleAdapter adapter;
    TextView tv_count;
    RadioGroup RgInventory;
    RadioButton RbInventorySingle;
    RadioButton RbInventoryLoop;
    Button BtSync, btn_back_product_list, BtInventory, BtClear;
    ListView LvTags;
    SimpleCursorAdapter adapter2;
    private String android_id;
    private Button btnFilter;//过滤
    private LinearLayout llContinuous;
    private MainActivity mContext;
    private HashMap<String, String> map;
    PopupWindow popFilter;
    ArrayList<String> result;
    private boolean shouldRefreshOnResume =false;
    String code_enterprise, name_inventory_pass;
    int inventoryID;
    int codeCompany;
    boolean detailFordevice, detailForDevice, inventory_type;
    ProgressDialog mypDialog;
    RelativeLayout relative_layout_filter, relative_layout_backButton;



    public static final String PROTOCOL_URLRFID="http://";
    public static final String DOMAIN_URLRFID=".izyrfid.com/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onCreateView ");
        return inflater
                .inflate(R.layout.uhf_readtag_fragment, container, false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("onDestroyView", "onDestroyView ");
        shouldRefreshOnResume = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("MY", "UHFReadTagFragment.onActivityCreated");
        initComponent();
    }


    //INICIALIZACION DE COMPONENTES
    private void initComponent(){
        mContext = (MainActivity) getActivity();
        tagList = new ArrayList<HashMap<String, String>>();
        BtClear = (Button) getView().findViewById(R.id.BtClear);
        //BtSync = (Button) getView().findViewById(R.id.BtSync);
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);
        btn_back_product_list=(Button) getView().findViewById(R.id.btn_back_product_list);
        //relative_layout_filter=(RelativeLayout) getView().findViewById(R.id.relative_layout_filter);
        relative_layout_backButton=(RelativeLayout) getView().findViewById(R.id.relative_layout_backButton);
        String tr = "";
        result=new ArrayList<>();
        inventoryID=mContext.getIntent().getIntExtra("inventoryID", 0);
        BtInventory = (Button) getView().findViewById(R.id.BtInventory);
        LvTags = (ListView) getView().findViewById(R.id.LvTags);
        llContinuous = (LinearLayout) getView().findViewById(R.id.llContinuous);
        adapter = new SimpleAdapter(mContext, tagList, R.layout.listtag_items,
                new String[]{"tagUii", "tagRssi"}, //"tagLen", "tagCount",
                new int[]{R.id.TvTagUii,  R.id.TvTagRssi}); // R.id.TvTagLen, R.id.TvTagCount,
        BtClear.setOnClickListener(new BtClearClickListener());
        //BtSync.setOnClickListener(new BtSyncClickListener());
        inventoryFlag = 1;
        BtInventory.setOnClickListener(new BtInventoryClickListener());
        //btnFilter = (Button) getView().findViewById(R.id.btnFilter);
        android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //GET INVENTORIES
        getCompany();
        code_enterprise=getCompany();
        btn_back_product_list.setOnClickListener(new BtnBackClickListener());


        LvTags.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        detailForDevice=mContext.getIntent().getBooleanExtra("inventoryType", false);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0], strs[2]); //, strs[1]
                mContext.playSound(1);
            }
        };
        name_inventory_pass=mContext.getIntent().getStringExtra("Name");
       // inventory_type=mContext.getIntent().getBooleanExtra("inventoryType", false);
       /* if(detailForDevice){
            relative_layout_filter.removeAllViews();
        }else{
            relative_layout_backButton.removeAllViews();
        }*/

    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("onResume", "Estado: onResume");
        loadData();
        adapter.notifyDataSetChanged();

    }
    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop", "Estado: onStop");
        stopInventory();
        shouldRefreshOnResume = true;
        insertTags();


    }
    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "Estado: onPause");
        // 停止识别
        stopInventory();
        shouldRefreshOnResume = true;
    }


    /**
     * @param epc
     * Agregar tag a la lista taglist
     */
    private void addEPCToList(String epc, String tid) {
        try {
            if (!TextUtils.isEmpty(epc)) {
                int index = checkIsExist(epc);

                map = new HashMap<String, String>();
                map.put("tagUii", epc);
                map.put("tagRssi", tid);

                //aca se divide
                //map.put("tagCount", String.valueOf(1));
                //map.put("tagRssi", rssi);
                //mContext.getAppContext().uhfQueue.offer(epc + "\t 1");

                if (index == -1) {

                    tagList.add(map);
                    LvTags.setAdapter(adapter);
                    tv_count.setText("" + adapter.getCount());
                } else {
                    int tagcount = Integer.parseInt(tagList.get(index).get("tagCount"), 10) + 1;
                    map.put("tagCount", String.valueOf(tagcount));
                    tagList.set(index, map);
                }
                adapter.notifyDataSetChanged();
            }
        }catch (Exception ex){
            Log.i("Error Exception List", ex.getLocalizedMessage());
        }
    }

    public class BtClearClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            clearData();
        }
    }

    public class BtnBackClickListener implements OnClickListener {
        @Override
        public void onClick(View vie){
            verifyDataAfterEnd();
        }
    }
    private void verifyDataAfterEnd(){
        //TagsRepository tagsRepository=new TagsRepository(mContext);
        //TagList Vacio o no
        if(tagList.size()>0){
            //Si viene inventario con detalle o sin detalle
            if(detailFordevice){
                insertTags();
                    Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                    goToDetailProduct.putExtra("Id",  inventoryID);
                    goToDetailProduct.putExtra("Name",  name_inventory_pass);
                    goToDetailProduct.putExtra("Name",  name_inventory_pass);
                    goToDetailProduct.putExtra("inventoryType", detailForDevice);
                    mContext.startActivity(goToDetailProduct);
                    mContext.onBackPressed();

            }else{
                insertTags();
                Intent goToMain=new Intent(mContext, MainActivity.class);
                mContext.startActivity(goToMain);
                mContext.onBackPressed();
            }
        }else{
            UIHelper.ToastMessage(mContext, "No hay codigos en Lectura", 3);
            if(detailFordevice){
                Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                goToDetailProduct.putExtra("Id",  inventoryID);
                goToDetailProduct.putExtra("Name",  name_inventory_pass);
                goToDetailProduct.putExtra("Name",  name_inventory_pass);
                goToDetailProduct.putExtra("inventoryType", detailForDevice);
                mContext.startActivity(goToDetailProduct);
                mContext.onBackPressed();
            }else{
                Intent goToMain=new Intent(mContext, MainActivity.class);
                mContext.startActivity(goToMain);
                mContext.onBackPressed();
            }
        }
    }
    private void insertTags(){
        final TagsRepository repositoryTag= new TagsRepository(mContext);
        mypDialog = new ProgressDialog(mContext);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Guardando codigos...");
        mypDialog.setCanceledOnTouchOutside(false);
        mypDialog.show();
        try{
            for(int index=0; index < tagList.size();index++){
                String strEPC=tagList.get(index).get("tagUii");
                String strTID=tagList.get(index).get("tagRssi");
                boolean saveRes=repositoryTag.InsertTag(strEPC,  inventoryID, android_id, strTID,0);
            }
            /*JSONArray codeArrays = new JSONArray(tagList.toString());
            for(int codId=0; codId<codeArrays.length();codId++){
                JSONObject jsonObject= codeArrays.getJSONObject(codId);
                String strEPC=jsonObject.getString("tagUii");
                String strTID=jsonObject.getString("tagRssi");
                Log.e("AFTER INSERT", "EPC: "+strEPC+"  TID:"+strTID);
                Log.e("TAGLIST", "TAGLIST: "+tagList.toString());
                boolean saveRes=repositoryTag.InsertTag(strEPC,  inventoryID, android_id, strTID,0);

            }*/
            mypDialog.dismiss();
        }catch (Exception ex){
            ex.printStackTrace();
            mypDialog.dismiss();
        }
    }

    private void clearData() {


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
                    mypDialog = new ProgressDialog((Activity) getContext());
                    mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mypDialog.setMessage("limpiando codigos en lectura...");
                    mypDialog.setCanceledOnTouchOutside(false);
                    mypDialog.show();
                    TagsRepository tagsRepository = new TagsRepository(getContext());
                    boolean cleartags=tagsRepository.ClearTags(inventoryID);
                    if(cleartags){
                        mypDialog.dismiss();
                        UIHelper.ToastMessage(getContext(), "Se limpio correctamente", 4);
                        tv_count.setText("0");
                        tagList.clear();
                        adapter.notifyDataSetChanged();
                    }

                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class BtInventoryClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readTag();
        }
    }

    private void readTag() {
        if (BtInventory.getText().equals(mContext.getString(R.string.btInventory))){
                    switch (inventoryFlag) {
                        case 1:// 单标签循环  .startInventoryTag((byte) 0, (byte) 0))
                            mContext.mReader.setEPCTIDMode(true);
                            if (mContext.mReader.startInventoryTag(0,0)) {
                                BtInventory.setText(mContext
                                        .getString(R.string.title_stop_Inventory));
                                loopFlag = true;
                                setViewEnabled(false);
                                new TagThread().start();
                            } else {
                                mContext.mReader.stopInventory();
                                Log.e("UHFReadTagFragment", "Open Failure");
                                //UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_open_fail);
        //					mContext.playSound(2);
                            }
                        break;
                        default:
                            break;
                }
        } else {
            TagsRepository tagsRepository=new TagsRepository(mContext);
            if(tagList.size()>0){
                if(detailFordevice){
                    try{
                        mypDialog = new ProgressDialog(mContext);
                        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mypDialog.setMessage("Procesando codigos...");
                        mypDialog.setCanceledOnTouchOutside(false);
                        mypDialog.show();
                        int contadorFound=0;
                        for(int tagPosition=0; tagPosition<tagList.size(); tagPosition++){
                            JSONObject jsonObject= new JSONObject(tagList.get(tagPosition));
                            String strEPC=jsonObject.getString("tagUii");
                            boolean respFound=tagsRepository.UpdateTagsFound(strEPC, inventoryID);
                            if(respFound){
                                contadorFound++;
                            }
                        }
                        stopInventory();
                        UIHelper.ToastMessage(mContext, "Lectura finalizado con exito, se encontraron "+contadorFound+" codigos", 5);
                        mypDialog.dismiss();
                    }catch (Exception e){
                        mypDialog.dismiss();
                        e.printStackTrace();
                    }
                }else{
                    stopInventory();
                    UIHelper.ToastMessage(mContext, "Lectura finalizado con exito", 5);
                }
            }else{
                stopInventory();
                UIHelper.ToastMessage(mContext, "Lectura finalizado", 3);
            }
        }
    }

    private void setViewEnabled(boolean enabled) {
        //btnFilter.setEnabled(enabled);
        btn_back_product_list.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        //BtSync.setEnabled(enabled);
    }
    private void btnSyncEnabled(boolean enabled){
        BtSync.setEnabled(enabled);
    }

    private void stopInventory() {
            if (loopFlag) {
                loopFlag = false;
                setViewEnabled(true);
                if (mContext.mReader.stopInventory()) {
                    BtInventory.setText(mContext.getString(R.string.btInventory));
                } else {
                    //UIHelper.ToastMessage(mContext,
                      //      R.string.uhf_msg_inventory_stop_fail);
                    Log.e("UHFReadTagFragment", "Stop fail");
                }
            }
    }
    /**
     * @param strEPC 索引
     * @return
     */
    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        if (StringUtils.isEmpty(strEPC)) {
            return existFlag;
        }
        String tempStr = "";
        for (int i = 0; i < tagList.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = tagList.get(i);
            tempStr = temp.get("tagUii");
            if (strEPC.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
    }

    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            String[] res = null;
            while (loopFlag) {
                res = mContext.mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    if (strTid.length() != 0 && !strTid.equals("0000000" + "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult =strTid;
                    } else {
                        strResult = "";
                    }
                        Message msg = handler.obtainMessage();
                        Log.e("EPC","EPC:"+ mContext.mReader.convertUiiToEPC(res[1])+"@"+res[2]+"@"+strResult);
                        msg.obj =mContext.mReader.convertUiiToEPC(res[1])+"@"+ res[2]+"@"+strResult; //+ "EPC:"
                        handler.sendMessage(msg);
                }
            }
        }
    }

    public void loadData(){
        TagsRepository tagRepo= new TagsRepository(this.mContext);
        ArrayList Tags=tagRepo.ViewAllTags(inventoryID);
        InventaryRespository inventaryRespository=new InventaryRespository(this.mContext);
        detailFordevice=inventaryRespository.inventoryDetailForDevice(inventoryID);
        try{
            if(Tags.size()!=0){
                for(int i=0; i<=Tags.size();i++){
                    String etags=String.valueOf(Tags.get(i));
                    String[] spliTags=etags.split("@");
                    String RFIDtagsString=spliTags[0];
                    String TIDtagsString=spliTags[1];
                    int index = checkIsExist(RFIDtagsString);
                     if(index == -1 ){
                        addEPCToList(RFIDtagsString, TIDtagsString);
                    }
                }

            }
            adapter.notifyDataSetChanged();
        }catch (Exception ex){
            Log.i("Error Exception", ex.getLocalizedMessage());
        }
    }

//ENVIO de tags
    public class BtSyncClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            JSONArray data=new JSONArray();
            if(tagList.size()>0){
                try {
                    final Context mcontext = getActivity();
                    RequestQueue requestQueue= Volley.newRequestQueue(mcontext);
                    String URL = PROTOCOL_URLRFID+code_enterprise.toLowerCase()+DOMAIN_URLRFID+"api/inventory/SaveTagReaded";

                    JSONArray arregloCodigos = new JSONArray(tagList.toString());
                    mypDialog = new ProgressDialog(mContext);
                    mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mypDialog.setMessage("Enviando codigos...");
                    mypDialog.setCanceledOnTouchOutside(false);
                    mypDialog.show();
                    for(int index=0;index < arregloCodigos.length(); index++){
                            JSONObject jsonBody=new JSONObject();
                            JSONObject jsonObject= arregloCodigos.getJSONObject(index);
                            String strEPC=jsonObject.getString("tagUii");
                            String strTID=jsonObject.getString("tagRssi");

                            jsonBody.put("InventoryId", String.valueOf(inventoryID));
                            jsonBody.put("TId", strTID);
                            jsonBody.put("IdHardware", android_id);
                            jsonBody.put("RFID", strEPC);

                            data.put(jsonBody);
                    }
                    Log.e("jsonBody", data.toString());
                    BooleanRequest booleanRequest = new BooleanRequest(1, URL, data, new Response.Listener<Boolean>() {
                        @Override
                        public void onResponse(Boolean response) {
                            if(response){
                                mypDialog.dismiss();
                                UIHelper.ToastMessage(mcontext, "Envio de codigos exitoso!!", 3);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mypDialog.dismiss();
                            if (error instanceof NetworkError) {
                                UIHelper.ToastMessage(mcontext, "Error de conexion, no hay conexion a internet", 3);
                            } else if (error instanceof ServerError) {
                                UIHelper.ToastMessage(mcontext, "Error de conexion, credenciales invalidas", 3);
                            } else if (error instanceof AuthFailureError) {
                                UIHelper.ToastMessage(mcontext, "Error de conexion, intente mas tarde.", 3);
                            } else if (error instanceof ParseError) {
                                UIHelper.ToastMessage(mcontext, "Error desconocido, intente mas tarde", 3);
                            } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                UIHelper.ToastMessage(mcontext, "Error con el servidor, intente mas tarde!!!", 3);
                            }
                        }
                    });
                    int socketTimeout = 30000;//30 seconds - change to what you want
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    booleanRequest.setRetryPolicy(policy);
                    requestQueue.add(booleanRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                UIHelper.ToastMessage(mContext, "Error no tiene codigos para enviar!!!", 3);
            }

        }
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

    @Override
    public void myOnKeyDwon() {
        readTag();
        }
    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(mContext);
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
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
