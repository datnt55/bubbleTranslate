package com.datnt.bubble.bubble;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;


public class MeaningLayout extends BubbleLayout {
    public static String TAG = MeaningLayout.class.getName();
    private MeaningTouchListener listener;
    public interface MeaningTouchListener {
        void onTouchOutside();
    }

    public MeaningTouchListener getOnTouchedListener() {
        return this.listener;
    }

    public void setOnTouchedListener(MeaningTouchListener listener) {
        this.listener = listener;
    }

    public MeaningLayout(Context context) {
        super(context);
    }

    public MeaningLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MeaningLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

//    /* renamed from: b */
//    protected void mo5562b() {
//        this.i.getDefaultDisplay().getMetrics(new DisplayMetrics());
//        Display defaultDisplay = getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        defaultDisplay.getSize(point);
//        this.h = point.x - getWidth();
//    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent != null) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_OUTSIDE:
                    if (this.listener != null) {
                        this.listener.onTouchOutside();
                        break;
                    }
                    break;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void updateMeaningContent(int i, int i2, int i3) {
        Object obj;
        this.windowManager.getDefaultDisplay().getRealMetrics(new DisplayMetrics());
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int height = point.y - getHeight();
        int i4 = point.x;
        if (i2 >= point.y / 2) {
            height = 0;
        }
        if (i3 > 0) {
            if (height == 0) {
                height += i3;
            } else {
                height -= i3;
            }
        }
        if (getResources().getConfiguration().orientation == 2) {
            obj = 1;
        } else {
            obj = null;
        }
        if (obj != null) {
            if (i >= point.x / 2) {
                i4 = 0;
            }
            getViewParams().x = i4;
            getViewParams().y = height;
        } else {
            getViewParams().x = (point.x - getWidth()) / 2;
            getViewParams().y = height;
        }
        windowManager.updateViewLayout(this, getViewParams());
    }

    protected void mo5564e() {
       windowManager.getDefaultDisplay().getRealMetrics(new DisplayMetrics());
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        Object obj = getResources().getConfiguration().orientation == 2 ? 1 : null;
//        int i = point.y;
//        if (obj != null) {
//            f7878a = (int) (((double) i) * 0.7d);
//            f7879b = (int) (((double) i) * 0.9d);
//            return;
//        }
//        f7878a = (int) (((double) i) * 0.4d);
//        f7879b = (int) (((double) i) * 0.6d);
    }

//    /* renamed from: f */
//    public void mo5565f() {
//        mo5564e();
//        int height = getHeight() / 2;
//        int i = C2783a.f7878a;
//        getViewParams().height = i;
//        this.i.updateViewLayout(this, getViewParams());
//        LayoutParams layoutParams = (LayoutParams) getChildAt(0).getLayoutParams();
//        layoutParams.height = i - 15;
//        getChildAt(0).setLayoutParams(layoutParams);
//        Log.d(TAG, "collapse height=" + i);
//    }
//
//    /* renamed from: g */
//    public void mo5566g() {
//        int i;
//        mo5564e();
//        this.i.getDefaultDisplay().getMetrics(new DisplayMetrics());
//        Display defaultDisplay = getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        defaultDisplay.getSize(point);
//        int i2 = point.y;
//        int height = getHeight() * 2;
//        if (height > C2783a.f7879b) {
//            height = C2783a.f7879b;
//        }
//        if (height > i2) {
//            i = i2;
//        } else {
//            i = height;
//        }
//        getViewParams().height = i;
//        this.i.updateViewLayout(this, getViewParams());
//        LayoutParams layoutParams = (LayoutParams) getChildAt(0).getLayoutParams();
//        layoutParams.height = i - 15;
//        getChildAt(0).setLayoutParams(layoutParams);
//        Log.d(TAG, "expand height=" + i + " maxheight=" + i2);
//    }
}
