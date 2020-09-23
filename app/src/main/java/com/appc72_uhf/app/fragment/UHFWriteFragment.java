package com.appc72_uhf.app.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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
    private boolean loopFlag = true;
    private MainActivity mContext;
    Handler handler;
    private EditText EtPtr_Write;
    private EditText EtLen_Write;
    private EditText EtData_Write;
    private EditText EtAccessPwd_Write;

    private Button BtWrite;

    private CheckBox cb_QT_W;
    private String codeRfidCompany, Barcode;
    private int ProductMasterId, DocumentId, switchTypeAway;
    private String EntryType;
    private boolean makeLabelBool;
    private static final long TICKS_AT_EPOCH = 621355968000000000L;
    private static final long TICKS_PER_MILLISECOND = 10000;
    public static final int ILLUM_AIAM_OFF=0;
    private int INVENTORY_FLAG = 1;
    private String EPCCaptura;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_write_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        EtPtr_Write = (EditText) getView().findViewById(R.id.EtPtr_Write);
        EtLen_Write = (EditText) getView().findViewById(R.id.EtLen_Write);
        EtData_Write = (EditText) getView().findViewById(R.id.EtData_Write);
        EtAccessPwd_Write = (EditText) getView().findViewById(R.id.EtAccessPwd_Write);
        BtWrite = (Button) getView().findViewById(R.id.BtWrite);

        cb_QT_W= (CheckBox) getView().findViewById(R.id.cb_QT_W);
        BtWrite.setOnClickListener(this);
        ProductMasterId= mContext.getIntent().getIntExtra("ProductMasterId", 0);
        DocumentId=mContext.getIntent().getIntExtra("DocumentId", 0);
        Barcode=mContext.getIntent().getStringExtra("Barcode");
        EntryType=mContext.getIntent().getStringExtra("EntryType");
        makeLabelBool=mContext.getIntent().getBooleanExtra("makeLabelBool", false);

        Log.e("ProDuctMasterId", ""+ ProductMasterId);
        Log.e("codeRfidCompany", ""+ codeRfidCompany);
        Log.e("DocumentId", ""+ DocumentId);
        Log.e("Barcode", ""+ Barcode);
        Log.e("EntryType", ""+ EntryType);
        Log.e("makeLabelBool", ""+ makeLabelBool);
        getCompany();
        generateEPC();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.BtWrite:
                write();
                break;
       }
    }
    private void generateEPC() {
        /**
         * READ BARCODE
         * */
        String barcodeText=Barcode;
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
        String ProductMasterRefactor = String.format("%8s", ProductMasterId).replace(' ', '0');
        String barcodeado=(barcodeText.length()>7)?barcodeText.substring(0, 8):String.format("%8s", barcodeText).replace(' ', '0');
        String rfid=codeRfidCompany+""+timstamp+""+barcodeado+""+ProductMasterRefactor;


        Log.e("ts", ""+ts);
        Log.e("Timestamp", ""+timstamp);
        Log.e("barcodeText", ""+barcodeado);
        Log.e("ProductMasterId", ""+ProductMasterId);

        Log.e("EPC", rfid+" el tamano es:"+rfid.length());

        EtData_Write.setText(rfid);
        switchTypeAway=1;
        //EtData_Barcode.requestFocus();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                EPCCaptura = msg.obj + "";
                //String[] strs = result.split("@");
                Log.e("EPCCaptura", EPCCaptura);
                Thread validateEPCThread= new Thread(new validationEPCThread());
                validateEPCThread.start();
                //validateEPC(EPCCaptura);
                mContext.playSound(1);
            }
        };
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
    private void validateEPC(){

        Log.e("EPC", "EPC VALIDATE"+codeRfidCompany);
        String firstFourdWordsEPC=EPCCaptura.substring(0, 4);
        Log.e("firstFourdWordsEPC", "firstFourdWordsEPC VALIDATE"+firstFourdWordsEPC);

        boolean validation= codeRfidCompany.equals(firstFourdWordsEPC);
        if(validation){
            Log.e("EQUALS", "Son iguales por lo tanto no etiqueta");
            mContext.playSound(1);
        }else {
            Log.e("EQUALS", "No son iguales, se etiqueta");
            write();
            mContext.playSound(1);
        }
    }



    class TagThread extends Thread {
        public void run() {
            String[] res=null;
            if(loopFlag) {
                res = mContext.mReader.readTagFromBuffer();
                if (res != null) {
                    String EPCCapture=mContext.mReader.convertUiiToEPC(res[1]);
                    Log.e("EPC","EPC:"+ EPCCapture);
                    Message msg = handler.obtainMessage();
                    // Log.e("EPC","EPC:"+ mContext.mReader.convertUiiToEPC(res[1])+"@"+res[2]+"@"+strResult);
                    msg.obj =EPCCapture; //+ "EPC:"
                    handler.sendMessage(msg);
                    mContext.mReader.stopInventory();
                    if(mContext.mReader.stopInventory()){
                        Log.i("Stop Inventory", "handheld stop inventory call with class");
                    }
                }
            }
        }
    }



    private class validationEPCThread implements Runnable{
        @Override
        public void run() {
           validateEPC();
        }
    }

    private void readTag() {
        Thread tagtread=new TagThread();
        if (INVENTORY_FLAG == 1) {// 单标签循环  .startInventoryTag((byte) 0, (byte) 0))
            if (mContext.mReader.startInventoryTag(0, 0)) {
                Log.e("Pas1", "pasare por aca1");
             tagtread.start();
            } else {
                tagtread.destroy();
                Log.e("Pas2", "pasare por aca2");
                mContext.mReader.stopInventory();
                Log.e("UHFReadTagFragment", "Open Failure");
            }
        }
    }

    @Override
    public void myOnKeyDwon() {
        Log.e("switchTypeAway", ""+switchTypeAway);
        switch(switchTypeAway){
            case 1:
                //readBarcode();
                UIHelper.ToastMessage(mContext, "ME ENCUENTRO EN ESTA ETAPA 1");
                readTag();
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

        String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码
        if (StringUtils.isNotEmpty(strPWD)) {
            if (strPWD.length() != 8) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            UIHelper.ToastMessage(mContext,
                                    R.string.uhf_msg_addr_must_len8);
                    }
                });
                return;
            } else if (!mContext.vailHexInput(strPWD)) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            UIHelper.ToastMessage(mContext,
                                    R.string.rfid_mgs_error_nohex);
                    }
                });
                return;
            }
        } else {
            strPWD = "00000000";
        }

        String strData = EtData_Write.getText().toString().trim();// 要写入的内容
        if (StringUtils.isEmpty(strData)) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_write_must_not_null);
                }
            });
            return;
        } else if (!mContext.vailHexInput(strData)) {
            mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
                }
            });
            return;
        }

        // 多字单次
        String cntStr = EtLen_Write.getText().toString().trim();
        if (StringUtils.isEmpty(cntStr)) {
            UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_not_null);
            return;
        } else if (!StringUtility.isDecimal(cntStr)) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIHelper.ToastMessage(mContext,
                         R.string.uhf_msg_len_must_decimal);
                }
            });
            return;
        }

        if ((strData.length()) % 4 != 0) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_must_len4x);
                    }
                });
            return;
        } else if (!mContext.vailHexInput(strData)) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
                }
            });
            return;
        }


        boolean result=false;
        boolean r;
        if(cb_QT_W.isChecked()){
            r= mContext.mReader.writeDataWithQT_Ex(strPWD,
                    BankEnum.valueOf("UII"),
                    2,
                    4,
                    strData);
        }else{
            r= mContext.mReader.writeData_Ex(strPWD,
                    BankEnum.valueOf("UII"),
                    2,
                    4,
                    strData);// 返回的UII strData
        }

        if (r) {
            result=true;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIHelper.ToastMessage(mContext, getString(R.string.uhf_msg_write_succ));
                }
            });

        } else {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);
                }
            });
        }
        if(!result){
            mContext.playSound(2);
        }else{
            mContext.playSound(1);
        }
    }

}
