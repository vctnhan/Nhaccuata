package com.example.hanwool.saleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hanwool.saleapp.PlayerOnlineActivity;
import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;
import com.example.hanwool.saleapp.modal.Song;

import java.util.ArrayList;

public class OnlineSongAdapter extends RecyclerView.Adapter<OnlineSongAdapter.ItemHolder> {
    private Context context;
    private ArrayList<OnlineSongUrlMp3> arraySong;
    ArrayList<Song> arrayCompare;

    private LayoutInflater layoutInflater;

    public OnlineSongAdapter(Context context, ArrayList<OnlineSongUrlMp3> arraySong) {
        this.context = context;
        this.arraySong = arraySong;
    }

    @NonNull
    @Override
    public OnlineSongAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_onlinesong, null);

        ItemHolder itemHolder = new ItemHolder(v);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineSongAdapter.ItemHolder holder, int position) {
        OnlineSongUrlMp3 song = arraySong.get(position);
        holder.txtTiltleSong.setText(song.getTitle());
        holder.txtTiltleSong.setTypeface(ResourcesCompat.getFont(context, R.font.tabithafull));
        holder.txtSinger.setText(song.getSinger());
        holder.txtSinger.setTypeface(ResourcesCompat.getFont(context, R.font.tabithafull));
        holder.txtDownload.setText(song.getDownloads());
        holder.txtDownload.setTypeface(ResourcesCompat.getFont(context, R.font.tabithafull));
        holder.txtView.setText(song.getViews());
        holder.txtView.setTypeface(ResourcesCompat.getFont(context, R.font.tabithafull));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.logo);
        requestOptions.error(R.drawable.errorimg);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(song.getImage())
                .into(holder.imgSong)
        ;
    }

    @Override
    public int getItemCount() {
        return arraySong.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView txtTiltleSong, txtSinger, txtDownload, txtView;
        public ImageView imgSong;


        public ItemHolder(View itemView) {
            super(itemView);
            txtTiltleSong = itemView.findViewById(R.id.txtTiltleSong);
            txtSinger = itemView.findViewById(R.id.txtSinger);
            txtDownload = itemView.findViewById(R.id.txtDownload);
            txtView = itemView.findViewById(R.id.txtViews);
            imgSong = itemView.findViewById(R.id.imgSong);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PlayerOnlineActivity.class);
                    intent.putExtra("songinfo", arraySong);
                    intent.putExtra("index", getPosition());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

}
