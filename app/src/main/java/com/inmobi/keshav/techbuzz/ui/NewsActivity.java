package com.inmobi.keshav.techbuzz.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.inmobi.ads.InMobiNative;
import com.inmobi.keshav.techbuzz.Info.Ad;
import com.inmobi.keshav.techbuzz.Info.News;
import com.inmobi.keshav.techbuzz.Info.NewsAndAd;
import com.inmobi.keshav.techbuzz.R;
import com.inmobi.keshav.techbuzz.adapters.TechAdapter;
import com.inmobi.keshav.techbuzz.getAds.AdCall;
import com.inmobi.keshav.techbuzz.getAds.AdConsumer;
import com.inmobi.sdk.InMobiSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NewsActivity extends ListActivity implements AdConsumer {

    private News[] mNews;
    private AdCall mAdCall = new AdCall();


    private List<NewsAndAd> mNewsAndAdList = new ArrayList<>();
    Map<Integer, InMobiNative> adMap = new ConcurrentHashMap<>();
    TechAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        InMobiSdk.init(this, "410fa5da6a504cf39d99cd70abdb90ac");
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

//        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                daysOfTheWeek);
//
//        setListAdapter(adapter);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.TECH_NEWS);
        mNews = Arrays.copyOf(parcelables, parcelables.length, News[].class);

        for (News news : mNews) {
            NewsAndAd newsAndAd = new NewsAndAd();
            newsAndAd.setNews(news);
            mNewsAndAdList.add(newsAndAd);
        }

        adapter = new TechAdapter(this, mNewsAndAdList);
        setListAdapter(adapter);
        mAdCall.getInmobiNative(4, this);
        mAdCall.getInmobiNative(8, this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("POsition", ""+position);
        InMobiNative inMobiNative = adMap.get(position);
        if (inMobiNative != null) {
            inMobiNative.reportAdClickAndOpenLandingPage(null);
        } else {
            String landingPage = mNews[position].getUrl();
            Log.d("landingUrl is: ", landingPage);

            // Toast.makeText(this,landingPage, Toast.LENGTH_LONG).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(landingPage));
            startActivity(browserIntent);
        }
    }

    @Override
    public void nativeReady(int position, InMobiNative inMobiNative) {
        NewsAndAd newsAndAd = parseNative(inMobiNative);
        if (newsAndAd != null) {
            adMap.put(position, inMobiNative);
            mNewsAndAdList.add(position, newsAndAd);
            adapter.notifyDataSetChanged();
        }
    }

    public Map<Integer, InMobiNative> getAdMap() {
        return adMap;
    }

    private NewsAndAd parseNative(InMobiNative inMobiNative) {
        NewsAndAd newsAndAd = null;
        Ad ad = new Ad();
        JSONObject content = null;
        try {
            content = new JSONObject((String) inMobiNative.getAdContent());
            String icon = content.getJSONObject("icon_xhdpi").getString("url");
            Log.d("URL", icon);
            String title = content.getString("title");
            String cta = content.getString("cta_install");
            String description = content.getString("subtitle");
            ad.setDescription(description);
            ad.setUrl(icon);
            ad.setTitle(title);
            ad.setRating(cta);
            newsAndAd = new NewsAndAd();
            newsAndAd.setInMobiNative(ad);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsAndAd;
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Map.Entry<Integer, InMobiNative> mapEntry : adMap.entrySet()) {
            InMobiNative inMobiNative = mapEntry.getValue();
            inMobiNative.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (Map.Entry<Integer, InMobiNative> mapEntry : adMap.entrySet()) {
            InMobiNative inMobiNative = mapEntry.getValue();
            inMobiNative.pause();
        }
    }
}
