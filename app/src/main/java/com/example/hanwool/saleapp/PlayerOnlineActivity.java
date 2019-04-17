package com.example.hanwool.saleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hanwool.saleapp.adapter.PlayerFragmentAdapter;
import com.example.hanwool.saleapp.adapter.ViewPagerPlayerOnlineFragmentAdapter;
import com.example.hanwool.saleapp.fragment.Disc_Fragment;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;
import com.example.hanwool.saleapp.modal.Song;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class PlayerOnlineActivity extends AppCompatActivity {
    public static MediaPlayer mp;
    Toolbar toolbarPlaynhac;
    ViewPager viewPagerPlaynhac;
    public static Bitmap songImage;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    Disc_FragmentOnline disc_FragmentOnline;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;
    private Handler myHandler = new Handler();
    FragmentManager fragmentManager;
    public static PlayerFragmentAdapter playerFragmentAdapter;
    SeekBar seekBar;
    ImageView btnBackward, btnPlay, btnForward, btnShuffle, btnRepeat;
    AnimationDrawable animationDrawable;
    LinearLayout bgPlayer;
    TextView tvName, tvTime, tvTiming;
    Uri u;
    public static ViewPagerPlayerOnlineFragmentAdapter viewPagerPlayerOnlineFragmentAdapter;
    Thread updateSeekBar;
    public static ArrayList<OnlineSongUrlMp3> arrayMp3;
    public static int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Anhxa();

        // animated background
        animationDrawable = (AnimationDrawable) bgPlayer.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    private void Anhxa() {
        if (mp != null ) {
            mp.stop();
        }
        if (PlayerActivity.mp != null ) {
            PlayerActivity.mp.stop();
        }
        mp = new MediaPlayer();
        Intent i = getIntent();
        //OnlineSongUrlMp3 onlineSongUrlMp3 = (OnlineSongUrlMp3) i.getSerializableExtra("songinfo");
        Bundle b = i.getExtras();
        arrayMp3 = new ArrayList<>();
        arrayMp3 = (ArrayList) b.getParcelableArrayList("songinfo");
        index = b.getInt("index", 0);
        bgPlayer = findViewById(R.id.bgPlayer);
        fragmentManager = getSupportFragmentManager();
        seekBar = findViewById(R.id.seekBar);
        viewPagerPlaynhac = findViewById(R.id.viewpagerPlaynhac);
        toolbarPlaynhac = findViewById(R.id.toolbarPlaynhac);
        setSupportActionBar(toolbarPlaynhac);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPlaynhac.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarPlaynhac.setTitleTextColor(Color.WHITE);
        //toolbarPlaynhac.setTitle(arrayMp3.get(index).getTitle());

        btnShuffle = findViewById(R.id.shuffle);
        btnRepeat = findViewById(R.id.repeat);
        btnBackward = findViewById(R.id.backward);
        btnForward = findViewById(R.id.forward);
        btnPlay = findViewById(R.id.play);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvTiming = findViewById(R.id.tvTiming);
        tvName.setText(arrayMp3.get(index).getTitle());
        mp.reset();

        seekBar.setMax(mp.getDuration());
//        try {
//            mp.reset();
//            mp.setDataSource(arrayMp3.get(index).getUrlMp3());
//            mp.prepare();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        playSong(index);
        disc_FragmentOnline = new Disc_FragmentOnline();
        viewPagerPlayerOnlineFragmentAdapter = new ViewPagerPlayerOnlineFragmentAdapter(getSupportFragmentManager());
//        viewPagerPlayerOnlineFragmentAdapter.AddFragment(Disc_FragmentOnline);
        viewPagerPlaynhac.setAdapter(viewPagerPlayerOnlineFragmentAdapter);
        disc_FragmentOnline = (Disc_FragmentOnline) viewPagerPlayerOnlineFragmentAdapter.getItem(1);
//                RequestOptions requestOptions = new RequestOptions();
//        requestOptions.placeholder(R.drawable.logo);
//        requestOptions.error(R.drawable.errorimg);
//        Glide.with(getApplicationContext())
//                .setDefaultRequestOptions(requestOptions)
//                .load(arrayMp3.get(index).getImage())
//                .into(Disc_FragmentOnline.imgDisc);

        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (isShuffle) {
                    Random rand = new Random();
                    index = rand.nextInt((arrayMp3.size() - 1) - 0 + 1) + 0;
                    playSong(index);
                } else if (isRepeat) {
                    playSong(index);
                } else {
                    mp.reset();
                    index++;
                    if (index >= arrayMp3.size())
                        index = 0;
                    playSong(index);
                }
            }
        });
        updateSeekBar = new Thread() {
            @Override
            public void run() {

                int totalDuration = mp.getDuration();
                int curentindex = 0;
                while (curentindex < totalDuration) {
                    try {
                        sleep(5000);
                        curentindex = mp.getCurrentPosition();
                        seekBar.setProgress(curentindex);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                super.run();
            }
        };

        myHandler.postDelayed(UpdateSongTime, 100);
        btnPlay.setImageResource(R.drawable.play);
        updateSeekBar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btnPlay.setImageResource(R.drawable.pause);
                    btnPlay.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        disc_FragmentOnline.objectAnimator.pause();
                    }
                    finalTime = mp.getDuration();
                    startTime = mp.getCurrentPosition();

                    if (oneTimeOnly == 0) {
                        seekBar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        disc_FragmentOnline.objectAnimator.resume();
                    }
                    mp.start();
                    btnPlay.setImageResource(R.drawable.play);
                }
            }
        });
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setImageResource(R.drawable.pause);
                if (isShuffle) {
                    Random rand = new Random();
                    oneTimeOnly = rand.nextInt((arrayMp3.size() - 1) - 0 + 2) + 0;
                    u = Uri.parse(arrayMp3.get(index).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    playSong(oneTimeOnly);
                    seekBar.setMax(mp.getDuration());

                } else {
                    mp.stop();
                    mp.release();
                    index = (index + 1) % arrayMp3.size();
                    u = Uri.parse(arrayMp3.get(index).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    playSong(index);
                    seekBar.setMax(mp.getDuration());
                }
            }
        });
        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setImageResource(R.drawable.pause);
                mp.stop();
                mp.release();
                index = (index - 1 < 0) ? arrayMp3.size() - 1 : index - 1;
                u = Uri.parse(arrayMp3.get(index).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                playSong(index);
                seekBar.setMax(mp.getDuration());
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.random);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.random);
                    btnRepeat.setImageResource(R.drawable.repeat);
                }
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.repeat);
                    btnShuffle.setImageResource(R.drawable.random);
                }
            }
        });
    }

    public void updateProgressBar() {
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    public void playSong(int songIndex) {
        // Play song
        // Play song
        try {
            if (mp == null) {
                mp = new MediaPlayer();
            }
            mp.reset();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(arrayMp3.get(songIndex).getUrlMp3());
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = arrayMp3.get(songIndex).getTitle();
            tvName.setText(songTitle);
            tvName.setTextColor(Color.parseColor("#ffffff"));
            tvTime.setText(String.valueOf(mp.getDuration() / (1000 * 60)));
            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.play);

            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration());
            getSupportActionBar().setTitle(arrayMp3.get(index).getTitle());
            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            finalTime = mp.getDuration();
            startTime = mp.getCurrentPosition();
            long startTimeMiliToSecond = TimeUnit.MILLISECONDS.toSeconds((long) startTime);
            long startTimeMiliToMinute = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
            long finalTimeMiliToSecond = TimeUnit.MILLISECONDS.toSeconds((long) finalTime);
            long finalTimeMiliToMinute = TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
            long startTimeMinuteToSecond = TimeUnit.MINUTES.toSeconds(startTimeMiliToMinute);
            long finalTimeMinuteToSecond = TimeUnit.MINUTES.toSeconds(finalTimeMiliToMinute);
            if (startTimeMiliToSecond -
                    startTimeMinuteToSecond < 10) {

                tvTiming.setText(String.format(startTimeMiliToMinute + ":0" +
                                (startTimeMiliToSecond -
                                        startTimeMinuteToSecond)
                        ,
                        startTimeMiliToMinute,
                        startTimeMiliToSecond -
                                startTimeMinuteToSecond)
                );
            } else {
                tvTiming.setText(String.format("%d:%d",
                        startTimeMiliToMinute,
                        startTimeMiliToSecond -
                                startTimeMinuteToSecond)
                );
            }
            if (finalTimeMiliToSecond -
                    finalTimeMinuteToSecond < 10) {

                tvTime.setText(String.format(finalTimeMiliToMinute + ":0" +
                                (finalTimeMiliToSecond -
                                        finalTimeMinuteToSecond)
                        ,
                        finalTimeMiliToMinute,
                        finalTimeMiliToSecond -
                                finalTimeMinuteToSecond)
                );
            } else {
                tvTime.setText(String.format("%d:%d",
                        finalTimeMiliToMinute,
                        finalTimeMiliToSecond -
                                finalTimeMinuteToSecond)
                );
            }
            seekBar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
