package com.appc72_uhf.app.activities;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterProductDetails;
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.repositories.DetailProductRepository;
import com.appc72_uhf.app.tools.UIHelper;

import java.util.ArrayList;

public class Detail_product_activity extends AppCompatActivity {
    private TextView tv_detail_inventory, tv_minor_count, tv_total_products;
    private ListView lv_detail_product;
    ArrayList<DataModelProductDetails> dataArrayProducts;
    AdapterProductDetails adapterProductDetails;
    int val_inventory, totalFoundEPC, totalFoundEPCFOUND;
    boolean chargeFirstTimeProduct;
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

        lv_detail_product=(ListView) findViewById(R.id.lv_detail_product);
        dataArrayProducts=new ArrayList<DataModelProductDetails>();
        val_inventory=getIntent().getIntExtra("Id", 0);
        getDataProductMaster();
        adapterProductDetails=new AdapterProductDetails(Detail_product_activity.this, dataArrayProducts);
        lv_detail_product.setAdapter(adapterProductDetails);
        tv_detail_inventory.setText(getIntent().getStringExtra("Name"));
        adapterProductDetails.notifyDataSetChanged();

        totalFoundEPCFOUND=getCountFoundProductTotal();
        tv_minor_count.setText(String.valueOf(totalFoundEPCFOUND));

        totalFoundEPC=getCountTotalProduct();
        tv_total_products.setText(String.valueOf(totalFoundEPC));




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
        if(val_inventory!=0){
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
}
