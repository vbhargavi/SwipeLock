package com.bhargavi.laxmi.swipelock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhargavi.laxmi.swipelock.adapter.AlbumAdapter;
import com.bhargavi.laxmi.swipelock.data.AlbumData;
import com.bhargavi.laxmi.swipelock.events.ActionModeEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by laxmi on 8/24/15.
 */
public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;

    private boolean isSelectionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PickLockApplication.eventBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_column_count));
        recyclerView.setLayoutManager(glm);

        AlbumAdapter adapter = new AlbumAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setAlbumAdapterListener(new AlbumAdapter.AlbumAdapterListener() {
            @Override
            public void onItemClicked(AlbumData albumData) {
                Intent intent = new Intent(getActivity(), FullImageActivity.class);
                intent.putExtra(FullImageActivity.EXTRA_ALBUM_NAME, albumData.getAlbumName());
                startActivity(intent);
                if (isSelectionMode) {
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    @Subscribe
    public void onActionModeChange(ActionModeEvent event) {
        isSelectionMode = event.isActionMode();
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
