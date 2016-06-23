package com.iminer.memoryinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class MemoryInfoView extends LinearLayout {
    private TextView tv_max_memory;
    private TextView tv_used_memory;
    private TextView tv_native_memory;
    private TextView tv_free_memory;
//    private TextView tv_allocated_memory;
    private TextView tv_total_memory;
    private View content;
    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;
    public MemoryInfoView(Context context) {
        super(context);
        init(context);
    }

    public MemoryInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MemoryInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.memory_info_view, this);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int widthSpec = MeasureSpec.makeMeasureSpec(context.getResources().getDisplayMetrics().widthPixels, MeasureSpec.UNSPECIFIED);
        int heightSpec = MeasureSpec.makeMeasureSpec(context.getResources().getDisplayMetrics().heightPixels, MeasureSpec.UNSPECIFIED);
        this.measure(widthSpec, heightSpec);
        int measureHeight = this.getMeasuredHeight();
        int measureWidth = this.getMeasuredWidth();
        Log.d("MemoryView", "measureHeight = " + measureHeight + ", measureWidth = " + measureWidth);
        content = findViewById(R.id.content);
        tv_max_memory = (TextView) findViewById(R.id.tv_max_memory);
        tv_used_memory = (TextView) findViewById(R.id.tv_used_memory);
        tv_native_memory = (TextView) findViewById(R.id.tv_native_memory);
        tv_free_memory = (TextView) findViewById(R.id.tv_free_memory);
//        tv_allocated_memory = (TextView) findViewById(R.id.tv_total_memory);
        tv_total_memory = (TextView) findViewById(R.id.tv_total_memory);
    }
    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
//                xInScreen = event.getRawX();
//                yInScreen = event.getRawY() - getStatusBarHeight();
                ViewGroup.LayoutParams lp = getLayoutParams();
                Log.d("MemoryViewTouch", lp.getClass().getCanonicalName());
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                isMoving = true;
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                }
                isMoving = false;

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
    private boolean isMoving;
    public boolean isMoving(){
        return isMoving;
    }

    private void updateViewPosition() {
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) getLayoutParams();
        lp.x = (int) (xInScreen - xInView);
        lp.y = (int) (yInScreen - yInView);
        Log.d("MemoryPosition", "x = " + lp.x + ", y = " + lp.y);
        windowManager.updateViewLayout(this, lp);
    }
    public int getContentWidth() {
        return content.getLayoutParams().width;
    }
    public int getContentHeight() {
        return content.getLayoutParams().height;
    }
    public void updateMaxMemory(CharSequence str) {
        tv_max_memory.setText(str);
    }
    public void updateFreeMemory(CharSequence str) {
        tv_free_memory.setText(str);
    }
//    public void updateAllocatedMemory(CharSequence str) {
//        tv_allocated_memory.setText(str);
//    }
    public void updateUsedMemory(CharSequence str) {
        tv_used_memory.setText(str);
    }
    public void updateNativeHeapMemory(CharSequence str) {
        tv_native_memory.setText(str);
    }
    public void updateTotalMemory(CharSequence str) {
        tv_total_memory.setText(str);
    }
}
