package com.appc72_uhf.app.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.appc72_uhf.app.activities.makelabel.CaptureBarcodeActivity;
import com.appc72_uhf.app.entities.DataModelVirtualDocument;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class AdapterMakeLabelList extends ArrayAdapter<DataModelVirtualDocument> implements View.OnClickListener{
    Context mContext;
    ArrayList<DataModelVirtualDocument> productList;

    public AdapterMakeLabelList(@NonNull Context mContext, ArrayList<DataModelVirtualDocument> productList) {
        super(mContext, R.layout.simple_list_data_productmaster_makelabel, productList);
        this.mContext = mContext;
        this.productList = productList;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelVirtualDocument dataModelVirtualDocument= (DataModelVirtualDocument)object;
        switch (v.getId()){
            case R.id.item_make_label:
                UIHelper.ToastMessage(getContext(), "estoy entrando en makelabel");
                Intent fragment=new Intent(getContext(), CaptureBarcodeActivity.class);
                fragment.putExtra("ProductMasterId", dataModelVirtualDocument.getProductMasterId());
                fragment.putExtra("DocumentId", dataModelVirtualDocument.getDocumentId());
                //fragment.putExtra("EntryType", "MakeLabel");
                //fragment.putExtra("makeLabelBool", true);
                mContext.startActivity(fragment);
                break;
        }
    }
    private class ViewHolder{
        TextView tv_productId, tv_document_id, tv_count_productMaster, tv_document_name;
        ImageButton item_make_label;
    }
    private int lastPosition=-1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        DataModelVirtualDocument dataModelVirtualDocument=getItem(position);
        ViewHolder holder;

        final View result;
        if(convertView==null){
            holder=new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(R.layout.simple_list_data_productmaster_makelabel, parent, false);

            holder.tv_productId=(TextView) convertView.findViewById(R.id.tv_productId);
            holder.tv_document_id=(TextView) convertView.findViewById(R.id.tv_document_id);
            holder.tv_document_name=(TextView) convertView.findViewById(R.id.tv_document_name);
            holder.tv_count_productMaster=(TextView) convertView.findViewById(R.id.tv_count_productMaster);
            holder.item_make_label=(ImageButton) convertView.findViewById(R.id.item_make_label);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition=position;
        //String codeBar=(dataModelVirtualDocument.getCodeBar()!=null)?"No code Bar":dataModelVirtualDocument.getCodeBar();
        holder.tv_productId.setText("Product Master: "+dataModelVirtualDocument.getProductMasterId());
        holder.tv_document_id.setText("Documento: "+dataModelVirtualDocument.getDocumentId());
        holder.tv_document_name.setText(dataModelVirtualDocument.getDef2()+"  "+dataModelVirtualDocument.getDef1());
        holder.tv_count_productMaster.setText(""+dataModelVirtualDocument.getCountrMaster());
        holder.item_make_label.setOnClickListener(this);
        holder.item_make_label.setTag(position);
        //holder.tv_master_id.setTag(position);
        return convertView;
    }
}
