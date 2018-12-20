package com.tiktokdemo.lky.tiktokdemo.record.bean;

/**
 * Created by lky on 2017/4/28.
 */

public class TidalPatRecordFilterInfo {

    private int mBackgroundRes;
    private String mFilterName;
    private int mFilterRes;

    public TidalPatRecordFilterInfo(int backgroundRes, String filterName, int filterRes) {
        mBackgroundRes = backgroundRes;
        mFilterName = filterName;
        mFilterRes = filterRes;
    }

    public void setBackgroundRes(int backgroundRes) {
        mBackgroundRes = backgroundRes;
    }

    public void setFilterName(String filterName) {
        mFilterName = filterName;
    }

    public void setFilterRes(int filterRes) {
        mFilterRes = filterRes;
    }

    public int getBackgroundRes() {
        return mBackgroundRes;
    }

    public String getFilterName() {
        return mFilterName;
    }

    public int getFilterRes() {
        return mFilterRes;
    }
}
