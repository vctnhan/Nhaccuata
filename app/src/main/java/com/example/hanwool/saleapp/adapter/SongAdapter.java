package com.example.hanwool.saleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanwool.saleapp.AllOfflineMusicActivity;
import com.example.hanwool.saleapp.FavouriteMusicActivity;
import com.example.hanwool.saleapp.LoginActivity;
import com.example.hanwool.saleapp.MainActivity;
import com.example.hanwool.saleapp.PlayerActivity;
import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.fragment.Disc_Fragment;
import com.example.hanwool.saleapp.modal.Song;
import com.example.hanwool.saleapp.modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SongAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Song> arraySong;
    ArrayList<Song> arrayCompare;
    MediaMetadataRetriever metaRetriver;
    private LayoutInflater layoutInflater;
    Disc_Fragment disc_fragment;
    boolean found;
    private FirebaseAuth mAuth;


    public SongAdapter(Context context, ArrayList<Song> arraySong) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoldder viewHoldder = null;
        if (view == null) {
            viewHoldder = new ViewHoldder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_allofflinesongs, null);
            viewHoldder.imgAlbumCover = view.findViewById(R.id.imgAlbumCover);
            viewHoldder.txtNamesong = view.findViewById(R.id.txtNamesong);
            viewHoldder.txtArtistsong = view.findViewById(R.id.txtArtistsong);
//            viewHoldder.txtLocationsong = view.findViewById(R.id.txtLocationsong);
            viewHoldder.txtDurationsong = view.findViewById(R.id.txtDurationsong);
            viewHoldder.checkBoxFavourite = view.findViewById(R.id.imgFavourite);
            view.setTag(viewHoldder);

        } else {
            viewHoldder = (ViewHoldder) view.getTag();
        }
        final Song song = (Song) getItem(i);
        mAuth = FirebaseAuth.getInstance();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("arraySong", arraySong);
                intent.putExtra("index", i);

//                if (PlayerActivity.mp != null){
//                    PlayerActivity.mp.stop();
//
//                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
//viewHoldder.txtLocationsong.setText(arraySong.get(i).getLocation().toString());

        try{
            metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(song.getLocation());
            byte[] art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage1 = BitmapFactory.decodeByteArray(art, 0, art.length);

//Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
            viewHoldder.imgAlbumCover.setImageBitmap(songImage1);

        }
        catch(Exception e){
            viewHoldder.imgAlbumCover.setImageResource(R.drawable.logo);
        }


        viewHoldder.txtNamesong.setText(song.getName());
        viewHoldder.txtArtistsong.setText(song.getArtist());
        long seconds = song.getDuration() / 1000;
        long second = (seconds) % 60;
        long minute = seconds / 60;

        if (second < 10) {
            viewHoldder.txtDurationsong.setText(minute + ":0" + second);
        } else {
            viewHoldder.txtDurationsong.setText(minute + ":" + second);
        }
        if (FavouriteMusicActivity.arrayFavourite != null){

        }else {
            FavouriteMusicActivity.arrayFavourite = new ArrayList<>();
        }
      //neu bai hat da co trong favorite se co trai tim k thi ngc lai
        ArrayList<Song> result = new ArrayList<>();
        boolean exist = false;
        for (int a =0; a < FavouriteMusicActivity.arrayFavourite.size(); a++ ){
            if (FavouriteMusicActivity.arrayFavourite.get(a).getLocation().equals(song.location)){
                viewHoldder.checkBoxFavourite.setChecked(true);
                exist=true;

            }

        }
        if (exist==false){
            viewHoldder.checkBoxFavourite.setChecked(false);

        }

        //
        final ViewHoldder finalViewHoldder = viewHoldder;
       viewHoldder.checkBoxFavourite.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (finalViewHoldder.checkBoxFavourite.isChecked()){
                   FavouriteMusicActivity.arrayFavourite.add(new Song(arraySong.get(i).getName(), arraySong.get(i).getArtist(),
                           arraySong.get(i).getDuration(), arraySong.get(i).getLocation()));
                   Toast.makeText(context, "Added to Favourite", Toast.LENGTH_SHORT).show();
//add  favorite song to firebase

//                   FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid())
//                           .child("FavouriteSong")
//                           .setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
//                       @Override
//                       public void onComplete(@NonNull Task<Void> task) {
//
//                           if (task.isSuccessful()){
//
//
//                               Toast.makeText(context, "Added to Favourite", Toast.LENGTH_SHORT).show();
//
//                           }else {
//
//                               Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                           }
//                       }
//                   });
                }
                else {
                    for (int a =0; a<FavouriteMusicActivity.arrayFavourite.size();a++){
                        if (arraySong.get(i).getLocation().equals(FavouriteMusicActivity.arrayFavourite.get(a).getLocation())){
                            FavouriteMusicActivity.arrayFavourite.remove(a);
                        }
                    }
                    Toast.makeText(context, "Removed from Favourite", Toast.LENGTH_SHORT).show();

                }
           }
       });
        return view;
    }


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
        public TextView txtNamesong, txtArtistsong, txtDurationsong ;
        public CheckBox checkBoxFavourite;
        public ImageView imgAlbumCover;

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
