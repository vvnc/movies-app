package com.vvnc.movies;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class CustomOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private boolean isLoading;

    CustomOnScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        this.isLoading = false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        final int VISIBLE_THRESHOLD = 3;
        final int MAX_ITEMS_ALLOWED = 30;

        super.onScrolled(recyclerView, dx, dy);
        int totalCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if(!isLoading && lastVisibleItemPosition + VISIBLE_THRESHOLD > totalCount) {
            // If reached the bottom then load next page:
            setIsLoading(true);
            onLoadNextPage();
        }

        if(!isLoading && layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            // If reached top then load previous page:
            setIsLoading(true);
            onLoadPreviousPage();
        }

        // If collection has too many items then delete the first (invisible) ones
        if (!isLoading && totalCount > MAX_ITEMS_ALLOWED) {
            // Remove the first or the last items depending on the current visible position:
            int middle = layoutManager.getItemCount() / 2;
            int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
            if (lastVisible > middle) {
                onRemoveFirstItems();
            } else {
                onRemoveLastItems();
            }
        }
    }

    public abstract void onLoadNextPage();

    public abstract void onLoadPreviousPage();

    public abstract void onRemoveFirstItems();

    public abstract void onRemoveLastItems();

    void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
}
