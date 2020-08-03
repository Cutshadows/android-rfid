package com.appc72_uhf.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.BarcodeActivity;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class AdapterDocumentsListMakeLabel extends ArrayAdapter<DatamodelDocumentsMakeLabel> implements View.OnClickListener{
    Context mContext;
    ArrayList<DatamodelDocumentsMakeLabel> datadocuments;

    public AdapterDocumentsListMakeLabel(@NonNull Context mContext, ArrayList<DatamodelDocumentsMakeLabel> datadocuments) {
        super(mContext, R.layout.simple_list_documents, datadocuments);
        this.mContext = mContext;
        this.datadocuments = datadocuments;
    }

    private class ViewHolder{
        TextView tv_doument_name, tv_location_document, tv_document_id, tv_resumen_document;
        ImageView item_makeLabel, item_delete;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        DatamodelDocumentsMakeLabel datamodelDocumentsMakeLabel=getItem(position);
        switch (v.getId()){
            case R.id.item_makeLabel:
                Intent goToMain=new Intent(getContext(), BarcodeActivity.class);
                goToMain.putExtra("DocumentId", datamodelDocumentsMakeLabel.getDocumentId());
                mContext.startActivity(goToMain);
                break;
            case R.id.item_delete:
                UIHelper.ToastMessage(getContext(), "Estoy en borrar", 6);
                break;
        }
    }

    private int lastPosition=-1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DatamodelDocumentsMakeLabel datamodelDocuments=getItem(position);
        ViewHolder holder;
        final View result;
        if (convertView == null) {
            holder=new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_documents, parent, false);
            holder.tv_doument_name=(TextView) convertView.findViewById(R.id.tv_doument_name);
            holder.tv_location_document=(TextView) convertView.findViewById(R.id.tv_location_document);
            holder.tv_document_id=(TextView) convertView.findViewById(R.id.tv_document_id);
            holder.tv_resumen_document=(TextView) convertView.findViewById(R.id.tv_resumen_document);
            holder.item_makeLabel=(ImageView) convertView.findViewById(R.id.item_makeLabel);
            holder.item_delete=(ImageView) convertView.findViewById(R.id.item_delete);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition=position;
        holder.tv_doument_name.setText(datamodelDocuments.getDocumentName().toUpperCase());
        holder.tv_location_document.setText(" "+datamodelDocuments.getLocationOriginName());
        holder.tv_document_id.setText("N° Doc: "+datamodelDocuments.getDocumentId());
        holder.tv_resumen_document.setText(""+datamodelDocuments.getCounterEnabled());
        holder.item_makeLabel.setOnClickListener(this);
        holder.item_delete.setOnClickListener(this);
        //holder.tv_doument_name.setText("DOCUMENTO HH");
        holder.tv_doument_name.setTag(position);
        holder.tv_location_document.setTag(position);
        holder.tv_document_id.setTag(position);
        holder.tv_resumen_document.setTag(position);
        holder.item_makeLabel.setTag(position);
        holder.item_delete.setTag(position);
        return convertView;
    }
}
