package com.strings.layne.mod_photo_manager.album;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.R2;
import com.strings.layne.mod_photo_manager.album.adapter.MyViewPagerAdapter;
import com.strings.layne.mod_photo_manager.album.view.MyViewPager;
import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.method.PhotosAccessor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusActivity;

/**
 * DisplayActivity
 * Created by laynehuang on 2017/1/4.
 */

@RequiresPresenter(DisplayPresenter.class)
public class DisplayActivity extends NucleusActivity<DisplayPresenter> {

    private static final String TAG = "DisplayActivity";
    private static final int DEFAULT_SELECT_COUNT = 9;

    @BindView(R2.id.vp_display_photos)
    MyViewPager viewPager;

    @BindView(R2.id.mv_display_selected_photo)
    ImageView mvSelectedPhoto;
    @BindView(R2.id.tv_display_title)
    TextView tvTitle;
    @BindView(R2.id.tv_display_done)
    TextView tvDone;
    @BindView(R2.id.mv_display_original_photo)
    ImageView mvOriginalPhoto;
    /**
     * 是否选当前这张图
     */
    private boolean isSelected = false;
    /**
     * 是否选当前这张图的原图
     */
    private boolean isOriginal = false;
    private DisplayPresenter presenter = null;
    private MyViewPagerAdapter adapter = null;
    private int nowPosition = 0;
    private int maxSelectCount = 0;

    @Override
    protected void onCreate(Bundle savedState) {

        super.onCreate(savedState);
        setContentView(R.layout.activity_display);
        ButterKnife.bind(this);
        Log.w(TAG, "on Create...");
        nowPosition = getIntent().getIntExtra(Constant.DISPLAY_POSITION, 0);
        maxSelectCount = getIntent().getIntExtra(Constant.SELECT_COUNT, DEFAULT_SELECT_COUNT);
        presenter = getPresenter();
        presenter.start(this, nowPosition);
    }

    public void viewPagerInit() {

        // Caching count
        viewPager.setOffscreenPageLimit(2);
        Log.d(TAG, nowPosition + "");
        adapter = new MyViewPagerAdapter(this);
        adapter.addAllPhotos(PhotosAccessor.getInstance().getPhotos());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(nowPosition);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //   Toast.makeText(DisplayActivity.this,"on page " + position + "..." , Toast.LENGTH_SHORT ).show();
                Log.d(TAG, "on page " + position + "...");
                nowPosition = position;
                presenter.setTitle(position);
                presenter.setUISelected(position);
                presenter.setUIOriginal(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void refreshCheckBox(boolean b) {
        isSelected = b;
        if (b) {
            mvSelectedPhoto.setImageResource(R.drawable.ic_album_select_y);
        } else {
            mvSelectedPhoto.setImageResource(R.drawable.ic_album_select_n);
        }
    }

    public void refreshOriginalMv(boolean b) {
        isOriginal = b;
        if (b) {
            mvOriginalPhoto.setImageResource(R.drawable.ic_album_original_y);
        } else {
            mvOriginalPhoto.setImageResource(R.drawable.ic_album_original_n);
        }
    }

    public void refreshTitle(int position, int total) {
        setTitle(position, total);
    }

    public void refreshBtnDone(int selectedCount) {
        setBtnDoneText(selectedCount);
    }

    public void setTitle(int position, int total) {
        tvTitle.setText(position + "/" + total);
    }

    public void setBtnDoneText(int count) {
        if (count > 0) {
            tvDone.setText(String.format(getString(R.string.album_sends), count));
            tvDone.setTextColor(ContextCompat.getColor(this, R.color.colorActionOptAble));
        } else {
            tvDone.setText(R.string.camera_send);
            tvDone.setTextColor(ContextCompat.getColor(this, R.color.colorActionOptDisable));
        }
    }

    public void selectOverHint() {
        mvSelectedPhoto.setImageResource(R.drawable.ic_album_select_n);
        Toast.makeText(this, String.format(getString(R.string.album_max_sends), maxSelectCount), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R2.id.mv_display_cancel)
    void finishSelected() {
        presenter.doneSelected(false);
    }

    @OnClick(R2.id.tv_display_done)
    void finishSelectedAndDone() {
        presenter.doneSelected(true);
    }

    @OnClick(R2.id.mv_display_selected_photo)
    void clickSelectPhoto() {
        isSelected = !isSelected;
        refreshCheckBox(isSelected);
        Log.d(TAG, "you clicking checkbox.." + " " + isSelected);
        presenter.setDBSelected(nowPosition, isSelected);
        presenter.setBtnDone();
        /**
         * 取消选择需要取消原图选择
         */
        if (!isSelected && isOriginal) {
            clickOriginal();
        }
    }

    @OnClick(R2.id.mv_display_original_photo)
    void clickOriginal() {
        isOriginal = !isOriginal;
        refreshOriginalMv(isOriginal);
        presenter.setDBOriginal(nowPosition, isOriginal);
        /**
         * 原图选择时候,若图片未选,要选上图片
         * 取消原图选择不需要取消选上图片
         */
        if (isOriginal && !isSelected) {
            clickSelectPhoto();
        }
    }
}
