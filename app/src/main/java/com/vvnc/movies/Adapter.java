package com.vvnc.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.BaseViewHolder> {
    private ArrayList<Page<ItemModel>> items;

    static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        private BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ItemViewHolder extends BaseViewHolder {
        private TextView title;
        private TextView dvdDate;
        private ImageView poster;

        private ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movie_title);
            dvdDate = itemView.findViewById(R.id.dvd_date);
            poster = itemView.findViewById(R.id.poster);
        }
    }

    private static class ProgressViewHolder extends BaseViewHolder {
        private ProgressBar progressBar;

        private ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    Adapter() {
        this.items = new ArrayList<>();
    }

    Adapter(int pageNum, ArrayList<ItemModel> firstPage) {
        this.items = new ArrayList<>();
        this.items.add(new Page<>(pageNum, firstPage));
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= calcItemsCount()) {
            return ItemType.PROGRESS.ordinal();
        } else {
            // TODO: get item by position, check it's type. If it's Progress then return PROGRESS
            return ItemType.MOVIE.ordinal();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.MOVIE.ordinal()) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder h = (ItemViewHolder) holder;
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            MovieModel item = (MovieModel) getItem(position);
            if (item == null) {
                throw new MoviesException("Couldn't get the item by position: " + position);
            }
            h.title.setText(item.getTitle());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            h.dvdDate.setText(dateFormat.format(item.getDvdDate()));
            h.poster.setImageDrawable(item.getPoster());
        } else if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder h = (ProgressViewHolder) holder;
            h.progressBar.setIndeterminate(true);
        } else {
            throw new MoviesException("Unexpected view holder type in the recycler view: " +
                    holder);
        }
    }

    @Override
    public int getItemCount() {
        return calcItemsCount() + 1; // because the extra one is the progress bar
    }

    int getFirstPageNum() {
        if (items.size() > 0) {
            return items.get(0).getPageNum();
        } else {
            return -1;
        }
    }

    int getLastPageNum() {
        if (items.size() > 0) {
            return items.get(items.size() - 1).getPageNum();
        } else {
            return -1;
        }
    }

    void pushPageFront(int pageNum, ArrayList<ItemModel> page) {
        items.add(0, new Page<>(pageNum, page));
    }

    int pushPageBack(int pageNum, ArrayList<ItemModel> page) {
        int insertStartIndex = calcItemsCount();
        items.add(new Page<>(pageNum, page));
        return insertStartIndex;
    }

    int removeFirstPage() {
        if (items.size() > 0) {
            int removedCount = items.get(0).getItems().size();
            items.remove(0);
            return removedCount;
        } else {
            return 0;
        }
    }

    RemovedItemsInfo removeLastPage() {
        if (items.size() > 0) {
            int lastPage = items.size() - 1;
            int removedStartPosition = getPosition(new ItemCoordinates(lastPage, 0));
            int removedCount = items.get(lastPage).getItems().size();
            items.remove(lastPage);
            return new RemovedItemsInfo(removedStartPosition, removedCount);
        } else {
            return null;
        }
    }

    private int getPosition(ItemCoordinates coordinates) {
        int position = 0;
        for (int page = 0; page < items.size(); page++) {
            if (page != coordinates.getPageNum()) {
                position += items.get(page).getItems().size();
            } else {
                position += coordinates.getItemIndex();
                return position;
            }
        }
        // Reached the end, the page is not found:
        return -1;
    }

    ItemCoordinates getItemCoordinates(int position) {
        if (position < 0) {
            return null;
        }
        int currentPosition = position;
        for (Page<ItemModel> page : items) {
            if (currentPosition < page.getItems().size()) {
                return new ItemCoordinates(page.getPageNum(), currentPosition);
            } else {
                currentPosition -= page.getItems().size();
            }
        }
        return null; // reached the end of collection, position is out of boundaries
    }

    private int calcItemsCount() {
        if (items == null) {
            return 0;
        }
        int size = 0;
        for (Page<ItemModel> page : items) {
            size += page.getItems().size();
        }
        return size;
    }

    private ItemModel getItem(int position) {
        if (position < 0) {
            return null;
        }
        int currentPosition = position;
        for (Page<ItemModel> page : items) {
            if (currentPosition < page.getItems().size()) {
                return page.getItems().get(currentPosition);
            } else {
                currentPosition -= page.getItems().size();
            }
        }
        return null; // reached the end of collection, position is out of boundaries
    }
}
