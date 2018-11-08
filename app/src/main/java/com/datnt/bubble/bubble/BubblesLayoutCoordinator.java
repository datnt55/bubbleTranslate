package com.datnt.bubble.bubble;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.datnt.bubble.BubblesService;

/**
 * Created by DatNT on 11/6/2018.
 */

public class BubblesLayoutCoordinator {
    private static BubblesLayoutCoordinator mThis;
    private BubbleTrashLayout bubbleTrash;
    private WindowManager mWindowManager;
    private BubblesService service;

    public static class Coordinator {
        private BubblesLayoutCoordinator layoutCoordinator = BubblesLayoutCoordinator.getInstance();

        public Coordinator(BubblesService bubblesService) {
            this.layoutCoordinator.service = bubblesService;
        }

        public Coordinator setBubbleTrash(BubbleTrashLayout BubbleTrashLayout) {
            this.layoutCoordinator.bubbleTrash = BubbleTrashLayout;
            return this;
        }

        public Coordinator setWindowManager(WindowManager windowManager) {
            this.layoutCoordinator.mWindowManager = windowManager;
            return this;
        }

        public BubblesLayoutCoordinator getLayoutCoordinator() {
            return this.layoutCoordinator;
        }
    }

    private static BubblesLayoutCoordinator getInstance() {
        if (mThis == null) {
            mThis = new BubblesLayoutCoordinator();
        }
        return mThis;
    }

    private BubblesLayoutCoordinator() {
    }

    public void updateBubbleTrash(BubbleLayout bubbleLayout, int i, int i2) {
        if (this.bubbleTrash != null) {
            this.bubbleTrash.setVisibility(View.VISIBLE);
            if (inRemoveBubbleArea(bubbleLayout)) {
                this.bubbleTrash.magnifierDismissAnim();
                this.bubbleTrash.vibrator();
                updateBubblePosition(bubbleLayout);
                return;
            }
            this.bubbleTrash.hideAnim();
        }
    }

    private void updateBubblePosition(BubbleLayout bubbleLayout) {
        View d = getFirstChild();
        int left = d.getLeft() + (d.getMeasuredWidth() / 2);
        int measuredHeight = ((d.getMeasuredHeight() / 2) + d.getTop()) - (bubbleLayout.getMeasuredHeight() / 2);
        bubbleLayout.getViewParams().x = left - (bubbleLayout.getMeasuredWidth() / 2);
        bubbleLayout.getViewParams().y = measuredHeight;
        this.mWindowManager.updateViewLayout(bubbleLayout, bubbleLayout.getViewParams());
    }

    public boolean inRemoveBubbleArea(BubbleLayout bubbleLayout) {
        if (this.bubbleTrash.getVisibility() != View.VISIBLE) {
            return false;
        }
        View child = getFirstChild();
        int measuredWidth = child.getMeasuredWidth();
        int measuredHeight = child.getMeasuredHeight();
        int left = child.getLeft() - (measuredWidth / 2);
        measuredWidth = (measuredWidth / 2) + (child.getLeft() + measuredWidth);
        int top = child.getTop() - (measuredHeight / 2);
        int top2 = (child.getTop() + measuredHeight) + (measuredHeight / 2);
        measuredHeight = bubbleLayout.getMeasuredWidth();
        int measuredHeight2 = bubbleLayout.getMeasuredHeight();
        int i = bubbleLayout.getViewParams().x;
        measuredHeight += i;
        int i2 = bubbleLayout.getViewParams().y;
        measuredHeight2 += i2;
        if (i < left || measuredHeight > measuredWidth || i2 < top || measuredHeight2 > top2) {
            return false;
        }
        return true;
    }

    public void removeBubbleTrash(BubbleLayout bubbleLayout) {
        Log.d("BubblesLayout", "notifyBubbleRelease");
        if (this.bubbleTrash != null) {
            if (inRemoveBubbleArea(bubbleLayout)) {
                Log.d("BubblesLayout", "Bubble in trashview");
                this.service.removeBubble(bubbleLayout);
            }
            this.bubbleTrash.setVisibility(View.GONE);
        }
    }

    public void hideBubbleTrash() {
        if (this.bubbleTrash != null) {
            this.bubbleTrash.setVisibility(View.GONE);
        }
    }

    private View getFirstChild() {
        return this.bubbleTrash.getChildAt(0);
    }
}