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
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.MakeLabelRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class BarcodeActivity extends AppCompatActivity {
    TextView et_searchBarcode;
    Button btn_searchProduct;
    ListView lv_detail_document;
    int documentId;
    private int codeCompany;
    private String code_enterprise;
    Context mContext;

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

        code_enterprise=getCompany();
        documentId=getIntent().getIntExtra("DocumentId", 0);

        UIHelper.ToastMessage(BarcodeActivity.this, "Document Id: "+documentId, 3);
    }
    private void getData() {
        MakeLabelRepository makeLabelRepository = new MakeLabelRepository(this.mContext);
        Log.e("CompanyId", ""+codeCompany);
        ArrayList<String> docs = makeLabelRepository.ViewDocumentsMakeLabel(codeCompany);
        Log.e("documents HH", docs.toString());
        for(int index=0; index<=docs.size()-1;index++){
            String recip=docs.get(index);
            Log.e("HasVirtualItems", recip);
            String[] strs = recip.split("@");
            Log.e("DocumentName[0]", strs[0]);
            Log.e("DocumentId[1]", strs[1]);
            Log.e("HasVirtualItems[2]", strs[2]);
            Log.e("Status[3]", strs[3]);
            Log.e("isSelected[4]", strs[4]);
            Log.e("LocationName[5]", strs[5]);
            Log.e("counterVirtualDoc[6]", strs[6]);
        }
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
