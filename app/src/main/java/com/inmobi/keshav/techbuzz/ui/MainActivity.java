package com.inmobi.keshav.techbuzz.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inmobi.keshav.techbuzz.Info.Data;
import com.inmobi.keshav.techbuzz.Info.News;
import com.inmobi.keshav.techbuzz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.OnClick;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TECH_NEWS = "TECH_NEWS";

    private Data mData;

    private Button mTechNews;
    private View.OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTechNews = (Button) findViewById(R.id.techNewsButton);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTechNews.setOnClickListener(null);
                getNews();
                Log.d(TAG, "getNews() is running");

//                startNewsActivity(view);
//                Log.d(TAG,"getNews() is running");

            }
        };
        mTechNews.setOnClickListener(mOnClickListener);
        Log.v(TAG, "Main is running");
    }

    private void startNewsActivity() {

        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra(TECH_NEWS, mData.getNewses());
        startActivity(intent);
    }

    private void getNews() {
        String apiKey = "07e6f1d1c4d448639da8c4d3c21d84a4";
        String source = "techcrunch";
        String newsUrl = "https://newsapi.org/v1/articles?source=" + source + "&apiKey=" + apiKey;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(newsUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    mTechNews.setOnClickListener(mOnClickListener);
                    Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            mData = parseNewsDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startNewsActivity();
                                    Log.d(TAG, "listActivity() is running");

                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }finally {
                        mTechNews.setOnClickListener(mOnClickListener);
                    }

                }
            });
        } else {
            mTechNews.setOnClickListener(mOnClickListener);
            Toast.makeText(this, "Network is unavailable",
                    Toast.LENGTH_LONG).show();

        }

    }

    private Data parseNewsDetails(String jsonData) throws JSONException {
        Data data = new Data();
        data.setNewses(getNewsArticles(jsonData));

        return data;

    }

    private News[] getNewsArticles(String jsonData) throws JSONException {

        JSONObject data = new JSONObject(jsonData);
        String source = data.getString("source");
        Log.d(TAG, "Source is: " + source);
        String sortBy = data.getString("sortBy");
        Log.d(TAG, "sort by: " + sortBy);

        JSONArray articles = data.getJSONArray("articles");

        News[] news1 = new News[articles.length()];
        for (int i = 0; i < articles.length(); i++) {
            JSONObject jsonArticles = articles.getJSONObject(i);
            News news2 = new News();


            news2.setAuthor(jsonArticles.getString("author"));
            news2.setTitle(jsonArticles.getString("title"));
            news2.setDescription(jsonArticles.getString("description"));
            news2.setUrl(jsonArticles.getString("url"));
            news2.setUrlToImage(jsonArticles.getString("urlToImage"));
            news2.setPublishedAt(jsonArticles.getString("publishedAt"));

            news1[i] = news2;

        }
        return news1;

    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }


}