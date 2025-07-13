package com.ylx.demoapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.ylx.demoapp.R;
import com.ylx.demoapp.dapter.MyPagerAdapter;

// MainActivity.java
public class RecycleViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recycle_view);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter(this));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("ylxqaq", "Action: " + ev.getAction() + ", X: " + ev.getX() + ", Y: " + ev.getY());
        return super.dispatchTouchEvent(ev);
    }
}