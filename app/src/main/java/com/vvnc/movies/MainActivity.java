package com.vvnc.movies;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static class RVUpdateHandler extends Handler {
        static class InsertItemsMessage {
            private int savedItemPosition;
            private int insertPageNum;
            private ArrayList<ItemModel> newPortion;

            InsertItemsMessage(int savedItemPosition,
                               int insertPageNum,
                               ArrayList<ItemModel> newPortion) {
                this.savedItemPosition = savedItemPosition;
                this.insertPageNum = insertPageNum;
                this.newPortion = newPortion;
            }

            int getSavedItemPosition() {
                return savedItemPosition;
            }

            int getInsertPageNum() {
                return insertPageNum;
            }

            ArrayList<ItemModel> getNewPortion() {
                return newPortion;
            }
        }

        private Adapter adapter;
        private CustomOnScrollListener onScrollListener;
        private LinearLayoutManager layoutManager;

        RVUpdateHandler(Adapter adapter,
                        CustomOnScrollListener onScrollListener,
                        LinearLayoutManager layoutManager) {
            this.adapter = adapter;
            this.onScrollListener = onScrollListener;
            this.layoutManager = layoutManager;
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == RVMsgType.PUSH_ITEMS_BACK.ordinal()) {
                InsertItemsMessage msgObj = (InsertItemsMessage) msg.obj;

                // Push back the new items:
                int insertStartIndex = adapter.pushPageBack(msgObj.getInsertPageNum(),
                        msgObj.getNewPortion());

                // Notify recycler view that the new items are inserted:
                adapter.notifyItemRangeInserted(insertStartIndex, msgObj.getNewPortion().size());

                // Scroll to saved item (if need to):
                if (msgObj.getSavedItemPosition() != NONE_SAVED_ITEM_POSITION) {
                    layoutManager.scrollToPosition(msgObj.getSavedItemPosition());
                }

                // Tell on scroll listener that the loading is over:
                onScrollListener.setIsLoading(false);
            } else if (msg.what == RVMsgType.PUSH_ITEMS_FRONT.ordinal()) {
                InsertItemsMessage msgObj = (InsertItemsMessage) msg.obj;

                // Push front the new items:
                adapter.pushPageFront(msgObj.getInsertPageNum(), msgObj.getNewPortion());

                // Notify recycler view that the new items are inserted:
                adapter.notifyItemRangeInserted(0, msgObj.getNewPortion().size());

                // Tell on scroll listener that the loading is over:
                onScrollListener.setIsLoading(false);
            } else if (msg.what == RVMsgType.PUSH_STUB_FRONT.ordinal()) {
                InsertItemsMessage msgObj = (InsertItemsMessage) msg.obj;

                // Push front the new items:
                adapter.pushPageFront(msgObj.getInsertPageNum(), msgObj.getNewPortion());

                // Notify recycler view that the new items are inserted:
                adapter.notifyItemRangeInserted(0, msgObj.getNewPortion().size());

                // The load is not over, don't set it false!
            } else if (msg.what == RVMsgType.REMOVE_FIRST_PAGE.ordinal()) {
                int removedCount = adapter.removeFirstPage();
                if (removedCount > -1) {
                    adapter.notifyItemRangeRemoved(0, removedCount);
                }
            } else {
                Log.e("HANDLER", "Unknown RecyclerView message type: " + msg.what);
            }
        }
    }

    private Adapter adapter;
    private RVUpdateHandler handler;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CustomOnScrollListener onScrollListener;
    private int[] placeholderIcons;
    private int currentPage;
    private int savedItemPosition;
    private static final int NONE_SAVED_ITEM_POSITION = -1;
    private final String CURRENT_PAGE_KEY = "CURRENT_PAGE";
    private final String CURRENT_ITEM_KEY = "CURRENT_ITEM";
    private Drawable currentPlaceholderIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore previous item position:
        currentPage = 0;
        savedItemPosition = 0;  // force scroll to the start if it's the first page
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
            savedItemPosition = savedInstanceState.getInt(CURRENT_ITEM_KEY);
        }

        // Init RecyclerView:
        setContentView(R.layout.activity_main);
        initPlaceholderIcons();
        layoutManager = new LinearLayoutManager(this);
        adapter = new Adapter();
        recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Add custom on scroll listener:
        onScrollListener = new CustomOnScrollListener(layoutManager) {
            @Override
            public void onLoadNextPage() {
                loadNextPage();
            }

            @Override
            public void onLoadPreviousPage() {
                loadPreviousPage();
            }

            @Override
            public void onRemoveFirstItems() {
                removeFirstPage();
            }

            @Override
            public void onRemoveLastItems() {
                removeLastPage();
            }
        };
        handler = new RVUpdateHandler(adapter, onScrollListener, layoutManager);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        ItemCoordinates coordinates = adapter.getItemCoordinates(
                layoutManager.findFirstVisibleItemPosition());
        if (coordinates == null) {
            savedInstanceState.putInt(CURRENT_PAGE_KEY, currentPage);
            savedInstanceState.putInt(CURRENT_ITEM_KEY, 0);
        } else {
            savedInstanceState.putInt(CURRENT_PAGE_KEY, coordinates.getPageNum());
            savedInstanceState.putInt(CURRENT_ITEM_KEY, coordinates.getItemIndex());
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    private void loadNextPage() {
        currentPlaceholderIcon = getPlaceholderIcon(currentPage);
        Thread loaderThread = new Thread(new Runnable() {
            public void run() {
                ArrayList<ItemModel> newPortion = MovieModel.loadPage(
                        currentPage,
                        currentPlaceholderIcon);
                Message msg = new Message();
                msg.what = RVMsgType.PUSH_ITEMS_BACK.ordinal();
                msg.obj = new RVUpdateHandler.InsertItemsMessage(savedItemPosition,
                        currentPage, newPortion);
                // Invalidate saved position:
                savedItemPosition = NONE_SAVED_ITEM_POSITION;
                currentPage++;
                handler.sendMessage(msg);
            }
        });
        loaderThread.start();
    }

    private void loadPreviousPage() {
        int firstPageNum = adapter.getFirstPageNum();
        if (firstPageNum == 0) {
            // Reached the beginning, nothing to load. Undo the loading:
            onScrollListener.setIsLoading(false);
            return;
        }
        final int previousPageNum = firstPageNum == -1 ? 0 : firstPageNum - 1;
        currentPlaceholderIcon = getPlaceholderIcon(previousPageNum);
        Thread loaderThread = new Thread(new Runnable() {
            public void run() {
                // Add stub item to the RV:
                ArrayList<ItemModel> stubPage = new ArrayList<>();
                stubPage.add( new StubModel() );
                Message msg = new Message();
                msg.what = RVMsgType.PUSH_STUB_FRONT.ordinal();
                msg.obj = new RVUpdateHandler.InsertItemsMessage(NONE_SAVED_ITEM_POSITION,
                        previousPageNum, stubPage);
                handler.sendMessage(msg);

                // Load real data:
                ArrayList<ItemModel> newPortion = MovieModel.loadPage(
                        previousPageNum,
                        currentPlaceholderIcon);

                // Remove stub page:
                removeFirstPage();

                msg = new Message();
                msg.what = RVMsgType.PUSH_ITEMS_FRONT.ordinal();
                msg.obj = new RVUpdateHandler.InsertItemsMessage(NONE_SAVED_ITEM_POSITION,
                        previousPageNum, newPortion);
                handler.sendMessage(msg);
            }
        });
        loaderThread.start();
    }

    private void removeFirstPage() {
        Message msg = new Message();
        msg.what = RVMsgType.REMOVE_FIRST_PAGE.ordinal();
        handler.sendMessage(msg);
    }

    private void removeLastPage() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                RemovedItemsInfo info = adapter.removeLastPage();
                if (info != null) {
                    currentPage--;
                    adapter.notifyItemRangeRemoved(info.getStartPosition(), info.getCount());
                }
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

    private Drawable getPlaceholderIcon(int pageNum) {
        // Get next icon from resources:
        return getResources().getDrawable(placeholderIcons[pageNum % placeholderIcons.length]);
    }
}
