package com.strings.layne.mod_photo_manager.album.adapter;

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
import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.camera.LocalDisplay;
import com.strings.layne.mod_photo_manager.croper.CropActivity;
import com.strings.layne.mod_photo_manager.domain.Photo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumCropAdapter extends RecyclerView.Adapter<AlbumCropAdapter.MyViewHolder> {

    private ArrayList<Photo> photos = new ArrayList<>();
    private AlbumActivity albumActivity = null;
    private LayoutInflater inflater;                // layoutInflater 用来加载布局的

    public AlbumCropAdapter(AlbumActivity albumActivity) {
        this.albumActivity = albumActivity;
        inflater = LayoutInflater.from(albumActivity);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onBindViewHolder(final AlbumCropAdapter.MyViewHolder holder, final int position) {
        ViewGroup.LayoutParams layoutParams = holder.mv.getLayoutParams();
        layoutParams.height = LocalDisplay.SCREEN_WIDTH_PIXELS / 4;
        holder.mv.setLayoutParams(layoutParams);

        String url = photos.get(position).getLocalUrl();
        Glide.with(albumActivity).load(url).asBitmap().centerCrop().into(holder.mv);

        holder.mv.setOnClickListener(view -> {
            // Todo : crop the single photo for result
            Intent intent = new Intent(albumActivity, CropActivity.class);
            intent.putExtra(Constant.REQUEST_SINGLE_IMAGE_PATH, photos.get(position).getLocalUrl());
            intent.putExtra(Constant.CROP_TYPE, albumActivity.getREQUEST_TYPE().ordinal());
            intent.putExtra(Constant.CROP_FROM, Constant.ALBUM_CROP);
            albumActivity.startActivityForResult(intent, Constant.REQUEST_CROP);
        });
    }

    @Override
    public AlbumCropAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_crop_local_photo, parent, false);
        AlbumCropAdapter.MyViewHolder holder = new AlbumCropAdapter.MyViewHolder(view);
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