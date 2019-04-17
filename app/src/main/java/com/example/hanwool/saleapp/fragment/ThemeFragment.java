package com.example.hanwool.saleapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.adapter.ChartAdapter;
import com.example.hanwool.saleapp.adapter.ThemeAdapter;
import com.example.hanwool.saleapp.modal.OnlineSongHtml;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ThemeFragment extends Fragment {
View view;
ProgressBar progressBar;
RecyclerView lstTheme;
ThemeAdapter themeAdapter;
    public ThemeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.theme_fragment, container, false);
        Anhxa();
        return view;
    }

    private void Anhxa() {
        progressBar = view.findViewById(R.id.proBar);
        lstTheme= view.findViewById(R.id.lstTheme);
        new DownloadThemeHtml().execute(HomeFragment.VIETNAM_URL);
    }
    class DownloadThemeHtml extends AsyncTask<String, Void, ArrayList<OnlineSongHtml>> {

        private static final String TAG = "DownloadTask";
        String titleMp3, imageMp3, linkHtml;
        ArrayList<OnlineSongHtml> arrayOnline = new ArrayList<>();
        ArrayList<OnlineSongUrlMp3> arrayToLv = new ArrayList<OnlineSongUrlMp3>();

        @Override
        protected ArrayList<OnlineSongHtml> doInBackground(String... strings) {
            Document document = null;

            try {
                document = (Document) Jsoup.connect(strings[0]).maxBodySize(0).get();
                if (document != null) {
                    //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                    Elements sub = document.select("div.col-md-3 > a");
                    for (Element element : sub) {
                        OnlineSongHtml onlineSongHtml = new OnlineSongHtml();
                        Element htmlSub = element.getElementsByTag("a").first();

                        if (htmlSub != null) {
                      String img;
                            linkHtml = htmlSub.attr("href");
                            titleMp3 = htmlSub.text();
                            img = htmlSub.attr("style");
                            onlineSongHtml.setUrlMp3Html(HomeFragment.BASE_URL+linkHtml);
                            onlineSongHtml.setTitle(titleMp3);
                            imageMp3= HomeFragment.BASE_URL+img.substring( img.indexOf("/imgs"), img.indexOf("')"));;
                            onlineSongHtml.setImage(imageMp3);
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
            progressBar.setVisibility(View.GONE);
            themeAdapter = new ThemeAdapter(getContext(),onlineSongHtmls);
            lstTheme.setHasFixedSize(true);
            lstTheme.setLayoutManager
                    (new GridLayoutManager(getContext(),2));
            lstTheme.setAdapter(themeAdapter);
            Log.e("lstTheme", "lstTheme: " + onlineSongHtmls.get(0).getImage() + "\n" + onlineSongHtmls.size());
            // tra ve : #cat-1
        }



    }
}
