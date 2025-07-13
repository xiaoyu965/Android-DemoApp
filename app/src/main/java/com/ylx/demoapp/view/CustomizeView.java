package com.ylx.demoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomizeView extends View {
    public CustomizeView(Context context) {
        super(context);
    }

    public CustomizeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = setSize(100, widthMeasureSpec);
        int h = setSize(100, heightMeasureSpec);
        if (w > h) {
            w = h;
        } else {
            h = w;
        }
        setMeasuredDimension(w, h);
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        //调用父View的onDraw函数，因为View这个类帮我们实现了一些
        // 基本的而绘制功能，比如绘制背景颜色、背景图片等
        super.onDraw(canvas);
        int r = getMeasuredWidth() / 2;//也可以是getMeasuredHeight()/2,本例中我们已经将宽高设置相等了
        //圆心的横坐标为当前的View的左边起始位置+半径
        int centerX = getLeft() + r;
        //圆心的纵坐标为当前的View的顶部起始位置+半径
        int centerY = getTop() + r;

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        //开始绘制
        canvas.drawCircle(centerX, centerY, r, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public int setSize(int size, int measureSpec) {
        int finalSize = size;
        int fSize = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                finalSize = fSize;
                break;
        }
        return finalSize;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
