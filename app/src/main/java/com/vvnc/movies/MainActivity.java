package com.vvnc.movies;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static class RVUpdateHandler extends Handler {
        private MoviesAdapter adapter;
        private CustomOnScrollListener onScrollListener;
        private LinearLayoutManager layoutManager;

        RVUpdateHandler(MoviesAdapter adapter,
                        CustomOnScrollListener onScrollListener,
                        LinearLayoutManager layoutManager) {
            this.adapter = adapter;
            this.onScrollListener = onScrollListener;
            this.layoutManager = layoutManager;
        }

        public void handleMessage(android.os.Message msg) {
            // Notify recycler view that the new items are inserted:
            adapter.notifyItemRangeInserted(msg.arg1, msg.arg2);

            // If inserted to the very beginning, then scroll to the top:
            if(msg.arg1 == 0) {
                layoutManager.scrollToPosition(0);
            }

            // Tell on scroll listener that the loading is over:
            onScrollListener.setIsLoading(false);
        }
    }

    private MoviesAdapter adapter;
    private RVUpdateHandler handler;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int[] placeholderIcons;
    private int currentPage;
    private final String CURRENT_PAGE_KEY = "CURRENT_PAGE";
    private Drawable currentPlaceholderIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        } else {
            currentPage = 0;
        }

        setContentView(R.layout.activity_main);
        initPlaceholderIcons();
        recyclerView = findViewById(R.id.movies_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MoviesAdapter();
        CustomOnScrollListener onScrollListener = new CustomOnScrollListener(layoutManager,
                currentPage)
        {
            @Override
            public void onLoadPage(int page) {
                loadMoreData(page);
            }

            @Override
            public void onTooManyItems() {
                removeFirstItems();
            }
        };
        handler = new RVUpdateHandler(adapter, onScrollListener, layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        ItemCoordinates coordinates = adapter.getItemCoordinates(
                layoutManager.findFirstVisibleItemPosition());
        if(coordinates == null) {
            savedInstanceState.putInt(CURRENT_PAGE_KEY, currentPage);
        } else {
            savedInstanceState.putInt(CURRENT_PAGE_KEY, coordinates.getPageNum());
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    private void loadMoreData(int page) {
        currentPage = page;
        currentPlaceholderIcon = genNextPlaceholderIcon();
        Thread loaderThread = new Thread(new Runnable() {
            public void run() {
                ArrayList<MovieModel> newPortion = MovieModel.loadPage(
                        currentPage,
                        currentPlaceholderIcon);
                int insertStartIndex = adapter.pushBackPage(currentPage, newPortion);
                Message msg = new Message();
                msg.arg1 = insertStartIndex;
                msg.arg2 = newPortion.size();
                handler.sendMessage(msg);
            }
        });
        loaderThread.start();
    }

    private void removeFirstItems() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Just remove the first page:
                int removedCount = adapter.removeFirstPage();
                adapter.notifyItemRangeRemoved(0, removedCount);
            }
        });
    }

    private void initPlaceholderIcons() {
        placeholderIcons = new int[]{
                R.drawable.coffee_cup,
                R.drawable.chocolate_,
                R.drawable.cookie_,
                R.drawable.croissant_,
                R.drawable.cupcake_,
                R.drawable.apple_,
                R.drawable.bacon_,
                R.drawable.bananas_,
                R.drawable.bell_pepper,
                R.drawable.broccoli_,
                R.drawable.carrot_,
                R.drawable.cheese_,
                R.drawable.cherry_,
                R.drawable.chicken_leg,
                R.drawable.chili_pepper,
                R.drawable.corn_,
                R.drawable.donut_,
                R.drawable.eggplant_,
                R.drawable.fortune_cookie,
                R.drawable.french_fries,
                R.drawable.fried_egg,
                R.drawable.grapes_,
                R.drawable.hamburger_,
                R.drawable.hot_dog,
                R.drawable.ice_cream,
                R.drawable.ketchup_,
                R.drawable.meatball_,
                R.drawable.melon_,
                R.drawable.milk_,
                R.drawable.milkshake_,
                R.drawable.mushroom_,
                R.drawable.mustard_,
                R.drawable.onigiri_,
                R.drawable.orange_,
                R.drawable.pea_,
                R.drawable.peach_,
                R.drawable.pear_,
                R.drawable.piece_of_cake,
                R.drawable.pineapple_,
                R.drawable.pizza_,
                R.drawable.popsicle_,
                R.drawable.pudding_,
                R.drawable.radish_,
                R.drawable.scallion_,
                R.drawable.soda_,
                R.drawable.strawberry_,
                R.drawable.sushi_,
                R.drawable.taco_,
                R.drawable.toast_,
                R.drawable.watermelon_
        };
    }

    private Drawable genNextPlaceholderIcon() {
        // Get next icon from resources:
        return getResources().getDrawable(placeholderIcons[currentPage % placeholderIcons.length]);
    }
}
