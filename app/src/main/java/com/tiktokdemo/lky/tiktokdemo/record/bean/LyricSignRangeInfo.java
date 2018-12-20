package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.Serializable;

import android.graphics.Color;

/**
 * Created by lky on 2017/8/15.
 */

public class LyricSignRangeInfo implements Serializable {

    private int mFrom;
    private int mTo;
    private int mTextColor;

    public LyricSignRangeInfo(int from, int to) {
        this.mFrom = from;
        this.mTo = to;
        this.mTextColor = Color.WHITE;
    }
    public LyricSignRangeInfo(int from, int to, int textColor) {
        this.mFrom = from;
        this.mTo = to;
        this.mTextColor = textColor;
    }

    public boolean isWrappedBy(int start, int end) {
        return (start > mFrom && start < mTo) || (end > mFrom && end < mTo);
    }

    public boolean contains(int start, int end) {
        return mFrom < start && mTo >= end;
    }

    public boolean isEqual(int start, int end) {
        return (mFrom == start && mTo == end) || (mFrom == end && mTo == start);
    }

    public int getAnchorPosition(int value) {
        if ((value - mFrom) - (mTo - value) >= 0) {
            return mTo;
        } else {
            return mFrom;
        }
    }

    public int getFrom() {
        return mFrom;
    }

    public Integer getFromInteger(){
        return mFrom;
    }

    public int getTo() {
        return mTo;
    }

    public Integer getToInteger(){
        return mTo;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setFrom(int from) {
        mFrom = from;
    }

    public void setTo(int to) {
        mTo = to;
    }
}
