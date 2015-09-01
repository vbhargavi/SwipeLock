package com.bhargavi.laxmi.swipelock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhargavi.laxmi.swipelock.data.AlbumData;
import com.bhargavi.laxmi.swipelock.data.ImageData;
import com.bhargavi.laxmi.swipelock.data.ImageDataManager;
import com.bhargavi.laxmi.swipelock.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by laxmi on 8/24/15.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AlbumData> mAlbums;

    private AlbumAdapterListener mListener;

    public interface AlbumAdapterListener {
        void onItemClicked(AlbumData albumData);
    }

    public void setAlbumAdapterListener(AlbumAdapterListener listener) {
        mListener = listener;
    }

    public AlbumAdapter(Context context) {
        mContext = context;
        mAlbums = ImageDataManager.getInstance().getAlbums();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.update(i);
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Context mContext;
        private TextView albumName;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            imageView = (ImageView) itemView.findViewById(R.id.image_imageview);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
        }

        public void update(final int position) {
            final AlbumData albumData = mAlbums.get(position);
            ArrayList<ImageData> imageDataArrayList = albumData.getPhotos();

            if (imageDataArrayList != null && imageDataArrayList.size() > 0) {
                final ImageData imageData = imageDataArrayList.get(0);
                Picasso.with(mContext)
                        .load(new File(imageData.getmLocation()))
                        .placeholder(R.drawable.placeholder)
                        .resize(200, 200)
                        .centerCrop()
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.placeholder);
            }

            albumName.setText("(" + albumData.getCnt() + ") " + albumData.getAlbumName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClicked(albumData);
                    }

                }
            });
        }
    }
}
