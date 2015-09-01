package com.bhargavi.laxmi.swipelock.data;

import java.util.ArrayList;

/**
 * Created by laxmi on 8/24/15.
 */
public class AlbumData {

    private String mAlbumName;
    private int cnt;
    private ArrayList<ImageData> mPhotos;

    public AlbumData(String albumName) {
        mAlbumName = albumName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public void add(ImageData imageData) {
        if (mPhotos == null) {
            mPhotos = new ArrayList<>();
        }
        mPhotos.add(imageData);
        cnt = mPhotos.size();
    }

    public ArrayList<ImageData> getPhotos() {
        return mPhotos;
    }
}
