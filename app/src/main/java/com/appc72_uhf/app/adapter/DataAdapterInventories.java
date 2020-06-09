package com.appc72_uhf.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
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

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.Detail_product_activity;
import com.appc72_uhf.app.entities.DatamodelInventories;
import com.appc72_uhf.app.repositories.CompanyRepository;
import com.appc72_uhf.app.repositories.InventaryRespository;
import com.appc72_uhf.app.repositories.TagsRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class DataAdapterInventories extends ArrayAdapter<DatamodelInventories> implements View.OnClickListener {

    Context mContext;
    ArrayList<DatamodelInventories> datalist;
    ProgressDialog mypDialog;
    String code_enterprise;
    private String android_id;


    public DataAdapterInventories(@NonNull Context context, int resource, ArrayList<DatamodelInventories> datalist ) {
        super(context,  resource, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DatamodelInventories datamodelInventories=(DatamodelInventories)object;
        InventaryRespository inventaryRespository=new InventaryRespository(getContext());
        code_enterprise=getCompany();
        android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        switch (v.getId())
        {
            case R.id.item_delete:
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
                            InventaryRespository inventaryRespository=new InventaryRespository(getContext());
                            mypDialog = new ProgressDialog((Activity) getContext());
                            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mypDialog.setMessage("Vaciando codigos disponibles...");
                            mypDialog.setCanceledOnTouchOutside(false);
                            mypDialog.show();
                            try{
                                boolean res= tagsRepository.DeleteAllTags(datamodelInventories.getId());
                                boolean resultUpdateFalse=inventaryRespository.DeleteInventory(datamodelInventories.getId());
                                if(resultUpdateFalse && res){
                                    mypDialog.dismiss();
                                    UIHelper.ToastMessage(getContext(), "El inventario '"+datamodelInventories.getName()+"' eliminado!!", 5);
                                    notifyDataSetChanged();
                                    Intent goToMain=new Intent(getContext(), MainActivity.class);
                                    mContext.startActivity(goToMain);

                                    //UIHelper.ToastMessage(getContext(), "Codigos elimandos exitosamente!!", 10);
                                }
                            }catch (Exception e){
                                mypDialog.dismiss();
                                UIHelper.ToastMessage(getContext(), "Error al eliminar codigos!!", 10);
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_global_detail:
                if(datamodelInventories.getDetailForDevice()){
                    Intent goToMain=new Intent(getContext(), Detail_product_activity.class);
                    goToMain.putExtra("Id",  datamodelInventories.getId());
                    goToMain.putExtra("Name",  datamodelInventories.getName());
                    goToMain.putExtra("inventoryType", datamodelInventories.getDetailForDevice());
                    mContext.startActivity(goToMain);

                }else{
                    Intent fragment=new Intent(getContext(), MainActivity.class);
                    fragment.putExtra("inventoryBool", true);
                    fragment.putExtra("inventoryID", datamodelInventories.getId());
                    fragment.putExtra("inventoryName", datamodelInventories.getName());
                    fragment.putExtra("inventoryType", datamodelInventories.getDetailForDevice());
                    mContext.startActivity(fragment);

                }
                break;
        }
    }
    private class ViewHolder{
        TextView title;
        ImageView item_delete;
        ImageButton btn_global_detail;
    }
    private int lastPosition = -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DatamodelInventories datamodelInventories=getItem(position);
        ViewHolder holder;
        final View result;

        if(convertView==null){
            holder= new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_inventories_1, parent, false);


            holder.title=(TextView) convertView.findViewById(R.id.tv_inventory);
            holder.item_delete=(ImageView)convertView.findViewById(R.id.item_delete);
            holder.btn_global_detail=(ImageButton)convertView.findViewById(R.id.btn_global_detail);

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
        holder.btn_global_detail.setOnClickListener(this);
        holder.item_delete.setOnClickListener(this);
        holder.btn_global_detail.setTag(position);
        holder.item_delete.setTag(position);

        if(datamodelInventories.getDetailForDevice()){
            convertView.setBackgroundResource(R.color.lightblue);
        }else{
            convertView.setBackgroundResource(R.color.sbc_header_text);
        }


        return convertView;
    }
    private String getCompany(){
        CompanyRepository companyRepository=new CompanyRepository(getContext());
        SharedPreferences preferenceCodeActive=getContext().getSharedPreferences("code_activate", Context.MODE_PRIVATE);
        String enterprises_code=preferenceCodeActive.getString("code_activate", "");
        String code_result="";
        if(enterprises_code.isEmpty()){
            Log.e("No data preferences", " Error data no empty "+enterprises_code);
        }else{
            code_result=enterprises_code;
        }
        return code_result;
    }

}
