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
    private ArrayList<MovieModel> movies;

    static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        private BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ItemViewHolder extends BaseViewHolder {
        // each data item is just a string in this case
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

    // Provide a suitable constructor (depends on the kind of dataset)
    MoviesAdapter(ArrayList<MovieModel> movies) {
        this.movies = movies;
    }

    @Override
    public int getItemViewType(int position) {
        return position >= movies.size() ? ItemType.PROGRESS.ordinal(): ItemType.MOVIE.ordinal();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ItemType.MOVIE.ordinal()) {
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
            h.title.setText(movies.get(position).getTitle());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            h.dvdDate.setText(dateFormat.format(movies.get(position).getDvdDate()));
            h.poster.setImageDrawable(movies.get(position).getPoster());
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
        return movies.size() + 1; // because the extra one is the progress bar
    }
}
