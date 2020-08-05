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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.Detail_product_activity;
import com.appc72_uhf.app.adapter.RecycleAdapter;
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
    ProgressDialog mypDialog, dialogSearchTags, dialogInsertTag, mDialogLoad;
    RelativeLayout  relative_layout_backButton;
    ImageButton imgbtn_indicator;
    String inventory_include_tid;
    boolean saveRestedTags=false;

    private ProgressBar main_progress;
    private int page_number=1;
    private int totalItemCount;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int previousTotal;
    private RecyclerView RvTags;
    private RecycleAdapter recycleAdapter;
    private ArrayList commentList=new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private boolean load;



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
        //RECYCLEVIEW DECLARANDO
        //RvTags=(RecyclerView)getView().findViewById(R.id.RvTags);

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

        /**
         * Recycle view infinity scroll
         */
        commentList= new ArrayList<>();
         LvTags.setAdapter(adapter);


        //pagination();



        adapter.notifyDataSetChanged();
        detailForDevice=mContext.getIntent().getBooleanExtra("inventoryType", false);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0], strs[2], 1); //, strs[1]
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
    private void pagination(){
        RvTags.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount=layoutManager.getChildCount();
                totalItemCount=layoutManager.getItemCount();
                firstVisibleItem=layoutManager.findFirstVisibleItemPosition();
                if(load){
                    if (totalItemCount > previousTotal) {
                        previousTotal=totalItemCount;
                        page_number++;
                        load=false;
                    }
                }
                if(!load && (firstVisibleItem+ visibleItemCount)>=totalItemCount){
                        getNext();
                        load=true;
                        Log.e("SCROLLING", "Page Number: "+page_number);
                }
            }
        });
    }
    private void getNext(){

    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("onResume", "Estado: onResume");
        ProgressDialog dialogProgre = new ProgressDialog(getContext());
        dialogProgre.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogProgre.setMessage("Cargando lectura, espere por favor...");
        dialogProgre.setCanceledOnTouchOutside(false);
        dialogProgre.show();
        loadData();
        adapter.notifyDataSetChanged();
        dialogProgre.dismiss();


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
    private void addEPCToList(String epc, String tid, int countRfid) {
        TagsRepository tagRepoCount= new TagsRepository(this.mContext);
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
                    int countTagsSQL=tagRepoCount.countTags(inventoryID);
                    int countAdapter=(adapter.getCount()<=100)?adapter.getCount():adapter.getCount()-100;
                    int contador=(countRfid==1)?countTagsSQL+countAdapter:countTagsSQL;
                    tv_count.setText(""+contador);
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
                boolean insertTags=insertTags();
                if(insertTags){
                    searchTags();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(saveRestedTags){
                    Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                    goToDetailProduct.putExtra("Id",  inventoryID);
                    goToDetailProduct.putExtra("Name",  name_inventory_pass);
                    goToDetailProduct.putExtra("Name",  name_inventory_pass);
                    goToDetailProduct.putExtra("inventoryType", detailForDevice);
                    goToDetailProduct.putExtra("EntryType", "Inventory");
                    mContext.startActivity(goToDetailProduct);
                    mContext.onBackPressed();
                }

            }else{
                boolean insertTags=insertTags();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("insertTags", "PASANDO POR ACA");
                if(saveRestedTags){
                    Intent goToMain=new Intent(mContext, MainActivity.class);
                    mContext.startActivity(goToMain);
                    mContext.onBackPressed();
                }
            }
        }else{
            UIHelper.ToastMessage(mContext, "No hay codigos en Lectura", 3);
            if(detailFordevice){
                Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                goToDetailProduct.putExtra("Id",  inventoryID);
                goToDetailProduct.putExtra("Name",  name_inventory_pass);
                goToDetailProduct.putExtra("Name",  name_inventory_pass);
                goToDetailProduct.putExtra("inventoryType", detailForDevice);
                goToDetailProduct.putExtra("EntryType", "Inventory");
                mContext.startActivity(goToDetailProduct);
                mContext.onBackPressed();
            }else{
                Intent goToMain=new Intent(mContext, MainActivity.class);
                mContext.startActivity(goToMain);
                mContext.onBackPressed();
            }
        }
    }
    private boolean insertTags(){
        boolean insertagsReturn=false;
        btn_back_product_list.setBackgroundResource(R.color.red);
        Handler handle = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mypDialog.incrementProgressBy(1); // Incremented By Value 2
            }
        };

        int tagSize=(tagList.size()<=100)?tagList.size():tagList.size()-100;
        mypDialog = new ProgressDialog(this.mContext);
        mypDialog.setMax(tagSize);
        mypDialog.setMessage("Guardando codigos...");
        mypDialog.setTitle("Mensaje de guardado"); // Setting Title
        mypDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mypDialog.setCanceledOnTouchOutside(false);
        mypDialog.setCancelable(false);
        mypDialog.show();
        if(tagList.size()>0){
            final TagsRepository repositoryTag= new TagsRepository(mContext);
            try{
                boolean saveRes=false;
                for(int index=0; index < tagList.size();index++){
                    if(index<=mypDialog.getMax()){
                        String strEPC=tagList.get(index).get("tagUii");
                        String strTID=tagList.get(index).get("tagRssi");
                        saveRes=repositoryTag.InsertTag(strEPC,  inventoryID, android_id, strTID,0);
                       // Thread.sleep(1000);
                        handle.sendMessage(handle.obtainMessage());
                        if(index==mypDialog.getMax()){
                            mypDialog.dismiss();
                        }
                    }
                }
                if(saveRes){
                    UIHelper.ToastMessage(mContext, "Codigos ingresados correctamente.", 2);
                    btn_back_product_list.setBackgroundResource(R.color.red);
                    saveRestedTags=true;
                    insertagsReturn=true;
                }
                insertagsReturn=false;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return insertagsReturn;

    }

    private void searchTags() {
        TagsRepository tagsRepository=new TagsRepository(mContext);
        if(tagList.size()>0){
            if(detailFordevice){
                try{
                    dialogSearchTags = new ProgressDialog(mContext);
                    dialogSearchTags.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialogSearchTags.setMessage("Procesando codigos...");
                    dialogSearchTags.setCanceledOnTouchOutside(false);
                    dialogSearchTags.show();
                    int contadorFound=0;
                    for(int tagPosition=0; tagPosition<tagList.size(); tagPosition++){
                        String strEPC=tagList.get(tagPosition).get("tagUii");
                        boolean respFound=tagsRepository.UpdateTagsFound(strEPC, inventoryID);
                        if(respFound){
                            contadorFound++;
                        }
                    }
                    UIHelper.ToastMessage(mContext, "Lectura finalizado con exito, se encontraron "+contadorFound+" codigos", 5);
                    dialogSearchTags.dismiss();
                }catch (Exception e){
                    dialogSearchTags.dismiss();
                    e.printStackTrace();
                }
            }
        }
    }


    public void clearData() {
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
                    if ((strTid.length() != 0 && strTid.length() <= 24) && !strTid.equals("0000000" + "000000000") && !strTid.equals("000000000000000000000000")) {
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
        Log.e("LoadData", "Estoy en load data");
        TagsRepository tagRepo= new TagsRepository(this.mContext);
        ArrayList Tags=tagRepo.ViewAllTags(inventoryID, inventory_include_tid.equals("true"));
        InventaryRespository inventaryRespository=new InventaryRespository(this.mContext);
        detailFordevice=inventaryRespository.inventoryDetailForDevice(inventoryID);
        try{
            if(Tags.size()!=0){
                    Log.e("TagsTags", ""+Tags.toString());
                    for(int i=0; i<=Tags.size();i++){
                        Log.e("index", "index: "+i);
                        String etags=String.valueOf(Tags.get(i));
                        String[] spliTags=etags.split("@");
                        String RFIDtagsString=spliTags[0];
                        String TIDtagsString="";
                        int countRfid=0;
                        if(inventory_include_tid.equals("true")){
                            TIDtagsString=spliTags[1];
                            countRfid=Integer.parseInt(spliTags[2]);
                        }
                        countRfid=Integer.parseInt(spliTags[1]);
                        int index = checkIsExist(RFIDtagsString);
                         if(index == -1 ){
                             addEPCToList(RFIDtagsString, TIDtagsString, countRfid);
                                  //commentList.add(new ListData(RFIDtagsString, TIDtagsString));
                        }
                    }
                //recycleAdapter.notifyDataSetChanged();
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
