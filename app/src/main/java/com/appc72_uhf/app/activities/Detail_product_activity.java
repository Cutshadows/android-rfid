package com.appc72_uhf.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.MainActivity;
import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterProductDetails;
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.repositories.DetailProductRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class Detail_product_activity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_detail_inventory, tv_minor_count, tv_total_products;
    private ListView lv_detail_product;
    private Button btn_take_inventory, btn_exit_productview;
    ArrayList<DataModelProductDetails> dataArrayProducts;
    AdapterProductDetails adapterProductDetails;
    String val_inventory;
    int totalFoundEPC, totalFoundEPCFOUND;
    boolean chargeFirstTimeProduct, inventory_type;
    String inventory_name_detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_product_activity);
        initComponent();
    }
    private void initComponent(){
        chargeFirstTimeProduct=true;
        tv_detail_inventory=(TextView)findViewById(R.id.tv_detail_inventory);
        tv_minor_count=(TextView)findViewById(R.id.tv_minor_count);
        tv_total_products=(TextView)findViewById(R.id.tv_total_products);
        btn_take_inventory=(Button) findViewById(R.id.btn_take_inventory);
        lv_detail_product=(ListView) findViewById(R.id.lv_detail_product);
        btn_exit_productview=(Button) findViewById(R.id.btn_exit_productview);


        dataArrayProducts=new ArrayList<DataModelProductDetails>();

        val_inventory=getIntent().getStringExtra("Id");
        inventory_name_detail=getIntent().getStringExtra("Name");
        inventory_type=getIntent().getBooleanExtra("inventoryType", false);

        getDataProductMaster();
        adapterProductDetails=new AdapterProductDetails(Detail_product_activity.this, dataArrayProducts);
        lv_detail_product.setAdapter(adapterProductDetails);



        tv_detail_inventory.setText("["+val_inventory+"]"+"  "+inventory_name_detail);
        //inventory_type=getIntent().getBooleanExtra("inventoryType", false);
        adapterProductDetails.notifyDataSetChanged();

        totalFoundEPCFOUND=getCountFoundProductTotal();
        tv_minor_count.setText(String.valueOf(totalFoundEPCFOUND));

        totalFoundEPC=getCountTotalProduct();
        tv_total_products.setText(String.valueOf(totalFoundEPC));
        btn_take_inventory.setOnClickListener(this);
        btn_exit_productview.setOnClickListener(this);
    }

    public int getCountFoundProductTotal(){
        int totalFoundPTrue=0;
        try{
            DetailProductRepository detailFoundPTrue=new DetailProductRepository(Detail_product_activity.this);
            totalFoundPTrue=detailFoundPTrue.OrderProductTotalEPCFOUND(val_inventory);
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalFoundPTrue;
    }

    public int getCountTotalProduct(){
        int totalFoundP=0;
        try{
            DetailProductRepository detailFoundP=new DetailProductRepository(Detail_product_activity.this);
             totalFoundP=detailFoundP.OrderProductTotalEPC(val_inventory);
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalFoundP;
    }
    public void getDataProductMaster(){
        if(!val_inventory.equals("")){
            try{
                DetailProductRepository detailProductRepository=new DetailProductRepository(this);
                ArrayList products=detailProductRepository.OrderProductMasterId(val_inventory);
                for(int indexProd=0; indexProd<=products.size(); indexProd++){
                    String receptProducts=products.get(indexProd).toString();
                    String[] strProds=receptProducts.split("@");
                    dataArrayProducts.add(new DataModelProductDetails(Integer.parseInt(strProds[0]), Integer.parseInt(strProds[1]), strProds[2], val_inventory, strProds[3]));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            UIHelper.ToastMessage(Detail_product_activity.this , "Inventario 0 no existe", 3);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_inventory:
                Intent fragment=new Intent(Detail_product_activity.this, MainActivity.class);
                fragment.putExtra("inventoryBool", true);
                fragment.putExtra("inventoryID", val_inventory);
                fragment.putExtra("inventoryName", inventory_name_detail);
                fragment.putExtra("Id",  val_inventory);
                fragment.putExtra("Name",  inventory_name_detail);
                fragment.putExtra("inventoryType", inventory_type);
                Detail_product_activity.this.startActivity(fragment);
                Detail_product_activity.this.onBackPressed();
                break;
            case R.id.btn_exit_productview:
                Intent MainTo=new Intent(Detail_product_activity.this, MainActivity.class);
                Detail_product_activity.this.startActivity(MainTo);
                Detail_product_activity.this.onBackPressed();
                break;
        }
    }
}
