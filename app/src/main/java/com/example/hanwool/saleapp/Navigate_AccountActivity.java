package com.example.hanwool.saleapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

public class Navigate_AccountActivity extends AppCompatActivity {
VideoView videoView;
    public static MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate__account);
        Anhxa();

       player = MediaPlayer.create(this, R.raw.sofaview);
        player.setLooping(true);
        player.start();
//        final MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.sofaview);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.backgroundvideo);
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
        videoView.setKeepScreenOn(true);
        videoView.setVideoURI(uri);
        videoView.start();

    }


    private void Anhxa() {
        videoView = findViewById(R.id.videoView);
        if (player != null){
            player.stop();
        }
    }

    public void btnGotoRegister(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();

    }

    public void btnGotoLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();

    }
}
