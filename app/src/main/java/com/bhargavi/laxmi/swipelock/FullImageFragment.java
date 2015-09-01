package com.bhargavi.laxmi.swipelock;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bhargavi.laxmi.swipelock.data.ImageData;
import com.bhargavi.laxmi.swipelock.util.AnimationUtil;
import com.bhargavi.laxmi.swipelock.util.ZoomableImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;


public class FullImageFragment extends Fragment {
    private static final String EXTRA_IMAGEDATA = "imageData";

    private ImageData mImageData;
    private ZoomableImageView imageView;
    private ProgressBar mProgressBar;

    private Target mTarget;

    public static FullImageFragment newInstance(ImageData imageData) {
        FullImageFragment fragment = new FullImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_IMAGEDATA, imageData);
        fragment.setArguments(args);
        return fragment;
    }

    public FullImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageData = getArguments().getParcelable(EXTRA_IMAGEDATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_image, container, false);

        imageView = (ZoomableImageView) view.findViewById(R.id.image_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.zoom_image_loading);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadImage();
            }
        }, 250);
    }

    protected void downloadImage() {
        //Target should be a global variable so Picasso has a strong reference to it. This avoids the target being destroyed by GC
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                if (!isAdded()) {
                    return;
                }
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                imageView.setImageDrawable(drawable);
                fadeInItem();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                fadeInItem();
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        Picasso.with(getActivity()).load(new File(mImageData.getmLocation())).resize(width, height).centerInside().into(mTarget);
    }

    private void fadeInItem() {
        AnimationUtil.crossFadeViews(imageView, mProgressBar);
    }
}
