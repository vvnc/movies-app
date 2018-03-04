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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.BaseViewHolder> {
    private ArrayList<Page<MovieModel>> movies;

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

    MoviesAdapter() {
        this.movies = new ArrayList<>();
    }

    MoviesAdapter(int pageNum, ArrayList<MovieModel> firstPage) {
        this.movies = new ArrayList<>();
        this.movies.add(new Page<>(pageNum, firstPage));
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= calcMoviesSize()) {
            return ItemType.PROGRESS.ordinal();
        } else {
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
            MovieModel item = getItem(position);
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
        return calcMoviesSize() + 1; // because the extra one is the progress bar
    }

    int pushBackPage(int pageNum, ArrayList<MovieModel> page) {
        int insertStartIndex = calcMoviesSize();
        movies.add(new Page<>(pageNum, page));
        return insertStartIndex;
    }

    int removeFirstPage() {
        int removedCount = movies.get(0).getItems().size();
        movies.remove(0);
        return removedCount;
    }

    ItemCoordinates getItemCoordinates(int position) {
        if (position < 0) {
            return null;
        }
        int currentPosition = position;
        for (Page<MovieModel> page : movies) {
            if (currentPosition < page.getItems().size()) {
                return new ItemCoordinates(page.getPageNum(), currentPosition);
            } else {
                currentPosition -= page.getItems().size();
            }
        }
        return null; // reached the end of collection, position is out of boundaries
    }

    private int calcMoviesSize() {
        if (movies == null) {
            return 0;
        }
        int size = 0;
        for (Page<MovieModel> page : movies) {
            size += page.getItems().size();
        }
        return size;
    }

    private MovieModel getItem(int position) {
        if (position < 0) {
            return null;
        }
        int currentPosition = position;
        for (Page<MovieModel> page : movies) {
            if (currentPosition < page.getItems().size()) {
                return page.getItems().get(currentPosition);
            } else {
                currentPosition -= page.getItems().size();
            }
        }
        return null; // reached the end of collection, position is out of boundaries
    }
}
