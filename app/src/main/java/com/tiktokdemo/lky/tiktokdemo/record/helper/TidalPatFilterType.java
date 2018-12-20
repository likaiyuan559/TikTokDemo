package com.tiktokdemo.lky.tiktokdemo.record.helper;


import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.R;
/**
 * Created by lky on 2017/4/28.
 */

public enum TidalPatFilterType {
    //filterName,backgroundRes,filterRes
    original(AppUtil.getString(R.string.tidal_pat_filter_original),R.mipmap.filter_background_original,R.drawable.filter_original),                         //原图
    cf_17(AppUtil.getString(R.string.tidal_pat_filter_cf_17),R.mipmap.filter_cf_17,R.drawable.cf17_lut),             //鲜艳
    sf_03(AppUtil.getString(R.string.tidal_pat_filter_sf_03),R.mipmap.filter_sf_03,R.drawable.sf03_lut),             //柔和
    fm_05(AppUtil.getString(R.string.tidal_pat_filter_fm_05),R.mipmap.filter_fm_05,R.drawable.fm05_lut),             //初夏
    fs_10(AppUtil.getString(R.string.tidal_pat_filter_fs_10),R.mipmap.filter_fs_10,R.drawable.fs10_lut),             //小清新
    fm_10(AppUtil.getString(R.string.tidal_pat_filter_fm_10),R.mipmap.filter_fm_10,R.drawable.fm10_lut),             //时光
    mod_09(AppUtil.getString(R.string.tidal_pat_filter_mod_09),R.mipmap.filter_mod_09,R.drawable.mod09_lut),             //摩登
    re_03(AppUtil.getString(R.string.tidal_pat_filter_re_03),R.mipmap.filter_re_03,R.drawable.re03_lut),             //复古风
    cf_19(AppUtil.getString(R.string.tidal_pat_filter_cf_19),R.mipmap.filter_cf_19,R.drawable.cf19_lut),             //温暖
    ins_02(AppUtil.getString(R.string.tidal_pat_filter_ins_02),R.mipmap.filter_ins_02,R.drawable.ins02_lut),             //怀旧
    bw_03(AppUtil.getString(R.string.tidal_pat_filter_bw_03),R.mipmap.filter_bw_03,R.drawable.bw03_lut);             //黑白



    private String mFilterName;
    private int mFilterBackgroundRes;
    private int mFilterRes;
    TidalPatFilterType(String filterName, int filterBackgroundRes, int filterRes) {
        this.mFilterName = filterName;
        this.mFilterBackgroundRes = filterBackgroundRes;
        this.mFilterRes = filterRes;
    }

    public String getFilterName() {
        return mFilterName;
    }

    public int getFilterBackgroundRes() {
        return mFilterBackgroundRes;
    }

    public int getFilterRes() {
        return mFilterRes;
    }
}
