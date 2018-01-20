package com.vvnc.movies;

import java.util.ArrayList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private MoviesAdapter adapter;
    private ArrayList<MovieModel> movies;
    private RecyclerView recyclerView;
    private CustomOnScrollListener onScrollListener;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.movies_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        movies = MovieModel.loadPage(this, 0);
        adapter = new MoviesAdapter(movies);
        recyclerView.setAdapter(adapter);

        onScrollListener = new CustomOnScrollListener(layoutManager, 1) {
            @Override
            public void onLoadPage(int page) {
                loadMoreData(page);
            }

            @Override
            public void onTooManyItems() {
                removeFirstItems();
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void loadMoreData(int page) {
        int insertedPosition = movies.size() + 1;
        ArrayList<MovieModel> newPortion = MovieModel.loadPage(this, page);
        movies.addAll(newPortion);
        adapter.notifyItemRangeInserted(insertedPosition, newPortion.size());
    }

    private void removeFirstItems() {
        // Remove all items before first visible item:
        int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
        movies.subList(0, firstVisiblePos - 1).clear();
        adapter.notifyItemRangeRemoved(0, firstVisiblePos);
    }
}
