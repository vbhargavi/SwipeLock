package com.bhargavi.laxmi.swipelock.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by laxmi on 7/28/15.
 */
public class ImageData implements Parcelable {

    private long mId;
    private String mLocation;
    private String mAlbumName;
    private String mImageName;
    private String mMimeType;
    private long mSize;
    private boolean isSelected;


    public ImageData(long id, String data, String albumName, String imageName, String mimeType, long size) {

        mId = id;
        mLocation = data;
        mAlbumName = albumName;
        mImageName = imageName;
        mMimeType = mimeType;
        mSize = size;


    }

    public long getmId() {
        return mId;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public String getmImageName() {
        return mImageName;
    }

    public String getmMimeType() {
        return mMimeType;
    }

    public long getmSize() {
        return mSize;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mLocation);
        dest.writeString(this.mAlbumName);
        dest.writeString(this.mImageName);
        dest.writeString(this.mMimeType);
        dest.writeLong(this.mSize);
    }

    protected ImageData(Parcel in) {
        this.mId = in.readLong();
        this.mLocation = in.readString();
        this.mAlbumName = in.readString();
        this.mImageName = in.readString();
        this.mMimeType = in.readString();
        this.mSize = in.readLong();
    }

    public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };
}
