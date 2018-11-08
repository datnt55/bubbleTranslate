package com.datnt.bubble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.datnt.bubble.bubble.BubbleLayout;
import com.datnt.bubble.bubble.OnInitializedCallback;

/**
 * Created by DatNT on 11/5/2018.
 */

public class BubblesManager {
    private static BubblesManager mBubblesManager;
    private Context mContext;
    private boolean connected;
    private BubblesService mService;
    private int layoutId;
    private OnInitializedCallback mCallback;
    private ServiceConnection mServiceConnection = new CustomServiceConnection(this);

    class CustomServiceConnection implements ServiceConnection {
        final BubblesManager mBubbleManager;

        CustomServiceConnection(BubblesManager BubblesManager) {
            this.mBubbleManager = BubblesManager;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.mBubbleManager.mService = ((BubblesService.CustomBinder) iBinder).getService();
            this.mBubbleManager.m11544c();
            this.mBubbleManager.connected = true;
            if (this.mBubbleManager.mCallback != null) {
                this.mBubbleManager.mCallback.OnInitialized();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            this.mBubbleManager.connected = false;
        }
    }

    public static class BubbleManagerInstance {
        private BubblesManager mBubbleManager;

        public BubbleManagerInstance(Context context) {
            this.mBubbleManager = BubblesManager.getManager(context);
        }

        public BubbleManagerInstance addCallBack(OnInitializedCallback mCallback) {
            this.mBubbleManager.mCallback = mCallback;
            return this;
        }

        public BubbleManagerInstance addBubble(int layout) {
            this.mBubbleManager.layoutId = layout;
            return this;
        }

        public BubblesManager getmBubbleManager() {
            return this.mBubbleManager;
        }
    }

    private static BubblesManager getManager(Context context) {
        if (mBubblesManager == null) {
            mBubblesManager = new BubblesManager(context);
        }
        return mBubblesManager;
    }

    public BubblesManager(Context context) {
        this.mContext = context;
    }

    private void m11544c() {
        this.mService.addOverlay(layoutId);
    }

    public void startService() {
        this.mContext.bindService(new Intent(this.mContext, BubblesService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void m11548b() {
        this.mContext.unbindService(this.mServiceConnection);
    }

    public void addBubble(BubbleLayout BubbleLayout, int i, int i2) {
        if (this.connected) {
            this.mService.addBubble(BubbleLayout, i, i2);
        }
    }

    public void removeBubble(BubbleLayout BubbleLayout) {
        if (this.connected && BubbleLayout != null) {
            this.mService.removeBubble(BubbleLayout);
        }
    }

}
