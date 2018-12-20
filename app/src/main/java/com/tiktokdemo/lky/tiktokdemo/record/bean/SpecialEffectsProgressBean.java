package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.Serializable;

/**
 * Created by lky on 2017/6/21.
 */

public class SpecialEffectsProgressBean implements Serializable {
    private SpecialEffectsType mType;
    private int mShowColor;
    private long mTimeStart;
    private long mTimeEnd;

    public void setType(SpecialEffectsType type) {
        mType = type;
    }

    public void setShowColor(int showColor) {
        mShowColor = showColor;
    }

    public void setTimeStart(long timeStart) {
        mTimeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        mTimeEnd = timeEnd;
    }

    public SpecialEffectsType getType() {
        return mType;
    }

    public int getShowColor() {
        return mShowColor;
    }

    public long getTimeStart() {
        return mTimeStart;
    }

    public long getTimeEnd() {
        return mTimeEnd;
    }
}
