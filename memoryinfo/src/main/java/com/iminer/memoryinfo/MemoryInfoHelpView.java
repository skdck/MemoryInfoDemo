package com.iminer.memoryinfo;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MemoryInfoHelpView extends LinearLayout {
    private TextView tv_mem_info_help;
    public MemoryInfoHelpView(Context context) {
        super(context);
        init(context);

    }

    public MemoryInfoHelpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MemoryInfoHelpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.mem_info_help_view, this);
        tv_mem_info_help = (TextView) findViewById(R.id.tv_mem_info_help);
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int widthSpec = MeasureSpec.makeMeasureSpec(screenWidth * 4 / 5, MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(screenHeight * 2 / 3 , MeasureSpec.AT_MOST);
        this.measure(widthSpec, heightSpec);
        tv_mem_info_help.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    public void setMemInfoContent(CharSequence content) {
        tv_mem_info_help.setText(content);
    }
    public int getContentWidth() {
        return getMeasuredWidth();
    }
    public int getContentHeight() {
        return getMeasuredHeight();
    }
    public void setCloseListener(OnClickListener l) {
        findViewById(R.id.tv_close).setOnClickListener(l);
    }
}
