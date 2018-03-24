package com.vvnc.movies;
import com.vvnc.lorem.Capitalization;
import com.vvnc.lorem.LoremIpsumGenerator;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

class MovieModel extends ItemModel {
    private static Calendar calendar;
    private static Random rand;
    private static final int PAGE_ITEMS_COUNT = 10;
    private static final int WORD_COUNT_MIN = 1;
    private static final int WORD_COUNT_MAX = 15;

    private String title;
    private Date dvdDate;
    private Drawable poster;

    static {
        calendar = Calendar.getInstance();
        rand = new Random();
        LoremIpsumGenerator.reset();
    }

    private MovieModel(String title, Date dvdDate, Drawable poster) {
        this.title = title;
        this.dvdDate = dvdDate;
        this.poster = poster;
    }

    String getTitle() {
        return title;
    }

    Date getDvdDate() {
        return dvdDate;
    }

    Drawable getPoster() {
        return poster;
    }

    static ArrayList<ItemModel> loadPage(int page, Drawable placeholderIcon){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.d("LOAD_MORE_INTERRUPTED", e.toString());
        }
        // Debug version: randomly generated data:
        ArrayList<ItemModel> data = new ArrayList<>(PAGE_ITEMS_COUNT);
        for(int i = 0x00; i < PAGE_ITEMS_COUNT; ++i) {
            data.add(new MovieModel(genNextTitle(page, i), genNextDate(), placeholderIcon));
        }
        return data;
    }

    private static Date genNextDate(){
        // Return the previous day:
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    private static String genNextTitle(int page, int itemNum){
        int wordCount = rand.nextInt(WORD_COUNT_MAX - WORD_COUNT_MIN) + WORD_COUNT_MIN;
        return String.format(Locale.ENGLISH,
                "Page: %d, item: %d. %s",
                page,
                itemNum,
                LoremIpsumGenerator.getNext(wordCount, Capitalization.FIRST_WORD));
    }
}
