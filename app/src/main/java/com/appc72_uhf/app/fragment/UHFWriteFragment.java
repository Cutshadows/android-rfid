package com.appc72_uhf.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.DeviceRepository;
import com.appc72_uhf.app.tools.StringUtils;
import com.appc72_uhf.app.tools.UIHelper;
import com.rscja.deviceapi.RFIDWithUHF.BankEnum;
import com.rscja.utility.StringUtility;

import java.util.TimeZone;

public class UHFWriteFragment extends KeyDwonFragment implements OnClickListener {
    private static final String TAG = "UHFWriteFragment";
    private MainActivity mContext;
    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    ProgressDialog mypDialog;
    Spinner SpinnerBank_Write;
    EditText EtPtr_Write, EtData_Barcode;
    EditText EtLen_Write;
    EditText EtData_Write;
    Button BtWrite;
    int ProductMasterId;
    String codeRfidCompany;
    Handler handler;
    int switchTypeAway=2; //1 = lector de validacion EPC 2=lectura del codigo de barra 3= quemar etiqueta
    private static final long TICKS_AT_EPOCH = 621355968000000000L;
    private static final long TICKS_PER_MILLISECOND = 10000;
    public static final int ILLUM_AIAM_OFF=0;
    CheckBox cb_QT_W;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_write_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            initComponent();

    }

    public void initComponent() {
        mContext = (MainActivity) getActivity();
        //SpinnerBank_Write = (Spinner) getView().findViewById(R.id.SpinnerBank_Write);
        //EtPtr_Write = (EditText) getView().findViewById(R.id.EtPtr_Write);
        //EtLen_Write = (EditText) getView().findViewById(R.id.EtLen_Write);
        EtData_Write = (EditText) getView().findViewById(R.id.EtData_Write);
        EtData_Barcode = (EditText) getView().findViewById(R.id.EtData_Barcode);
        cb_QT_W= (CheckBox) getView().findViewById(R.id.cb_QT_W);
        ProductMasterId= mContext.getIntent().getIntExtra("ProductMasterId", 0);
        inventoryFlag = 1;
        EtData_Write.setEnabled(false);
        BtWrite = (Button) getView().findViewById(R.id.BtWrite);
        BtWrite.setOnClickListener(this);
        getCompany();
        /*SpinnerBank_Write.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String element = adapterView.getItemAtPosition(i).toString();// 得到spanner的值
                if(element.equals("EPC")){
                    EtPtr_Write.setText("2");
                }else{
                    EtPtr_Write.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        //EtData_Barcode.requestFocus();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String EPCCaptura = msg.obj + "";
                //String[] strs = result.split("@");
                Log.e("EPCCaptura", EPCCaptura);
                    validationEPC(EPCCaptura);
                mContext.playSound(1);
            }
        };

    }


    private void validationEPC(String EPCCaptura){
    //if (!TextUtils.isEmpty(EPCCaptura)) {
        String epcTransform = EPCCaptura.substring(0, 4);
        Log.e("EPC", "EPCTransform:" + epcTransform);
       /* if (epcTransform.equals(codeRfidCompany)) {
            UIHelper.ToastMessage(mContext, "Esta etiqueta ya esta siendo utilizada", 3);
            switchTypeAway = 1;
        } else {*/
            //switchTypeAway = 2;
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
            builder.setTitle(R.string.ap_dialog_makelabel_coderfid);
            builder.setMessage("Acercar la etiqueta \n para lectura  del codigo de barra");
            builder.setIcon(R.drawable.button_bg_up);
                            /*builder.setNegativeButton(R.string.ap_dialog_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });*/
            builder.setNeutralButton(R.string.ap_dialog_acept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    EtData_Barcode.requestFocus();
                    EtData_Barcode.addTextChangedListener(new TextChangedListener<EditText>(EtData_Barcode) {
                        @Override
                        public void beforeTextChanged(CharSequence barcodeSecuence, int start, int count, int after) {
                            super.beforeTextChanged(barcodeSecuence, start, count, after);
                            Log.e("beforeTextChanged", "Charsecuence"+barcodeSecuence+" Inicio"+start+" Contador"+count+" Despues"+after);
                            if(barcodeSecuence.length()>0){
                                EtData_Barcode.clearFocus();
                                BtWrite.requestFocus();
                                generateEPC();
                            }

                        }

                        @Override
                        public void onTextChanged(EditText target, Editable s) {
                            //Log.e("beforeTextChanged", ""+target+" "+s);
                        }
                    });
                    /*String barcodeText=EtData_Barcode.getText().toString().trim();
                    if(barcodeText.length()!=0){
                        Log.e("BarcodeText", "ahora puede etiquetar");
                    }*/

                }
            });
            builder.create().show();
        //}
    //}
}

    public abstract class TextChangedListener<T> implements TextWatcher {
        private T target;

        public TextChangedListener(T target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            this.onTextChanged(target, s);
        }

        public abstract void onTextChanged(T target, Editable s);
    }
    private void readBarcode(){
        Log.e("readBarcode", "ingrese a leer codigo de barra");
        EtData_Barcode.requestFocus();
        //EtData_Barcode.requestFocus();
        EtData_Barcode.addTextChangedListener(new TextChangedListener<EditText>(EtData_Barcode) {
            @Override
            public void beforeTextChanged(CharSequence barcodeSecuence, int start, int count, int after) {
                super.beforeTextChanged(barcodeSecuence, start, count, after);
                Log.e("beforeTextChanged", "Charsecuence"+barcodeSecuence+" Inicio"+start+" Contador"+count+" Despues"+after);
                if(barcodeSecuence.length()>0){
                    EtData_Barcode.clearFocus();
                    BtWrite.requestFocus();
                    generateEPC();
                    switchTypeAway=3;
                }

            }

            @Override
            public void onTextChanged(EditText target, Editable s) {
                //Log.e("beforeTextChanged", ""+target+" "+s);
            }
        });
       // UIHelper.ToastMessage(mContext, "Lea etiqueta del codigo de barra", 3);
        //String barcodeText = EtData_Barcode.getText().toString().trim();
    }
    private void generateEPC() {
        /**
         * READ BARCODE
         * */
            String barcodeText=EtData_Barcode.getText().toString().trim();
            Log.e("tamBarcode", ""+barcodeText.length());
        /**
         * TIMESTAMP
         */
        long ts=((System.currentTimeMillis() + TimeZone.getDefault().getRawOffset())*TICKS_PER_MILLISECOND)+TICKS_AT_EPOCH;
        String timstamp=String.valueOf(ts).substring(3, 7);
        /**
         * FIN TIMESTAMP
         * PRODUCTMASTER HEXADECIMAL
         */
            int strProd=ProductMasterId;
            Character productMaster= Character.valueOf((char) strProd);
            //int strProd=ProductMasterId;
            String hexCxhar= StringUtility.char2HexString(productMaster);
            Log.e("hexCxhar", ""+hexCxhar);
           /* if(strProd.length()>6){
                strProd=strProd.substring(0, 6);
            }else if(strProd.length()<6){
                //strProd=strProd.l
            }*/
           /**Hexadecimal productmaster no se utilizara
           *StringBuilder hexProductM= new StringBuilder();
           *String caracteresHexadecimales="0123456789abcdef";
           *while(strProd>0){
           *    int rest=strProd%16;
           *   hexProductM.insert(0, caracteresHexadecimales.charAt(rest));
           *    strProd/=16;
           *}
            */
           // hexProductM = new StringBuilder(String.format("%6s", hexProductM.toString()).replace(' ', '0'));

            String ProductMasterRefactor = String.format("%8s", ProductMasterId).replace(' ', '0');
            String barcodeado=(barcodeText.length()>7)?barcodeText.substring(0, 8):String.format("%8s", barcodeText).replace(' ', '0');
           // Log.e("ProductMasterRefactor", ProductMasterRefactor);
            String rfid=codeRfidCompany+""+timstamp+""+barcodeado+""+ProductMasterRefactor;


            Log.e("ts", ""+ts);
            Log.e("Timestamp", ""+timstamp);
            Log.e("barcodeText", ""+barcodeado);
            Log.e("ProductMasterId", ""+ProductMasterId);

            Log.e("EPC", rfid+" el tamano es:"+rfid.length());

            EtData_Write.setText(rfid);
            switchTypeAway=3;
    }
    /*public static String fill(String text, int size) {
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < size) {
            builder.append('0');
        }
        return builder.toString();
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.BtWrite:
                write();
                break;
       }
    }

 private void write(){
         /*String strPtr = EtPtr_Write.getText().toString().trim();
         if (StringUtils.isEmpty(strPtr)) {
             UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_not_null);
             return;
         } else if (!StringUtility.isDecimal(strPtr)) {
             UIHelper.ToastMessage(mContext,
                     R.string.uhf_msg_addr_must_decimal);
             return;
         }*/
         String strPWD = "00000000";
         String strData = EtData_Write.getText().toString().trim();// 要写入的内容
         if (StringUtils.isEmpty(strData)) {
             UIHelper.ToastMessage(mContext,
                     R.string.uhf_msg_write_must_not_null);
             return;
         } else if (!mContext.vailHexInput(strData)) {
             UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
             return;
         }
         // 多字单次
         /*String cntStr = EtLen_Write.getText().toString().trim();
         if (StringUtils.isEmpty(cntStr)) {
             UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_not_null);
             return;
         } else if (!StringUtility.isDecimal(cntStr)) {
             UIHelper.ToastMessage(mContext,
                     R.string.uhf_msg_len_must_decimal);
             return;
         }*/
         if ((strData.length()) % 4 != 0) {
             UIHelper.ToastMessage(mContext,
                     R.string.uhf_msg_write_must_len4x);
             return;
         } else if (!mContext.vailHexInput(strData)) {
             UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
             return;
         }
         boolean r;
         boolean result=false;
         String Bank="";
         /*if(SpinnerBank_Write.getSelectedItemPosition()==0){
             Log.e("SpinnerBank", SpinnerBank_Write.getSelectedItem().toString());
                Bank="UII";
             }else{
             Bank=SpinnerBank_Write.getSelectedItem().toString();
         }*/
         if(cb_QT_W.isChecked()){
             Log.e("Rtrue", "Pase por true cb_QT_w");
             r= mContext.mReader.writeDataWithQT_Ex(strPWD,
                     BankEnum.valueOf("UII"),
                     2,
                     4,
                     strData); //strData
         }else{
             Log.e("Rfalse", "Pase por false cb_QT_w strPWD:"+strPWD+"  strPtr:"+2+" cnStr"+4+"  BankEnum:"+BankEnum.valueOf("UII")+"  OtroBankEnum:"+BankEnum.valueOf("UII"));
             r= mContext.mReader.writeData_Ex(strPWD,
                     BankEnum.valueOf("UII"),
                     2,
                     4,
                     strData);
                     //Integer.parseInt(strPtr),
                     ///Integer.valueOf(cntStr), strData);// 返回的UII strData
         }
         if (r) {
             result=true;
             UIHelper.ToastMessage(mContext, getString(R.string.uhf_msg_write_succ));
         } else {
             UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);
         }
     if(!result){
         mContext.playSound(2);
     }else{
         mContext.playSound(1);
     }
 }


    private void getCompany(){
        DeviceRepository deviceRepository=new DeviceRepository(mContext);
        CompanyRepository companyRepository=new CompanyRepository(mContext);
        SharedPreferences preferenceCodeActive= getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        int companyId=0;

        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code.toLowerCase();
            companyId=companyRepository.getCompanieId(code_result);
            codeRfidCompany=deviceRepository.FindRFIDCode(companyId);
        }
    }

    private void readTag() {
        if (inventoryFlag == 1) {// 单标签循环  .startInventoryTag((byte) 0, (byte) 0))
            if (mContext.mReader.startInventoryTag(0, 0)) {
                Log.e("Pas1", "pasare por aca1");
                loopFlag = true;
                //setViewEnabled(false);
                //ACA SET IMAGEN
               new TagThread().run();
            } else {
                Log.e("Pas2", "pasare por aca2");
                mContext.mReader.stopInventory();
                Log.e("UHFReadTagFragment", "Open Failure");
                //UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_open_fail);
                //					mContext.playSound(2);
            }
        }
        /*} else {
            stopInventory();
            UIHelper.ToastMessage(mContext, "Lectura detenida", 3);
        }*/
    }

    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            String[] res = null;
            if(loopFlag) {
                res = mContext.mReader.readTagFromBuffer();
                if (res != null) {
                    String EPCCaptura=mContext.mReader.convertUiiToEPC(res[1]);
                    Log.e("EPC","EPC:"+ EPCCaptura);
                    Message msg = handler.obtainMessage();
                    // Log.e("EPC","EPC:"+ mContext.mReader.convertUiiToEPC(res[1])+"@"+res[2]+"@"+strResult);
                    msg.obj =EPCCaptura; //+ "EPC:"
                    handler.sendMessage(msg);
                    loopFlag=false;
                }
            }
        }
    }

    @Override
    public void myOnKeyDwon() {
        Log.e("switchTypeAway", ""+switchTypeAway);
        switch(switchTypeAway){
            case 2:
                readBarcode();
                break;
            case 3:
                write();
               break;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        //stopInventory();


    }
    @Override
    public void onPause() {
        super.onPause();
        //stopInventory();
    }


}
