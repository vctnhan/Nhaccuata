package com.example.hanwool.saleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanwool.saleapp.AllOfflineMusicActivity;
import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.modal.Song;

import java.util.ArrayList;

public class FavouriteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Song> arraySong;
    ArrayList<Song> arrayCompare;
    MediaMetadataRetriever metaRetriver;
    private LayoutInflater layoutInflater;

    public FavouriteAdapter(Context context, ArrayList<Song> arraySong) {
        this.context = context;
        this.arraySong = arraySong;
    }

    @Override
    public int getCount() {
        return arraySong.size();
    }

    @Override
    public Object getItem(int i) {
        return arraySong.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHoldder viewHoldder = null;
        if (view == null) {
            viewHoldder = new ViewHoldder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_favouritesongs, null);
            viewHoldder.imgAlbumCover = view.findViewById(R.id.imgAlbumCover);
            viewHoldder.txtNamesong = view.findViewById(R.id.txtNamesong);
            viewHoldder.txtArtistsong = view.findViewById(R.id.txtArtistsong);
            viewHoldder.txtDurationsong = view.findViewById(R.id.txtDurationsong);
            view.setTag(viewHoldder);
        } else {
            viewHoldder = (ViewHoldder) view.getTag();
        }
//        Giohang giohang = (Giohang) getItem(i);
//        viewHoldder.txtTengiohang.setText(giohang.getTensp());
//        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
//        viewHoldder.txtGiagiohang.setText(decimalFormat.format(giohang.getGiasp()) + "VNÄ");
//        Picasso.with(context).load(giohang.getHinhsp())
//                .placeholder(R.drawable.imgerror)
//                .error(R.drawable.imgerror)
//                .into(viewHoldder.imgGiohang);
//        viewHoldder.btnValues.setText(giohang.getSoluongsp() + "");

        Song song = (Song) getItem(i);
        try{
             metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(song.getLocation());
            byte[] art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            viewHoldder.imgAlbumCover.setImageBitmap(songImage);

        }
        catch(Exception e){
            viewHoldder.imgAlbumCover.setImageResource(R.drawable.icon_mp3);
        }
        viewHoldder.txtNamesong.setText(song.getName());
        viewHoldder.txtNamesong.setTypeface(ResourcesCompat.getFont(context, R.font.tabithafull));
        viewHoldder.txtArtistsong.setText(song.getArtist());
        viewHoldder.txtArtistsong.setTypeface(ResourcesCompat.getFont(context, R.font.tabithafull));
        long seconds = song.getDuration() / 1000;
        long second = (seconds) % 60;
        long minute = seconds / 60;

        if (second < 10) {
            viewHoldder.txtDurationsong.setText(minute + ":0" + second);
        } else {
            viewHoldder.txtDurationsong.setText(minute + ":" + second);
        }
        return view;
    }
//favourite click

    // search view
    @Override
    public Filter getFilter() {
        return new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Song> results = new ArrayList<Song>();
                if (arrayCompare == null)
                    arrayCompare = arraySong;
                if (constraint != null) {
                    if (arrayCompare != null && arrayCompare.size() > 0) {
                        for (final Song song : arrayCompare) {
                            if (song.getName().toLowerCase().contains(constraint.toString()) ||
                                    song.getArtist().toLowerCase().contains(constraint.toString()))

                                results.add(song);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                arraySong = (ArrayList<Song>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHoldder {
        public TextView txtNamesong, txtArtistsong, txtDurationsong;
        public ImageView imgAlbumCover;

    }

}
