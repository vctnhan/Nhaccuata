package com.example.hanwool.saleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.SongInChartActivity;
import com.example.hanwool.saleapp.modal.OnlineSongHtml;

import java.util.ArrayList;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ItemHolder> {
    Context context;
    ArrayList<OnlineSongHtml> arrayTheme;

    public ThemeAdapter(Context context, ArrayList<OnlineSongHtml> arrayTheme) {
        this.context = context;
        this.arrayTheme = arrayTheme;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_theme,null);

        ItemHolder itemHolder= new ItemHolder(v);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        OnlineSongHtml onlineSongHtml = arrayTheme.get(position);
        holder.txtChart.setText(onlineSongHtml.getTitle());
//        holder.txtChart.setBackgroundColor(R.drawable.color1_adapter);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.logo);
        requestOptions.error(R.drawable.errorimg);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(arrayTheme.get(position).getImage())
                .into(holder.imgTheme)

        ;

    }

    @Override
    public int getItemCount() {
        return arrayTheme.size();
    }

    public class  ItemHolder extends RecyclerView.ViewHolder{
        public TextView txtChart;
        public ImageView imgTheme;



        public ItemHolder(View itemView) {
            super(itemView);
            txtChart= itemView.findViewById(R.id.txtChart);
            imgTheme= itemView.findViewById(R.id.imgTheme);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SongInChartActivity.class);
                    intent.putExtra("url",arrayTheme.get(getPosition()).getUrlMp3Html());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}
