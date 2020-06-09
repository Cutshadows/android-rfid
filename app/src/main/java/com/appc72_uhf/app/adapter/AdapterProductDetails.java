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
import com.appc72_uhf.app.activities.ProductMasterDetailActivity;
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.repositories.DetailProductRepository;

import java.util.ArrayList;

public class AdapterProductDetails extends ArrayAdapter<DataModelProductDetails> implements View.OnClickListener {
    Context mContext;
    ArrayList<DataModelProductDetails> datalist;
    public AdapterProductDetails(@NonNull Context context, ArrayList<DataModelProductDetails> datalist){
        super(context, R.layout.simple_list_data_products, datalist);
        this.datalist=datalist;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelProductDetails dataModelProductDetails=(DataModelProductDetails)object;
        switch (v.getId())
        {
            case R.id.item_more_details:
                Intent goToMain=new Intent(getContext(), ProductMasterDetailActivity.class);
                goToMain.putExtra("InventoryId",  dataModelProductDetails.getInventoryId());
                goToMain.putExtra("ProductMasterId",  dataModelProductDetails.getProductMasterId());
                goToMain.putExtra("Id",  dataModelProductDetails.getInventoryId());
                goToMain.putExtra("Name",  dataModelProductDetails.getName());
                mContext.startActivity(goToMain);
                break;
        }
    }

    private class ViewHolder{
        TextView tv_name_product, tv_count, tv_product_master_id;
        ImageButton item_more_details;
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
            convertView=inflater.inflate(R.layout.simple_list_data_products, parent, false);

            holder.tv_product_master_id=(TextView) convertView.findViewById(R.id.tv_master_id);
            holder.tv_name_product=(TextView) convertView.findViewById(R.id.tv_name_product);
            holder.tv_count=(TextView) convertView.findViewById(R.id.tv_count);
            holder.item_more_details=(ImageButton) convertView.findViewById(R.id.item_more_details);
            result=convertView;
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }
        //final DatamodelInventories datamodelInventories=datalist.get(position);
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        DetailProductRepository detailProductRepository=new DetailProductRepository(this.mContext);
        int productTrueCount=detailProductRepository.CountProductFoundTrue(dataModelProductDetails.getInventoryId(), dataModelProductDetails.getProductMasterId());
        lastPosition = position;
        holder.tv_product_master_id.setText("ID: "+dataModelProductDetails.getCode());
        holder.tv_name_product.setText(dataModelProductDetails.getName());
        holder.tv_count.setText(" "+productTrueCount+" de "+dataModelProductDetails.getContador());
        holder.item_more_details.setOnClickListener(this);
        holder.item_more_details.setTag(position);

        if(productTrueCount < dataModelProductDetails.getContador()){
            convertView.setBackgroundResource(R.color.yellow_adapter_product);
        }else if(productTrueCount==dataModelProductDetails.getContador()){
            convertView.setBackgroundResource(R.color.green2);
        }

        return convertView;
    }

}
