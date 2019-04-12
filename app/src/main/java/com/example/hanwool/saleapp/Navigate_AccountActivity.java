package com.example.hanwool.saleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import static com.example.hanwool.saleapp.PlayerActivity.mp;

public class Navigate_AccountActivity extends AppCompatActivity {
VideoView videoView;
    public static MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate__account);
        Anhxa();
        if (PlayerActivity.mp != null && PlayerActivity.mp.isPlaying()) {
            PlayerActivity.mp.stop();
        }
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


    @SuppressLint("ResourceType")
    private void Anhxa() {
        videoView = findViewById(R.id.videoView);
        if (player != null){
            player.stop();
        }
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), Color.parseColor("#2a2701")));
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
