package com.heyhou.social.video;

/**
 * Created by starjiang on 17-6-21.
 */

public class FilterInfo {

    public final static int FILTER_SOUL_FADE_OUT = 1;

    private int filter;
    private long startTime;
    private long endTime;

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
