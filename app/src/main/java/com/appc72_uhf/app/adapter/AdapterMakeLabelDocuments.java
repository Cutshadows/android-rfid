package com.appc72_uhf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;

import java.util.ArrayList;

public class AdapterMakeLabelDocuments extends ArrayAdapter<DatamodelDocumentsMakeLabel> {
    Context mContext;
    ArrayList<DatamodelDocumentsMakeLabel> datadocuments;

    public AdapterMakeLabelDocuments(@NonNull Context mContext, ArrayList<DatamodelDocumentsMakeLabel> datadocuments) {
        super(mContext, R.layout.simple_list_documents_server, datadocuments);
        this.mContext = mContext;
        this.datadocuments = datadocuments;
    }
    private class ViewHolder{
        TextView tv_name_document;
    }
    private int lastPosition= -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        DatamodelDocumentsMakeLabel datamodelDocumentsMakeLabel=getItem(position);

        ViewHolder holder;
        final View result;
        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_documents_server, parent, false);

            holder.tv_name_document=(TextView) convertView.findViewById(R.id.tv_name_document);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition = position;
        holder.tv_name_document.setText("Aqui Documentos "+datamodelDocumentsMakeLabel.getDocumentId());
        holder.tv_name_document.setTag(position);
        return convertView;
    }
}

