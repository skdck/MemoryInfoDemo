package com.iminer.memoryinfo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryInfoService extends Service {
    private static final long REFRESH_INTERVAL = 1000;
    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;
    private MemoryInfoWindowManager memoryInfoMgr;

    public MemoryInfoService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        memoryInfoMgr = new MemoryInfoWindowManager(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, REFRESH_INTERVAL);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Service被终止的同时也停止定时器继续运行
        timer.cancel();
        timer = null;
        memoryInfoMgr.removeMemoryInfoView();
        memoryInfoMgr.removeMemoryInfoHelpView();
    }
    class RefreshTask extends TimerTask {

        @Override
        public void run() {

            // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
            if (!memoryInfoMgr.checkMemoryInfoViewExist() && !memoryInfoMgr.checkMemInfoHelpExist()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!memoryInfoMgr.checkMemoryInfoViewExist() && !memoryInfoMgr.checkMemInfoHelpExist()) {
                            memoryInfoMgr.createMemoryInfoView();
                        }
                    }
                });
            }
            // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。
            else if (memoryInfoMgr.checkMemoryInfoViewExist()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (memoryInfoMgr.checkMemoryInfoViewExist()) {
                            memoryInfoMgr.updateMemoryInfo();
                        }
                    }
                });
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }
}
