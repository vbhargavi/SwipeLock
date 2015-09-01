package com.bhargavi.laxmi.swipelock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bhargavi.laxmi.swipelock.data.ImageData;
import com.bhargavi.laxmi.swipelock.data.ImageDataManager;
import com.bhargavi.laxmi.swipelock.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by laxmi on 7/30/15.
 */
public class ImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<ImageData> mImageData;

    private boolean isSelectionMode;

    private ImageAdapterInterface mListener;

    public interface ImageAdapterInterface {
        void onItemClicked(int position);
    }

    public void setImageAdapterListener(ImageAdapterInterface listener) {
        mListener = listener;
    }

    public ImageAdapter(Context context, ArrayList<ImageData> imageData) {
        mContext = context;
        mImageData = imageData;
    }

    public void setIsSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if (!isSelectionMode) {
            ImageDataManager.getInstance().selectAll(false);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.update(i);
    }

    @Override
    public int getItemCount() {
        if (mImageData != null) {
            return mImageData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Context mContext;
        private View selectedView;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            imageView = (ImageView) itemView.findViewById(R.id.image_imageview);
            selectedView = itemView.findViewById(R.id.select_view);
        }

        public void update(final int position) {
            final ImageData imageData = mImageData.get(position);
            Picasso.with(mContext)
                    .load(new File(imageData.getmLocation()))
                    .placeholder(R.drawable.placeholder)
                    .resize(200, 200)
                    .centerCrop()
                    .into(imageView);

            selectedView.setVisibility(imageData.isSelected() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectionMode) {
                        imageData.setIsSelected(!imageData.isSelected());
                    }

                    if (mListener != null) {
                        mListener.onItemClicked(position);
                    }

                    if (imageData.isSelected()) {
                        selectedView.setVisibility(View.VISIBLE);
                    } else {
                        selectedView.setVisibility(View.GONE);
                    }

                }
            });
        }
    }


}
