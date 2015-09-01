package com.bhargavi.laxmi.swipelock;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bhargavi.laxmi.swipelock.data.ImageDataManager;
import com.bhargavi.laxmi.swipelock.events.ActionModeEvent;


public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_IS_SELECTED = "is_selected";

    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;

    private ActionMode mActionMode;

    private boolean isSelectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();

        setupDrawerLayout();

        if (savedInstanceState == null) {
            goToAllPhotos();
        } else {
            isSelectionMode = savedInstanceState.getBoolean(EXTRA_IS_SELECTED);
            if (isSelectionMode) {
                startActionMode();
            }
        }
    }

    private void goToAllPhotos() {
        AllPhotosFragment galleryFragment = new AllPhotosFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fragment, galleryFragment);
        transaction.commitAllowingStateLoss();
    }

    private void goToAlbums() {
        AlbumFragment albumFragment = new AlbumFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fragment, albumFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_IS_SELECTED, isSelectionMode);
        super.onSaveInstanceState(outState);
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }


    public ActionMode startActionMode() {
        return mToolbar.startActionMode(mActionModeCallback);
    }

    public void onItemSelected(int cnt) {
        if (mActionMode != null) {
            mActionMode.setTitle("Selected " + cnt);
        }
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            final ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            setTitle(R.string.all_photos);
        }
    }

    public void setTitle(int stringId) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(stringId);
        }
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_albums:
                                setTitle(R.string.albums);
                                goToAlbums();
                                break;
                            case R.id.menu_all_photos:
                                setTitle(R.string.all_photos);
                                goToAllPhotos();
                                break;
                            case R.id.menu_change_pin:
                                Intent intent = new Intent(MainActivity.this, LockScreenActivity.class);
                                intent.putExtra(LockScreenActivity.EXTRA_CHANGE_PIN, true);
                                startActivity(intent);
                                break;
                        }
                        menuItem.setChecked(true);

                    }
                }, 500);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.action_lock) {
            if (mActionMode != null) {
                return true;
            }

            mActionMode = startActionMode(mActionModeCallback);
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            try {
                PickLockApplication.eventBus.post(new ActionModeEvent(true));
            } catch (Exception ex) {

            }

            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            isSelectionMode = true;

            inflater.inflate(R.menu.menu_pic_select, menu);

            mode.setTitle("Selected " + ImageDataManager.getInstance().getSelectionCount());
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.pic_select:
                    if (ImageDataManager.getInstance().getSelectedImages().size() > 0) {
                        Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                        intent.putExtra(FullImageActivity.EXTRA_POSITION, 0);
                        intent.putExtra(FullImageActivity.EXTRA_IS_SELECTED, true);
                        startActivity(intent);
                        finish();
                    } else {
                        mActionMode.finish();
                    }
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            PickLockApplication.eventBus.post(new ActionModeEvent(false));
            mActionMode = null;
            isSelectionMode = false;
        }
    };


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
