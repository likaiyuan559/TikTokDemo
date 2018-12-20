package com.tiktokdemo.lky.tiktokdemo.record.effects.adapter;


import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsType;
/**
 * Created by lky on 2017/6/26.
 */

public interface TidalPatSpecialEffectsFilterClickListener {

    void onItemTouchDown(int position, SpecialEffectsType specialEffectsType);
    void onItemTouchUp(int position);
    void onItemStateChange(int position, boolean isSelected, SpecialEffectsType specialEffectsType);
}
