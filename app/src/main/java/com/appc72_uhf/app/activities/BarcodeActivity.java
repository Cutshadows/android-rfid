package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterMakeLabelList;
import com.appc72_uhf.app.entities.DataModelVirtualDocument;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.MakeLabelRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BarcodeActivity extends AppCompatActivity {
    TextView et_searchBarcode;
    Button btn_searchProduct;
    ListView lv_detail_document;
    int documentId;
    private int codeCompany;
    private String code_enterprise;
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
        et_searchBarcode=(TextView) findViewById(R.id.et_searchBarcode);
        btn_searchProduct=(Button) findViewById(R.id.btn_searchProduct);
        lv_detail_document=(ListView) findViewById(R.id.lv_detail_document);
        dataVirtualDocuments=new ArrayList<DataModelVirtualDocument>();

        code_enterprise=getCompany();
        documentId=getIntent().getIntExtra("DocumentId", 0);

        getData();

        adapterMakeLabelList=new AdapterMakeLabelList(mContext, dataVirtualDocuments);
        lv_detail_document.setAdapter(adapterMakeLabelList);
        adapterMakeLabelList.notifyDataSetChanged();

    }
    private void getData() {
        MakeLabelRepository makeLabelRepository = new MakeLabelRepository(this.mContext);
        ArrayList<String> docs = makeLabelRepository.ViewVirtualTagsEnabled(documentId);
        try {
            ArrayList<String> dataInter=new ArrayList<>();
            String[] ddv = new String[0];
            Log.e("docs", ""+docs.toString());
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
                Log.e("interparameter", ""+interParameter[0]+"@"+interParameter[1]+"@"+interParameter[2]);
                int codigoInterno=Integer.parseInt(interParameter[0]);
                String def1=interParameter[1];
                String def2=interParameter[2];
                int counterProductMaster=makeLabelRepository.ViewVirtualCount(codigoInterno, Integer.parseInt(ddv[3]));
                Log.e("counterProductMaster", ""+counterProductMaster);
                dataVirtualDocuments.add(new DataModelVirtualDocument(codigoInterno, Integer.parseInt(ddv[3]), counterProductMaster, def1, def2));
            }
        }catch (JSONException jse){
            Log.e("JSONException", ""+jse.getLocalizedMessage());
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
