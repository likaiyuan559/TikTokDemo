package com.tiktokdemo.lky.tiktokdemo.record.bean;

/**
 * Created by lky on 2018/1/8.
 */

public class RecordingDemoBean {

    private String userId;
    private String mBGMPath;
    private String mBGMLoadPath;
    private String mAudioTrackPath;
    private long mDuration;
    private String mAudioName;
    private String mLyricStr;
    private long mRecordDuration;

    private int mMusicId;
    private String mMusicCover;
    private String mCombinePath;
    private long mCreateTime;

    private String actName;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBGMPath(String BGMPath) {
        mBGMPath = BGMPath;
    }

    public void setBGMLoadPath(String BGMLoadPath) {
        mBGMLoadPath = BGMLoadPath;
    }

    public void setAudioTrackPath(String audioTrackPath) {
        mAudioTrackPath = audioTrackPath;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setAudioName(String audioName) {
        mAudioName = audioName;
    }


    public void setLyricStr(String lyricStr) {
        mLyricStr = lyricStr;
    }

    public void setRecordDuration(long recordDuration) {
        mRecordDuration = recordDuration;
    }

    public void setMusicId(int musicId) {
        mMusicId = musicId;
    }

    public void setMusicCover(String musicCover) {
        mMusicCover = musicCover;
    }

    public void setCombinePath(String combinePath) {
        mCombinePath = combinePath;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getBGMPath() {
        return mBGMPath;
    }

    public String getBGMLoadPath() {
        return mBGMLoadPath;
    }

    public String getAudioTrackPath() {
        return mAudioTrackPath;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getAudioName() {
        return mAudioName;
    }

    public String getLyricStr() {
        return mLyricStr;
    }

    public long getRecordDuration() {
        return mRecordDuration;
    }

    public int getMusicId() {
        return mMusicId;
    }

    public String getMusicCover() {
        return mMusicCover;
    }

    public String getCombinePath() {
        return mCombinePath;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActName() {
        return actName;
    }
}
