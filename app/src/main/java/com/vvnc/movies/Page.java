package com.vvnc.movies;

import java.util.ArrayList;

class Page<DataType> {
    private int pageNum;
    private ArrayList<DataType> items;

    Page(int pageNum, ArrayList<DataType> items) {
        this.pageNum = pageNum;
        this.items = items;
    }

    public int getPageNum() {
        return pageNum;
    }

    public ArrayList<DataType> getItems() {
        return items;
    }
}
