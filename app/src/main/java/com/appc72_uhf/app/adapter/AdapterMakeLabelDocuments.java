package com.appc72_uhf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
        TextView tv_document_id, tv_location_document, tv_document_name;
        ImageButton item_more_details_documents;
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

            holder.tv_document_id=(TextView) convertView.findViewById(R.id.tv_document_id);
            holder.tv_document_name=(TextView) convertView.findViewById(R.id.tv_document_name);
            holder.tv_location_document=(TextView) convertView.findViewById(R.id.tv_location_document);
            holder.item_more_details_documents=(ImageButton) convertView.findViewById(R.id.item_more_details_documents);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition = position;
        holder.tv_document_name.setText(datamodelDocumentsMakeLabel.getDocumentName().toUpperCase());
        holder.tv_document_id.setText("N° Doc: "+datamodelDocumentsMakeLabel.getDocumentId());
        holder.tv_location_document.setText(" "+datamodelDocumentsMakeLabel.getLocationOriginName().toUpperCase());
        holder.tv_document_name.setTag(position);
        holder.tv_document_id.setTag(position);
        holder.tv_location_document.setTag(position);
        //holder.item_more_details_documents.setBackgroundResource(R.color.color_primary);
        convertView.setBackgroundResource(R.color.lightblue);
        return convertView;
    }
}

