package com.vvnc.movies;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    static class RVUpdateHandler extends Handler {
        private MoviesAdapter adapter;

        RVUpdateHandler(MoviesAdapter adapter) {
            this.adapter = adapter;
        }

        public void handleMessage(android.os.Message msg) {
            adapter.notifyItemRangeInserted(msg.arg1, msg.arg2);
        }
    }

    private MoviesAdapter adapter;
    private RVUpdateHandler handler;
    private ArrayList<MovieModel> movies;
    private RecyclerView recyclerView;
    private CustomOnScrollListener onScrollListener;
    private LinearLayoutManager layoutManager;
    private Random random;
    private int[] placeholderIcons;
    private int currentPage = 0;
    private Drawable currentPlaceholderIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();
        placeholderIcons = new int[]{
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
                R.drawable.chocolate_,
                R.drawable.coffee_cup,
                R.drawable.cookie_,
                R.drawable.corn_,
                R.drawable.croissant_,
                R.drawable.cupcake_,
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

        recyclerView = findViewById(R.id.movies_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        movies = MovieModel.loadPage(0, genNextPlaceholderIcon());
        adapter = new MoviesAdapter(movies);
        handler = new RVUpdateHandler(adapter);
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
        currentPage = page;
        currentPlaceholderIcon = genNextPlaceholderIcon();
        Thread loaderThread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.d("SLEEP_FAILED", e.toString());
                }
                int insertStart = movies.size();
                ArrayList<MovieModel> newPortion = MovieModel.loadPage(
                        currentPage,
                        currentPlaceholderIcon);
                movies.addAll(newPortion);
                Message msg = new Message();
                msg.arg1 = insertStart;
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
                // Remove all items before first visible item:
                int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
                if (firstVisiblePos > 1) {
                    movies.subList(0, firstVisiblePos - 1).clear();
                    adapter.notifyItemRangeRemoved(0, firstVisiblePos - 1);
                }
            }
        });
    }

    private Drawable genNextPlaceholderIcon() {
        // Get next random icon from resources:
        return getResources().getDrawable(
                placeholderIcons[random.nextInt(placeholderIcons.length)]);
    }
}
