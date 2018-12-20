package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tiktokdemo.lky.tiktokdemo.record.helper.RecordTimeType;

/**
 * Created by lky on 2017/5/4.
 */
public class TidalPatRecordDraftBean implements Serializable {

    private String userId;
    private int musicId;
    private String musicName;
    private String musicCover;
    private String musicLocalUrl;
    private String videoCover;
    private String videoName;
    private String videoLocalUrl;
    private int topicId;
    private String topicName;
    private long createTime;
    private String videoLocalArrays;
    private int recordContinueTime;
    private long cutMusicPosition;
    private String breakTimeArrays;
    private float mOriginalVolume;
    private float mBackgroundVolume;
    private boolean isOpenBeauty;
    private boolean isHasFilter;
    private boolean isHasSpecialEffects;
    private String mSpecialEffectsFilters;
    private SpecialEffectsType mSpecialEffectsType;
    private SpecialEffectsParentType mSpecialEffectsParentType;
    private boolean isFromCropVideo;
    private RecordTimeType mRecordTimeType;
    private int mBattleId;
    private String mBattleName;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setMusicCover(String musicCover) {
        this.musicCover = musicCover;
    }

    public void setMusicLocalUrl(String musicLocalUrl) {
        this.musicLocalUrl = musicLocalUrl;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setVideoLocalUrl(String videoLocalUrl) {
        this.videoLocalUrl = videoLocalUrl;
    }

    public int getMusicId() {
        return musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getMusicCover() {
        return musicCover;
    }

    public String getMusicLocalUrl() {
        return musicLocalUrl;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoLocalUrl() {
        return videoLocalUrl;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setVideoLocalArrays(String videoLocalArrays) {
        this.videoLocalArrays = videoLocalArrays;
    }

    public String getVideoLocalArrays() {
        return videoLocalArrays;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setVideoLocalArraysFromList(ArrayList<String> localArrays){
        Gson gson = new Gson();
        this.videoLocalArrays = gson.toJson(localArrays);
    }

    public ArrayList<String> getVideoLocalArrayFromList(){
        if(TextUtils.isEmpty(this.videoLocalArrays)){
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> arrayList = new Gson().fromJson(this.videoLocalArrays,type);
        return arrayList;
    }

    public void setRecordContinueTime(int recordContinueTime) {
        this.recordContinueTime = recordContinueTime;
    }

    public void setCutMusicPosition(long cutMusicPosition) {
        this.cutMusicPosition = cutMusicPosition;
    }

    public void setBreakTimeArrays(String breakTimeArrays) {
        this.breakTimeArrays = breakTimeArrays;
    }

    public int getRecordContinueTime() {
        return recordContinueTime;
    }

    public long getCutMusicPosition() {
        return cutMusicPosition;
    }

    public String getBreakTimeArrays() {
        return breakTimeArrays;
    }

    public void setBreakTimeArraysFromList(ArrayList<Integer> arrayList){
        Gson gson = new Gson();
        this.breakTimeArrays = gson.toJson(arrayList);
    }

    public ArrayList<Integer> getBreakTimeArraysFromList(){
        if(TextUtils.isEmpty(this.breakTimeArrays)){
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer> arrayList = new Gson().fromJson(this.breakTimeArrays,type);
        return arrayList;
    }

    public void setOriginalVolume(float originalVolume) {
        mOriginalVolume = originalVolume;
    }

    public void setBackgroundVolume(float backgroundVolume) {
        mBackgroundVolume = backgroundVolume;
    }

    public float getOriginalVolume() {
        return mOriginalVolume;
    }

    public float getBackgroundVolume() {
        return mBackgroundVolume;
    }

    public void setOpenBeauty(boolean openBeauty) {
        isOpenBeauty = openBeauty;
    }

    public void setHasFilter(boolean hasFilter) {
        isHasFilter = hasFilter;
    }

    public boolean isOpenBeauty() {
        return isOpenBeauty;
    }

    public boolean isHasFilter() {
        return isHasFilter;
    }

    public void setHasSpecialEffects(boolean hasSpecialEffects) {
        isHasSpecialEffects = hasSpecialEffects;
    }

    public boolean isHasSpecialEffects() {
        return isHasSpecialEffects;
    }

    public void setSpecialEffectsFilters(String specialEffectsFilters) {
        mSpecialEffectsFilters = specialEffectsFilters;
    }

    public String getSpecialEffectsFilters() {
        return mSpecialEffectsFilters;
    }

    public void setSpecialEffectsFiltersFromList(ArrayList<SpecialEffectsProgressBean> specialEffectsFiltersFromList){
//        try {
//            mSpecialEffectsFilters = HomeCacheUtil.serialize(specialEffectsFiltersFromList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public ArrayList<SpecialEffectsProgressBean> getSpecialEffectsFiltersFromList(){
//        if(TextUtils.isEmpty(mSpecialEffectsFilters)){
//            return new ArrayList<>();
//        }
//        try {
//            return (ArrayList<SpecialEffectsProgressBean>) HomeCacheUtil.deSerialization(mSpecialEffectsFilters);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        return new ArrayList<>();
    }

    public void setSpecialEffectsType(SpecialEffectsType specialEffectsType) {
        mSpecialEffectsType = specialEffectsType;
    }

    public SpecialEffectsType getSpecialEffectsType() {
        return mSpecialEffectsType;
    }

    public void setSpecialEffectsParentType(SpecialEffectsParentType specialEffectsParentType) {
        mSpecialEffectsParentType = specialEffectsParentType;
    }

    public SpecialEffectsParentType getSpecialEffectsParentType() {
        return mSpecialEffectsParentType;
    }

    public void setFromCropVideo(boolean fromCropVideo) {
        isFromCropVideo = fromCropVideo;
    }

    public boolean isFromCropVideo() {
        return isFromCropVideo;
    }

    public void setRecordTimeType(RecordTimeType recordTimeType) {
        mRecordTimeType = recordTimeType;
    }

    public RecordTimeType getRecordTimeType() {
        return mRecordTimeType;
    }

    public void setBattleId(int battleId) {
        mBattleId = battleId;
    }

    public void setBattleName(String battleName) {
        mBattleName = battleName;
    }

    public int getBattleId() {
        return mBattleId;
    }

    public String getBattleName() {
        return mBattleName;
    }
}
