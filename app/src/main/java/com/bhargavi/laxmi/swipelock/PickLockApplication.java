package com.bhargavi.laxmi.swipelock;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by laxmi on 8/24/15.
 */
public class PickLockApplication extends Application {
    public static Bus eventBus = new Bus();
}
