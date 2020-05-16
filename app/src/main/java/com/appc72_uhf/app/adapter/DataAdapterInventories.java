package com.appc72_uhf.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.repositories.TagsRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class DataAdapterInventories extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {

    Context mContext;
    ArrayList<DatamodelInventories> datalist;
    ProgressDialog mypDialog;



    public DataAdapterInventories(@NonNull Context context, ArrayList<DatamodelInventories> datalist ) {
        super(context, R.layout.simple_list_inventories_1, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DatamodelInventories datamodelInventories=(DatamodelInventories)object;
        InventaryRespository inventaryRespository=new InventaryRespository(getContext());


        switch (v.getId())
        {
            case R.id.item_info:
                Intent goToMain=new Intent(getContext(), Detail_product_activity.class);
                goToMain.putExtra("Id",  String.valueOf(datamodelInventories.getId()));
                goToMain.putExtra("Name",  datamodelInventories.getName());
                mContext.startActivity(goToMain);
                break;
            case R.id.item_sync:
                UIHelper.ToastMessage(getContext(), "ESTOY EN SINCRONIZAR", 3);
                break;
            case R.id.item_void:
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
                    builder.setTitle(R.string.ap_dialog_inventario_vaciar);
                    builder.setMessage("Vaciar codigos para este inventario?");
                    builder.setIcon(R.drawable.button_bg_up);

                    builder.setNegativeButton(R.string.ap_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton(R.string.ap_dialog_acept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            TagsRepository tagsRepository=new TagsRepository(getContext());
                            mypDialog = new ProgressDialog((Activity) getContext());
                            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mypDialog.setMessage("Vaciando codigos disponibles...");
                            mypDialog.setCanceledOnTouchOutside(false);
                            mypDialog.show();
                            try{
                                boolean res= tagsRepository.DeleteAllTags(datamodelInventories.getId());
                                UIHelper.ToastMessage(getContext(), "RFID: "+res, 10);
                                Log.e("STRING RESP", ""+res);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private class ViewHolder{
        TextView title;
        ImageView info, item_sync, item_void;
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
            holder.item_sync=(ImageView)convertView.findViewById(R.id.item_sync);
            holder.item_void=(ImageView)convertView.findViewById(R.id.item_void);

            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        holder.title.setText(datamodelInventories.getName());
        holder.item_void.setOnClickListener(this);
        holder.item_sync.setOnClickListener(this);
        holder.info.setOnClickListener(this);
        holder.item_void.setTag(position);
        holder.item_sync.setTag(position);
        holder.info.setTag(position);

        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
            holder.info.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
