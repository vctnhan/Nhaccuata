package com.example.hanwool.saleapp.fragment;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.example.hanwool.saleapp.PlayerActivity;
import com.example.hanwool.saleapp.R;

import com.example.hanwool.saleapp.adapter.SongAdapter;
import com.example.hanwool.saleapp.modal.Song;
import com.squareup.picasso.Picasso;

public class Disc_Fragment extends Fragment {
    View view;
    public static de.hdodenhof.circleimageview.CircleImageView imgDisc;
    public  static  RotateAnimation rotateAnimation;
    String songImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_disc, container, false);
            imgDisc = view.findViewById(R.id.imgDisc);
//            objectAnimator = ObjectAnimator.ofFloat(imgDisc, "rotation",0f,360f);
//            objectAnimator.setDuration(10000);
//            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
//            objectAnimator.setInterpolator(new LinearInterpolator());
//            imgDisc.setImageResource(R.drawable.like);
//
        if (getArguments() != null){
            songImage = getArguments().getString("songImageString");
            Toast.makeText(getContext(), songImage, Toast.LENGTH_SHORT).show();
        }


//            imgDisc.setImageBitmap(PlayerActivity.songImage);


         rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

       imgDisc.startAnimation(rotateAnimation);
            return view;
    }

    public void getImage(Bitmap hinhanh){
        imgDisc.setImageBitmap(hinhanh);
    }

}
