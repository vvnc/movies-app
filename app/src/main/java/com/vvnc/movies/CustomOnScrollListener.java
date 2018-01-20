package com.vvnc.movies;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class CustomOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private int currentPage;
    private final int epsilon = 1;
    private final int threshold = 25;

    CustomOnScrollListener(LinearLayoutManager layoutManager, int startPage) {
        this.layoutManager = layoutManager;
        this.currentPage = startPage;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // If collection has too many items then delete the first (invisible) ones
        int totalCount = layoutManager.getItemCount();
        if (totalCount > threshold) {
            onTooManyItems();
        }

        // If reached the bottom then load next page:
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if (lastVisibleItemPosition > totalCount - 1 - epsilon) {
            onLoadPage(currentPage++);
        }
    }

    public abstract void onLoadPage(int pageNumber);

    public abstract void onTooManyItems();
}
