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
import com.strings.layne.mod_photo_manager.album.DisplayActivity;
import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.camera.LocalDisplay;
import com.strings.layne.mod_photo_manager.domain.Photo;
import com.strings.layne.mod_photo_manager.method.PhotosAccessor;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AlbumMultiAdapter
 * Created by laynehuang on 2017/1/4.
 */
public class AlbumMultiAdapter extends RecyclerView.Adapter<AlbumMultiAdapter.MyViewHolder> {

    private ArrayList<Photo> photos = new ArrayList<>();
    private AlbumActivity albumActivity;
    private LayoutInflater inflater;                // layoutInflater 用来加载布局的
    private boolean isViewCover = false;

    public AlbumMultiAdapter(AlbumActivity albumActivity) {
        this.albumActivity = albumActivity;
        inflater = LayoutInflater.from(albumActivity);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ViewGroup.LayoutParams layoutParams = holder.mv.getLayoutParams();
        layoutParams.height = LocalDisplay.SCREEN_WIDTH_PIXELS / 4;
        holder.mv.setLayoutParams(layoutParams);

        String url = photos.get(position).getLocalUrl();
        Glide.with(albumActivity).load(url).asBitmap().centerCrop().into(holder.mv);

        if (photos.get(position).isSelected()) {
            holder.cb.setImageResource(R.drawable.ic_album_select_y);
        } else {
            holder.cb.setImageResource(R.drawable.ic_album_select_n);
        }

        holder.cover.setVisibility(isViewCover && !photos.get(position).isSelected() ?
                View.VISIBLE :
                View.GONE);

        holder.mv.setOnClickListener(view -> {
            Intent intent = new Intent(albumActivity, DisplayActivity.class);
            intent.putExtra(Constant.DISPLAY_POSITION, position);
            intent.putExtra(Constant.SELECT_COUNT, PhotosAccessor.getInstance().getMaxSelectCount());
            albumActivity.startActivityForResult(intent, Constant.REQUEST_MULTI_SEND);
        });
        /*
            take care ,,, you need to modify the data in adapter and db
         */
        holder.cb.setOnClickListener(view -> {

            Boolean b = !photos.get(position).isSelected();
            boolean result = PhotosAccessor.getInstance().setSelected(position, b);

            if (!result) {
                holder.cb.setImageResource(R.drawable.ic_album_select_n);
                photos.get(position).setSelected(false);
                albumActivity.selectOverHint();

            } else {

                if (b) {
                    holder.cb.setImageResource(R.drawable.ic_album_select_y);
                } else {
                    holder.cb.setImageResource(R.drawable.ic_album_select_n);
                }

                photos.get(position).setSelected(b);

                int nowSelectCount = PhotosAccessor.getInstance().getSelectedPhotosCount();
                boolean shouldBe = false;

                if (nowSelectCount == PhotosAccessor.getInstance().getMaxSelectCount()) {
                    shouldBe = true;
                }

                if (isViewCover != shouldBe) {
                    isViewCover = shouldBe;
                    this.notifyDataSetChanged();
                }

            }
            albumActivity.setBtnDoneText(PhotosAccessor.getInstance().getSelectedPhotosCount());
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_multi_local_photo, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public void refreshPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.mv_show_photo)
        ImageView mv;
        @BindView(R2.id.mv_select_photo)
        ImageView cb;

        @BindView(R2.id.mv_photo_cover)
        View cover;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
