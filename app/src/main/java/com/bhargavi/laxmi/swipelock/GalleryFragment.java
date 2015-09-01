package com.bhargavi.laxmi.swipelock;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhargavi.laxmi.swipelock.adapter.ImageAdapter;
import com.bhargavi.laxmi.swipelock.data.ImageData;
import com.bhargavi.laxmi.swipelock.data.ImageDataManager;
import com.bhargavi.laxmi.swipelock.events.ActionModeEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private static final String EXTRA_ALBUM_NAME = "album_name";

    private ImageAdapter imageAdapter;
    private RecyclerView recyclerView;

    private ArrayList<ImageData> mImageDataArrayList;

    private MainActivity mMainActivity;

    public static GalleryFragment newInstance(String albumName) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ALBUM_NAME, albumName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PickLockApplication.eventBus.register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_column_count));
        recyclerView.setLayoutManager(glm);

        Bundle bundle = getArguments();
        String albumName = bundle.getString(EXTRA_ALBUM_NAME);

        if (!TextUtils.isEmpty(albumName)) {
            mImageDataArrayList = albumName.equalsIgnoreCase("All") ? ImageDataManager.getInstance().getAllImages() : ImageDataManager.getInstance().getAlbumImages(albumName);
        } else {
            mImageDataArrayList = new ArrayList<>();
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateView();
    }

    private void populateView() {
        imageAdapter = new ImageAdapter(getActivity(), mImageDataArrayList);
        recyclerView.setAdapter(imageAdapter);

        imageAdapter.setImageAdapterListener(new ImageAdapter.ImageAdapterInterface() {
            @Override
            public void onItemClicked(int position) {
                if (!mMainActivity.isSelectionMode()) {
                    Intent intent = new Intent(getActivity(), FullImageActivity.class);
                    intent.putExtra(FullImageActivity.EXTRA_POSITION, position);
                    intent.putExtra(FullImageActivity.EXTRA_IS_SELECTED, false);
                    startActivity(intent);
                } else {
                    mMainActivity.onItemSelected(ImageDataManager.getInstance().getSelectionCount());
                }
            }
        });

        imageAdapter.setIsSelectionMode(mMainActivity.isSelectionMode());
    }

    @Subscribe
    public void onActionModeChange(ActionModeEvent event) {
        imageAdapter.setIsSelectionMode(event.isActionMode());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            PickLockApplication.eventBus.unregister(this);
        } catch (Exception ex) {

        }
    }
}
