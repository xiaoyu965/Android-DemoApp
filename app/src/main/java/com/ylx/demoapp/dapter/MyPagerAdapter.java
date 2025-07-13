package com.ylx.demoapp.dapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ylx.demoapp.R;

public class MyPagerAdapter extends RecyclerView.Adapter<MyPagerAdapter.PagerViewHolder> {
    private Context context;

    public MyPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public PagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, parent, false);
        return new PagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PagerViewHolder holder, int position) {
        // 设置每个页面的RecyclerView
        RecyclerView recyclerView = holder.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MyRecyclerAdapter());
    }

    @Override
    public int getItemCount() {
        return 3; // 3个页面
    }

    static class PagerViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public PagerViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
