package com.vvnc.movies;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class CustomOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private int currentPage;
    private boolean isLoading;

    CustomOnScrollListener(LinearLayoutManager layoutManager, int startPage) {
        this.layoutManager = layoutManager;
        this.currentPage = startPage;
        this.isLoading = false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        final int VISIBLE_THRESHOLD = 3;
        final int MAX_ITEMS_ALLOWED = 100;

        super.onScrolled(recyclerView, dx, dy);
        int totalCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if(!isLoading && lastVisibleItemPosition + VISIBLE_THRESHOLD > totalCount) {
            // If reached the bottom then load next page:
            setIsLoading(true);
            onLoadPage(currentPage++);
        }

        // If collection has too many items then delete the first (invisible) ones
        if (!isLoading && totalCount > MAX_ITEMS_ALLOWED) {
            onTooManyItems();
        }
    }

    public abstract void onLoadPage(int pageNumber);

    public abstract void onTooManyItems();

    void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
}
