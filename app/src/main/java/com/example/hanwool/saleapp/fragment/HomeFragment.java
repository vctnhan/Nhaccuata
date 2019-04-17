package com.example.hanwool.saleapp.fragment;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.hanwool.saleapp.Asyntask.DownloadTaskLosslessHtml;
import com.example.hanwool.saleapp.CheckConnectionManager;
import com.example.hanwool.saleapp.MyReceiver;
import com.example.hanwool.saleapp.R;
import com.example.hanwool.saleapp.adapter.ChartAdapter;
import com.example.hanwool.saleapp.adapter.OnlineSongAdapter;
import com.example.hanwool.saleapp.modal.OnlineSongHtml;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;
import com.example.hanwool.saleapp.myInterface.OnTaskCompleted;
import com.example.hanwool.saleapp.myInterface.OnTaskCompletedNewSong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements
        OnTaskCompleted, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    HashMap<String, String> HashMapForURL;
    HashMap<String, Integer> HashMapForLocalRes;
    SliderLayout slideAds;
    View view;
    public static final String VIETNAM_URL = "https://beta.chiasenhac.vn/mp3/vietnam.html";
    public static final String BASE_URL = "https://chiasenhac.vn";
    public static final String NEW_SONG_URL= "https://beta.chiasenhac.vn/bai-hat-moi.html";
    public static final String CHART_URL= "https://beta.chiasenhac.vn/nhac-hot.html";
    OnTaskCompleted onTaskCompleted = this;
    private ArrayList<OnlineSongUrlMp3> arrayMp3;
    DownloadTaskLosslessHtml downloadTaskLosslessHtml;
    RecyclerView lstNewSong;
    OnlineSongAdapter onlineSongAdapter;
    ProgressBar progressBar,progressBarImg, progressBarChart;
    RecyclerView lstChart;
    ChartAdapter chartAdapter;
    ScrollView scrollView;
    private BroadcastReceiver MyReceiver = null;
    public HomeFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);

        // Inflate the layout for this fragment
        Anhxa();

        return view;

    }

    private void Anhxa() {

        slideAds = view.findViewById(R.id.slideAds);
        lstNewSong = view.findViewById(R.id.lstNewSong);
        progressBar = view.findViewById(R.id.proBar);
        progressBarImg = view.findViewById(R.id.proImgBar);
        progressBarChart= view.findViewById(R.id.proBarChart);
        lstChart = view.findViewById(R.id.lstChart);
        scrollView = view.findViewById(R.id.linearLayout4);
        Drawable background = scrollView.getBackground();
        background.setAlpha(80);
if (!CheckConnectionManager.getConnectivityStatusString(getContext()).matches("No internet is available")){
    try {
        DownloadTaskLosslessHtml downloadTaskLosslessHtml = new DownloadTaskLosslessHtml(this);
        DownloadNewSongHtml downloadNewSongHtml= new DownloadNewSongHtml();
        DownloadChartHtml downloadChartHtml= new DownloadChartHtml();
        downloadTaskLosslessHtml.execute(BASE_URL);
        downloadNewSongHtml.execute(NEW_SONG_URL);
        downloadChartHtml.execute(CHART_URL);

    }
    catch (Exception e){
        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
    }
}


    }



    public void AddImagesUrlOnline(ArrayList<OnlineSongHtml> arrayList) {
        HashMapForURL = new HashMap<String, String>();
        for (int i = 0; i < 4; i++) {
            HashMapForURL.put(arrayList.get(i).getTitle()
                    , arrayList.get(i).getImage());
            //   Log.e("test: ", arrayList.get(i).getTitle());
        }

    }

    @Override
    public void onTaskCompleted(ArrayList<OnlineSongHtml> arrayList) {
        AddImagesUrlOnline(arrayList);

        for (String name : HashMapForURL.keySet()) {
            progressBarImg.setVisibility(View.GONE);
            TextSliderView textSliderView = new TextSliderView(getActivity());

            textSliderView
                    .description(name)
                    .image(HashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .empty(R.drawable.placeholderimg)
                    .error(R.drawable.errorimg)
                    .setOnSliderClickListener((BaseSliderView.OnSliderClickListener) this);

//            textSliderView.bundle(new Bundle());
//
//            textSliderView.getBundle()
//                    .putString("extra", name);

            slideAds.addSlider(textSliderView);

        }

    }


    @Override
    public void onStop() {

//        slideAds.stopAutoCycle();

        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {

        Log.d("Slider Demo", "Page Changed: " + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

// New SOng
class DownloadNewSongHtml extends AsyncTask<String, Void, ArrayList<OnlineSongHtml>> {

    private static final String TAG = "DownloadTask";
    String linkMp3, imgMp3, titleMp3, singerMp3, linkHtml;
    ArrayList<OnlineSongHtml> arrayOnline = new ArrayList<>();
    ArrayList<OnlineSongUrlMp3> arrayToLv = new ArrayList<OnlineSongUrlMp3>();

    @Override
    protected ArrayList<OnlineSongHtml> doInBackground(String... strings) {
        Document document = null;

        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Elements sub = document.select("ul.list-unstyled.list_music div.media-body.align-items-stretch.d-flex.flex-column.justify-content-between.p-0");
                for (Element element : sub) {
                    OnlineSongHtml onlineSongHtml = new OnlineSongHtml();
                    Element htmlObject = element.select("h5.media-title.mt-0.mb-0").first();
                    Element singerSub = element.select("div.author").first();
                    Element htmlSub = htmlObject.getElementsByTag("a").first();

                    if (htmlSub != null) {
                        linkHtml = htmlSub.attr("href");
                        titleMp3 = htmlSub.text();
                        onlineSongHtml.setUrlMp3Html(linkHtml);
                        onlineSongHtml.setTitle(titleMp3);

                    }
                    if (singerSub != null) {
                        singerMp3 = singerSub.text();
                        onlineSongHtml.setSinger(singerMp3);
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
        for (int i = 0; i < 6; i++) {
            new DownloadMp3Url().execute(onlineSongHtmls.get(i).getUrlMp3Html());
        Log.e("newsongsinger: ", onlineSongHtmls.get(2).getUrlMp3Html() + " " + onlineSongHtmls.size());
        }
        //   Log.e("newsong", "newsong: " + onlineSongHtmls.get(0).getSinger() + "\n" + onlineSongHtmls.size());
        // tra ve : /mp3/chinese/c-pop/em-bang-long-lam-mot-nguoi-binh-thuong-o-ben-canh-anh~vuong-that-that~tsvrw7czqa9tv1.html
    }


 class DownloadMp3Url extends AsyncTask<String, Void, ArrayList<OnlineSongUrlMp3>> {

        private static final String TAG = "DownloadTask";
        String linkMp3, imgMp3, titleMp3, singerMp3, viewMp3, downloadMp3;
        public OnTaskCompletedNewSong onTaskCompletedNewSong = null;

        public DownloadMp3Url(OnTaskCompletedNewSong onTaskCompletedNewSong) {
            this.onTaskCompletedNewSong = onTaskCompletedNewSong;
        }

        public DownloadMp3Url() {
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
           // onTaskCompletedNewSong.OnTaskCompletedNewSong(onlineSongUrlMp3s);
            Log.e(TAG, "onPostExecuteMP3: " + onlineSongUrlMp3s.get(0).getTitle() );
            progressBar.setVisibility(View.GONE);
            arrayToLv.add(new OnlineSongUrlMp3(
                    onlineSongUrlMp3s.get(0).getUrlMp3(),
                    onlineSongUrlMp3s.get(0).getImage(),
                    onlineSongUrlMp3s.get(0).getTitle(),
                    onlineSongUrlMp3s.get(0).getSinger(),
                    onlineSongUrlMp3s.get(0).getViews(),
                    onlineSongUrlMp3s.get(0).getDownloads() ));
            onlineSongAdapter = new OnlineSongAdapter(getContext(),arrayToLv);
            lstNewSong.setHasFixedSize(true);
            lstNewSong.setLayoutManager
                    (new LinearLayoutManager(getContext()));
            lstNewSong.setAdapter(onlineSongAdapter);
            //Setup data recyclerView

            //  onlineSongAdapter =  new OnlineSongAdapter(homeFragment.getContext() ,onlineSongUrlMp3s);
            //  Log.e("test1 ", ""+onlineSongAdapter.getItem(0));
//         lstNewSong.setAdapter(onlineSongAdapter);
            //  Toast.makeText(MainActivity.this, onlineSongUrlMp3s.get(0).getUrlMp3(), Toast.LENGTH_SHORT).show();

        }


    }
}
//********* Chart
class DownloadChartHtml extends AsyncTask<String, Void, ArrayList<OnlineSongHtml>>  {

    private static final String TAG = "DownloadTask";
    String titleMp3, singerMp3, linkHtml;
    ArrayList<OnlineSongHtml> arrayOnline = new ArrayList<>();
    ArrayList<OnlineSongUrlMp3> arrayToLv = new ArrayList<OnlineSongUrlMp3>();

    @Override
    protected ArrayList<OnlineSongHtml> doInBackground(String... strings) {
        Document document = null;

        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Elements sub = document.select("div.col-md-9 li.nav-item");
                for (Element element : sub) {
                    OnlineSongHtml onlineSongHtml = new OnlineSongHtml();
                    Element htmlSub = element.getElementsByTag("a").first();

                    if (htmlSub != null) {
                        linkHtml = htmlSub.attr("href");
                        titleMp3 = htmlSub.text();
                        onlineSongHtml.setUrlMp3Html(linkHtml);
                        onlineSongHtml.setTitle(titleMp3);

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
        progressBarChart.setVisibility(View.GONE);
         chartAdapter = new ChartAdapter(getContext(),onlineSongHtmls);
        lstChart.setHasFixedSize(true);
        lstChart.setLayoutManager
                (new GridLayoutManager(getContext(),2));
        lstChart.setAdapter(chartAdapter);
       Log.e("chart", "chart: " + onlineSongHtmls.get(0).getTitle() + "\n" + onlineSongHtmls.size());
        // tra ve : #cat-1
    }



    }

}

