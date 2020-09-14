package com.appc72_uhf.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.fragment.KeyDwonFragment;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.DeviceRepository;

public class VerificateEPCActivity extends KeyDwonFragment {
    TextView EtPreparingMakeLabel;
    private int inventoryFlag = 1;
    Handler handler;
    private boolean loopFlag = false;
    private MainActivity mContext;
    String codeRfidCompany;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.activity_verificate_e_p_c, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readTag();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("onViewCreated", "vista creada");
        initComponent();

    }



    public void initComponent() {
        Log.e("onActivityCreated", "actividad creada");
        mContext = (MainActivity) getActivity();
        EtPreparingMakeLabel=(TextView) getView().findViewById(R.id.EtPreparingMakeLabel);
        inventoryFlag = 1;
        getCompany();



        //EtData_Barcode.requestFocus();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String EPCCaptura = msg.obj + "";
                Log.e("EPCCaptura", EPCCaptura);
                validationEPC(EPCCaptura);
                mContext.playSound(1);
            }
        };
    }


    private void validationEPC(String EPCCaptura){
        if (!TextUtils.isEmpty(EPCCaptura)) {
            String epcTransform = EPCCaptura.substring(0, 4);
                    //switchTypeAway = 2;
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
                    builder.setTitle(R.string.ap_dialog_makelabel_coderfid);
                    String message=(epcTransform.equals(codeRfidCompany))?"La etiqueta, ya esta siendo utilizada":"";
                    builder.setMessage(message);
                    builder.setIcon(R.drawable.button_bg_up);
                   if (epcTransform.equals(codeRfidCompany)) {
                       builder.setNegativeButton(R.string.ap_dialog_cancel_mklabel, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                               Intent goToMain=new Intent(getContext(), BarcodeActivity.class);
                               goToMain.putExtra("DocumentId", mContext.getIntent().getIntExtra("DocumentId", 0));
                               goToMain.putExtra("makeLabelBool", 0);
                               mContext.startActivity(goToMain);
                           }
                       });
                   }else {
                       Intent fragment=new Intent(getContext(), MainActivity.class);
                       fragment.putExtra("ProductMasterId", mContext.getIntent().getIntExtra("ProductMasterId", 0));
                       fragment.putExtra("EntryType", "MakeLabel");
                       fragment.putExtra("makeLabelBool", 2);
                       mContext.startActivity(fragment);
                   }
                    builder.create().show();
        }else{
            readTag();
        }
    }

    private void readTag() {
        if (inventoryFlag == 1) {// 单标签循环  .startInventoryTag((byte) 0, (byte) 0))
            if (mContext.mReader.startInventoryTag(0, 0)) {
                Log.e("Pas1", "pasare por aca1");
                loopFlag = true;
                //setViewEnabled(false);
                //ACA SET IMAGEN
                try {
                    Thread.sleep(2000);
                    new TagThread().start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
    private void stopInventory() {
        if (!loopFlag) {
            //loopFlag = false;
            //mContext.mReader.uhfStopUpdate();
            if (mContext.mReader.stopInventory()) {
                Log.e("UHFReadTagFragment", "se detuvo");
            } else {
                Log.e("UHFReadTagFragment", "Stop fail");
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        stopInventory();


    }
    @Override
    public void onPause() {
        super.onPause();
        stopInventory();
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
}
