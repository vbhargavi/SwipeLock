package com.bhargavi.laxmi.swipelock.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by laxmi on 8/23/15.
 */
public class ImageDataManager {
    private ArrayList<ImageData> mPhotoSources;
    private HashMap<String, AlbumData> mAlbums;

    private ImageDataManager() {

    }

    private static ImageDataManager sInstance;

    public static synchronized ImageDataManager getInstance() {
        if (sInstance == null) {
            sInstance = new ImageDataManager();
        }

        return sInstance;
    }

    public void loadData(Cursor data) {
        mPhotoSources = new ArrayList<>();
        mAlbums = new HashMap<>();
        while (data != null && data.moveToNext()) {
            String albumName = data.getString(2);
            ImageData photo = new ImageData(data.getLong(0), data.getString(1), albumName, data.getString(3),
                    data.getString(4), data.getLong(5));
            /*
             * Add to main list
             */
            mPhotoSources.add(photo);
            /*
             * Add to album
             */
            AlbumData albumData = mAlbums.get(albumName);
            if (albumData == null) {
                albumData = new AlbumData(albumName);
                mAlbums.put(albumName, albumData);
            }
            albumData.add(photo);
        }
    }

    public ArrayList<AlbumData> getAlbums() {
        if (mAlbums != null) {
            return new ArrayList<>(mAlbums.values());
        }

        return new ArrayList<>();
    }

    public ArrayList<ImageData> getAlbumImages(String albumName) {
        if (mAlbums != null && albumName != null && mAlbums.get(albumName) != null) {
            return mAlbums.get(albumName).getPhotos();
        }

        return new ArrayList<>();
    }

    public void selectAll(boolean isSelected) {
        for (ImageData imageData : mPhotoSources) {
            imageData.setIsSelected(isSelected);
        }
    }

    public ArrayList<ImageData> getAllImages() {
        return mPhotoSources;
    }

    public ArrayList<ImageData> getSelectedImages() {
        ArrayList<ImageData> selectedImages = new ArrayList<>();
        if (mPhotoSources != null) {
            for (ImageData imageData : mPhotoSources) {
                if (imageData.isSelected()) {
                    selectedImages.add(imageData);
                }
            }
        }

        return selectedImages;
    }

    public int getSelectionCount() {
        int count = 0;
        if (mPhotoSources != null) {
            for (ImageData imageData : mPhotoSources) {
                if (imageData.isSelected()) {
                    count++;
                }
            }
        }
        return count;
    }
}
