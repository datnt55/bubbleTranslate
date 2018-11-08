package com.datnt.bubble.bubble;


import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

/**
 * Created by DatNT on 11/6/2018.
 */

public class BubbleBaseLayout extends FrameLayout {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private BubblesLayoutCoordinator layoutCoordinator;

    public void setLayoutCoordinator(BubblesLayoutCoordinator layoutCoordinator) {
        this.layoutCoordinator = layoutCoordinator;
    }

    BubblesLayoutCoordinator getLayoutCoordinator() {
        return this.layoutCoordinator;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.mWindowManager = windowManager;
    }

    WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public void setViewParams(WindowManager.LayoutParams layoutParams) {
        this.mLayoutParams = layoutParams;
    }

    public WindowManager.LayoutParams getViewParams() {
        return this.mLayoutParams;
    }

    public BubbleBaseLayout(Context context) {
        super(context);
    }

    public BubbleBaseLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BubbleBaseLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
