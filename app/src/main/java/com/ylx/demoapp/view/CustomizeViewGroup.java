package com.ylx.demoapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CustomizeViewGroup extends ViewGroup {
    // 必须有的构造函数1
    public CustomizeViewGroup(Context context) {
        super(context);
    }

    // 必须有的构造函数2
    public CustomizeViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 测量所有子View
        int totalHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Log.e("ylx", "onMeasure: " + i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            totalHeight += child.getMeasuredHeight();
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 垂直排列子View
        int currentY = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(0, currentY, child.getMeasuredWidth(), currentY + child.getMeasuredHeight());
            currentY += child.getMeasuredHeight();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
