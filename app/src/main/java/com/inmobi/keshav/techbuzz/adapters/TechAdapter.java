package com.inmobi.keshav.techbuzz.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inmobi.ads.InMobiNative;
import com.inmobi.keshav.techbuzz.Info.Ad;
import com.inmobi.keshav.techbuzz.Info.News;
import com.inmobi.keshav.techbuzz.Info.NewsAndAd;
import com.inmobi.keshav.techbuzz.R;
import com.inmobi.keshav.techbuzz.ui.MainActivity;
import com.inmobi.keshav.techbuzz.ui.NewsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by keshav.p on 10/12/16.
 */
public class TechAdapter extends ArrayAdapter<NewsAndAd>{
    private Context mContext;
    private List<NewsAndAd> mNewsAndAdList;

    public TechAdapter(Context context, List<NewsAndAd> newsAndAdList){
        super(context, R.layout.tech_list_item);
        mContext= context;
        mNewsAndAdList = newsAndAdList;
    }

    @Override
    public int getCount() {
        return mNewsAndAdList.size();
    }

    @Override
    public NewsAndAd getItem(int i) {
        return mNewsAndAdList.get(i);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        NewsAndAd newsAndAd = getItem(i);
        if(view == null || view.getTag() == null) {
            //brand new
            view = LayoutInflater.from(mContext).inflate(R.layout.tech_list_item, null);
            holder = new ViewHolder();
            holder.articleImage = (ImageView) view.findViewById(R.id.articleImageView);
            holder.title = (TextView) view.findViewById(R.id.titleTextView);
            holder.description = (TextView) view.findViewById(R.id.descriptionTextView);
            holder.author = (TextView) view.findViewById(R.id.authorTextView);
            holder.tvAd = (TextView) view.findViewById(R.id.tvAd);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        if(newsAndAd.getad() == null){
            News news = newsAndAd.getNews();
            Picasso.with(mContext).load(news.getUrlToImage()).placeholder(R.drawable.classic).resize(100, 100)
                    .centerCrop().into(holder.articleImage);
            holder.title.setText(news.getTitle());
            holder.description.setText(news.getDescription());
            holder.author.setText(news.getAuthor());
            holder.tvAd.setVisibility(View.GONE);
        } else {
            Ad ad = newsAndAd.getad();
            Picasso.with(mContext).load(ad.getUrl()).placeholder(R.drawable.classic).resize(100, 100)
                    .centerCrop().into(holder.articleImage);
            holder.title.setText(ad.getTitle());
            holder.description.setText(ad.getDescription());
            holder.author.setText("INSTALL");
            InMobiNative.bind(view,((NewsActivity) mContext).getAdMap().get(i));
            holder.tvAd.setVisibility(View.VISIBLE);

        }

        return view;
    }

    public static class ViewHolder{
        ImageView articleImage;
        TextView title;
        TextView description;
        TextView author;
        TextView tvAd;

    }
}
