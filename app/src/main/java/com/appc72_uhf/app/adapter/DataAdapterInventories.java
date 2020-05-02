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
import com.appc72_uhf.app.activities.Detail_product_activity;
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class DataAdapterInventories extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {

    Context mContext;
    ArrayList<DatamodelInventories> datalist;


    public DataAdapterInventories(@NonNull Context context, ArrayList<DatamodelInventories> datalist ) {
        super(context, R.layout.simple_list_inventories_1, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DatamodelInventories datamodelInventories=(DatamodelInventories)object;

        switch (v.getId())
        {
            case R.id.item_info:
                UIHelper.ToastMessage(getContext(), "MENSAJE DEL BOTON"+datamodelInventories.getName()+" ID:"+datamodelInventories.getId() , 5);
                Intent goToMain=new Intent(getContext(), Detail_product_activity.class);
                goToMain.putExtra("Id",  String.valueOf(datamodelInventories.getId()));
                mContext.startActivity(goToMain);
                break;
        }
    }

    private class ViewHolder{
        TextView title;
        ImageView info;
    }
    private int lastPosition = -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        DatamodelInventories datamodelInventories=getItem(position);

        ViewHolder holder;

        final View result;

        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            //LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.simple_list_inventories_1, parent, false);

            holder.title=(TextView) convertView.findViewById(R.id.tv_inventory);
            holder.info=(ImageView)convertView.findViewById(R.id.item_info);
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
        holder.title.setText(datamodelInventories.getName());
        holder.info.setOnClickListener(this);
        holder.info.setTag(position);

        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
        }

        return convertView;
    }

}
