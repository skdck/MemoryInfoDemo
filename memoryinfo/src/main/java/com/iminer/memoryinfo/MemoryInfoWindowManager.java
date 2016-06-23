package com.iminer.memoryinfo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.text.DecimalFormat;

public class MemoryInfoWindowManager {
    private static final int UNIT_B = 0;
    private static final int UNIT_KB = 10;
    private static final int UNIT_MB = 20;

    private MemoryInfoView memoryInfoView;
    private boolean memoryInfoViewIsExist;

    private Context mContext;

    private MemoryInfoHelpView memoryInfoHelpView;
    private boolean memoryInfoHelpViewIsExist;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private WindowManager mWindowManager;
    private DecimalFormat df = new DecimalFormat("#.##");
    public MemoryInfoWindowManager(Context context) {
        mContext = context;

    }

    /**
     * 更新内存视图数据
     */
    public void updateMemoryInfo() {
        MemoryInfo info = getMemoryInfo();
        if (!memoryInfoView.isMoving()) {
            memoryInfoView.updateMaxMemory("最大：" + (info.maxSize >> UNIT_MB) + "MB");

            memoryInfoView.updateTotalMemory("当前占用：" + df.format((float)info.totalSize / (1 << UNIT_MB)) + "MB");

            float freeSize = ((float)info.freeSize / (1 << UNIT_MB));
            memoryInfoView.updateFreeMemory("剩余：" + df.format(freeSize) + "MB");

            float usedPercent = (((float)info.totalSize - info.freeSize) * 100) / info.maxSize;
            memoryInfoView.updateUsedMemory("已使用：" + df.format(usedPercent) + "%");

            float nativeHeapSize = ((float)info.nativeHeapSize / (1 << UNIT_MB));
            memoryInfoView.updateNativeHeapMemory("本地堆：" + df.format(nativeHeapSize) + "MB");
        }
    }

    public boolean checkMemoryInfoViewExist() {
        return memoryInfoViewIsExist;
    }

    public void createMemoryInfoView() {

        WindowManager windowManager = getWindowManager(mContext);
        if (memoryInfoView == null) {

            memoryInfoView = new MemoryInfoView(mContext);
            WindowManager.LayoutParams memoryViewParams;
            memoryViewParams = new WindowManager.LayoutParams();
            memoryViewParams.x = mContext.getResources().getDisplayMetrics().widthPixels;
            memoryViewParams.y = 0;
            memoryViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            memoryViewParams.format = PixelFormat.RGBA_8888;
            memoryViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            memoryViewParams.gravity = Gravity.LEFT | Gravity.TOP;
            memoryViewParams.width = memoryInfoView.getContentWidth();
            memoryViewParams.height = memoryInfoView.getContentHeight();
            Log.d("MemoryView", "w = " + memoryInfoView.getContentWidth() + ", h = " + memoryInfoView.getContentHeight() + ", x = " + memoryViewParams.x);
            windowManager.addView(memoryInfoView, memoryViewParams);
            memoryInfoViewIsExist = true;
        }
        memoryInfoView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                removeMemoryInfoView();
                createMemoryInfoHelpView();
                return true;
            }
        });
    }

    public void removeMemoryInfoView() {
        if (memoryInfoViewIsExist) {
            WindowManager windowManager = getWindowManager(mContext);
            windowManager.removeView(memoryInfoView);
            memoryInfoView = null;
            memoryInfoViewIsExist = false;
        }
    }
    public void createMemoryInfoHelpView() {
        WindowManager windowManager = getWindowManager(mContext);
        if (memoryInfoHelpView == null) {

            memoryInfoHelpView = new MemoryInfoHelpView(mContext);
            WindowManager.LayoutParams memoryInfoHelpViewParams;
            memoryInfoHelpViewParams = new WindowManager.LayoutParams();
            memoryInfoHelpViewParams.x = (mContext.getResources().getDisplayMetrics().widthPixels - memoryInfoHelpView.getContentWidth()) >> 1;
            memoryInfoHelpViewParams.y = (mContext.getResources().getDisplayMetrics().heightPixels - memoryInfoHelpView.getContentHeight()) >> 1;
            memoryInfoHelpViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            memoryInfoHelpViewParams.format = PixelFormat.RGBA_8888;
            memoryInfoHelpViewParams.gravity = Gravity.LEFT | Gravity.TOP;
            memoryInfoHelpViewParams.width = memoryInfoHelpView.getContentWidth();
            memoryInfoHelpViewParams.height = memoryInfoHelpView.getContentHeight();
            windowManager.addView(memoryInfoHelpView, memoryInfoHelpViewParams);
            memoryInfoHelpViewIsExist = true;
            memoryInfoHelpView.setCloseListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMemoryInfoHelpView();
                    createMemoryInfoView();
                }
            });
        }
    }

    public void removeMemoryInfoHelpView() {
        if (memoryInfoHelpViewIsExist) {
            WindowManager windowManager = getWindowManager(mContext);
            windowManager.removeView(memoryInfoHelpView);
            memoryInfoHelpView = null;
            memoryInfoHelpViewIsExist = false;
        }
    }
    public boolean checkMemInfoHelpExist() {
        return memoryInfoHelpViewIsExist;
    }
    public MemoryInfo getMemoryInfo() {
        MemoryInfo info = new MemoryInfo();
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        long freeMemory = rt.freeMemory();
        long totalMemory = rt.totalMemory();
        Log.d("MemoryView", "maxMemory = " + maxMemory + ", freeMemory = " + freeMemory);
        info.maxSize = maxMemory;

        info.freeSize = freeMemory;

        info.totalSize = totalMemory;
        info.nativeHeapSize = Debug.getNativeHeapSize();
        return info;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     *            必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
