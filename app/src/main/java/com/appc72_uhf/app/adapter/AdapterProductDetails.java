package com.appc72_uhf.app.adapter;

import android.content.Context;
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
import com.appc72_uhf.app.entities.DataModelProductDetails;

import java.util.ArrayList;

public class AdapterProductDetails extends ArrayAdapter<DataModelProductDetails> {
    Context mContext;
    ArrayList<DataModelProductDetails> datalist;
    public AdapterProductDetails(@NonNull Context context, ArrayList<DataModelProductDetails> datalist){
        super(context, R.layout.simple_list_data_products, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }
    private class ViewHolder{
        TextView name, cuantity;
        ImageView found, not_found;
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
            //LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.simple_list_inventories_1, parent, false);

            holder.name=(TextView) convertView.findViewById(R.id.tv_tittle_detail);
            holder.found=(ImageView)convertView.findViewById(R.id.item_found);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        //final DatamodelInventories datamodelInventories=datalist.get(position);
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        holder.name.setText(dataModelProductDetails.getName());
        holder.found.setTag(position);

        if(dataModelProductDetails.getFound().equals("true")){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
        }

        return convertView;
    }

}
