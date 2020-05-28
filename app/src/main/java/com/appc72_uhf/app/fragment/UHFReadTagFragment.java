package com.appc72_uhf.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.repositories.TagsRepository;
import com.appc72_uhf.app.tools.StringUtils;
import com.appc72_uhf.app.tools.UIHelper;
import com.rscja.deviceapi.RFIDWithUHF;

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
    Button BtClear;
    TextView tv_count;
    RadioGroup RgInventory;
    RadioButton RbInventorySingle;
    RadioButton RbInventoryLoop;
    Button BtSync;
    Button BtInventory;
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
    String code_enterprise;
    int inventoryID;
    int codeCompany;
    boolean detailFordevice;
    ProgressDialog mypDialog;



    public static final String PROTOCOL_URLRFID="https://";
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
        BtSync = (Button) getView().findViewById(R.id.BtSync);
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);
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
        BtSync.setOnClickListener(new BtSyncClickListener());
        inventoryFlag = 1;
        BtInventory.setOnClickListener(new BtInventoryClickListener());
        btnFilter = (Button) getView().findViewById(R.id.btnFilter);
        android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //GET INVENTORIES
        getCompany();
        code_enterprise=getCompany();

        btnFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popFilter == null) {
                    View viewPop = LayoutInflater.from(mContext).inflate(R.layout.popwindow_filter, null);

                    popFilter = new PopupWindow(viewPop, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

                    popFilter.setTouchable(true);
                    popFilter.setOutsideTouchable(true);
                    popFilter.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    popFilter.setBackgroundDrawable(new BitmapDrawable());

                    final EditText etLen = (EditText) viewPop.findViewById(R.id.etLen);
                    final EditText etPtr = (EditText) viewPop.findViewById(R.id.etPtr);
                    final EditText etData = (EditText) viewPop.findViewById(R.id.etData);
                    final RadioButton rbEPC = (RadioButton) viewPop.findViewById(R.id.rbEPC);
                    final RadioButton rbTID = (RadioButton) viewPop.findViewById(R.id.rbTID);
                    final RadioButton rbUser = (RadioButton) viewPop.findViewById(R.id.rbUser);
                    final Button btSet = (Button) viewPop.findViewById(R.id.btSet);


                    btSet.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String filterBank = "UII";
                            if (rbEPC.isChecked()) {
                                filterBank = "UII";
                            } else if (rbTID.isChecked()) {
                                filterBank = "TID";
                            } else if (rbUser.isChecked()) {
                                filterBank = "USER";
                            }
                            if (etLen.getText().toString() == null || etLen.getText().toString().isEmpty()) {
                                UIHelper.ToastMessage(mContext, "La longitud de los datos no puede estar vacía.");
                                return;
                            }
                            if (etPtr.getText().toString() == null || etPtr.getText().toString().isEmpty()) {
                                UIHelper.ToastMessage(mContext, "La dirección de inicio no puede estar vacía");
                                return;
                            }
                            int ptr = StringUtils.toInt(etPtr.getText().toString(), 0);
                            int len = StringUtils.toInt(etLen.getText().toString(), 0);
                            String data = etData.getText().toString().trim();
                            if (len > 0) {
                                String rex = "[\\da-fA-F]*"; //匹配正则表达式，数据为十六进制格式
                                if (data == null || data.isEmpty() || !data.matches(rex)) {
                                    UIHelper.ToastMessage(mContext, "Los datos filtrados deben ser datos hexadecimales.");
//									mContext.playSound(2);
                                    return;
                                }

                                if (mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf(filterBank), ptr, len, data, false)) {
                                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_succ);
                                } else {
                                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_fail);
//									mContext.playSound(2);
                                }
                            } else {
                                //禁用过滤
                                String dataStr = "";
                                if (mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("UII"), 0, 0, dataStr, false)
                                        && mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("TID"), 0, 0, dataStr, false)
                                        && mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("USER"), 0, 0, dataStr, false)) {
                                    UIHelper.ToastMessage(mContext, R.string.msg_disable_succ);
                                } else {
                                    UIHelper.ToastMessage(mContext, R.string.msg_disable_fail);
                                }
                            }


                        }
                    });
                    CheckBox cb_filter = (CheckBox) viewPop.findViewById(R.id.cb_filter);
                    rbEPC.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rbEPC.isChecked()) {
                                etPtr.setText("32");
                            }
                        }
                    });
                    rbTID.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rbTID.isChecked()) {
                                etPtr.setText("0");
                            }
                        }
                    });
                    rbUser.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rbUser.isChecked()) {
                                etPtr.setText("0");
                            }
                        }
                    });

                    cb_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) { //启用过滤

                            } else { //禁用过滤

                            }
                            popFilter.dismiss();
                        }
                    });
                }
                if (popFilter.isShowing()) {
                    popFilter.dismiss();
                    popFilter = null;
                } else {
                    popFilter.showAsDropDown(view);
                }
            }
        });

        LvTags.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("data Handle", ""+msg);
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0], strs[2]); //, strs[1]
                mContext.playSound(1);
            }
        };
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
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "Estado: onPause");

        // 停止识别
        //stopInventory();
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


    private void clearData() {
        TagsRepository tagsRepository = new TagsRepository(this.mContext);

        boolean cleartags=tagsRepository.ClearTags(inventoryID);
        if(cleartags){
            UIHelper.ToastMessage(this.mContext, "Se limpio correctamente", 4);
            tv_count.setText("0");
            tagList.clear();
            adapter.notifyDataSetChanged();
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
                                UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_open_fail);
        //					mContext.playSound(2);
                            }
                        break;
                        default:
                            break;
                }
        } else {
            if(detailFordevice){
                TagsRepository tagsRepository=new TagsRepository(mContext);
                if(tagList.size()>0){
                    try{
                        mypDialog = new ProgressDialog(mContext);
                        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mypDialog.setMessage("Procesando encontrados...");
                        mypDialog.setCanceledOnTouchOutside(false);
                        mypDialog.show();
                        UIHelper.ToastMessage(mContext, "ESTOY EN LA OPCION 1 DE TAGLIST > 0", 3);
                        for(int tagPosition=0; tagPosition<tagList.size(); tagPosition++){

                            JSONObject jsonObject= new JSONObject(tagList.get(tagPosition));
                            String strEPC=jsonObject.getString("tagUii");
                            boolean respFound=tagsRepository.UpdateTagsFound(strEPC, inventoryID);
                            if(respFound){
                                mypDialog.dismiss();
                                UIHelper.ToastMessage(mContext, "Encontrado en inventario", 12);
                                stopInventory();
                            }else{
                                mypDialog.dismiss();
                                UIHelper.ToastMessage(mContext, "No encontrado en inventario", 12);
                                stopInventory();
                            }
                        }

                    }catch (Exception e){
                        mypDialog.dismiss();
                        e.printStackTrace();
                    }
                }else{
                    UIHelper.ToastMessage(mContext, "ESTOY EN LA OPCION 2 DE TAGLIST == 0", 3);
                    mypDialog.dismiss();
                    stopInventory();
                }
            }else{
                UIHelper.ToastMessage(mContext, "OPCION 3 SIN DETALLES", 3);
                stopInventory();
            }
        }
    }

    private void setViewEnabled(boolean enabled) {
        btnFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        BtSync.setEnabled(enabled);
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
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_inventory_stop_fail);
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
            Context mContextReadTag = getActivity();
            final TagsRepository repositoryTag= new TagsRepository(mContextReadTag);
            //String test;
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
                    Log.e("TID", " "+strResult);
                    Log.e("repositoryTag.InsertTag",""+mContext.mReader.convertUiiToEPC(res[1])+" InventoryId:"+inventoryID+" ANDROIDID:"+android_id+" TID:"+strResult+" TAGSTATUS:"+0);

                    boolean saveRes=repositoryTag.InsertTag(mContext.mReader.convertUiiToEPC(res[1]),  inventoryID, android_id, strResult,0);
                    if(saveRes){
                        Message msg = handler.obtainMessage();
                        Log.e("EPC","EPC:"+ mContext.mReader.convertUiiToEPC(res[1])+"@"+res[2]+"@"+strResult);
                        msg.obj =mContext.mReader.convertUiiToEPC(res[1])+"@"+ res[2]+"@"+strResult; //+ "EPC:"
                        handler.sendMessage(msg);
                    }else{
                        Toast.makeText(mContext, "No se puede ingresar tag duplicado", Toast.LENGTH_SHORT).show();
                        Log.i("Duplicate error", "Duplicate tag");
                    }
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
                    Log.e("FOR DATA", "EPC: "+RFIDtagsString+"  TID: "+TIDtagsString);
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


    public class BtSyncClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            JSONArray data=new JSONArray();
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
                Log.e("TODO ELGLOSE", ""+data);

                BooleanRequest booleanRequest = new BooleanRequest(1, URL, data, new Response.Listener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        if(response){
                            mypDialog.dismiss();
                            UIHelper.ToastMessage(mcontext, "Envio de tags con exito!!"+response, 3);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mypDialog.dismiss();
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                        } else if (error instanceof AuthFailureError) {
                        } else if (error instanceof ParseError) {
                        } else if (error instanceof NoConnectionError) {
                        } else if (error instanceof TimeoutError) {
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
