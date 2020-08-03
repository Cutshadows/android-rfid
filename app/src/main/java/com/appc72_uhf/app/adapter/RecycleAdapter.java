package com.appc72_uhf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.entities.ListData;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {
    private ArrayList applicationList;
    private Context mContext;

    public RecycleAdapter(ArrayList applicationList){
        this.applicationList=applicationList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.listtag_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListData lsData= (ListData) applicationList.get(position);
        holder.tagInventory.setText(lsData.getName());
        //Glide.with(mContext).load().into(holder.tagInventory);

    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tagInventory;
        public MyViewHolder(View itemView){
            super(itemView);
            tagInventory= itemView.findViewById(R.id.TvTagUii);
        }
    }
    public void addTextTags(ArrayList tags){
       // for(Application tag: tags){
            applicationList.add("tags");
        //}
    }
}
