package com.vvnc.movies;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class CustomOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private final int epsilon = 1;
    private final int threshold = 25;

    CustomOnScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int totalCount = layoutManager.getItemCount();
        if(totalCount > threshold) {
            onTooManyItems();
        }
        if(lastVisibleItemPosition > totalCount - 1 - epsilon) {
            onLoadMore();
        }
    }

    public abstract void onLoadMore();

    public abstract void onTooManyItems();
}
