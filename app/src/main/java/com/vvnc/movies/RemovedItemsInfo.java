package com.vvnc.movies;

class RemovedItemsInfo {
    private int startPosition;
    private int count;

    RemovedItemsInfo(int startIndex, int count) {
        this.startPosition = startIndex;
        this.count = count;
    }

    int getStartPosition() {
        return startPosition;
    }

    int getCount() {
        return count;
    }
}
