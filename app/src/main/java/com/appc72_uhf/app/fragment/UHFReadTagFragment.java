package com.appc72_uhf.app.fragment;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
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
    private Spinner spInventories;
    ArrayList<String> result = new ArrayList<>();
    ArrayAdapter<String> adapterSelect;

    private boolean shouldRefreshOnResume =false;



    public static final String URLRFID="http://demo.izyrfid.com/";


    protected Context context;
    //private boolean valLoadData=false;

    /*public static UHFReadTagFragment newInstance() {
        UHFReadTagFragment fragment = new UHFReadTagFragment();
        return fragment;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapterSelect.clear();
        getInventories();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MY", "UHFReadTagFragment.onCreateView ");

        return inflater
                .inflate(R.layout.uhf_readtag_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shouldRefreshOnResume = true;
        Log.e("onDestroyView", "Estado: onDestroyView");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.e("onViewStateRestored", "Estado: onViewStateRestored");
        /*spInventories=(Spinner) getView().findViewById(R.id.spInventories);
        //GET INVENTORIES
        adapterSelect=new ArrayAdapter<String>(this.mContext, R.layout.spinner_item_inventories, result);
        spInventories.setAdapter(adapterSelect);
        adapterSelect.notifyDataSetChanged();
        getInventories();*/


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("MY", "UHFReadTagFragment.onActivityCreated");
        mContext = (MainActivity) getActivity();

        tagList = new ArrayList<HashMap<String, String>>();
        spInventories=(Spinner) getView().findViewById(R.id.spInventories);



        BtClear = (Button) getView().findViewById(R.id.BtClear);
        BtSync = (Button) getView().findViewById(R.id.BtSync);
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);

        String tr = "";
        result=new ArrayList<>();

        //RbInventorySingle = (RadioButton) getView().findViewById(R.id.RbInventorySingle);
        RbInventoryLoop = (RadioButton) getView()
                .findViewById(R.id.RbInventoryLoop);

        BtInventory = (Button) getView().findViewById(R.id.BtInventory);
        LvTags = (ListView) getView().findViewById(R.id.LvTags);


        llContinuous = (LinearLayout) getView().findViewById(R.id.llContinuous);

        adapter = new SimpleAdapter(mContext, tagList, R.layout.listtag_items,
                new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                new int[]{R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
                        R.id.TvTagRssi});


        BtClear.setOnClickListener(new BtClearClickListener());
        //BtImport.setOnClickListener(new BtImportcClickListener());
        BtSync.setOnClickListener(new BtSyncClickListener());
        RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
        BtInventory.setOnClickListener(new BtInventoryClickListener());
        btnFilter = (Button) getView().findViewById(R.id.btnFilter);

        android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);




        //GET INVENTORIES
        adapterSelect=new ArrayAdapter<String>(this.mContext, R.layout.spinner_item_inventories, result);
        spInventories.setAdapter(adapterSelect);
        adapterSelect.notifyDataSetChanged();
        spInventories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter (mContext, R.layout.spinner_item_inventories, null, from, to, 0);
        cursorAdapter.setDropDownViewResource (R.layout.spinner_item_inventories);
        spInventories.setAdapter (cursorAdapter);
        spInventories.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {
                if (i != -1) {
                    Cursor c = (Cursor) adapterView.getItemAtPosition (i);

                }
            }

            @Override
            public void onNothingSelected (AdapterView<?> adapterView) {

            }
        });*/




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
        //clearData();
        Log.i("MY", "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());




        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("data Handle", ""+msg);
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0]); //, strs[1]
                mContext.playSound(1);
            }
        };
        //CARGAR LISTA
        loadData();
        getInventories();



    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("onResume", "Estado: onResume");
        if(shouldRefreshOnResume){
            // refresh fragment
            adapterSelect.clear();
            getInventories();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }
    @Override
    public void onPause() {
        Log.i("MY", "UHFReadTagFragment.onPause");
        super.onPause();
        // 停止识别
        stopInventory();
        if(shouldRefreshOnResume){
            // refresh fragment
            adapterSelect.clear();
        }
    }


    /**
     * @param epc
     * Agregar tag a la lista taglist
     */
    private void addEPCToList(String epc) {
        try {
            if (!TextUtils.isEmpty(epc)) {
                int index = checkIsExist(epc);

                map = new HashMap<String, String>();
                map.put("tagUii", epc);

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


    /*** El import lo desactive por que no esta funcionando
     *
    public class BtImportClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (BtInventory.getText().equals(
                    mContext.getString(R.string.btInventory))) {
                if(tagList.size()==0) {
                    UIHelper.ToastMessage(mContext, "Sin exportación de datos");
                    return;
                }
                boolean re = FileImport.daochu("", tagList);
                if (re) {
                    UIHelper.ToastMessage(mContext, "Exportación exitosa");
                    tv_count.setText("0");
                    tagList.clear();
                    Log.i("MY", "tagList.size " + tagList.size());
                    adapter.notifyDataSetChanged();
                }
            }
            else
            {
                UIHelper.ToastMessage(mContext, "Deje de escanear antes de exportar.");
            }
        }
    }***/

    private void clearData() {
        TagsRepository tagsRepository = new TagsRepository(this.mContext);
        tagsRepository.ClearTags("1");
        tv_count.setText("0");
        tagList.clear();
        Log.i("MY", "tagList.size " + tagList.size());
        adapter.notifyDataSetChanged();
    }

    public class RgInventoryCheckedListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            llContinuous.setVisibility(View.GONE);
            /*** Aqui activaba el modo single de capturar tag del boton
             if (checkedId == RbInventorySingle.getId()) {
                // 单步识别
                inventoryFlag = 0;
            } else
             ***/
            if (checkedId == RbInventoryLoop.getId()) {
                // 单标签循环识别
                inventoryFlag = 1;
                llContinuous.setVisibility(View.VISIBLE);
            }
        }
    }


    public class BtInventoryClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readTag();
        }
    }

    private void readTag() {
        if (BtInventory.getText().equals(
                mContext.getString(R.string.btInventory)))// 识别标签
        {
            switch (inventoryFlag) {
                /***case 0:// 单步
                {
                    String strUII = mContext.mReader.inventorySingleTag();
                    if (!TextUtils.isEmpty(strUII)) {
                        String strEPC = mContext.mReader.convertUiiToEPC(strUII);
                        addEPCToList(strEPC, "N/A");
                        tv_count.setText("" + adapter.getCount());
                    } else {
                        UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_fail);
//					mContext.playSound(2);
                    }
                }
                break;***/
                case 1:// 单标签循环  .startInventoryTag((byte) 0, (byte) 0))
                {
                  //  mContext.mReader.setEPCTIDMode(true);
                    if (mContext.mReader.startInventoryTag(0,0)) {
                        BtInventory.setText(mContext
                                .getString(R.string.title_stop_Inventory));
                        loopFlag = true;
                        setViewEnabled(false);
                        new TagThread().start();
                    } else {
                        mContext.mReader.stopInventory();
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_inventory_open_fail);
//					mContext.playSound(2);
                    }
                }
                break;
                default:
                    break;
            }
        } else {
            stopInventory();
        }
    }

    private void setViewEnabled(boolean enabled) {
        //RbInventorySingle.setEnabled(enabled);
        RbInventoryLoop.setEnabled(enabled);
        btnFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
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
            String test;
            String strTid;
            String strResult;
            String[] res = null;
            while (loopFlag) {
                res = mContext.mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    Log.i("data","TID:"+res[1]);
                    if (strTid.length() != 0 && !strTid.equals("0000000" + "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data","EPC:"+mContext.mReader.convertUiiToEPC(res[1])+" | "+strResult);
                    boolean saveRes=repositoryTag.InsertTag(strResult +" EPC:"+ mContext.mReader.convertUiiToEPC(res[1]), android_id, "1", strResult,true);
                    if(saveRes){
                        Message msg = handler.obtainMessage();
                        Log.e("EPC","EPC:"+ mContext.mReader.convertUiiToEPC(res[1]));
                        msg.obj = strResult +"EPC:"+ mContext.mReader.convertUiiToEPC(res[1])+ "@" + res[2]; //+ "EPC:"
                        handler.sendMessage(msg);
                    }else{
                        Log.i("Duplicate error", "Duplicate tag");
                    }
                }
            }
        }
    }

    public void loadData(){
        TagsRepository tagRepo= new TagsRepository(this.mContext);
        ArrayList<String> Tags=tagRepo.ViewAllTags();
        try{
            if(Tags.size()!=0){
                for(int i=0; i<=Tags.size();i++){
                    String tagsString=String.valueOf(Tags.get(i));
                    int index = checkIsExist(tagsString);
                    if(index == -1 ){
                        addEPCToList(tagsString);
                    }
                }

            }
        }catch (Exception ex){
            Log.i("Error Exception", ex.getLocalizedMessage());
        }
    }


    public class BtSyncClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                Context mcontext = getActivity();

                RequestQueue requestQueue= Volley.newRequestQueue(mcontext);
                JSONObject jsonBody;
                jsonBody = new JSONObject();
                String URL = URLRFID+"api/inventory/SaveTagReaded";
                JSONArray data=new JSONArray();

                for(int index=0;index<=tagList.size()-1; index++){
                    String strEPC=tagList.get(index).toString();

                    String strEPCleanFront=strEPC.replace("{tagUii=EPC:", "");
                    String strEPCleanEnd=strEPCleanFront.replace("}", "");

                    jsonBody.put("InventoryId","1");
                    jsonBody.put("TId", "");
                    jsonBody.put("IdHardware", android_id);
                    jsonBody.put("RFID",strEPCleanEnd);
                    Log.e("JSONObject", jsonBody.toString());
                    data.put(jsonBody);
                }

                Log.e("JSONArray", data.toString());
                String requestBody = data.toString();

                BooleanRequest booleanRequest = new BooleanRequest(1, URL, data, new Response.Listener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        Toast.makeText(mContext, "Envio de tags con exito!!", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Error con el servidor, intente mas tarde!!!", Toast.LENGTH_LONG).show();
                    }
                });
                booleanRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 30000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 30000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });
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

    public void getInventories() {
        result.clear();
        try{
            InventaryRespository inv = new InventaryRespository(this.mContext);
            ArrayList<String> invs = inv.ViewAllInventories();
            adapterSelect.notifyDataSetInvalidated();

            for(int inven=0; inven<=invs.size()-1; inven++){
                String recip=invs.get(inven);
                String[] strs = recip.split("@");
                result.add(strs[1]);

            }

            //adapter2= new SimpleCursorAdapter(this.mContext, R.layout.spinner_item_inventories, result);
            //adapterSelect=new ArrayAdapter<String>(this.mContext, R.layout.spinner_item_inventories, result);
            //spInventories.setAdapter(adapterSelect);
            /*adapterSelect.notifyDataSetChanged();*/
            adapterSelect.notifyDataSetChanged();
        }catch (Exception ex){
            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void myOnKeyDwon() {
        readTag();
        }

}
