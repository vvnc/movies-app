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
    final int loadStep = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.movies_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        movies = MovieModel.getNewPortion(this,loadStep);
        adapter = new MoviesAdapter(movies);
        recyclerView.setAdapter(adapter);

        onScrollListener = new CustomOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                loadMoreData(loadStep);
            }

            @Override
            public void onTooManyItems() {
                removeFirstItems();
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void loadMoreData(int count) {
        int insertedPosition = movies.size() + 1;
        movies.addAll(MovieModel.getNewPortion(this, count));
        adapter.notifyItemRangeInserted(insertedPosition, count);
    }

    private void removeFirstItems() {
        // Remove all items before first visible item:
        int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
        movies.subList(0, firstVisiblePos - 1).clear();
        adapter.notifyItemRangeRemoved(0, firstVisiblePos );
    }
}
