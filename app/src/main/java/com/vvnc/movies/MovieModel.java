package com.vvnc.movies;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.vvnc.lorem.Capitalization;
import com.vvnc.lorem.LoremIpsumGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

class MovieModel {
    private static Calendar calendar;
    private static final int pageItemsCount = 10;
    private static final int wordCountMin = 1;
    private static final int wordCountMax = 15;
    private static Random random;

    private String title;
    private Date dvdDate;
    private Drawable poster;

    static {
        calendar = Calendar.getInstance();
        random = new Random();
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

    static ArrayList<MovieModel> loadPage(Context context, int page){
        // Debug version: randomly generated data:
        ArrayList<MovieModel> data = new ArrayList<>(pageItemsCount);
        Drawable icon = genNextIcon(context);
        for(int i = 0x00; i < pageItemsCount; ++i) {
            data.add(new MovieModel(genNextTitle(page), genNextDate(), icon));
        }
        return data;
    }

    private static Date genNextDate(){
        // Return the previous day:
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    private static String genNextTitle(int page){
        Random rand = new Random();
        int wordCount = rand.nextInt(wordCountMax - wordCountMin) + wordCountMin;
        return String.format(Locale.ENGLISH,
                "Page: %d. %s",
                page,
                LoremIpsumGenerator.getNext(wordCount, Capitalization.FIRST_WORD));
    }

    private static Drawable genNextIcon(Context context){
        // Get next random icon from resources
        int[] icons = new int[] {
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
        return context.getResources().getDrawable(icons[random.nextInt(icons.length)]);
    }
}
