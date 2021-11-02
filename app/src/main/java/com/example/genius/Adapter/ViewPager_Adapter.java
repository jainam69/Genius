package com.example.genius.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.genius.Model.BannerModel;
import com.example.genius.R;


import java.util.List;

public class ViewPager_Adapter extends PagerAdapter {
    private final Context context;
    public List<BannerModel> list;

    public ViewPager_Adapter(Context context, List<BannerModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.imageview_list, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        Glide.with(context).load(list.get(position).getFilePath()).into(imageView);
        /*byte[] imageVal = Base64.decode(list.get(position).getBannerImageText(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);
        imageView.setImageBitmap(decodedByte);*/
        container.addView(view);
        return view;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
