package com.appc72_uhf.app.adapter;

import android.content.Context;
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
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class DataAdapterInventoryServer extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {
    Context mContext;
    ArrayList<DatamodelInventories> datalist;

    public DataAdapterInventoryServer(@NonNull Context context, ArrayList<DatamodelInventories> datalist) {
        super(context, R.layout.smple_list_inventory_server, datalist);
        this.datalist = datalist;
        this.mContext = context;
    }

    @Override
    public void onClick(View v){
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DatamodelInventories datamodelInventories=(DatamodelInventories)object;
        CheckBox chb_takeInventory= (CheckBox) v.findViewById(R.id.chbx_takeInventory);
        InventaryRespository inventaryRespository=new InventaryRespository(getContext());
        switch (v.getId()){
            case R.id.chbx_takeInventory:
                if(chb_takeInventory.isChecked()){
                    boolean  resultUpdateTrue=inventaryRespository.UpdateSelect(datamodelInventories.getId(), 1);
                    if(resultUpdateTrue){
                        UIHelper.ToastMessage(getContext(), "MENSAJE DEL BOTON"+datamodelInventories.getName()+" ID:"+datamodelInventories.getId()+"is Checked"+chb_takeInventory.isChecked(), 5);
                    }
                }else {
                   boolean resultUpdateFalse=inventaryRespository.UpdateSelect(datamodelInventories.getId(), 0);
                    if(resultUpdateFalse){
                        UIHelper.ToastMessage(getContext(), "MENSAJE DEL BOTON"+datamodelInventories.getName()+" ID:"+datamodelInventories.getId()+"is Checked"+chb_takeInventory.isChecked(), 5);
                    }
                }
                break;
        }
    }

    private class ViewHolder{
        CheckBox chbx_takeInventory;
        TextView tv_inventory;
    }
    private int lastPosition= -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        //Get the data item for this position
        DatamodelInventories datamodelInventories=getItem(position);

        ViewHolder holder;

        final View result;

        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.smple_list_inventory_server, parent, false);

            holder.tv_inventory=(TextView) convertView.findViewById(R.id.tv_inventory);
            holder.chbx_takeInventory=(CheckBox) convertView.findViewById(R.id.chbx_takeInventory);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        // final DatamodelInventories datamodelInventories=datalist.get(position);

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition = position;
        holder.tv_inventory.setText(datamodelInventories.getName());
        holder.chbx_takeInventory.setOnClickListener(this);

        if(datamodelInventories.getisSelect()==1){
            holder.chbx_takeInventory.setChecked(true);
        }else{
            holder.chbx_takeInventory.setChecked(false);
        }
        holder.chbx_takeInventory.setTag(position);
        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
        }

        return convertView;
    }

}
