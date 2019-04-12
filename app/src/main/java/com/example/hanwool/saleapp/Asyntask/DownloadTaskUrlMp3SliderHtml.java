package com.example.hanwool.saleapp.Asyntask;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hanwool.saleapp.fragment.HomeFragment;
import com.example.hanwool.saleapp.modal.OnlineSongHtml;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;
import com.example.hanwool.saleapp.myInterface.OnTaskCompleteMp3Slider;
import com.example.hanwool.saleapp.myInterface.OnTaskCompleted;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.hanwool.saleapp.fragment.HomeFragment.BASE_URL;

public class DownloadTaskUrlMp3SliderHtml extends AsyncTask<String, Void, ArrayList<OnlineSongHtml>> implements OnTaskCompleteMp3Slider {

    private static final String TAG = "DownloadTask";
    String linkMp3, imgMp3, titleMp3, singerMp3, linkHtml;

    ArrayList<OnlineSongHtml> arrayOnline = new ArrayList<>();

    @Override
    protected ArrayList<OnlineSongHtml> doInBackground(String... strings) {
        Document document = null;

        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Elements sub = document.select("div.name.d-table-cell");
                for (Element element : sub) {
                    OnlineSongHtml onlineSongHtml = new OnlineSongHtml();
                    Element htmlSub = element.getElementsByTag("a").first();

                    if (htmlSub != null) {
                        linkHtml = htmlSub.attr("href");
                        onlineSongHtml.setUrlMp3Html(linkHtml);
                    }

                    //Add to list
                    arrayOnline.add(onlineSongHtml);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayOnline;
    }

    @Override
    protected void onPostExecute(ArrayList<OnlineSongHtml> onlineSongHtmls) {
        super.onPostExecute(onlineSongHtmls);
        for (int i = 0; i < onlineSongHtmls.size(); i++) {
            new DownloadMp3UrlSlider(this).execute(onlineSongHtmls.get(i).getUrlMp3Html());

        }
       // Log.e("url", "urllossless: " + onlineSongHtmls.get(0).getUrlMp3Html() + "\n" + onlineSongHtmls.size());
    }
    @Override
    public void onTaskCompleteMp3Slider(ArrayList<OnlineSongUrlMp3> arrayList) {
       // Log.e("url", "urllossless: " + arrayList.get(0).getSinger() + "\n" + arrayList.size());
    }

}

