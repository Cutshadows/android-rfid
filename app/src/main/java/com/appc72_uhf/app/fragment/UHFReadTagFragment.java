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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.Detail_product_activity;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.repositories.TagsRepository;
import com.appc72_uhf.app.tools.StringUtils;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class UHFReadTagFragment extends KeyDwonFragment {

    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    Handler handler;
    private ArrayList<HashMap<String, String>> tagList;
    SimpleAdapter adapter;
    TextView tv_count, tv_TID;
    RadioGroup RgInventory;
    Button  btn_back_product_list, BtInventory, BtClear; //BtSync
    ListView LvTags;
    private String android_id;
    private LinearLayout llContinuous;
    private MainActivity mContext;
    private HashMap<String, String> map;
    ArrayList<String> result;
    private boolean shouldRefreshOnResume =false;
    String code_enterprise, name_inventory_pass;
    String inventoryID;
    int codeCompany;
    boolean detailFordevice, detailForDevice;
    ProgressDialog mypDialog;
    RelativeLayout  relative_layout_backButton;
    ImageButton imgbtn_indicator;
    String inventory_include_tid;



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
        //BtSync = (Button) getView().findViewById(R.id.BtSync);
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);
        btn_back_product_list=(Button) getView().findViewById(R.id.btn_back_product_list);
        //relative_layout_filter=(RelativeLayout) getView().findViewById(R.id.relative_layout_filter);
        relative_layout_backButton=(RelativeLayout) getView().findViewById(R.id.relative_layout_backButton);
        imgbtn_indicator=(ImageButton) getView().findViewById(R.id.imgbtn_indicator);
        imgbtn_indicator.setBackgroundResource(R.drawable.circle_indicator_stop);
        tv_TID=(TextView) getView().findViewById(R.id.tv_TID);

        String tr = "";
        result=new ArrayList<>();
        inventoryID=mContext.getIntent().getStringExtra("inventoryID");
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
        if(inventory_include_tid.equals("true")){
                tv_TID.setVisibility(View.VISIBLE);
        }else {
            tv_TID.setVisibility(View.INVISIBLE);
        }

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
                searchTags();
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
        if(tagList.size()>0){
            final TagsRepository repositoryTag= new TagsRepository(mContext);
            mypDialog = new ProgressDialog(mContext);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Guardando codigos...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
            try{
                boolean saveRes=false;
                for(int index=0; index < tagList.size();index++){
                    String strEPC=tagList.get(index).get("tagUii");
                    String strTID=tagList.get(index).get("tagRssi");
                    saveRes=repositoryTag.InsertTag(strEPC,  inventoryID, android_id, strTID,0);
                }
                if(saveRes){
                    UIHelper.ToastMessage(mContext, "Codigos ingresados correctamente.", 2);
                }
                mypDialog.dismiss();
            }catch (Exception ex){
                ex.printStackTrace();
                mypDialog.dismiss();
            }
        }

    }

    private void searchTags() {
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
                        String strEPC=tagList.get(tagPosition).get("tagUii");
                        boolean respFound=tagsRepository.UpdateTagsFound(strEPC, inventoryID);
                        if(respFound){
                            contadorFound++;
                        }
                    }
                    UIHelper.ToastMessage(mContext, "Lectura finalizado con exito, se encontraron "+contadorFound+" codigos", 5);
                    mypDialog.dismiss();
                }catch (Exception e){
                    mypDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }
    }


    private void clearData() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
            builder.setTitle(R.string.ap_dialog_inventario_vaciar);
            builder.setMessage("¿Desea limpiar lectura?");
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
                            if(inventory_include_tid.equals("true")){
                                mContext.mReader.setEPCTIDMode(true);
                                //mContext.mReader.setFastID(true);
                                mContext.mReader.setTagFocus(true);
                            }else if(inventory_include_tid.equals("false")){
                                mContext.mReader.setEPCTIDMode(false);
                                //mContext.mReader.setFastID(true);
                                mContext.mReader.setTagFocus(true);
                            }
                            if (mContext.mReader.startInventoryTag(0,0)) {
                                BtInventory.setText(mContext
                                        .getString(R.string.title_stop_Inventory));
                                loopFlag = true;
                                setViewEnabled(false);
                                //ACA SET IMAGEN
                                imgbtn_indicator.setBackgroundResource(R.drawable.circle_indicator_on);
                                new TagThread().start();
                            } else {
                                imgbtn_indicator.setBackgroundResource(R.drawable.circle_indicator_stop);
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

                stopInventory();
                UIHelper.ToastMessage(mContext, "Lectura detenida", 3);
        }
    }



    private void setViewEnabled(boolean enabled) {
        //btnFilter.setEnabled(enabled);
        btn_back_product_list.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        //BtSync.setEnabled(enabled);
    }
    private void stopInventory() {
            if (loopFlag) {
                loopFlag = false;
                setViewEnabled(true);
                if (mContext.mReader.stopInventory()) {
                    BtInventory.setText(mContext.getString(R.string.btInventory));
                    imgbtn_indicator.setBackgroundResource(R.drawable.circle_indicator_stop);
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
                        strResult = " ";
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
            Log.i("Error Exception", ""+ex.getLocalizedMessage());
        }
    }


    @Override
    public void myOnKeyDwon() {
        readTag();
    }
    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(mContext);
        InventaryRespository inventaryRespository= new InventaryRespository(mContext);
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        inventory_include_tid=inventaryRespository.inventoryWithTID(inventoryID);
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
