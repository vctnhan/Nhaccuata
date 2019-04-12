package com.example.hanwool.saleapp.Asyntask;

import android.os.AsyncTask;

import com.example.hanwool.saleapp.modal.OnlineSongHtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.hanwool.saleapp.fragment.HomeFragment.BASE_URL;

public class DownloadTaskVietNamHtml
        extends AsyncTask<String, Void, ArrayList<OnlineSongHtml>> {

    private static final String TAG = "DownloadTask";
    String linkHtml;
    ArrayList<OnlineSongHtml> arrayOnline = new ArrayList<>();
    @Override
    protected ArrayList<OnlineSongHtml> doInBackground(String... strings) {
        Document document = null;

        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Elements sub = document.select("h3.card-title");
                for (Element element : sub) {
                    OnlineSongHtml onlineSongHtml = new OnlineSongHtml();
                    Element linkSubject = element.getElementsByTag("a").first();
                    if (linkSubject != null) {
                        linkHtml = linkSubject.attr("href");
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
      //  new DownloadTaskUrlMp3().execute(BASE_URL+onlineSongHtmls.get(0).getUrlMp3Html());

    }
}