package com.strings.layne.mod_photo_manager.method;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by laynehuang on 2016/12/19.
 * synchronous
 */

public class PhotoSaveService {

    private Bitmap bitmap = null;
    private File outputFile = null;

    public PhotoSaveService(Bitmap bitmap, File outputFile) {
        this.bitmap = bitmap;
        this.outputFile = outputFile;
    }

    public PhotoSaveService(File inputFile, File outputFile) {
        try {
            FileInputStream fis = new FileInputStream(inputFile.getAbsolutePath());
            this.bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.outputFile = outputFile;
    }

    public PhotoSaveService(Context context, String url, int width, int height, File outputFile) {
        try {
            this.bitmap = Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .into(width, height)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        this.outputFile = outputFile;
    }

    public void start() throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
