package com.strings.layne.mod_photo_manager.domain;

import java.io.Serializable;

/**
 * Created by laynehuang on 2016/12/12.
 * Photo (table)
 */

public class Photo implements Serializable {

    private String previewUrl;
    private String webformatUrl;
    private String localUrl;
    private int webformatWidth;
    private int webformatHeight;
    private int degree;
    private boolean isSelected;
    private boolean isOriginal;
    private String tags;

    public Photo() {
    }

    public Photo(String previewUrl, String webformatUrl, String localUrl, int webformatWidth, int webformatHeight, int degree, boolean isSelected, boolean isOriginal, String tags) {
        this.previewUrl = previewUrl;
        this.webformatUrl = webformatUrl;
        this.localUrl = localUrl;
        this.webformatWidth = webformatWidth;
        this.webformatHeight = webformatHeight;
        this.degree = degree;
        this.isSelected = isSelected;
        this.isOriginal = isOriginal;
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getWebformatUrl() {
        return webformatUrl;
    }

    public void setWebformatUrl(String webformatUrl) {
        this.webformatUrl = webformatUrl;
    }

    public int getWebformatWidth() {
        return webformatWidth;
    }

    public void setWebformatWidth(int webformatWidth) {
        this.webformatWidth = webformatWidth;
    }

    public int getWebformatHeight() {
        return webformatHeight;
    }

    public void setWebformatHeight(int webformatHeight) {
        this.webformatHeight = webformatHeight;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }
}