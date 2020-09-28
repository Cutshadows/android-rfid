package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.tools.UIHelper;

public class CaptureBarcodeActivity extends AppCompatActivity {
    EditText et_barcode, et_barcode_after;
    ImageView imgSecuenceBarcode;
    AnimationDrawable frameAnimation;
    String barcode, barcodeado;
    int ProductMasteId, DocumentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_barcode_activity);
        imgSecuenceBarcode=(ImageView) findViewById(R.id.ImgSecuenceBarcode);
        et_barcode=(EditText) findViewById(R.id.et_barcode);
        imgSecuenceBarcode.setImageResource(0);

        imgSecuenceBarcode.setBackgroundResource(R.drawable.animation);

        frameAnimation=(AnimationDrawable) imgSecuenceBarcode.getBackground();

        frameAnimation.start();

        ProductMasteId=this.getIntent().getIntExtra("ProductMasterId", 0);
        DocumentId=this.getIntent().getIntExtra("DocumentId", 0);

        Log.e("ProductMasteId", ""+ProductMasteId);
        Log.e("DocumentId", ""+DocumentId);


        barcodeado="";
        Log.e("getNumberOfFrames", ""+frameAnimation.getNumberOfFrames());
        barcode=getBarcode();
        if(barcode.length()>0){
            et_barcode.setText(barcode.trim());
            et_barcode.setEnabled(false);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Log.i("tag", "This'll run 300 milliseconds later");
                            exitCapture();
                        }
                    }, 2000);
        }else{
            et_barcode.requestFocus();
            //barcodeado=(et_barcode.getText().toString().length()>7)?et_barcode.getText().toString().substring(0,8):String.format("%8s", et_barcode.getText().toString()).replace(' ', '0');

            et_barcode.addTextChangedListener(new TextChangedListener<EditText>(et_barcode) {
                @Override
                public void beforeTextChanged(CharSequence barcodeEt, int start, int count, int after) {
                    super.beforeTextChanged(barcodeEt, start, count, after);
                    if(barcodeEt.length()>0){
                            barcodeado=et_barcode.getText().toString().trim();
                    }else{
                        if(count==0) {
                            et_barcode.requestFocus();
                        }
                    }
                }
                @Override
                public void onTextChanged(EditText barcodeEt, Editable s) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    et_barcode.setEnabled(false);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    exitCapture();
                                }
                            }, 2000);

                }
            });

        }
    }
    public void exitCapture(){
        UIHelper.ToastMessage(this, "Codigo de barra preparado para etiquetado");
        Intent fragment=new Intent(CaptureBarcodeActivity.this, MainActivity.class);
        if(barcodeado.equals("")){
            fragment.putExtra("Barcode", et_barcode.getText().toString().trim());
        }else{
            fragment.putExtra("Barcode", barcodeado.trim());
        }
        fragment.putExtra("ProductMasterId", ProductMasteId);
        fragment.putExtra("DocumentId", DocumentId);
        fragment.putExtra("EntryType", "MakeLabel");
        fragment.putExtra("makeLabelBool", true);
        this.startActivity(fragment);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefereneceBarcode=this.getSharedPreferences("barcode", Context.MODE_PRIVATE);
        SharedPreferences.Editor barcode_request=prefereneceBarcode.edit();
        barcode_request.clear();
        barcode_request.remove("barcode");
        barcode_request.apply();
    }
    public void onBackPressed() {
        Intent toMainAfterDelete=new Intent(this, MainActivity.class);
        toMainAfterDelete.putExtra("EntryType", "MakeLabel");
        toMainAfterDelete.putExtra("makeLabelBool", false);
        toMainAfterDelete.putExtra("DocumentId", DocumentId);
        super.onBackPressed();
        //codigo adicional
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences prefereneceBarcode=this.getSharedPreferences("barcode", Context.MODE_PRIVATE);
        SharedPreferences.Editor barcode_request=prefereneceBarcode.edit();
        barcode_request.clear();
        barcode_request.remove("barcode");
        barcode_request.apply();
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefereneceBarcode=this.getSharedPreferences("barcode", Context.MODE_PRIVATE);
        SharedPreferences.Editor barcode_request=prefereneceBarcode.edit();
        barcode_request.clear();
        barcode_request.remove("barcode");
        barcode_request.apply();
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefereneceBarcode=this.getSharedPreferences("barcode", Context.MODE_PRIVATE);
        SharedPreferences.Editor barcode_request=prefereneceBarcode.edit();
        barcode_request.clear();
        barcode_request.remove("barcode");
        barcode_request.apply();
    }

    private String getBarcode(){
        SharedPreferences prefereneceBarcode=this.getSharedPreferences("barcode", Context.MODE_PRIVATE);
        String barcode_request=prefereneceBarcode.getString("barcode", "");
        String barcode_result="";
        if(barcode_request.isEmpty()){
            Log.e("getBarcode", " Error no hay barcode "+barcode_request);
        }else{
            barcode_result=barcode_request;
        }
        return barcode_result;
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
}

