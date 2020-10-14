package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterMakeLabelList;
import com.appc72_uhf.app.entities.DataModelVirtualDocument;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.MakeLabelRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BarcodeActivity  extends AppCompatActivity {
    EditText et_searchBarcode;
    Button btn_searchProduct;
    ListView lv_detail_document;
    int documentId;
    private int codeCompany;
    private String code_enterprise, code_bar;
    Context mContext;

    DataModelVirtualDocument dvdVirtual;
    ArrayList<DataModelVirtualDocument> dataVirtualDocuments;
    AdapterMakeLabelList adapterMakeLabelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_activity);
        initComponent();

    }
    public void initComponent(){

        mContext = BarcodeActivity.this;
        et_searchBarcode=(EditText) findViewById(R.id.et_searchBarcode);

        btn_searchProduct=(Button) findViewById(R.id.btn_searchProduct);
        lv_detail_document=(ListView) findViewById(R.id.lv_detail_document);
        dataVirtualDocuments=new ArrayList<DataModelVirtualDocument>();

        code_enterprise=getCompany();
        SharedPreferences prefereneceBarcode=this.getSharedPreferences("barcode", Context.MODE_PRIVATE);
        SharedPreferences.Editor barcode_request=prefereneceBarcode.edit();
        barcode_request.clear();
        barcode_request.apply();
        documentId=getIntent().getIntExtra("DocumentId", 0);

        getData();

        adapterMakeLabelList=new AdapterMakeLabelList(mContext, dataVirtualDocuments);
        lv_detail_document.setAdapter(adapterMakeLabelList);
        adapterMakeLabelList.notifyDataSetChanged();
        et_searchBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence barcodeSecuence, int start, int contador, int after) {
               // Log.e("beforeTextChanged", "Charsecuence"+barcodeSecuence+" Inicio"+start+" Contador"+contador+" Despues"+after);

            }
            @Override
            public void onTextChanged(CharSequence barcodeSecuence, int start, int before, int count) {
                //Log.e("beforeTextChanged", "Charsecuence"+barcodeSecuence+" Inicio"+start+" Contador"+count+" Despues"+before);
                if(count>0){
                   // Log.e("et_searchBarcode", "se guardo como localstorage"+et_searchBarcode.getText().toString());
                    ReadtBarCode(et_searchBarcode.getText().toString().trim());
                }else{
                    et_searchBarcode.clearFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /*et_searchBarcode.addTextChangedListener(new TextChangedListener<EditText> (et_searchBarcode){
            @Override
            public void beforeTextChanged(CharSequence barcodeSecuence, int start, int contador, int after) {
                super.beforeTextChanged(barcodeSecuence, start, contador, after);
                Log.e("beforeTextChanged", "Charsecuence"+barcodeSecuence+" Inicio"+start+" Contador"+contador+" Despues"+after);
                if(after>0){
                    Log.e("et_searchBarcode", "se guardo como localstorage"+et_searchBarcode.getText().toString().trim());
                        ReadtBarCode(et_searchBarcode.getText().toString().trim());
                }else{
                    et_searchBarcode.clearFocus();
                }

            }
            @Override
            public void onTextChanged(EditText target, Editable s) {

            }
        });*/

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
    private void getData() {
        MakeLabelRepository makeLabelRepository = new MakeLabelRepository(this.mContext);
        ArrayList<String> docs = makeLabelRepository.ViewVirtualTagsEnabled(documentId);
        try {
            ArrayList<String> dataInter=new ArrayList<>();
            String[] ddv = new String[0];
            for(int index=0; index<docs.size();index++){
                String recip=docs.get(index);
                ddv = recip.split("@");
                JSONObject jsonObject= new JSONObject(ddv[0]);
                for(int indexLabel=0; indexLabel<jsonObject.length(); indexLabel++){
                        int productMasterId=jsonObject.getInt("ProductMasterId");
                        String def1=jsonObject.getString("Def1");
                        String def2=jsonObject.getString("Def2");
                        String columndef1=jsonObject.getString("ColumDef1");
                        String columndef2=jsonObject.getString("ColumDef2");
                        boolean repite=isReply(dataInter, productMasterId);
                        if(!repite){
                             dataInter.add(productMasterId+"@"+def2+"@"+def1);
                        }

                }
            }

            for(int indexEntry=0; indexEntry<dataInter.size(); indexEntry++){
                String[] interParameter=dataInter.get(indexEntry).split("@");
                //Log.e("interparameter", ""+interParameter[0]+"@"+interParameter[1]+"@"+interParameter[2]);
                int codigoInterno=Integer.parseInt(interParameter[0]);
                String def1=interParameter[1];
                String def2=interParameter[2];
                int counterProductMaster=makeLabelRepository.ViewVirtualCount(codigoInterno, Integer.parseInt(ddv[3]));
                //Log.e("counterProductMaster", ""+counterProductMaster);
                dataVirtualDocuments.add(new DataModelVirtualDocument(codigoInterno, Integer.parseInt(ddv[3]), counterProductMaster, def1, def2));
            }
        }catch (JSONException jse){
            Log.i("JSONException", ""+jse.getLocalizedMessage());
        }
    }
    public void ReadtBarCode(String bar_code){
        Log.e("bar_code", "se guardo como localstorage"+bar_code);
            SharedPreferences savePreferencesBarcode=getSharedPreferences("barcode", Context.MODE_PRIVATE);
            String barcode_request=savePreferencesBarcode.getString("barcode", "");
            if(barcode_request.isEmpty()){
                SharedPreferences.Editor obj_edite_barcode=savePreferencesBarcode.edit();
                obj_edite_barcode.putString("barcode", bar_code);
                obj_edite_barcode.apply();
                Log.e("exito", "se guardo como localstorage");
            }else {
                Log.e("fail", "tiene un barcode asignado");
            }

    }
    public static boolean isReply(ArrayList<String> array, int valor) {
        boolean repetido = false;
        for (int i = 0; i < array.size() && !repetido; i++) {
            if (Integer.parseInt(array.get(i).split("@")[0])==valor) {
                repetido = true;
            }
        }
        return repetido;
    }

   /* public class Sorting {
        public void main (ArrayList<Integer> args) {
            Log.e("args", ""+args.toString());
            ArrayList<Integer> ar = new ArrayList<>(args);
            Collections.sort(ar);
            int contador=0;
            int aux=ar.get(0);
            for (int i = 0; i < ar.size(); i++) {
                if(aux == ar.get(i)){
                    contador++;
                } else {
                    Log.e("contador",contador + ",");
                    contador=1;
                    aux=ar.get(i);
                }
            }
            Log.e("contador2", ""+contador );
        }
    }*/

    public void onBackPressed() {
        Intent toMainAfterDelete=new Intent(this, MainActivity.class);
        toMainAfterDelete.putExtra("EntryType", "MakeLabel");
        toMainAfterDelete.putExtra("makeLabelBool", false);
        super.onBackPressed();
        //codigo adicional
    }

    private String getCompany(){


        CompanyRepository companyRepository=new CompanyRepository(mContext);
        SharedPreferences preferenceCodeActive=BarcodeActivity.this.getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        int companyId;
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code.toLowerCase();
            companyId=companyRepository.getCompanieId(code_result);
            codeCompany=companyId;
        }
        return code_result;
    }

}
