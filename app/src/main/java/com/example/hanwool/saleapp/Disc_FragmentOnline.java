package com.example.hanwool.saleapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hanwool.saleapp.PlayerActivity;
import com.example.hanwool.saleapp.PlayerOnlineActivity;
import com.example.hanwool.saleapp.R;

import com.example.hanwool.saleapp.adapter.SongAdapter;
import com.example.hanwool.saleapp.modal.Song;
import com.squareup.picasso.Picasso;

public class Disc_FragmentOnline extends Fragment {
    View view;
    public static de.hdodenhof.circleimageview.CircleImageView imgDisc;
    public static RotateAnimation rotateAnimation;
    String songImage;
    RelativeLayout bgDiscFragment;
    public static ObjectAnimator objectAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_disc, container, false);
        imgDisc = view.findViewById(R.id.imgDisc);
        bgDiscFragment = view.findViewById(R.id.bgDiscFragment);
        Drawable background = bgDiscFragment.getBackground();
        background.setAlpha(80);
        objectAnimator = ObjectAnimator.ofFloat(imgDisc, "rotation", 0f, 360f);
        objectAnimator.setDuration(10000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        loadImage();
        objectAnimator.start();


        return view;
    }

    public void loadImage() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.logo);
        requestOptions.error(R.drawable.errorimg);
        Glide.with(getActivity().getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(PlayerOnlineActivity.arrayMp3.get(PlayerOnlineActivity.index).getImage())
                .into(imgDisc);
    }
}
