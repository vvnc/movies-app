package com.vvnc.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private ArrayList<MovieModel> movies;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title;
        private TextView dvdDate;
        private ImageView poster;
        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movie_title);
            dvdDate = itemView.findViewById(R.id.dvd_date);
            poster = itemView.findViewById(R.id.poster);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    MoviesAdapter(ArrayList<MovieModel> movies) {
        this.movies = movies;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(movies.get(position).getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        holder.dvdDate.setText(dateFormat.format(movies.get(position).getDvdDate()));
        holder.poster.setImageDrawable(movies.get(position).getPoster());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return movies.size();
    }
}
