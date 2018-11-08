package com.datnt.bubble;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.datnt.bubble.bubble.BubbleBaseLayout;
import com.datnt.bubble.bubble.BubbleLayout;
import com.datnt.bubble.bubble.BubbleTrashLayout;
import com.datnt.bubble.bubble.BubblesLayoutCoordinator;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_PHONE;

/**
 * Created by matt on 08/08/2016.
 */

public class BubblesService extends Service{
    private static final String TAG = BubblesService.class.getSimpleName();
    private WindowManager windowManager;
    private CustomBinder mBinder = new CustomBinder(this);
    private List<BubbleLayout> listBubbleLayout = new ArrayList();
    private BubbleTrashLayout bubbleTrashLayout;
    private BubblesLayoutCoordinator layoutCoordinator;

    public class CustomBinder extends Binder {
        final BubblesService bubblesService;

        public CustomBinder(BubblesService bubblesService) {
            this.bubblesService = bubblesService;
        }

        public BubblesService getService() {
            return this.bubblesService;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        for (BubbleLayout b : this.listBubbleLayout) {
            removeInnerBubble(b);
        }
        this.listBubbleLayout.clear();
        return super.onUnbind(intent);
    }

    private WindowManager getWindowManager() {
        if (this.windowManager == null) {
            this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return this.windowManager;
    }

    public void addBubble(BubbleLayout bubbleLayout, int i, int i2) {
        WindowManager.LayoutParams params = addBubbleParam(i, i2);
        bubbleLayout.setWindowManager(getWindowManager());
        bubbleLayout.setViewParams(params);
        bubbleLayout.setLayoutCoordinator(this.layoutCoordinator);
        this.listBubbleLayout.add(bubbleLayout);
        innerAddBubble(bubbleLayout);
    }

    private WindowManager.LayoutParams addBubbleParam(int i, int i2) {
        int type;
        if (Build.VERSION.SDK_INT >= 26) {
            type = TYPE_APPLICATION_OVERLAY;
        } else {
            type = TYPE_PHONE;
        }
        WindowManager.LayoutParams layoutParams
                = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, 
                    type, 
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        layoutParams.gravity = Gravity.LEFT;
        layoutParams.x = i;
        layoutParams.y = i2;
        return layoutParams;
    }

    private void innerAddBubble(final BubbleBaseLayout bubbleLayout) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                   public void run() {
                try {
                    getWindowManager().addView(bubbleLayout, bubbleLayout.getViewParams());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeBubble(BubbleLayout bubbleLayout) {
        removeInnerBubble(bubbleLayout);
    }

    private void removeInnerBubble(final BubbleLayout b) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                getWindowManager().removeView(b);
                for (BubbleLayout bubbleLayout : listBubbleLayout) {
                    if (bubbleLayout == b) {
                        bubbleLayout.removeBubble();
                        listBubbleLayout.remove(bubbleLayout);
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }
    
    public void addOverlay(int i) {
        if (i != 0) {
            this.bubbleTrashLayout = new BubbleTrashLayout(this);
            this.bubbleTrashLayout.setWindowManager(windowManager);
            this.bubbleTrashLayout.setViewParams(addTrashBubbleParam());
            this.bubbleTrashLayout.setVisibility(View.GONE);
            LayoutInflater.from(this).inflate(i, this.bubbleTrashLayout, true);
            addTrashBubble(this.bubbleTrashLayout);
            setBubbleCoordinator();
        }
    }

    private WindowManager.LayoutParams addTrashBubbleParam() {
        int type;
        if (Build.VERSION.SDK_INT >= 26) {
            type = TYPE_APPLICATION_OVERLAY;
        } else {
            type = TYPE_PHONE;
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        layoutParams.x = 0;
        layoutParams.y = 0;
        return layoutParams;
    }

    private void setBubbleCoordinator() {
        layoutCoordinator = new BubblesLayoutCoordinator.Coordinator(this).setWindowManager(getWindowManager()).setBubbleTrash(this.bubbleTrashLayout).getLayoutCoordinator();
    }

    private void addTrashBubble(final BubbleBaseLayout layoutBubble) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                      public void run() {
                try {
                    getWindowManager().addView(layoutBubble, layoutBubble.getViewParams());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
