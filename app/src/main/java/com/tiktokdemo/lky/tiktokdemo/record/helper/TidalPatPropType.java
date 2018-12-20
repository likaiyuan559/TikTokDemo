package com.tiktokdemo.lky.tiktokdemo.record.helper;


import com.tiktokdemo.lky.tiktokdemo.R;
/**
 * Created by lky on 2017/5/3.
 */

public enum  TidalPatPropType {
    DEFAULT(R.mipmap.chaopai_luzhi_wudaoju),
    CIGAR(R.mipmap.icon_cigar),
    GLASSES(R.mipmap.icon_glasses),
    FACECLOTH(R.mipmap.icon_towel),
    GROUP1(R.mipmap.icon_group2),
    GROUP2(R.mipmap.icon_group1);


    private int mBackgroundRes;
    TidalPatPropType(int backgroundRes) {
        this.mBackgroundRes = backgroundRes;
    }

    public int getBackgroundRes() {
        return mBackgroundRes;
    }
}
