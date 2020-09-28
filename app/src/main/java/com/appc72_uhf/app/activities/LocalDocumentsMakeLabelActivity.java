package com.appc72_uhf.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterDocumentsListMakeLabel;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.fragment.KeyDwonFragment;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.MakeLabelRepository;

import java.util.ArrayList;

public class LocalDocumentsMakeLabelActivity extends KeyDwonFragment implements View.OnClickListener{
    private ListView lv_local_documents;
    private ImageButton btnDocumentMakeLabel;
    private MainActivity mContext;
    private int codeCompany;
    private String code_enterprise;
    private ArrayList<DatamodelDocumentsMakeLabel> docArraylist;
    private AdapterDocumentsListMakeLabel adapterDocumentsListMakeLabel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.layout_local_documents_make_label_activity, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponent();

    }
    @Override
    public void onResume() {
        Log.e("onResume", "ON RESUME 1 INVENTORY LIST");
        super.onResume();
        docArraylist.clear();
        getData();
        adapterDocumentsListMakeLabel.notifyDataSetChanged();
    }
    private void initComponent(){
        mContext = (MainActivity) getActivity();
        lv_local_documents=(ListView) getView().findViewById(R.id.lv_local_documents);
        btnDocumentMakeLabel=(ImageButton)getView().findViewById(R.id.btnDocumentMakeLabel);
        code_enterprise=getCompany();
        btnDocumentMakeLabel.setOnClickListener(this);
        docArraylist=new ArrayList<DatamodelDocumentsMakeLabel>();

        getData();

        adapterDocumentsListMakeLabel=new AdapterDocumentsListMakeLabel(getContext(), docArraylist);
        lv_local_documents.setAdapter(adapterDocumentsListMakeLabel);
        adapterDocumentsListMakeLabel.notifyDataSetChanged();

    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDocumentMakeLabel:
                Intent goToDocuments=new Intent(getContext(), Make_label_documents_activity.class);
                startActivity(goToDocuments);
                break;
        }

    }
    private void getData() {
        MakeLabelRepository makeLabelRepository = new MakeLabelRepository(this.mContext);
        ArrayList<String> docs = makeLabelRepository.ViewDocumentsMakeLabel(codeCompany);
        Log.e("documents HH", docs.toString());
        for(int index=0; index<=docs.size()-1;index++){
            String recip=docs.get(index);
            Log.e("HasVirtualItems", recip);
            String[] strs = recip.split("@");
            int countTagEnabled=makeLabelRepository.countTagsVirtualEnabled(Integer.parseInt(strs[1]));
            docArraylist.add(new DatamodelDocumentsMakeLabel(strs[0], strs[5], Integer.parseInt(strs[1]), countTagEnabled));
        }
    }

    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(mContext);
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
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
