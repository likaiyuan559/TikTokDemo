package com.heyhou.social.video;

/**
 * Created by lky on 2016/12/9.
 */

public enum VideoTimeType {

    SPEED_M4(-3),//慢速4倍
    SPEED_M2(-2),//慢速2倍
    SPEED_N1(1),//正常速度
    SPEED_P2(2),//快速2倍
    SPEED_P4(3);//快速4倍

    private int value;

    public int getValue() {
        return value;
    }

    VideoTimeType(int value) {
        this.value = value;
    }
}
