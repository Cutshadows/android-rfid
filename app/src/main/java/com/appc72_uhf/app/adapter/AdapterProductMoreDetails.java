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
import com.appc72_uhf.app.entities.DataModelProductDetails;

import java.util.ArrayList;

public class AdapterProductMoreDetails extends ArrayAdapter<DataModelProductDetails> {
    Context mContext;
    ArrayList<DataModelProductDetails> datalist;

    public AdapterProductMoreDetails(@NonNull Context context, @NonNull ArrayList<DataModelProductDetails> datalist) {
        super(context, R.layout.simple_detail_more_products, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }
    private class ViewHolder{
        TextView tv_epc_code;
        ImageButton item_status_found;
    }
    private int lastPosition = -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        DataModelProductDetails dataModelProductDetails=getItem(position);
        ViewHolder holder;

        final View result;

        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView=inflater.inflate(R.layout.simple_detail_more_products, parent, false);

            holder.tv_epc_code=(TextView) convertView.findViewById(R.id.tv_epc_code);
            holder.item_status_found=(ImageButton) convertView.findViewById(R.id.item_status_found);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        holder.tv_epc_code.setText("EPC: "+dataModelProductDetails.getEPC());
        holder.tv_epc_code.setTag(position);

        if(dataModelProductDetails.getFound().equals("true")){
            holder.item_status_found.setBackgroundResource(R.color.green);
        }else{
            holder.item_status_found.setBackgroundResource(R.color.red);
        }

        return convertView;
    }
}
