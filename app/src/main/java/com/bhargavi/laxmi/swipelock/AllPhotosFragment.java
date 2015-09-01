package com.bhargavi.laxmi.swipelock;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhargavi.laxmi.swipelock.data.AlbumData;
import com.bhargavi.laxmi.swipelock.data.ImageDataManager;

import java.util.ArrayList;

/**
 * Created by laxmi on 8/24/15.
 */
public class AllPhotosFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<AlbumData> albumDataArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_photos, null);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        setupTablayout();
        return view;
    }

    private void setupTablayout() {
        albumDataArrayList = ImageDataManager.getInstance().getAlbums();
        viewPager.setAdapter(new AlbumPagerAdapter(getChildFragmentManager()));

        tabLayout.addTab(tabLayout.newTab().setText("All Photos"));

        for (AlbumData albumData : albumDataArrayList) {
            tabLayout.addTab(tabLayout.newTab().setText(albumData.getAlbumName()));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private class AlbumPagerAdapter extends FragmentStatePagerAdapter {

        public AlbumPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GalleryFragment.newInstance(position == 0 ? "All" : albumDataArrayList.get(position - 1).getAlbumName());
        }

        @Override
        public int getCount() {
            return albumDataArrayList.size() + 1;
        }
    }
}
