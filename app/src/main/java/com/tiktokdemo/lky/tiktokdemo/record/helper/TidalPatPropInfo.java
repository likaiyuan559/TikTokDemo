package com.tiktokdemo.lky.tiktokdemo.record.helper;


import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.helper.FilterInfo;
/**
 * Created by lky on 2017/5/3.
 */

public class TidalPatPropInfo {
    private int[] mRes;
    private FilterInfo mFilterInfo;
    private int mBackgroundRes;

    public void setRes(int[] res) {
        mRes = res;
    }

    public void setFilterInfo(FilterInfo filterInfo) {
        mFilterInfo = filterInfo;
    }

    public void setBackgroundRes(int backgroundRes) {
        mBackgroundRes = backgroundRes;
    }

    public int[] getRes() {
        return mRes;
    }

    public FilterInfo getFilterInfo() {
        return mFilterInfo;
    }

    public int getBackgroundRes() {
        return mBackgroundRes;
    }
}
