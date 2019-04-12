package com.example.hanwool.saleapp.Asyntask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.hanwool.saleapp.MainActivity;
import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.adapter.OnlineSongAdapter;
import com.example.hanwool.saleapp.fragment.HomeFragment;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;
import com.example.hanwool.saleapp.myInterface.OnTaskCompleteMp3Slider;
import com.example.hanwool.saleapp.myInterface.OnTaskCompleted;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static android.content.Intent.getIntent;
import static android.content.Intent.parseIntent;

public class DownloadMp3UrlSlider extends AsyncTask<String, Void, ArrayList<OnlineSongUrlMp3>> {

    private static final String TAG = "DownloadTask";
    String linkMp3, imgMp3, titleMp3, singerMp3, viewMp3, downloadMp3;
    public OnTaskCompleteMp3Slider onTaskCompleteMp3Slider = null;
OnlineSongAdapter onlineSongAdapter;
ListView lstNewSong;
HomeFragment homeFragment;


    public DownloadMp3UrlSlider(OnTaskCompleteMp3Slider onTaskCompleteMp3Slider) {
        this.onTaskCompleteMp3Slider = onTaskCompleteMp3Slider;
    }
    public DownloadMp3UrlSlider() {
    }


    @Override
    protected ArrayList<OnlineSongUrlMp3> doInBackground(String... strings) {
        Document document = null;
        ArrayList<OnlineSongUrlMp3> arrayOnlineMp3 = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                OnlineSongUrlMp3 onlineSongUrlMp3 = new OnlineSongUrlMp3();

                Element linkSubject = document.select("div.col-12 li:nth-child(1)>a").first();
                Element imgSubject = document.select("div.card.card-details img").first();
                Element titleSubject =
                        document.select
                                ("div.col-md-4 > div.card.card-details > div.card-body > h4").first();
                Element singerSubject =
                        document.select
                                ("div.col-md-4 > div.card.card-details > div.card-body > ul.list-unstyled>li:nth-child(1)").first();
                Element viewNdownloadSubject =
                        document.select
                                ("div.d-flex.justify-content-between.mb-3.box1.music-listen-title> span.d-flex.listen").first();

                if (linkSubject != null) {
                    linkMp3 = linkSubject.attr("href");
                    onlineSongUrlMp3.setUrlMp3(linkMp3);
                }
                if (imgSubject != null) {
                    imgMp3 = imgSubject.attr("src");
                    onlineSongUrlMp3.setImage(imgMp3);
                }
                if (titleSubject != null) {
                    titleMp3 = titleSubject.text();
                    onlineSongUrlMp3.setTitle(titleMp3);
                }
                if (singerSubject != null) {
                    singerMp3 = singerSubject.text();
                    onlineSongUrlMp3.setSinger(singerMp3);
                }
//                if (imgSubject != null) {
//                    imgMp3 = imgSubject.attr("src");
//                    onlineSongUrlMp3.setImage(imgMp3);
//                }
                if (viewNdownloadSubject != null) {
                    String a = viewNdownloadSubject.text();
                    String[] words = a.split("\\s", 0);
                    viewMp3 = words[1];
                    downloadMp3 = words[4];
                    onlineSongUrlMp3.setViews(viewMp3);
                    onlineSongUrlMp3.setDownloads(downloadMp3);
                }
                //Add to list
                arrayOnlineMp3.add(onlineSongUrlMp3);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayOnlineMp3;
    }

    @Override
    protected void onPostExecute(ArrayList<OnlineSongUrlMp3> onlineSongUrlMp3s) {
        super.onPostExecute(onlineSongUrlMp3s);
onTaskCompleteMp3Slider.onTaskCompleteMp3Slider(onlineSongUrlMp3s);
        //Setup data recyclerView

         //  onlineSongAdapter =  new OnlineSongAdapter(homeFragment.getContext() ,onlineSongUrlMp3s);
      //  Log.e("test1 ", ""+onlineSongAdapter.getItem(0));
//         lstNewSong.setAdapter(onlineSongAdapter);
        //  Toast.makeText(MainActivity.this, onlineSongUrlMp3s.get(0).getUrlMp3(), Toast.LENGTH_SHORT).show();
      //  Log.e("test: ", onlineSongUrlMp3s.get(0).getViews() + " " + onlineSongUrlMp3s.size());
    }


}