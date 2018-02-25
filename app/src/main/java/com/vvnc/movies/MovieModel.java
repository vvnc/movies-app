package com.vvnc.movies;
import com.vvnc.lorem.Capitalization;
import com.vvnc.lorem.LoremIpsumGenerator;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

class MovieModel {
    private static Calendar calendar;
    private static Random rand;
    private static final int pageItemsCount = 10;
    private static final int wordCountMin = 1;
    private static final int wordCountMax = 15;

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

    static ArrayList<MovieModel> loadPage(int page, Drawable placeholderIcon){
        // Debug version: randomly generated data:
        ArrayList<MovieModel> data = new ArrayList<>(pageItemsCount);
        for(int i = 0x00; i < pageItemsCount; ++i) {
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
        int wordCount = rand.nextInt(wordCountMax - wordCountMin) + wordCountMin;
        return String.format(Locale.ENGLISH,
                "Page: %d, item: %d. %s",
                page,
                itemNum,
                LoremIpsumGenerator.getNext(wordCount, Capitalization.FIRST_WORD));
    }
}
