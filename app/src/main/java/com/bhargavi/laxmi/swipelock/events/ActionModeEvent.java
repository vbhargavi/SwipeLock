package com.bhargavi.laxmi.swipelock.events;

/**
 * Created by laxmi on 8/24/15.
 */
public class ActionModeEvent {
    private boolean isActionMode;

    public ActionModeEvent(boolean isActionMode) {
        this.isActionMode = isActionMode;
    }

    public boolean isActionMode() {
        return isActionMode;
    }
}
