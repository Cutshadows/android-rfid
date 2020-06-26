package com.appc72_uhf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.entities.DataModelVirtualDocument;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.repositories.MakeLabelRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class AdapterMakeLabelDocuments extends ArrayAdapter<DatamodelDocumentsMakeLabel> implements View.OnClickListener {
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
        CheckBox chbx_download_documents;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object=getItem(position);

        final DatamodelDocumentsMakeLabel dModelMakeLabel=(DatamodelDocumentsMakeLabel)object;
       // final DataModelVirtualDocument dModVirtualDocument=(DataModelVirtualDocument)object;
        CheckBox chbx_download_documents=(CheckBox) v.findViewById(R.id.chbx_download_documents);

        switch (v.getId()){
            case R.id.chbx_download_documents:
                if(chbx_download_documents.isChecked()){
                    MakeLabelRepository makeLabelRepository=new MakeLabelRepository(getContext());
                    boolean resultDocumentInsert=makeLabelRepository.InsertDocuments(
                            dModelMakeLabel.getDocumentName(),
                            dModelMakeLabel.getDocumentId(),
                            dModelMakeLabel.getDeviceId(),
                            dModelMakeLabel.getFechaAsignacion(),
                            dModelMakeLabel.getAsignadoPor(),
                            dModelMakeLabel.isAllowLabeling(),
                            dModelMakeLabel.getAssociatedDocumentId(),
                            dModelMakeLabel.getAssociatedDocNumber(),
                            dModelMakeLabel.getDocumentTypeId(),
                            dModelMakeLabel.getDescription(),
                            dModelMakeLabel.getCreatedDate(),
                            dModelMakeLabel.getLocationOriginId(),
                            dModelMakeLabel.getLocationOriginName(),
                            dModelMakeLabel.getDestinationLocationId(),
                            dModelMakeLabel.getAux1(),
                            dModelMakeLabel.getAux2(),
                            dModelMakeLabel.getAux3(),
                            dModelMakeLabel.getClient(),
                            dModelMakeLabel.getStatus(),
                            dModelMakeLabel.isHasVirtualItems(),
                            dModelMakeLabel.getReaderId()
                    );

                    if(resultDocumentInsert){
                        for(int indexVirtual=0; indexVirtual<dModelMakeLabel.getDocumentDetailsVirtual().size(); indexVirtual++){
                            DataModelVirtualDocument dModVirtualDocument =dModelMakeLabel.getDocumentDetailsVirtual().get(indexVirtual);


                        }
                    }
                }else{
                    UIHelper.ToastMessage(mContext, "Se elimina los documentos para makelabel "+dModelMakeLabel.getDocumentId(), 4);

                }
                break;
        }

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
            holder.chbx_download_documents=(CheckBox)convertView.findViewById(R.id.chbx_download_documents);
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
        holder.chbx_download_documents.setOnClickListener(this);
        holder.tv_document_name.setTag(position);
        holder.tv_document_id.setTag(position);
        holder.tv_location_document.setTag(position);
        //holder.item_more_details_documents.setBackgroundResource(R.color.color_primary);
        holder.chbx_download_documents.setTag(position);
        convertView.setBackgroundResource(R.color.lightblue);
        return convertView;
    }
}

