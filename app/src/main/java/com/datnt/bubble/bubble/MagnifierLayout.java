package com.datnt.bubble.bubble;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;

public class MagnifierLayout extends BubbleLayout {
    public static String TAG = MagnifierLayout.class.getSimpleName();
    private int centerX;
    private int centerY;
    private boolean longClick;
    private int screenOrientation = 0;
    private MagnifierTouchReleasedListener listener;
    private MagnifierLayout mThis;

    public interface MagnifierTouchReleasedListener {
        void onOrientationChange(MagnifierLayout magnifierLayout, int i);
        void onTouchBubble(MagnifierLayout magnifierLayout, int i, int i2);
        void onTouchOutside(MagnifierLayout magnifierLayout, int i, int i2);
        void onBubbleMove(MagnifierLayout magnifierLayout, int i, int i2);
    }

    public MagnifierTouchReleasedListener getOnMagnifierTouchReleasedListener() {
        return this.listener;
    }

    public void setOnMagnifierTouchReleasedListener(MagnifierTouchReleasedListener listener) {
        this.listener = listener;
    }

    public MagnifierLayout(Context context) {
        super(context);
        mThis = this;
    }

    public MagnifierLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mThis = this;
    }

    public MagnifierLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        mThis = this;
    }

    public int getCenterX() {
        this.centerX = (getWidth() / 2) + getViewParams().x;
        return this.centerX;
    }

    public void setCenterX(int i) {
        this.centerX = i;
    }

    public int getCenterY() {
        this.centerY = (getWidth() / 2) + getViewParams().y;
        return this.centerY;
    }

    public void setCenterY(int i) {
        this.centerY = i;
    }

    public void  hideAnim(){
        setVisibility(INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
            }
        },1000);
    }

    protected void getMarginRight() {
        windowManager.getDefaultDisplay().getMetrics(new DisplayMetrics());
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        marginRight = point.x - getWidth();
    }

    public void m11514a(int i, int i2) {
        getViewParams().x = i;
        getViewParams().y = i2;
        windowManager.updateViewLayout(this, getViewParams());
    }

    public int getScreenOrientation() {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        if (point.x == point.y) {
            return SCREEN_ORIENTATION_LANDSCAPE;
        }
        if (point.x < point.y) {
            return SCREEN_ORIENTATION_PORTRAIT;
        }
        return SCREEN_ORIENTATION_USER;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent != null) {
            int currentTimeMillis;
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.currentTime = System.currentTimeMillis();
                    this.isRelease = false;
                    this.longClick = false;
                    setAlpha(1.0f);
//                    new CountDownTimer( 2200, 2200) {
//                        public void onTick(long j) {
//                        }
//
//                        public void onFinish() {
//                            Log.d(MagnifierLayout.TAG, "MotionEvent.ACTION_DOWN Longtime finished");
//                            if (!isRelease && System.currentTimeMillis() - currentTime > 2000) {
//                                isRelease = true;
//                                longClick = true;
//                                bubbleClickListener.onSupperLongClick(mThis);
//                            }
//                        }
//                    }.start();
                    break;
                case MotionEvent.ACTION_UP:
                    currentTimeMillis = (int) (System.currentTimeMillis() - this.currentTime);
                    Log.d(TAG, "MotionEvent.ACTION_UP timeClicked=" + currentTimeMillis);
                    isRelease = true;
                    if (getLayoutCoordinator() == null || !getLayoutCoordinator().inRemoveBubbleArea(this)) {
                        if (currentTimeMillis < 100) {
                            Log.d(TAG, "MotionEvent.ACTION_UP firstClicked=" + firstClicked);
                            if (firstClicked) {
                                firstClicked = false;
                                currentTimeMillis = (int) (System.currentTimeMillis() - secondTabTime);
                                Log.d(TAG, "MotionEvent.ACTION_UP firstTimeClicked=" + currentTimeMillis + " thread_hold=" + 750);
                                if (this.bubbleClickListener != null && currentTimeMillis < 750) {
                                    isRelease = true;
                                    this.bubbleClickListener.onDoubleTap(this);
                                }
                            } else {
                                secondTabTime = System.currentTimeMillis();
                                firstClicked = true;
                            }
                            int rawX = (int) (motionEvent.getRawX() - this.startX);
                            int rawY = (int) (motionEvent.getRawY() - this.startY);
                            Log.d(TAG, "MotionEvent.ACTION_UP deltaX=" + rawX + " deltaY=" + rawY);
                            if (Math.abs(rawX) > 20 || Math.abs(rawY) > 20) {
                                gotoRightScreen(rawX);
                                setAlpha(0.4f);
                                if (this.bubbleClickListener != null) {
                                    this.bubbleClickListener.onBubbleSwipe(this);
                                }
                            }
                        } else if (!(this.listener == null || this.longClick)) {
                            int width = getWidth();
                            Log.e(TAG, "Bubble Width=" + width);
                            isRelease = true;
                            int a = getViewParams().y;
                            this.centerX = getViewParams().x + (width / 2);
                            this.centerY = (width / 2) + getViewParams().y;
                            this.listener.onTouchBubble(this, this.centerX, this.centerY);
                        }
                        getLayoutCoordinator().hideBubbleTrash();
                        return true;
                    }
                    Log.d(TAG, "checkIfBubbleIsOverTrash = true");
                    return super.onTouchEvent(motionEvent);
                case MotionEvent.ACTION_MOVE:
//                    if (!isRelease) {
//                        if (System.currentTimeMillis() - currentTime > 2000) {
//                            this.isRelease = true;
//                            this.longClick = true;
//                            this.bubbleClickListener.onSupperLongClick(this);
//                            break;
//                        }
//                    }
                    int width = getWidth();
                    this.centerX = getViewParams().x + (width / 2);
                    int a = getViewParams().y;
                    this.centerY = (width / 2) + getViewParams().y;
                    this.listener.onBubbleMove(this, this.centerX, this.centerY);
                    if (getScreenOrientation() != this.screenOrientation) {
                        this.screenOrientation = getScreenOrientation();
                        this.listener.onOrientationChange(this, this.screenOrientation);
                        break;
                    }
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    if (this.listener == null) {
                        return true;
                    }
                    this.isRelease = true;
                    currentTimeMillis = getWidth();
                    this.centerX = getViewParams().x + (currentTimeMillis / 2);
                    this.centerY = (currentTimeMillis / 2) + getViewParams().y;
                    this.listener.onTouchOutside(this, this.centerX, this.centerY);
                    return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
