package com.appc72_uhf.app.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.entities.DataModelReceptionAutomatic;
import com.appc72_uhf.app.repositories.ReceptionRepository;

import java.util.ArrayList;

public class AdapterListAutomaticReception extends ArrayAdapter<DataModelReceptionAutomatic> implements View.OnClickListener {
    Context mContext;
    ArrayList<DataModelReceptionAutomatic> dataReceptionDocument;
    ProgressDialog myDialog;
    private String android_id;
    public static final String PROTOCOL_URLRFID="https://";
    public static final String DOMAIN_URLRFID=".izyrfid.com/";

    public AdapterListAutomaticReception(@NonNull Context context, int resource, ArrayList<DataModelReceptionAutomatic> dataReceptionDocument) {
        super(context, resource, dataReceptionDocument);
        this.mContext = context;
        this.dataReceptionDocument = dataReceptionDocument;
    }
    @Override
    public void onClick(View v){
        int position=(Integer) v.getTag();
        Object object=getItem(position);

        final DataModelReceptionAutomatic dataModelReceptionAutomatic=(DataModelReceptionAutomatic)object;


        switch (v.getId()){
            case R.id.btn_reception_comentary:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.ap_dialog_comentary_reception);
                builder.setMessage(dataModelReceptionAutomatic.getComentarios());
                builder.setIcon(R.drawable.button_bg_up);
                builder.setNegativeButton(R.string.ap_dialog_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                   /* AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    final CharSequence[] dialog=new CharSequence[1];
                    dialog[0]=dataModelReceptionAutomatic.getComentarios();

                    builder.setTitle("Comentario "+dataModelReceptionAutomatic.getLocation())
                            .setItems(dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create();*/
                builder.create().show();
                break;
        }

    }
    private class ViewHolder{
        TextView tv_reception_title, tv_date_reception, tv_date_reception_count;
        ImageView item_delete, item_sync;
        ImageButton btn_reception_comentary, btn_global_reception_automatic;
    }
    private int lastPosition=-1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ReceptionRepository receptionRepository=new ReceptionRepository(mContext);
        DataModelReceptionAutomatic dataModelReceptionAutomatic=getItem(position);
        ViewHolder holder;
        final View result;
        if(convertView==null){
            holder=new ViewHolder();

            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.simple_list_reception_automatics, parent, false);

            holder.tv_reception_title=(TextView) convertView.findViewById(R.id.tv_reception_title);
            holder.tv_date_reception=(TextView) convertView.findViewById(R.id.tv_date_reception);
            holder.tv_date_reception_count=(TextView) convertView.findViewById(R.id.tv_date_reception_count);
            holder.btn_reception_comentary=(ImageButton)convertView.findViewById(R.id.btn_reception_comentary);

            result=convertView;
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);

        lastPosition=position;

        String locationName=receptionRepository.locationToString(dataModelReceptionAutomatic.getLocation());
        holder.tv_reception_title.setText("Ubicacion: "+locationName.toLowerCase().trim());
        holder.tv_date_reception.setText(dataModelReceptionAutomatic.getFecha());
        holder.tv_date_reception_count.setText("Contador: "+dataModelReceptionAutomatic.getCounter());
        holder.btn_reception_comentary.setOnClickListener(this);
        holder.btn_reception_comentary.setTag(position);

        return convertView;
    }
}
