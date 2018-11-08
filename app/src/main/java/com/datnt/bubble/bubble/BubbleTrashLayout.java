package com.datnt.bubble.bubble;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Vibrator;

import com.datnt.bubble.R;

/**
 * Created by DatNT on 11/6/2018.
 */

public class BubbleTrashLayout extends BubbleBaseLayout {
    private boolean isShow = false;
    private boolean attached = false;

    public BubbleTrashLayout(Context context) {
        super(context);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public void setVisibility(int i) {
        if (this.attached && i != getVisibility()) {
            if (i == 0) {
                loadAnimation(R.animator.bubble_trash_shown_animator);
            } else {
                loadAnimation(R.animator.bubble_trash_hide_animator);
            }
        }
        super.setVisibility(i);
    }

    void magnifierDismissAnim() {
        if (!this.isShow) {
            this.isShow = true;
            loadAnimation(R.animator.bubble_trash_shown_magnetism_animator);
        }
    }

    void vibrator() {
        ((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(70);
    }

    void hideAnim() {
        if (this.isShow) {
            this.isShow = false;
            loadAnimation(R.animator.bubble_trash_hide_magnetism_animator);
        }
    }

    private void loadAnimation(int i) {
        if (!isInEditMode()) {
            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), i);
            animatorSet.setTarget(getChildAt(0));
            animatorSet.start();
        }
    }
}
