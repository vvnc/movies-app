package com.vvnc.movies;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class CustomOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private int currentPage;
    private boolean isLoading;
    private int previousTotalCount;
    private final int maxItemsAllowed = 25;
    private final int visibleThreshold = 3;

    CustomOnScrollListener(LinearLayoutManager layoutManager, int startPage) {
        this.layoutManager = layoutManager;
        this.currentPage = startPage;
        this.isLoading = false;
        this.previousTotalCount = 0;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int totalCount = layoutManager.getItemCount();

        if (isLoading && totalCount > previousTotalCount) {
            // If it's loading and data set increased then it means that the loading is over:
            if (totalCount > previousTotalCount) {
                isLoading = false;
                previousTotalCount = totalCount;
            }
        }

        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if(!isLoading && lastVisibleItemPosition + visibleThreshold > totalCount) {
            // If reached the bottom then load next page:
            isLoading = true;
            onLoadPage(currentPage++);
        }

        // If collection has too many items then delete the first (invisible) ones
        if (!isLoading && totalCount > maxItemsAllowed) {
            onTooManyItems();
            previousTotalCount = layoutManager.getItemCount();
        }
    }

    public abstract void onLoadPage(int pageNumber);

    public abstract void onTooManyItems();
}
