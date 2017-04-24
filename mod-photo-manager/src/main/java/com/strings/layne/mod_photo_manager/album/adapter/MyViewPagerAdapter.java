package com.strings.layne.mod_photo_manager.album.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.strings.layne.mod_photo_manager.domain.Photo;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by laynehuang on 2017/1/5.
 * MyViewPagerAdapter
 */

public class MyViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Photo> photos = new ArrayList<Photo>();

    public MyViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        PhotoView photoView = new PhotoView(context);
        Glide.with(context).load(photos.get(position).getLocalUrl()).asBitmap().into(photoView);
        view.addView(photoView);
        return photoView;
    }

    public void addAllPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
}