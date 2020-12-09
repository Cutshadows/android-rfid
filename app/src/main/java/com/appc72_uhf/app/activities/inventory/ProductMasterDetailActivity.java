package com.appc72_uhf.app.activities.inventory;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.adapter.AdapterProductMoreDetails;
import com.appc72_uhf.app.entities.DataModelProductDetails;
import com.appc72_uhf.app.repositories.DetailProductRepository;

import java.util.ArrayList;

public class ProductMasterDetailActivity extends AppCompatActivity {
    String val_inventory;
    int product_master_id;
    TextView title_master_product, tv_code_product;
    ListView lv_products_code;
    ArrayList<DataModelProductDetails> dataArrayMoreProducts;
    AdapterProductMoreDetails adapterProductMoreDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_master_detail_activity);
        initComponent();
    }
    private void initComponent(){
        title_master_product=(TextView) findViewById(R.id.title_master_product);
        tv_code_product=(TextView) findViewById(R.id.tv_code_product);
        lv_products_code=(ListView)findViewById(R.id.lv_products_code);
        dataArrayMoreProducts=new ArrayList<DataModelProductDetails>();

        val_inventory=getIntent().getStringExtra("InventoryId");
        product_master_id=getIntent().getIntExtra("ProductMasterId", 0);

        getDetailProduct();
        adapterProductMoreDetails=new AdapterProductMoreDetails(ProductMasterDetailActivity.this, dataArrayMoreProducts);
        lv_products_code.setAdapter(adapterProductMoreDetails);
        adapterProductMoreDetails.notifyDataSetChanged();

    }

    public void getDetailProduct(){
        try{
            DetailProductRepository detailProductRepository=new DetailProductRepository(this);
            ArrayList codigoEPC=detailProductRepository.ProductListEPC(val_inventory, product_master_id);
            for(int ind=0;ind<codigoEPC.size(); ind++ ){
                String strCode=codigoEPC.get(ind).toString();
                String[] splitCode=strCode.split("@");
                title_master_product.setText(splitCode[2]);
                tv_code_product.setText(splitCode[3]);
                dataArrayMoreProducts.add(new DataModelProductDetails(splitCode[0], splitCode[1]));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
