package com.tiktokdemo.lky.tiktokdemo.record.helper;

import java.io.Serializable;

/**
 * Created by lky on 2017/8/1.
 */

public enum  RecordTimeType implements Serializable {
    RECORD_TIME_15(15),
    RECORD_TIME_120(120);

    private int mSecond;

    RecordTimeType(int second) {
        this.mSecond = second;
    }

    public int getValue() {
        return mSecond;
    }
}
