package com.appc72_uhf.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.entities.DataModelReceptionDocuments;

import java.util.ArrayList;


public class AdpaterListReceptionDocuments extends ArrayAdapter<DataModelReceptionDocuments> implements View.OnClickListener {

    Context mContext;
    ArrayList<DataModelReceptionDocuments> dataReceptionDocument;
    ProgressDialog myDialog;
    private String android_id;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com/";

    public AdpaterListReceptionDocuments(@NonNull Context context, int resource, ArrayList<DataModelReceptionDocuments> dataReceptionDocument) {
        super(context, resource, dataReceptionDocument);
        this.mContext = context;
        this.dataReceptionDocument = dataReceptionDocument;
    }
    @Override
    public void onClick(View v){

    }
    private class ViewHolder{
        TextView tv_reception_title;
        ImageView item_delete, item_sync;
        ImageButton btn_global_detail;
    }
    private int lastPosition=-1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        DataModelReceptionDocuments dataModelReceptionDocuments=getItem(position);
        ViewHolder holder;
        final View result;
        if(convertView==null){
            holder=new ViewHolder();

            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_reception_documents, parent, false);

            holder.tv_reception_title=(TextView) convertView.findViewById(R.id.tv_reception_title);

            result=convertView;
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition=position;
        holder.tv_reception_title.setText(dataModelReceptionDocuments.getEPC());

        return convertView;
    }
}
