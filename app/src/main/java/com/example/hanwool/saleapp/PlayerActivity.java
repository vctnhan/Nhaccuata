package com.example.hanwool.saleapp;

import android.content.Context;
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
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hanwool.saleapp.adapter.PlayerFragmentAdapter;
import com.example.hanwool.saleapp.adapter.ViewPagerPlayerFragmentAdapter;
import com.example.hanwool.saleapp.fragment.Disc_Fragment;
import com.example.hanwool.saleapp.modal.Song;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    public static MediaPlayer mp;
    Toolbar toolbarPlaynhac;
    ViewPager viewPagerPlaynhac;
    public static ArrayList<Song> arraySong;
    public static Bitmap songImage;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    Disc_Fragment disc_fragment;
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

    int position;
    Uri u;
    public static ViewPagerPlayerFragmentAdapter viewPagerPlayerFragmentAdapter;
    Thread updateSeekBar;

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

        btnShuffle = findViewById(R.id.shuffle);
        btnRepeat = findViewById(R.id.repeat);
        btnBackward = findViewById(R.id.backward);
        btnForward = findViewById(R.id.forward);
        btnPlay = findViewById(R.id.play);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvTiming = findViewById(R.id.tvTiming);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        arraySong = (ArrayList) b.getParcelableArrayList("arraySong");
        position = b.getInt("index", 0);
//        Bundle bundle = new Bundle();
//        bundle.putString("songImageString", arraySong.get(position).location);
//set Fragmentclass Arguments
        disc_fragment = new Disc_Fragment();
        //disc_fragment.setArguments(bundle);
        viewPagerPlayerFragmentAdapter = new ViewPagerPlayerFragmentAdapter(getSupportFragmentManager());
//        viewPagerPlayerFragmentAdapter.AddFragment(disc_fragment);
        viewPagerPlaynhac.setAdapter(viewPagerPlayerFragmentAdapter);
        disc_fragment = (Disc_Fragment) viewPagerPlayerFragmentAdapter.getItem(1);


        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(arraySong.get(position).getLocation());
            byte[] art = metaRetriver.getEmbeddedPicture();
            songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            disc_fragment.imgDisc.setImageBitmap(songImage);
            disc_fragment.objectAnimator.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
        }


//
        //  Toast.makeText(getApplicationContext(),arraySong.get(position).getName(), Toast.LENGTH_SHORT).show();
        if (mp != null) {
            mp.stop();
        }
        if (PlayerOnlineActivity.mp !=null){
            PlayerOnlineActivity.mp.stop();
        }
        mp = new MediaPlayer();

        u = Uri.parse(arraySong.get(position).getLocation().toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        playSong(position);
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (isShuffle) {
                    Random rand = new Random();
                    position = rand.nextInt((arraySong.size() - 1) - 0 + 1) + 0;
                    playSong(position);
                } else if (isRepeat) {
                    playSong(position);
                } else {
                    mp.reset();
                    position++;
                    if (position >= arraySong.size())
                        position = 0;
                    playSong(position);
                }
            }
        });
        updateSeekBar = new Thread() {
            @Override
            public void run() {

                int totalDuration = mp.getDuration();
                int curentPosition = 0;
                while (curentPosition < totalDuration) {
                    try {
                        sleep(5000);
                        curentPosition = mp.getCurrentPosition();
                        seekBar.setProgress(curentPosition);
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
                        Disc_Fragment.objectAnimator.pause();
                    }
                    finalTime = mp.getDuration();
                    startTime = mp.getCurrentPosition();

                    if (oneTimeOnly == 0) {
                        seekBar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Disc_Fragment.objectAnimator.resume();
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
                    oneTimeOnly = rand.nextInt((arraySong.size() - 1) - 0 + 2) + 0;
                    playSong(oneTimeOnly);
                    seekBar.setMax(mp.getDuration());

                } else {
                    mp.stop();
                    mp.release();
                    position = (position + 1) % arraySong.size();
                    u = Uri.parse(arraySong.get(position).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);

                    playSong(position);
                    seekBar.setMax(mp.getDuration());
                }

                try {
                    MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
                    metaRetriver.setDataSource(arraySong.get(position).getLocation());
                    byte[] art = metaRetriver.getEmbeddedPicture();
                    songImage = BitmapFactory.decodeByteArray(art, 0, art.length);

//Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
                    disc_fragment.imgDisc.setImageBitmap(songImage);
                    disc_fragment.objectAnimator.start();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setImageResource(R.drawable.pause);
                mp.stop();
                mp.release();
                position = (position - 1 < 0) ? arraySong.size() - 1 : position - 1;

                u = Uri.parse(arraySong.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                playSong(position);
                seekBar.setMax(mp.getDuration());

                try {
                    MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
                    metaRetriver.setDataSource(arraySong.get(position).getLocation());
                    byte[] art = metaRetriver.getEmbeddedPicture();
                    songImage = BitmapFactory.decodeByteArray(art, 0, art.length);

//Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
                    disc_fragment.imgDisc.setImageBitmap(songImage);
                    disc_fragment.objectAnimator.start();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
                }
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
            mp.setDataSource(arraySong.get(songIndex).getLocation().toString());

            mp.prepare();
            mp.start();


            // Displaying Song title
            String songTitle = arraySong.get(songIndex).getName();
            tvName.setText(songTitle);
            tvName.setTextColor(Color.parseColor("#ffffff"));

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.play);

            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration());

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

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}

