package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.Serializable;

/**
 * Created by lky on 2017/6/21.
 */

public enum  SpecialEffectsType implements Serializable {
    SoulOut(1),
    Shake(0),
    Default(0),
    TimeBack(0);

    private int filter;

    SpecialEffectsType(int filter) {
        this.filter = filter;
    }

    public int getFilter() {
        return filter;
    }
}
