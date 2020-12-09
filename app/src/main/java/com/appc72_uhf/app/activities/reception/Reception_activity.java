package com.appc72_uhf.app.activities.reception;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdpaterListReceptionDocuments;
import com.appc72_uhf.app.entities.DataModelReceptionDocuments;
import com.appc72_uhf.app.fragment.KeyDwonFragment;

import java.util.ArrayList;

public class Reception_activity extends KeyDwonFragment {
    private MainActivity mContext;
    ListView lv_data_reception;


    ArrayList<DataModelReceptionDocuments> dataArrayListReception;
    AdpaterListReceptionDocuments adpaterListReceptionDocuments;
    DataModelReceptionDocuments dataModelReceptionDocuments;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_activity);

    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.activity_reception_activity, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }
    private void initComponents(){
        mContext = (MainActivity) getActivity();
        lv_data_reception=(ListView) getView().findViewById(R.id.lv_data_reception);
       // ibtn_reception=(ImageButton) getView().findViewById(R.id.ibtn_reception);


        dataArrayListReception=new ArrayList<DataModelReceptionDocuments>();
        dataArrayListReception.add(new DataModelReceptionDocuments("AD1231232132311231232222", 21, 23, "0", "demo"));
        dataArrayListReception.add(new DataModelReceptionDocuments("AD1231232132311231232222", 21, 23, "0", "demo"));
        dataArrayListReception.add(new DataModelReceptionDocuments("AD1231232132311231232222", 21, 23, "0", "demo"));
        dataArrayListReception.add(new DataModelReceptionDocuments("AD1231232132311231232222", 21, 23, "0", "demo"));
        dataArrayListReception.add(new DataModelReceptionDocuments("AD1231232132311231232222", 21, 23, "0", "demo"));
        adpaterListReceptionDocuments=new AdpaterListReceptionDocuments(mContext, R.layout.simple_list_reception_documents, dataArrayListReception);
        lv_data_reception.setAdapter(adpaterListReceptionDocuments);
        adpaterListReceptionDocuments.notifyDataSetChanged();

        ((BaseAdapter) lv_data_reception.getAdapter()).notifyDataSetChanged();
    }
}
