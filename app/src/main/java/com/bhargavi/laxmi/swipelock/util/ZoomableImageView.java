package com.bhargavi.laxmi.swipelock.util;

/**
 * Created by laxmi on 8/24/15.
 */

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;


public class ZoomableImageView extends ImageView {
    private static final String TAG = ZoomableImageView.class.getSimpleName();
    private static final boolean DEBUG = false;

    // ignore scaling events smaller than this
    private static final float MINIMUM_SCALING_THRESHOLD = 0.003f;
    private static final int VELOCITY_THRESHOLD = 200;
    private static final int MAX_VELOCITY = 4000;
    private static final int ZOOM_DURATION = 250;

    private static final int MATCH_DEFAULT = 0;
    private static final int MATCH_WIDTH = 1;
    private static final int MATCH_HEIGHT = 2;

    private int mMatchMode;

    private float mActualScale = 1.0f;
    private float mMinScale;
    private float mMedScale;
    private float mMaxScale;

    private Drawable mDrawable;
    private Flinger mFlinger;
    private Handler mHandler;
    private boolean mIsFlinging;
    private Matrix mMatrix;
    protected final float[] mMatrixValues = new float[9];

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    private OnTapListener mOnTapListener;

    private int mPreviousWidth;

    public interface OnTapListener {
        public void onTap(float x, float y);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ZoomableImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mFlinger = new Flinger(context);
        mHandler = new Handler();

        mActualScale = 0;
        mPreviousWidth = 0;
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());

        /*if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZoomableImageView);
            mMatchMode = a.getInt(R.styleable.ZoomableImageView_match, MATCH_DEFAULT);
            a.recycle();
        }*/

        setLayoutListener();

        mMatrix = new Matrix();
        setScaleType(ImageView.ScaleType.MATRIX);
        setImageMatrix(mMatrix);
    }

    public void setLayoutListener() {
        if (mOnGlobalLayoutListener == null) {
            mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    if (DEBUG) Log.i(TAG, "onGlobalLayout()");

                    if (getWidth() != mPreviousWidth) {
                        updateZoom();
                        mPreviousWidth = getWidth();

                        getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
                        mOnGlobalLayoutListener = null;
                    }
                }
            };
            getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    }

    @SuppressWarnings("deprecation")
    public void removeLayoutListener() {
        if (mOnGlobalLayoutListener != null) {
            getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
            mOnGlobalLayoutListener = null;
        }
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        mOnTapListener = onTapListener;
    }

    protected float getValue(Matrix matrix, int index) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[index];
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mDrawable = drawable;
        super.setImageDrawable(mDrawable);
    }

    private void updateZoom() {
        if (mDrawable != null) {
            float tx = 0f;
            float ty = 0f;

            switch (mMatchMode) {
                case MATCH_DEFAULT:
                    mMinScale = Math.min((float) getWidth() / (float) mDrawable.getIntrinsicWidth(),
                            (float) getHeight() / (float) mDrawable.getIntrinsicHeight());
                    break;
                case MATCH_WIDTH:
                    mMinScale = (float) getWidth() / (float) mDrawable.getIntrinsicWidth();
                    break;
                case MATCH_HEIGHT:
                    mMinScale = (float) getHeight() / (float) mDrawable.getIntrinsicHeight();
                    break;
            }

            if (mActualScale > 0) {
                tx = getValue(mMatrix, Matrix.MTRANS_X);
                ty = getValue(mMatrix, Matrix.MTRANS_Y);
            }

            if (mActualScale < mMinScale) {
                mActualScale = mMinScale;
            }

            mMedScale = 2 * mMinScale;
            mMaxScale = 2 * mMedScale;

            float scaledWidth = mActualScale * mDrawable.getIntrinsicWidth();
            float scaledHeight = mActualScale * mDrawable.getIntrinsicHeight();

            if (scaledWidth < getWidth() || tx >= 0) {
                if (mMatchMode == MATCH_HEIGHT && !(scaledWidth < getWidth())) {
                    tx = 0;
                } else {
                    tx = (getWidth() - scaledWidth) / 2f;
                }
            } else if (tx + scaledWidth < getWidth()) {
                tx = getWidth() - scaledWidth;
            }

            if (scaledHeight < getHeight() || ty >= 0) {
                if (mMatchMode == MATCH_WIDTH && !(scaledHeight < getHeight())) {
                    ty = 0;
                } else {
                    ty = (getHeight() - scaledHeight) / 2f;
                }
            } else if (ty + scaledHeight < getHeight()) {
                ty = (getHeight() - scaledHeight);
            }

            mMatrix.reset();
            mMatrix.postScale(mActualScale, mActualScale);
            mMatrix.postTranslate(tx, ty);

            setImageMatrix(mMatrix);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleGestureDetector.onTouchEvent(ev);

        if (!mScaleGestureDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(ev);
        }

        return true;
    }

    private void scale(float scale, float cx, float cy) {
        scale = Math.max(mMinScale, Math.min(scale, mMaxScale));
        float delta = scale / mActualScale;

        mMatrix.postScale(delta, delta, cx, cy);

        RectF rect = new RectF(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());

        mMatrix.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        int viewHeight = getHeight();

        if (height < viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = getHeight() - rect.bottom;
        }

        int viewWidth = getWidth();
        if (width < viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }

        if (deltaX != 0 || deltaY != 0) {
            mMatrix.postTranslate(deltaX, deltaY);
        }

        setImageMatrix(mMatrix);
        mActualScale = getValue(mMatrix, Matrix.MSCALE_X);
    }

    private void scale(float scale, final float cx,
                       final float cy, final float duration) {
        final long start = System.currentTimeMillis();
        final float delta = (scale - mActualScale) / duration;
        final float startScale = mActualScale;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                float currentTime = Math.min(duration, now - start);
                float currentScale = startScale + (delta * currentTime);
                scale(currentScale, cx, cy);

                if (currentTime < duration) {
                    mHandler.post(this);
                }
            }
        });
    }

    private class Flinger implements Runnable {
        private final Scroller mScroller;
        private int mLastX;
        private boolean mFlingX;
        private int mLastY;
        private boolean mFlingY;

        public Flinger(Context context) {
            mScroller = new Scroller(context, new DecelerateInterpolator());
        }

        public void startFling(int velocityX, int velocityY) {
            removeCallbacks(this);
            mIsFlinging = true;
            mFlingX = true;
            mFlingY = true;

            mLastX = (int) getValue(mMatrix, Matrix.MTRANS_X);
            mLastY = (int) getValue(mMatrix, Matrix.MTRANS_Y);
            int minX = (int) (getWidth() - mActualScale * mDrawable.getIntrinsicWidth());
            int minY = (int) (getHeight() - mActualScale * mDrawable.getIntrinsicHeight());

            if (minX > 0) {
                mFlingX = false;
                velocityX = 0;
            }

            if (minY > 0) {
                mFlingY = false;
                velocityY = 0;
            }

            mScroller.fling(mLastX, mLastY, velocityX, velocityY, minX, 0, minY, 0);
            ViewCompat.postOnAnimation(ZoomableImageView.this, this);
        }

        public void stopFling() {
            removeCallbacks(this);
            endFling();
        }

        private void endFling() {
            mScroller.forceFinished(true);
            mIsFlinging = false;
        }

        @Override
        public void run() {
            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();

            float dx = 0.0f;
            float dy = 0.0f;

            if (mFlingX) {
                int x = scroller.getCurrX();
                dx = x - mLastX;
                mLastX = x;
            }
            if (mFlingY) {
                int y = scroller.getCurrY();
                dy = y - mLastY;
                mLastY = y;
            }

            mMatrix.postTranslate(dx, dy);
            setImageMatrix(mMatrix);

            if (more && mIsFlinging) {
                ViewCompat.postOnAnimation(ZoomableImageView.this, this);
            } else {
                endFling();
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mIsFlinging) return true;
            if (DEBUG) Log.i(TAG, "onDoubleTap(" + mActualScale + ")");
            float x = e.getX();
            float y = e.getY();

            if (mActualScale < 0.75f * mMedScale) {
                scale(mMedScale, x, y, ZOOM_DURATION);
            } else if (mActualScale < (mMaxScale - 0.01f)) {
                scale(mMaxScale, x, y, ZOOM_DURATION);
            } else {
                scale(mMinScale, x, y, ZOOM_DURATION);
            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mIsFlinging) return true;
            if (mScaleGestureDetector.isInProgress()) return false;

            if (mOnTapListener != null) {
                float x = e.getX();
                float y = e.getY();
                float[] pts = {x, y};

                Matrix inverse = new Matrix();
                mMatrix.invert(inverse);
                inverse.mapPoints(pts);

                mOnTapListener.onTap(pts[0], pts[1]);
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mIsFlinging) return true;
            if (mScaleGestureDetector.isInProgress()) return false;

            if (DEBUG) Log.i(TAG, "onScroll()");

            float dx = 0;
            float dy = 0;
            float x = getValue(mMatrix, Matrix.MTRANS_X);
            float y = getValue(mMatrix, Matrix.MTRANS_Y);

            if (x - distanceX <= 0) {
                float right = x + mActualScale * mDrawable.getIntrinsicWidth();

                if (right - distanceX > getWidth()) {
                    dx = -distanceX;
                }
            }

            if (y - distanceY <= 0) {
                float bottom = y + mActualScale * mDrawable.getIntrinsicHeight();

                if (bottom - distanceY > getHeight()) {
                    dy = -distanceY;
                }
            }

            mMatrix.postTranslate(dx, dy);
            setImageMatrix(mMatrix);
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mIsFlinging) return true;
            if (mScaleGestureDetector.isInProgress()) return false;

            if (Math.abs(velocityX) > VELOCITY_THRESHOLD || Math.abs(velocityY) > VELOCITY_THRESHOLD) {
                if (DEBUG) Log.i(TAG, "onFling()");

                velocityX = Math.max(-MAX_VELOCITY, Math.min(0.5f * velocityX, MAX_VELOCITY));
                velocityY = Math.max(-MAX_VELOCITY, Math.min(0.5f * velocityY, MAX_VELOCITY));

                mFlinger.startFling((int) (velocityX), (int) (velocityY));
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {
            if (DEBUG) Log.i(TAG, "onDown()");

            if (mIsFlinging) {
                mFlinger.stopFling();
            }

            return false;
        }
    }

    private class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (DEBUG) Log.i(TAG, "onScale()");
            float scale = detector.getScaleFactor() * mActualScale;

            if (Math.abs(scale - mActualScale) < MINIMUM_SCALING_THRESHOLD) {
                return false;
            }

            scale(scale, detector.getFocusX(), detector.getFocusY());

            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // intentionally blank
        }
    }
}

