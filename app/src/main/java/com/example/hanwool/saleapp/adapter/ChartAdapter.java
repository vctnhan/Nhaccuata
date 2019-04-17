package com.example.hanwool.saleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.SongInChartActivity;
import com.example.hanwool.saleapp.modal.OnlineSongHtml;

import java.util.ArrayList;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ItemHolder> {
        Context context;
        ArrayList<OnlineSongHtml> arrayChart;

public ChartAdapter(Context context, ArrayList<OnlineSongHtml> arrayChart) {
        this.context = context;
        this.arrayChart = arrayChart;
        }

@Override
public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.custom_chart,null);

        ItemHolder itemHolder= new ItemHolder(v);

        return itemHolder;
        }

@Override
public void onBindViewHolder(ItemHolder holder, int position) {

        OnlineSongHtml onlineSongHtml = arrayChart.get(position);
        holder.txtChart.setText(onlineSongHtml.getTitle());
        holder.txtChart.setBackgroundColor(R.drawable.color1_adapter);
       switch (position){
           case 1:
               holder.txtChart.setBackgroundResource(R.drawable.vietnamflag);
               break;
           case 2:
               holder.txtChart.setBackgroundResource(R.drawable.usflag);
               break;
           case 3:
               holder.txtChart.setBackgroundResource(R.drawable.chinaflag);
               break;
           case 4:
               holder.txtChart.setBackgroundResource(R.drawable.koreaflag);
               break;
           case 5:
               holder.txtChart.setBackgroundResource(R.drawable.japanflag);
               break;
           case 6:
               holder.txtChart.setBackgroundResource(R.drawable.franceflag);
               break;
       }
       Drawable background =   holder.txtChart.getBackground();
    background.setAlpha(201);
        }

@Override
public int getItemCount() {
        return arrayChart.size();
        }

public class  ItemHolder extends RecyclerView.ViewHolder{
    public TextView txtChart;


    public ItemHolder(View itemView) {
        super(itemView);
        txtChart= itemView.findViewById(R.id.txtChart);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SongInChartActivity.class);
                intent.putExtra("url",arrayChart.get(getPosition()).getUrlMp3Html());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
}
}