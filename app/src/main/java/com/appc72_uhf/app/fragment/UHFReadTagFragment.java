package com.appc72_uhf.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UHFReadTagFragment extends KeyDwonFragment {

    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    Handler handler;
    private int sumTag=0;
    private ArrayList<HashMap<String, String>> tagList, maestroTagList;
    private SimpleAdapter adapter;
    private TextView tv_count, tv_TID;
    private RadioGroup RgInventory;
    private Button  btn_back_product_list, BtInventory, BtClear;
    ListView LvTags;
    private String android_id;
    private LinearLayout llContinuous;
    private MainActivity mContext;
    private HashMap<String, String> map;
    ArrayList<String> result;
    String code_enterprise, name_inventory_pass;
    String inventoryID;
    int codeCompany;
    boolean detailFordevice, detailForDevice;
    ProgressDialog mypDialog, dialogSearchTags, myDialogInsertTags;
    RelativeLayout  relative_layout_backButton;
    ImageButton imgbtn_indicator;
    String inventory_include_tid;
    boolean saveRestedTags=false;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onCreateView ");
        return inflater
                .inflate(R.layout.uhf_readtag_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponent();
    }


    //INICIALIZACION DE COMPONENTES
    private void initComponent(){
        mContext = (MainActivity) getActivity();
        tagList = new ArrayList<HashMap<String, String>>();
        maestroTagList= new ArrayList<HashMap<String, String>>();
        BtClear = (Button) getView().findViewById(R.id.BtClear);
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);
        btn_back_product_list=(Button) getView().findViewById(R.id.btn_back_product_list);
        relative_layout_backButton=(RelativeLayout) getView().findViewById(R.id.relative_layout_backButton);
        imgbtn_indicator=(ImageButton) getView().findViewById(R.id.imgbtn_indicator);
        imgbtn_indicator.setBackgroundResource(R.drawable.circle_indicator_stop);
        tv_TID=(TextView) getView().findViewById(R.id.tv_TID);
        //RECYCLEVIEW DECLARANDO

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
        ProgressDialog dialogProgre = new ProgressDialog(getContext());
        dialogProgre.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogProgre.setMessage("Cargando lectura, espere por favor...");
        dialogProgre.setCanceledOnTouchOutside(false);
        dialogProgre.show();
        maestroTagList.clear();
        tagList.clear();
        tv_count.setText("0");
        loadData();
        adapter.notifyDataSetChanged();
        dialogProgre.dismiss();


    }
    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop", "Estado: onStop");
        //int saveMasterResponse=validationEPCToSave(maestroTagList.size());
       // if(maestroTagList.size()>0 && saveMasterResponse==0){
            //insertTags();
            //myDialogInsertTags.dismiss();
        //}
        stopInventory();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "Estado: onPause");

        //if(maestroTagList.size()!=0 && saveMasterResponse==0){
            //myDialogInsertTags.dismiss();
        //}
        if(maestroTagList.size()>0){
            insertTags();
            //myDialogInsertTags.dismiss();
        }
        stopInventory();
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

                if (index == -1) {
                    maestroTagList.add(map);
                    if(tagList.size()<=100){
                        tagList.add(maestroTagList.get(sumTag));
                    }
                    LvTags.setAdapter(adapter);
                    int contador=maestroTagList.size();
                    tv_count.setText(""+contador);
                    sumTag++;

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
    /**
     * @param epc
     * Agregar tag a la lista taglist
     */
    private void addEPCToListFromDb(String epc, String tid) {
        try {
            if (!TextUtils.isEmpty(epc)) {
                    map = new HashMap<String, String>();
                    map.put("tagUii", epc);
                    map.put("tagRssi", tid);
                    tagList.add(map);
                    LvTags.setAdapter(adapter);
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
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIHelper.ToastMessage(mContext, "Espere...", (maestroTagList.size()/(10)));
            }
        });

        //btn_back_product_list.setBackgroundResource(R.color.yellow);
        //TagList Vacio o no
        if(maestroTagList.size()>0){
            //Si viene inventario con detalle o sin detalle
            insertTags();
        }else{
            UIHelper.ToastMessage(mContext, "No hay codigos en Lectura", 3);
            if(detailFordevice){
                Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                goToDetailProduct.putExtra("Id",  inventoryID);
                goToDetailProduct.putExtra("Name",  name_inventory_pass);
                goToDetailProduct.putExtra("inventoryType", detailForDevice);
                goToDetailProduct.putExtra("EntryType", "Inventory");
                goToDetailProduct.putExtra("inventoryBool", true);
                mContext.startActivity(goToDetailProduct);
                mContext.onBackPressed();
            }else{
                Intent goToMain=new Intent(mContext, MainActivity.class);
                goToMain.putExtra("EntryType", "Inventory");
                goToMain.putExtra("inventoryBool", false);
                mContext.startActivity(goToMain);
                mContext.onBackPressed();
            }
        }
    }
    private void insertTags(){
        btn_back_product_list.setBackgroundResource(R.color.red);

        //int tagSize=(tagList.size()<=100)?tagList.size():tagList.size()-100;
        int tagSize=maestroTagList.size();
        /**
         * Validacion para actualizar el estado de la lectura
         */
        int valEPCToSave=validationEPCToSave(tagSize);
        if(valEPCToSave==0) {
           myDialogInsertTags = new ProgressDialog(this.mContext);
            @SuppressLint("HandlerLeak") final Handler handle = new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    myDialogInsertTags.incrementProgressBy(1);
                }
            };
            myDialogInsertTags.setMax(tagSize);
            myDialogInsertTags.setMessage("Guardando codigos...");
            myDialogInsertTags.setTitle("No interrumpir operación"); // Setting Title
            myDialogInsertTags.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            myDialogInsertTags.setCanceledOnTouchOutside(false);
            myDialogInsertTags.setCancelable(false);
            if (maestroTagList.size() > 0) {
                MiTareaAsincrona taskMy = new MiTareaAsincrona();
                taskMy.execute();

            }
        }else if(valEPCToSave==1){
            saveRestedTags=true;
            if(detailFordevice){
                if(saveRestedTags){
                        Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                        goToDetailProduct.putExtra("Id",  inventoryID);
                        goToDetailProduct.putExtra("Name",  name_inventory_pass);
                        goToDetailProduct.putExtra("Name",  name_inventory_pass);
                        goToDetailProduct.putExtra("inventoryType", detailForDevice);
                        goToDetailProduct.putExtra("EntryType", "Inventory");
                        goToDetailProduct.putExtra("inventoryBool", true);
                        mContext.startActivity(goToDetailProduct);
                        mContext.onBackPressed();
                    }
                }else{
                    if(saveRestedTags){
                        Intent goToMain=new Intent(mContext, MainActivity.class);
                        goToMain.putExtra("EntryType", "Inventory");
                        goToMain.putExtra("inventoryBool", false);
                        mContext.startActivity(goToMain);
                        mContext.onBackPressed();
                    }
                }
        }

       // return insertagsReturn;
    }

    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... paramss) {
            /*for(int i=1; i<=10; i++) {
            }*/
            TagsRepository repositoryTag = new TagsRepository(mContext);
            boolean respon=false;
            try {
                respon=repositoryTag.InsertTag(maestroTagList, inventoryID, android_id, 0);
                int timeTolapse=(maestroTagList.size()>=1000)?10:20;
                for (int index = 0; index < maestroTagList.size(); index++) {
                    if (index <= myDialogInsertTags.getMax()) {
                        publishProgress(index *2);
                        // String strEPC = maestroTagList.get(index).get("tagUii");
                        //String strTID = maestroTagList.get(index).get("tagRssi");
                        //Log.e("SaveRes", "" + saveRes);
                        Thread.sleep(timeTolapse);
                        //myDialogInsertTags.incrementProgressBy(index);
                        //int indexTransform=(index==maestroTagList.size()-1)?index+1:index;
                        //int indexTransform=(index==maestroTagList.size()-1)?index+1:index;
                        //if ((index) == (myDialogInsertTags.getMax() - 1)) {
                        //     myDialogInsertTags.dismiss();
                        // }
                        if (isCancelled())
                            break;
                    }
                }
            }catch(Exception ex){
                Log.e("Exception", ""+ex.getLocalizedMessage());
                //ex.printStackTrace();
            }

            return respon;
        }

        @Override
        protected void onProgressUpdate(Integer... val) {
            int progreso = val[0].intValue();
            myDialogInsertTags.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialogInsertTags.setMax(maestroTagList.size());
            myDialogInsertTags.setProgress(0);
            myDialogInsertTags.show();
            //myDialogInsertTags.show();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado)
            {
               // UIHelper.ToastMessage(mContext, "Ingreso finalizado", 2);
                    myDialogInsertTags.dismiss();
                    btn_back_product_list.setBackgroundResource(R.color.red);
                    UIHelper.ToastMessage(mContext, "Codigos ingresados correctamente.", 2);
                    if (!detailFordevice) {
                        saveRestedTags = true;
                    }
                if(detailFordevice){
                    searchTags();
                    if(saveRestedTags){
                        Intent goToDetailProduct=new Intent(mContext, Detail_product_activity.class);
                        goToDetailProduct.putExtra("Id",  inventoryID);
                        goToDetailProduct.putExtra("Name",  name_inventory_pass);
                        goToDetailProduct.putExtra("Name",  name_inventory_pass);
                        goToDetailProduct.putExtra("inventoryType", detailForDevice);
                        goToDetailProduct.putExtra("EntryType", "Inventory");
                        goToDetailProduct.putExtra("inventoryBool", true);
                        mContext.startActivity(goToDetailProduct);
                        mContext.onBackPressed();
                    }

                }else{
                        Intent goToMain=new Intent(mContext, MainActivity.class);
                        goToMain.putExtra("EntryType", "Inventory");
                        goToMain.putExtra("inventoryBool", false);
                        mContext.startActivity(goToMain);
                        mContext.onBackPressed();
                }
            }else{
                myDialogInsertTags.dismiss();

            }
        }

        @Override
        protected void onCancelled() {
            /*Toast.makeText(mContext, "Ingreso cancelado!",
                    Toast.LENGTH_SHORT).show();*/
        }
    }

      private boolean searchTags() {
        UIHelper.ToastMessage(mContext, "Procesando codigos en detalle...", (maestroTagList.size()*10));
        TagsRepository tagsRepository=new TagsRepository(mContext);
        boolean resultFound=false;
        if(maestroTagList.size()>0){
            if(detailFordevice){
                try{
                    dialogSearchTags = new ProgressDialog(mContext);
                    dialogSearchTags.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialogSearchTags.setMessage("Procesando codigos...");
                    dialogSearchTags.setCanceledOnTouchOutside(false);
                    dialogSearchTags.show();
                    int contadorFound=0;
                    //for(int tagPosition=0; tagPosition<maestroTagList.size(); tagPosition++){
                        //String strEPC=maestroTagList.get(tagPosition).get("tagUii");
                        contadorFound=tagsRepository.UpdateTagsFound(maestroTagList, inventoryID);
                        //if(respFound){
                            //contadorFound++;
                        //}
                    //}
                   // Log.e("Found", ""+contadorFound);
                    saveRestedTags=true;
                    if(contadorFound>0){
                        dialogSearchTags.dismiss();
                        UIHelper.ToastMessage(mContext, "Lectura finalizado con exito, se encontraron "+contadorFound+" codigos", 5);
                        resultFound=true;
                    }else if(contadorFound==0){
                        UIHelper.ToastMessage(mContext, "Lectura finalizado, no se encontraron codigos en detalle", 5);
                    }
                }catch (Exception e){
                    dialogSearchTags.dismiss();
                    e.printStackTrace();
                }
            }
        }
        return resultFound;
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
                    SharedPreferences savePreferenceMasterTagToSave=mContext.getSharedPreferences(String.valueOf(inventoryID), Context.MODE_PRIVATE);
                    SharedPreferences.Editor obj_edite_maestro_clear=savePreferenceMasterTagToSave.edit();
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
                        sumTag=0;
                        tagList.clear();
                        maestroTagList.clear();
                        adapter.notifyDataSetChanged();
                        obj_edite_maestro_clear.remove(String.valueOf(inventoryID));
                        obj_edite_maestro_clear.apply();
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
            if(mContext.mReader.setPower(30)){
                Log.i("SetPower", "Set power readInventory :"+mContext.mReader.getPower());
            }
            if (inventoryFlag == 1) {
                if (inventory_include_tid.equals("true")) {
                    mContext.mReader.setEPCTIDMode(true);
                    mContext.mReader.setTagFocus(true);
                } else if (inventory_include_tid.equals("false")) {
                    mContext.mReader.setEPCTIDMode(false);
                    mContext.mReader.setTagFocus(true);
                }
                if (mContext.mReader.startInventoryTag(0, 0)) {
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
            }
        } else {

            stopInventory();
            UIHelper.ToastMessage(mContext, "Lectura detenida", 3);
        }
    }



    private void setViewEnabled(boolean enabled) {
        btn_back_product_list.setEnabled(enabled);
        BtClear.setEnabled(enabled);
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
        for (int i = 0; i < maestroTagList.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = maestroTagList.get(i);
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
                    // Log.e("EPC","EPC:"+ mContext.mReader.convertUiiToEPC(res[1])+"@"+res[2]+"@"+strResult);
                    msg.obj =mContext.mReader.convertUiiToEPC(res[1])+"@"+ res[2]+"@"+strResult; //+ "EPC:"
                    handler.sendMessage(msg);
                }
            }
            if(!loopFlag){
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.ToastMessage(mContext, "Espere mientras se detiene la lectura", 3);
                    }
                });
            }
        }
    }

    private void loadData(){
        TagsRepository tagRepo= new TagsRepository(this.mContext);
        ArrayList Tags=tagRepo.ViewAllTags(inventoryID, inventory_include_tid.equals("true"));
        InventaryRespository inventaryRespository=new InventaryRespository(this.mContext);
        detailFordevice=inventaryRespository.inventoryDetailForDevice(inventoryID);

        maestroTagList=Tags;

        try{
            if(Tags.size()!=0){

                List<List<HashMap>> smalltags= Lists.partition(Tags, 100);
                int contador=Tags.size();
                /**
                 * Validacion para actualizar el estado de la lectura
                 */
                validationEPCToSave(Tags.size());
                /*map = new HashMap<String, String>();

                for(int indTags=0;indTags<Tags.size();indTags++){
                    String etags=String.valueOf(Tags.get(indTags));
                    String[] spliTags=etags.split("@");
                    String RFIDtagsString=spliTags[0];
                    String TIDtagsString="";
                    if(inventory_include_tid.equals("true")){
                        TIDtagsString=spliTags[1];
                    }
                    map.put("tagUii", RFIDtagsString);
                    map.put("tagRssi", TIDtagsString);
                    maestroTagList.add(map);

                }*/
                //Log.e("maestroTag", maestroTagList.toString()+" snall size"+smalltags.get(0).size());

                for(int i=0; i<smalltags.get(0).size();i++){
                    String RFIDtagsString=smalltags.get(0).get(i).get("tagUii").toString();
                    //String[] spliTags=etags.split("@");
                    //String RFIDtagsString=spliTags[0];
                    String TIDtagsString="";
                    if(inventory_include_tid.equals("true")){
                        TIDtagsString=smalltags.get(0).get(i).get("tagRssi").toString();
                    }

                   // int index = checkIsExist(smalltags.get(0).get(i).get("tagUii"));
                    //if(index == -1 ){
                        tv_count.setText(""+contador);
                        addEPCToListFromDb(RFIDtagsString, TIDtagsString);
                   // }
                }
            }
            adapter.notifyDataSetChanged();
        }catch (Exception ex){
            Log.i("Error Exception", ""+ex.getLocalizedMessage());
        }

    }

    private int validationEPCToSave(int tagSize){
        SharedPreferences savePreferenceMasterTagToSave=mContext.getSharedPreferences(String.valueOf(inventoryID), Context.MODE_PRIVATE);
        int masterTagToSave=savePreferenceMasterTagToSave.getInt(String.valueOf(inventoryID), 0);
        int response = 3;
            if (tagSize == masterTagToSave) {
                SharedPreferences.Editor obj_edite_maestro = savePreferenceMasterTagToSave.edit();
                obj_edite_maestro.apply();
                //Log.e("masterTagToSave", "SON iguales No se actualiza" + masterTagToSave + " tagSize" + tagSize);
                response = 1;
            } else {
                SharedPreferences.Editor obj_edite_maestro = savePreferenceMasterTagToSave.edit();
                obj_edite_maestro.putInt(String.valueOf(inventoryID), tagSize);
                obj_edite_maestro.apply();
                //Log.e("masterTagToSave", "NO SON iguales se actualiza el guardado" + masterTagToSave + " tagSize" + tagSize);
                response = 0;
            }
            //Log.e("response", ""+response);
        return  response;
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