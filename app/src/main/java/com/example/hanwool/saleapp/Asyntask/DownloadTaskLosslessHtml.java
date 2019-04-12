package com.example.hanwool.saleapp.Asyntask;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hanwool.saleapp.modal.OnlineSongHtml;
import com.example.hanwool.saleapp.myInterface.OnTaskCompleted;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.hanwool.saleapp.fragment.HomeFragment.BASE_URL;

public class DownloadTaskLosslessHtml extends AsyncTask<String, Void, ArrayList<OnlineSongHtml>> {

    String linkHtml, singer, image, title;
    String linkMp3;
    ArrayList<OnlineSongHtml> arrayOnline = new ArrayList<>();
    // private OnTaskCompleted listener;
    public OnTaskCompleted onTaskCompleted = null;

    public DownloadTaskLosslessHtml(OnTaskCompleted onTaskCompleted) {
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected ArrayList<OnlineSongHtml> doInBackground(String... strings) {
        Document document = null;

        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Elements sub = document.
                        select("div.item > div.card.card1.slide");
                for (Element element : sub) {
                    OnlineSongHtml onlineSongHtml = new OnlineSongHtml();
                    Element cardHeader = element.select("div.card-header").first();
                    Element htmlSub = cardHeader.getElementsByTag("a").first();
                    Element titleSub = cardHeader.getElementsByTag("a").first();
                    Element cardAuthor = element.select("div.card-body > p.card-text.author").first();
                    if (htmlSub != null) {
                        linkHtml = htmlSub.attr("href");
                        onlineSongHtml.setUrlMp3Html(linkHtml);
                    }
                    if (titleSub != null) {
                        title = titleSub.attr("title");
                        onlineSongHtml.setTitle(title);
                    }
                    if (cardHeader != null) {
                        String temp = cardHeader.attr("style")
                                .toString();
                        // URL of image
                        image = temp
                                .substring(temp.indexOf("(") + 1, temp.indexOf(")"));
                        onlineSongHtml.setImage(image);
                    }
                    if (cardAuthor != null) {
                        singer = cardAuthor.text();
                        onlineSongHtml.setSinger(singer);
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
        onTaskCompleted.onTaskCompleted(onlineSongHtmls);
    //    Log.e("TEST: ", "" + onlineSongHtmls.get(0).getSinger());
        for (int i = 0; i < onlineSongHtmls.size(); i++) {
             new DownloadTaskUrlMp3SliderHtml().execute(BASE_URL + onlineSongHtmls.get(i).getUrlMp3Html());

        }

    }
}