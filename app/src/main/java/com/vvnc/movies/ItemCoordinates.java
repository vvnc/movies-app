package com.vvnc.movies;

class ItemCoordinates {
    private int pageNum;
    private int itemIndex;

    ItemCoordinates(int pageNum, int itemIndex) {
        this.pageNum = pageNum;
        this.itemIndex = itemIndex;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getItemIndex() {
        return itemIndex;
    }

}
