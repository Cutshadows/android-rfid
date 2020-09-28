package com.appc72_uhf.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.entities.DatamodelDocumentsMakeLabel;
import com.appc72_uhf.app.repositories.MakeLabelRepository;
import com.appc72_uhf.app.tools.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterMakeLabelDocuments extends ArrayAdapter<DatamodelDocumentsMakeLabel> implements View.OnClickListener {
    Context mContext;
    ArrayList<DatamodelDocumentsMakeLabel> datadocuments;
    ProgressDialog mypDialog;


    public AdapterMakeLabelDocuments(@NonNull Context mContext, ArrayList<DatamodelDocumentsMakeLabel> datadocuments) {
        super(mContext, R.layout.simple_list_documents_server, datadocuments);
        this.mContext = mContext;
        this.datadocuments = datadocuments;
    }


    private class ViewHolder{
        TextView tv_document_id, tv_location_document, tv_document_name, tv_resumen_document;
        //ImageButton item_more_details_documents;
        CheckBox chbx_download_documents;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object=getItem(position);

        final DatamodelDocumentsMakeLabel dModelMakeLabel=(DatamodelDocumentsMakeLabel)object;
        CheckBox chbx_download_documents=(CheckBox) v.findViewById(R.id.chbx_download_documents);
        MakeLabelRepository makeLabelRepository=new MakeLabelRepository(getContext());



        switch (v.getId()){
            case R.id.chbx_download_documents:
                if(chbx_download_documents.isChecked()){
                    mypDialog = new ProgressDialog(getContext());
                    mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mypDialog.setMessage("Habilitando etiquetado documento :"+dModelMakeLabel.getDocumentId()+"...");
                    mypDialog.setCanceledOnTouchOutside(false);
                    mypDialog.show();
                        boolean resultDocumentInsert=makeLabelRepository.InsertDocuments(
                                dModelMakeLabel.getDocumentName(),
                                dModelMakeLabel.getDocumentId(),
                                dModelMakeLabel.getDeviceId(),
                                dModelMakeLabel.getFechaAsignacion(),
                                dModelMakeLabel.isAllowLabeling(),
                                dModelMakeLabel.getAssociatedDocumentId(),
                                dModelMakeLabel.getAssociatedDocNumber(),
                                dModelMakeLabel.getLocationOriginName(),
                                dModelMakeLabel.getDestinationLocationId(),
                                dModelMakeLabel.getClient(),
                                dModelMakeLabel.getStatus(),
                                dModelMakeLabel.isHasVirtualItems(),
                                1,
                                dModelMakeLabel.getCompanyId()
                        );

                    if(resultDocumentInsert){
                        boolean inserListTag=false;
                        for(int indexVirtual=0; indexVirtual < dModelMakeLabel.getDocumentDetailsVirtual().length(); indexVirtual++){
                            try{
                                JSONObject jsonObject=dModelMakeLabel.getDocumentDetailsVirtual().getJSONObject(indexVirtual);
                                Log.e("jsonObject", jsonObject.toString());
                                int id=jsonObject.getInt("Id");
                                JSONObject productMasterArray=jsonObject.getJSONObject("ProductMaster");
                                String productVirtualId=jsonObject.getString("ProductVirtualId");
                                int documentId=jsonObject.getInt("DocumentId");
                                String codeBar=jsonObject.getString("CodeBar");
                                inserListTag=makeLabelRepository.insertVirtualTag(
                                        id,
                                        productMasterArray.toString(),
                                        productVirtualId,
                                        documentId,
                                        codeBar
                                );
                            }catch (JSONException jsEx){
                                Log.e("jsEx", ""+jsEx.getLocalizedMessage());
                                jsEx.printStackTrace();
                            }
                        }
                        if(inserListTag){
                            UIHelper.ToastMessage(getContext(), "Se habilito los documentos para etiquetado.", 3);
                        }
                    }
                    mypDialog.dismiss();
                }else{
                    UIHelper.ToastMessage(mContext, "Se elimina los documentos para makelabel "+dModelMakeLabel.getDocumentId(), 4);
                    boolean deleteDocument=makeLabelRepository.deleteDocument(dModelMakeLabel.getDocumentId());
                    if(deleteDocument){
                      UIHelper.ToastMessage(getContext(), "El inventario '"+dModelMakeLabel.getDocumentName()+"' esta deshabilitado!!", 5);
                    }
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
            //holder.item_more_details_documents=(ImageButton) convertView.findViewById(R.id.item_more_details_documents);
            holder.tv_resumen_document=(TextView) convertView.findViewById(R.id.tv_resumen_document);
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
        holder.tv_resumen_document.setText(""+datamodelDocumentsMakeLabel.getDocumentDetailsVirtual().length());
        holder.chbx_download_documents.setOnClickListener(this);
        holder.tv_document_name.setTag(position);
        holder.tv_document_id.setTag(position);
        holder.tv_location_document.setTag(position);
        holder.tv_resumen_document.setTag(position);
        //holder.item_more_details_documents.setBackgroundResource(R.color.color_primary);
        convertView.setBackgroundResource(R.color.lightblue);
        if(datamodelDocumentsMakeLabel.getIsSelected()==1){
            holder.chbx_download_documents.setChecked(true);
        }else if(datamodelDocumentsMakeLabel.getIsSelected()==0){
            holder.chbx_download_documents.setChecked(false);
        }
        holder.chbx_download_documents.setTag(position);
        /*Object object=getItem(position);
        final DatamodelDocumentsMakeLabel dModelMakeLabel=(DatamodelDocumentsMakeLabel)object;
        for(int indexVirtual=0; indexVirtual < dModelMakeLabel.getDocumentDetailsVirtual().length(); indexVirtual++){
            try{
                JSONObject jsonObject=dModelMakeLabel.getDocumentDetailsVirtual().getJSONObject(indexVirtual);
                Log.e("jsonObject", jsonObject.toString());

                int id=jsonObject.getInt("Id");
                Log.e("Id", "Id: "+id);

                JSONObject productMasterArray=jsonObject.getJSONObject("ProductMaster");
                Log.e("ProductMasterArray", ""+productMasterArray.toString());

                String productVirtualId=jsonObject.getString("ProductVirtualId");
                Log.e("productVirtualId", "productVirtualId: "+productVirtualId);

                int documentId=jsonObject.getInt("DocumentId");
                Log.e("documentId", "documentId: "+documentId);

                String codeBar=jsonObject.getString("CodeBar");
                Log.e("codeBar", "codeBar: "+codeBar);


            }catch (JSONException jsEx){
                Log.e("jsEx", ""+jsEx.getLocalizedMessage());
                jsEx.printStackTrace();
            }
        }*/


        return convertView;
    }
}

