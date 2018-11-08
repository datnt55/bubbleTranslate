package com.datnt.bubble;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.datnt.bubble.bubble.BubbleLayout;
import com.datnt.bubble.bubble.MagnifierLayout;
import com.datnt.bubble.bubble.OnInitializedCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static android.view.View.INVISIBLE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = BubblesService.class.getSimpleName();
    protected MagnifierLayout mMagnifierLayout;
    protected ProgressBar progressBar;
    private BubblesManager mBubbleManager;
    private int startX, startY;
    private MediaProjectionManager mediaProjectionManager;
    private ImageReader imageReader;
    private MediaProjection mMediaProjection;
    private Handler handler;
    Handler f7657q = new Handler();
    private  int heightScreen, widthScreen, mScreenDensity;
    private VirtualDisplay mVirtualDisplay ;
    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";
    private int mResultCode;
    private Intent mResultData;
    private static final int REQUEST_MEDIA_PROJECTION = 1;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            mResultCode = bundle.getInt(STATE_RESULT_CODE);
            mResultData = bundle.getParcelable(STATE_RESULT_DATA);
            if (this.mediaProjectionManager == null) {
                new Thread() {
                    public void run() {
                        Looper.prepare();
                        handler = new Handler();
                        Looper.loop();
                    }
                }.start();
                this.mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                moveTaskToBack(true);
                //onNewIntent(getIntent());
            }
        } else {
            new Thread() {
                public void run() {
                    Looper.prepare();
                    handler = new Handler();
                    Looper.loop();
                }
            }.start();
            this.mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            moveTaskToBack(true);
            //onNewIntent(getIntent());
        }
        if (Settings.canDrawOverlays(this)) {
            addOverlayView();
        } else {
            checkDrawOverlayPermission();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }
    public final static int REQUEST_CODE = 10101;

    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                addOverlayView();
            } else {
                Toast.makeText(this, "Sorry. Can't draw overlays without permission...", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "User cancelled");
                //Toast.makeText(getActivity(), R.string.user_cancelled, Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "Starting screen capture");
            mResultCode = resultCode;
            mResultData = data;
            setUpMediaProjection();
            setUpVirtualDisplay();
        }
    }

    private void addOverlayView() {

        if (this.mBubbleManager != null) {
            this.mBubbleManager.removeBubble(this.mMagnifierLayout);
            this.mMagnifierLayout = null;
        }
        this.mBubbleManager = new BubblesManager.BubbleManagerInstance(this).addBubble(R.layout.bubble_trash_layout).addCallBack(new OnInitializedCallback() {
            @Override
            public void OnInitialized() {
                Log.e(TAG, "initializeBubblesManager onInitialized");
                initComponents();
            }
        }).getmBubbleManager();
        this.mBubbleManager.startService();


        BubblesManager bubblesManager = new BubblesManager(this);
        bubblesManager.startService();
        moveTaskToBack(true);
    }

    private void initComponents() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightScreen = displayMetrics.heightPixels;
        widthScreen = displayMetrics.widthPixels;
        mScreenDensity = displayMetrics.densityDpi;
        mMagnifierLayout = (MagnifierLayout) LayoutInflater.from(this).inflate(R.layout.bubble_layout, null);
        this.mMagnifierLayout.setShouldStickToWall(false);
        mBubbleManager.addBubble(mMagnifierLayout, (widthScreen / 2) - 90, heightScreen / 3);
        mMagnifierLayout.setOnBubbleRemoveListener(new MagnifierRemoveListener(this));
        mMagnifierLayout.setOnBubbleClickListener(new MagnifierClickListener(this));
        mMagnifierLayout.setOnMagnifierTouchReleasedListener(new MagnifierLayout.MagnifierTouchReleasedListener() {
            @Override
            public void onOrientationChange(MagnifierLayout magnifierLayout, int i) {

            }

            @Override
            public void onTouchBubble(MagnifierLayout magnifierLayout, int i, int i2) {
                Log.d("MagnifierLayout", "coord: " + i + " " + i2);
                startX = i;
                startY = i2;
//                if (this.mThis.f7639W == null) {
//                    this.mThis.m11174A();
                      startCapture();
//                    return;
//                }
                try {
                    //m11265o();
                    //TODO: handler
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTouchOutside(MagnifierLayout magnifierLayout, int i, int i2) {

            }

            @Override
            public void onBubbleMove(MagnifierLayout magnifierLayout, int i, int i2) {
//                Log.d("onMagnifierMoved", "deltaX=" + Math.abs(i - startX) + " deltaY=" + Math.abs(i2 - startY));
//                if (System.currentTimeMillis() - this.mThis.f7630N >= 300) {
//                    this.mThis.f7655o = i;
//                    this.mThis.f7656p = i2;
//                    if (this.mThis.am == 2) {
//                        mMeaningLayout.m11504a(mMagnifierLayout.getCenterX(), mMagnifierLayout.getCenterY(), this.mThis.f7625I);
//                    } else if (this.mThis.f7639W == null) {
//                        mMeaningLayout.m11504a(mMagnifierLayout.getCenterX(), mMagnifierLayout.getCenterY(), this.mThis.f7625I);
//                    } else if (this.mThis.f7639W != null) {
//                        try {
//                            this.mThis.m11265o();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            }
        });
        progressBar = mMagnifierLayout.findViewById(R.id.progressLookingUp);
        progressBar.setVisibility(INVISIBLE);
    }

    private void setUpMediaProjection() {

        mMediaProjection = mediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void setUpVirtualDisplay() {
        try {
            imageReader = ImageReader.newInstance(widthScreen, heightScreen, ImageFormat.RGB_565, 2);
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screencap", widthScreen, heightScreen, mScreenDensity, 9, this.imageReader.getSurface(), null, handler);
            this.imageReader.setOnImageAvailableListener(new CaptureImageAvailable(this), handler);
        } catch (Exception e) {
        }
    }

    private void startCapture() {
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            Log.i(TAG, "Requesting confirmation");
            // This initiates a prompt dialog for the user to confirm screen projection.
            startActivityForResult(this.mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }

    }

    public class MagnifierRemoveListener implements BubbleLayout.BubbleRemoveListener {
        MainActivity mThis;
        MagnifierRemoveListener(MainActivity mThis) {
            this.mThis = mThis;
        }

        public void onBubbleRemove(BubbleLayout bubbleLayout) {
            Log.d(TAG, "mBubbleView onBubbleRemoved");
            finishApp();
        }
    }

    class MagnifierClickListener implements BubbleLayout.BubbleClickListener {
        MainActivity mThis;
        class SupperLongClickRunable implements Runnable {
            final MagnifierClickListener f7592a;

            SupperLongClickRunable(MagnifierClickListener MagnifierClickListener) {
                this.f7592a = MagnifierClickListener;
            }

            public void run() {
                Log.d(TAG, "onBubbleSupperLongClick");
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
                if (mMagnifierLayout != null) {
                    mMagnifierLayout.setVisibility(INVISIBLE);
                }
                showNotificationShowBubble();
            }
        }

        MagnifierClickListener(MainActivity mThis) {
            this.mThis = mThis;
        }

        @Override
        public void onBubbleSwipe(BubbleLayout BubbleLayout) {

        }

        @Override
        public void onDoubleTap(BubbleLayout BubbleLayout) {
//            Intent intent = new Intent(this.mThis, SettingsActivity.class);
//            this.mThis.m11288z();
//            this.mThis.m11174A();
//            this.mThis.aK = true;
//            this.mThis.startActivityForResult(intent, 2034);
        }

        @Override
        public void onSupperLongClick(BubbleLayout BubbleLayout) {
            this.mThis.runOnUiThread(new SupperLongClickRunable(this));
        }

    }


    private void showNotificationShowBubble() {
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.putExtra("NotificationClose", "NotificationClose");
//        intent.addFlags(603979808);
//        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
//        intent2.putExtra("NotificationShowBubble", "NotificationShowBubble");
//        intent2.addFlags(603979808);
//        PendingIntent.getBroadcast(this, 100, intent, 0).cancel();
//        PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 1, intent, 268435456);
//        PendingIntent activity2 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2, 0);
//        intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.putExtra("showSettings", "NotificationClose");
//        intent.addFlags(603979808);
//        PendingIntent activity3 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService("notification");
//        Resources resources = getApplicationContext().getResources();
//        Notification.Builder builder = new Notification.Builder(getApplicationContext());
//        builder.setContentIntent(activity2).setSmallIcon(R.drawable.ic_notification).setLargeIcon(BitmapFactory.decodeResource(resources, C2729R.mipmap.ic_launcher)).setTicker(resources.getString(C2729R.string.app_name)).setPriority(2).setWhen(0).setAutoCancel(true).setOngoing(false).setContentTitle(resources.getString(C2729R.string.app_name)).addAction(C2729R.drawable.ic_notification_settings, "Settings", activity3).addAction(C2729R.drawable.ic_notification_close, "Close", activity).setContentText("Tap to open");
//        Notification notification = builder.getNotification();
//        notification.flags |= 16;
//        notificationManager.notify(100, builder.build());
    }

    private void finishApp() {
        Log.d(TAG, "finishApp");
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(100);
        if (this.mBubbleManager != null) {
            this.mBubbleManager.m11548b();
        }
        finishAffinity();
        finishAndRemoveTask();
        Process.killProcess(Process.myPid());
    }


    @Override
    protected void onResume() {
        super.onResume();
        moveTaskToBack(true);
    }

    private class CaptureImageAvailable implements ImageReader.OnImageAvailableListener {
        MainActivity f7601a;

        public CaptureImageAvailable(MainActivity captureScreenActivity) {
            this.f7601a = captureScreenActivity;
        }

        public void onImageAvailable(final ImageReader imageReader) {
            //Toast.makeText(f7601a,"Capture",Toast.LENGTH_SHORT).show();
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                public void run() {
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//            });
            Bitmap result = saveToBitmap(imageReader);
        }
    }

    private Bitmap saveToBitmap(ImageReader reader){
        Log.i(TAG, "in OnImageAvailable");
        Bitmap bitmap = null;
        Image img = null;
        try {
            img = reader.acquireLatestImage();
            if (img != null) {
                Image.Plane[] planes = img.getPlanes();
                if (planes[0].getBuffer() == null) {
                    return null;
                }
                int width = img.getWidth();
                int height = img.getHeight();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                byte[] newData = new byte[width * height * 4];

                int offset = 0;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                bitmap = Bitmap.createBitmap(displayMetrics,width, height, Bitmap.Config.RGB_565);
                ByteBuffer buffer = planes[0].getBuffer();
                for (int i = 0; i < height; ++i) {
                    for (int j = 0; j < width; ++j) {
                        int pixel = 0;
                        pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                        pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                        pixel |= (buffer.get(offset + 2) & 0xff);       // B
                        pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                        bitmap.setPixel(j, i, pixel);
                        offset += pixelStride;
                    }
                    offset += rowPadding;
                }
                img.close();
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bitmap) {
                bitmap.recycle();
            }
            if (null != img) {
                img.close();
            }

        }
        return null;
    }
}
