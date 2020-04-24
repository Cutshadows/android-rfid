package com.appc72_uhf.app.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.R;
import com.rscja.deviceapi.RFIDWithUHF;

public class HandHeld_activity extends AppCompatActivity implements View.OnClickListener {

    public RFIDWithUHF mReader;
    private boolean loopFlag=false;
    private Button btn_readRfid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_held_activity);

        initUHF();

        btn_readRfid=(Button) findViewById(R.id.btn_readRFID);
        btn_readRfid.setOnClickListener(this);
    }

    public void initUHF() {
        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {
            Log.i("error", "error al iniciar lector "+ex.getMessage());
            //toastMessage(ex.getMessage());
            return;
        }

        if (mReader != null) {
            new InitTask().execute();
            //mReader.init();
        }
    }

    public void read() {
        if(mReader.startInventoryTag(0,0) ){
            loopFlag=true;
           new TagThread().start();
        }else {
            mReader.stopInventory();
        }

    }

    @Override
    public void onClick(View v) {
        read();
    }


    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            String[] res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data","EPC:"+res[1]+"|"+strResult);
                    //Log.i("data","EPC:"+mReader.convertUiiToEPC(res[1])+" | "+strResult+" @ "+res[2]);
                    /*Message msg = handler.obtainMessage();
                    msg.obj = strResult + "EPC:" + mContext.mReader.convertUiiToEPC(res[1]) + "@" + res[2];

                    handler.sendMessage(msg);*/
                }
            }
        }
    }
    @Override
    protected void onDestroy() {

        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }
    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (!result) {
                //Toast.makeText(this, "fallo al iniciar", Toast.LENGTH_LONG).show();
                Log.i("INIT FAIL", "FAIL TO TRY INIT");
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
    }
}
