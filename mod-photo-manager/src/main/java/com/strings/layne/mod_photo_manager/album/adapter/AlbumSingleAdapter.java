package com.strings.layne.mod_photo_manager.album.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.R2;
import com.strings.layne.mod_photo_manager.album.AlbumActivity;
import com.strings.layne.mod_photo_manager.camera.LocalDisplay;
import com.strings.layne.mod_photo_manager.domain.Photo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.strings.layne.mod_photo_manager.camera.Constant.RESULT_SINGLE_IMAGE_PATH;

/**
 * AlbumSingleAdapter
 * Created by laynehuang on 2017/4/18.
 */

public class AlbumSingleAdapter extends RecyclerView.Adapter<AlbumSingleAdapter.MyViewHolder> {

    private ArrayList<Photo> photos = new ArrayList<>();
    private AlbumActivity albumActivity;
    private LayoutInflater inflater;                // layoutInflater 用来加载布局的

    public AlbumSingleAdapter(AlbumActivity albumActivity) {
        this.albumActivity = albumActivity;
        inflater = LayoutInflater.from(albumActivity);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ViewGroup.LayoutParams layoutParams = holder.mv.getLayoutParams();
        layoutParams.height = LocalDisplay.SCREEN_WIDTH_PIXELS / 4;
        holder.mv.setLayoutParams(layoutParams);

        String url = photos.get(position).getLocalUrl();
        Glide.with(albumActivity).load(url).asBitmap().centerCrop().into(holder.mv);

        holder.mv.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(RESULT_SINGLE_IMAGE_PATH, photos.get(position).getLocalUrl());
            albumActivity.setResult(Activity.RESULT_OK, intent);
            albumActivity.finish();
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_single_local_photo, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public void refreshPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.mv_photo)
        ImageView mv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
