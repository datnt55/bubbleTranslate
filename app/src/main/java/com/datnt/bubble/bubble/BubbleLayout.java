package com.datnt.bubble.bubble;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.datnt.bubble.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by DatNT on 11/6/2018.
 */

public class BubbleLayout  extends BubbleBaseLayout {
    public static String TAG = BubbleLayout.class.getSimpleName();
    protected float rawX;
    protected float rawY;
    protected int startX;
    protected int startY;
    protected BubbleClickListener bubbleClickListener;
    protected long currentTime;
    protected long secondTabTime;
    protected int marginRight;
    protected WindowManager windowManager;
    protected boolean shouldStickToWall = true;
    protected boolean firstClicked = false;
    protected boolean f7927k = true;
    protected boolean isMove = false;
    protected boolean isRelease = false;
    private BubbleRemoveListener bubbleRemoveListener;
    private UpdateViewRunnable runnable = new UpdateViewRunnable(this);

    public interface BubbleRemoveListener {
        void onBubbleRemove(BubbleLayout BubbleLayout);
    }
    
    public interface BubbleClickListener {
        void onBubbleSwipe(BubbleLayout BubbleLayout);
        void onDoubleTap(BubbleLayout BubbleLayout);
        void onSupperLongClick(BubbleLayout BubbleLayout);
    }

    private class UpdateViewRunnable implements Runnable {
        final BubbleLayout mBubbleLayout;
        private Handler handler;
        private float x;
        private float y;
        private long deltaTime;

        private UpdateViewRunnable(BubbleLayout BubbleLayout) {
            this.mBubbleLayout = BubbleLayout;
            this.handler = new Handler(Looper.getMainLooper());
        }
        
        private void goToRightRunnable(float x, float y) {
            this.x = x;
            this.y = y;
            this.deltaTime = System.currentTimeMillis();
            this.handler.post(this);
        }

        public void run() {
            if (this.mBubbleLayout.getRootView() != null && this.mBubbleLayout.getRootView().getParent() != null) {
                float min = Math.min(1.0f, ((float) (System.currentTimeMillis() - this.deltaTime)) / 400.0f);
                this.mBubbleLayout.updateBubbleView((this.x - ((float) this.mBubbleLayout.getViewParams().x)) * min, (this.y - ((float) this.mBubbleLayout.getViewParams().y)) * min);
                if (min < 1.0f) {
                    this.handler.post(this);
                }
            }
        }

        private void removeCallback() {
            this.handler.removeCallbacks(this);
        }
    }

    public void setOnBubbleRemoveListener(BubbleRemoveListener listener) {
        this.bubbleRemoveListener = listener;
    }

    public void setOnBubbleClickListener(BubbleClickListener listener) {
        this.bubbleClickListener = listener;
    }

    public BubbleLayout(Context context) {
        super(context);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setClickable();
    }

    public BubbleLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setClickable();
    }

    public BubbleLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setClickable();
    }

    public void setShouldStickToWall(boolean stickToWall) {
        this.shouldStickToWall = stickToWall;
    }
    
    public void removeBubble() {
        if (this.bubbleRemoveListener != null) {
            this.bubbleRemoveListener.onBubbleRemove(this);
        }
    }

    private void setClickable() {
        setClickable(true);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.e(TAG, "onTouchEvent event =  " + motionEvent.getAction());
        if (motionEvent != null) {
            switch (motionEvent.getAction()) {
                case ACTION_DOWN:
                    this.startX = getViewParams().x;
                    this.startY = getViewParams().y;
                    this.rawX = motionEvent.getRawX();
                    this.rawY = motionEvent.getRawY();
                    downClickAnim();
                    this.currentTime = System.currentTimeMillis();
                    this.isMove = false;
                    getMarginRight();
                    this.runnable.removeCallback();
                    break;
                case ACTION_UP:
                    setDefaultBubblePosition();
                    if (getLayoutCoordinator() != null) {
                        getLayoutCoordinator().removeBubbleTrash(this);
                        upClickAnim();
                        break;
                    }
                    break;
                case ACTION_MOVE:
                    if (this.f7927k) {
                        int rawX = (int) (motionEvent.getRawX() - this.rawX);
                        int rawY = (int) (motionEvent.getRawY() - this.rawY);
                        int i = this.startX + rawX;
                        int i2 = this.startY + rawY;
                        getViewParams().x = i;
                        getViewParams().y = i2;
                        if (Math.abs(rawX) > 50 || Math.abs(rawY) > 50) {
                            this.isMove = true;
                        }
                        getWindowManager().updateViewLayout(this, getViewParams());
                        if (getLayoutCoordinator() != null) {
                            getLayoutCoordinator().updateBubbleTrash(this, i, i2);
                            break;
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void show() {
        if (!isInEditMode()) {
            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.bubble_shown_animator);
            animatorSet.setTarget(this);
            animatorSet.start();
        }
    }

    private void downClickAnim() {
        if (!isInEditMode()) {
            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.bubble_down_click_animator);
            animatorSet.setTarget(this);
            animatorSet.start();
        }
    }

    private void upClickAnim() {
        if (!isInEditMode()) {
            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.bubble_up_click_animator);
            animatorSet.setTarget(this);
            animatorSet.start();
        }
    }

    protected void getMarginRight() {
        this.windowManager.getDefaultDisplay().getRealMetrics(new DisplayMetrics());
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.marginRight = point.x - getWidth();
    }

    public void setDefaultBubblePosition() {
        goToRight(false);
    }

    public void goToRight(boolean z) {
        if (this.shouldStickToWall || z) {
            this.runnable.goToRightRunnable(getViewParams().x >= this.marginRight / 2 ? (float) this.marginRight : 0.0f, (float) getViewParams().y);
        }
    }

    public void gotoRightScreen(int margin) {
        Log.d(TAG, "goToWall: shouldStickToWall=" + this.shouldStickToWall + " deltaX=" + margin);
        float f = 0.0f;
        if (margin > 0) {
            f = (float) this.marginRight;
        }
        Log.d(TAG, "goToWall: " + margin + " " + f);
        this.runnable.goToRightRunnable(f, (float) getViewParams().y);
    }

    public void getRightMargin() {
        this.windowManager.getDefaultDisplay().getRealMetrics(new DisplayMetrics());
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int width = getWidth();
        getViewParams().x = (point.x - width) / 2;
        this.windowManager.updateViewLayout(this, getViewParams());
    }

    private void updateBubbleView(float x, float y) {
        WindowManager.LayoutParams viewParams = getViewParams();
        viewParams.x = (int) (((float) viewParams.x) + x);
        viewParams = getViewParams();
        viewParams.y = (int) (((float) viewParams.y) + y);
        this.windowManager.updateViewLayout(this, getViewParams());
    }
}
