package com.bhargavi.laxmi.swipelock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bhargavi.laxmi.swipelock.data.ImageData;
import com.bhargavi.laxmi.swipelock.data.ImageDataManager;

import java.util.ArrayList;


public class FullImageActivity extends AppCompatActivity {
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_IS_SELECTED = "is_selected";
    public static final String EXTRA_ALBUM_NAME = "album_name";

    private ViewPager viewPager;
    private ImagePagerAdapter viewPagerAdapter;

    private ArrayList<ImageData> imageData;
    private boolean isSelected;
    private String mAlbumName;

    private Toolbar mToolBar;

    private TextView mImageNumber;
    private TextView mImageName;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        initToolBar();

        setTitle(null);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mImageNumber = (TextView) findViewById(R.id.textView_imageNo);
        mImageName = (TextView) findViewById(R.id.textView_imageName);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPosition = bundle.getInt(EXTRA_POSITION);
            isSelected = bundle.getBoolean(EXTRA_IS_SELECTED, false);
            mAlbumName = bundle.getString(EXTRA_ALBUM_NAME, null);
            // and get whatever type user account id is
        }

        if (mAlbumName != null) {
            imageData = ImageDataManager.getInstance().getAlbumImages(mAlbumName);
        } else {
            imageData = isSelected ? ImageDataManager.getInstance().getSelectedImages() : ImageDataManager.getInstance().getAllImages();
        }

        viewPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(mPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mPosition = i;
                uploadImageNumber();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        uploadImageNumber();
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            mToolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_back));
        }
    }

    private void uploadImageNumber() {
        int totalCnt = imageData.size();
        mImageNumber.setText((mPosition + 1) + " of " + totalCnt);
        String imgName = imageData.get(mPosition).getmImageName();
        mImageName.setText(imgName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageDataManager.getInstance().selectAll(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            return FullImageFragment.newInstance(imageData.get(position));
        }

        @Override
        public int getCount() {
            return imageData.size();
        }
    }
}
