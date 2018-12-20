package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import android.text.TextUtils;

import com.tiktokdemo.lky.tiktokdemo.Constant;
import com.tiktokdemo.lky.tiktokdemo.utils.FileUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.HomeCacheUtil;
import com.tiktokdemo.lky.tiktokdemo.HomeCallBack;

/**
 * Created by lky on 2017/8/28.
 */
public class RecordingStudioInformationBean implements  Serializable {

    private String userId;
    private String mBGMPath;
    private String mBGMLoadPath;
    private String mAudioTrack1Path;
    private String mAudioTrack2Path;
    private long mDuration;

    private String mAudioName;

    private String mAudioTrack1VoiceInfos;
    private String mAudioTrack2VoiceInfos;

    private transient ArrayList<VoiceFrequencyInfo> mVoiceFrequencyInfoData1;
    private transient ArrayList<VoiceFrequencyInfo> mVoiceFrequencyInfoData2;

    private String mLyricRangeInfos;

    private String mLyricOvalStr;
    private String mLyricStr;

    private int mMusicId;
    private String mMusicCover;
    private int mBattleId;
    private String mBattleName;
    private String mCombinePath;
    private long mCreateTime;

    private float mBeatAuditionVolume = 50f;
    private float mVocal1AuditionVolume = 100f;
    private float mVocal2AuditionVolume = 100f;

    private long mRecordDuration;

    private String actName;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setBGMPath(String BGMPath) {
        mBGMPath = BGMPath;
    }

    public void setBGMLoadPath(String BGMLoadPath) {
        mBGMLoadPath = BGMLoadPath;
    }

    public void setAudioTrack1Path(String audioTrack1Path) {
        mAudioTrack1Path = audioTrack1Path;
    }

    public void setAudioTrack2Path(String audioTrack2Path) {
        mAudioTrack2Path = audioTrack2Path;
    }

//    public void setAudioTrack1Data(short[] audioTrack1Data) {
//        mAudioTrack1Data = audioTrack1Data;
//    }
//
//    public void setAudioTrack2Data(short[] audioTrack2Data) {
//        mAudioTrack2Data = audioTrack2Data;
//    }

    public void setAudioTrack1VoiceInfos(String audioTrack1VoiceInfos) {
        mAudioTrack1VoiceInfos = audioTrack1VoiceInfos;
    }

    public void setAudioTrack2VoiceInfos(String audioTrack2VoiceInfos) {
        mAudioTrack2VoiceInfos = audioTrack2VoiceInfos;
    }

//    public void setAudioTrack1VoiceInfosFromArray(final ArrayList<VoiceFrequencyInfo> audioTrack1VoiceInfos){
//        try {
//            mAudioTrack1VoiceInfos = HomeCacheUtil.serialize(audioTrack1VoiceInfos);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setAudioTrack2VoiceInfosFromArray(final ArrayList<VoiceFrequencyInfo> audioTrack2VoiceInfos){
//        try {
//            mAudioTrack2VoiceInfos = HomeCacheUtil.serialize(audioTrack2VoiceInfos);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void setAudioTrackVoiceInfosFromArray(final ArrayList<VoiceFrequencyInfo> audioTrack1VoiceInfos, final ArrayList<VoiceFrequencyInfo> audioTrack2VoiceInfos, final HomeCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mVoiceFrequencyInfoData1 = audioTrack1VoiceInfos;
                    mVoiceFrequencyInfoData2 = audioTrack2VoiceInfos;

                    mAudioTrack1VoiceInfos = Constant.RECORD_AUDIO_CACHE_PATH_TEMP + File.separator + System
                            .currentTimeMillis() + "_1_AudioData.string";
                    mAudioTrack2VoiceInfos = Constant.RECORD_AUDIO_CACHE_PATH_TEMP + File.separator + System
                            .currentTimeMillis() + "_2_AudioData.string";
                    FileUtils.object2File(mVoiceFrequencyInfoData1,mAudioTrack1VoiceInfos);
                    FileUtils.object2File(mVoiceFrequencyInfoData2,mAudioTrack2VoiceInfos);
//
//                    if(mAudioTrack1VoiceInfos == null){
//                        mAudioTrack1VoiceInfos = HomeCacheUtil.serialize(audioTrack1VoiceInfos);
//                    }else{
//                        synchronized (mAudioTrack1VoiceInfos) {
//                            mAudioTrack1VoiceInfos = HomeCacheUtil.serialize(audioTrack1VoiceInfos);
//                        }
//                    }
//                    if(mAudioTrack2VoiceInfos == null){
//                        mAudioTrack2VoiceInfos = HomeCacheUtil.serialize(audioTrack2VoiceInfos);
//                    }else{
//                        synchronized (mAudioTrack2VoiceInfos) {
//                            mAudioTrack2VoiceInfos = HomeCacheUtil.serialize(audioTrack2VoiceInfos);
//                        }
//                    }
                    if(callBack != null){
                        callBack.finish(null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getAudioTrack1VoiceInfosFromArray(final HomeCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(callBack == null){
                    return ;
                }
                if(TextUtils.isEmpty(mAudioTrack1VoiceInfos)){
                    callBack.finish(new ArrayList<VoiceFrequencyInfo>());
                    return;
                }
                if(mVoiceFrequencyInfoData1 != null){
                    callBack.finish(mVoiceFrequencyInfoData1);
                    return;
                }
                if(mVoiceFrequencyInfoData1 == null && mAudioTrack1VoiceInfos.endsWith(".string")){
                    File file = new File(mAudioTrack1VoiceInfos);
                    if (file.exists()) {
                        try {
                            mVoiceFrequencyInfoData1 = (ArrayList<VoiceFrequencyInfo>) FileUtils.file2Object(mAudioTrack1VoiceInfos);
                            callBack.finish(mVoiceFrequencyInfoData1);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }else{
                    callBack.finish(new ArrayList<VoiceFrequencyInfo>());
                }
            }
        }).start();
    }

    public void getAudioTrack2VoiceInfosFromArray(final HomeCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(callBack == null){
                    return ;
                }
                if(TextUtils.isEmpty(mAudioTrack2VoiceInfos)){
                    callBack.finish(new ArrayList<VoiceFrequencyInfo>());
                    return;
                }
                if(mVoiceFrequencyInfoData2 != null){
                    callBack.finish(mVoiceFrequencyInfoData2);
                    return;
                }
                if(mVoiceFrequencyInfoData2 == null && mAudioTrack2VoiceInfos.endsWith(".string")){
                    File file = new File(mAudioTrack2VoiceInfos);
                    if(file.exists()){
                        try {
                            mVoiceFrequencyInfoData2 = (ArrayList<VoiceFrequencyInfo>) FileUtils.file2Object(mAudioTrack2VoiceInfos);
                            callBack.finish(mVoiceFrequencyInfoData2);
                            return;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                callBack.finish(new ArrayList<VoiceFrequencyInfo>());
            }
        }).start();
    }

    public boolean isAudioTrack1VoiceInfosFromArrayEmpty(){
        return mVoiceFrequencyInfoData1 == null || mVoiceFrequencyInfoData1.isEmpty();
    }

    public boolean isAudioTrack2VoiceInfosFromArrayEmpty(){
        return mVoiceFrequencyInfoData2 == null || mVoiceFrequencyInfoData2.isEmpty();
    }

    public void setLyricStr(String lyricStr) {
        mLyricStr = lyricStr;
    }

    public void setBattleId(int battleId) {
        mBattleId = battleId;
    }

    public void setBattleName(String battleName) {
        mBattleName = battleName;
    }

    public String getBGMPath() {
        return mBGMPath;
    }

    public String getBGMLoadPath() {
        return mBGMLoadPath;
    }

    public String getAudioTrack1Path() {
        return mAudioTrack1Path;
    }

    public String getAudioTrack2Path() {
        return mAudioTrack2Path;
    }

//    public short[] getAudioTrack1Data() {
//        return mAudioTrack1Data;
//    }
//
//    public short[] getAudioTrack2Data() {
//        return mAudioTrack2Data;
//    }

    public String getAudioTrack1VoiceInfos() {
        if(mAudioTrack1VoiceInfos == null){
            return "";
        }
        synchronized (mAudioTrack1VoiceInfos){
            return mAudioTrack1VoiceInfos;
        }
    }

    public String getAudioTrack2VoiceInfos() {
        if(mAudioTrack2VoiceInfos == null){
            return "";
        }
        synchronized (mAudioTrack2VoiceInfos){
            return mAudioTrack2VoiceInfos;
        }
    }

    public String getLyricStr() {
        return mLyricStr;
    }

    public void setLyricRangeInfos(String lyricRangeInfos) {
        mLyricRangeInfos = lyricRangeInfos;
    }

    public void setLyricRangeInfosFromArrayList(ArrayList<LyricSignRangeInfo> lyricRangeInfos) {
        try {
            mLyricRangeInfos = HomeCacheUtil.serialize(lyricRangeInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLyricOvalStr(String lyricOvalStr) {
        mLyricOvalStr = lyricOvalStr;
    }

    public String getLyricRangeInfos() {
        return mLyricRangeInfos;
    }

    public ArrayList<LyricSignRangeInfo> getLyricRangeInfosFromArray() {
        if(TextUtils.isEmpty(mLyricRangeInfos)){
            return new ArrayList<>();
        }
        try {
            return (ArrayList<LyricSignRangeInfo>) HomeCacheUtil.deSerialization(mLyricRangeInfos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String getLyricOvalStr() {
        return mLyricOvalStr;
    }

    public void setMusicId(int musicId) {
        mMusicId = musicId;
    }

    public void setMusicCover(String musicCover) {
        mMusicCover = musicCover;
    }

    public int getMusicId() {
        return mMusicId;
    }

    public String getMusicCover() {
        return mMusicCover;
    }

    public int getBattleId() {
        return mBattleId;
    }

    public String getBattleName() {
        return mBattleName;
    }

    public void setCombinePath(String combinePath) {
        mCombinePath = combinePath;
    }

    public String getCombinePath() {
        return mCombinePath;
    }

    public void setAudioName(String audioName) {
        mAudioName = audioName;
    }

    public String getAudioName() {
        return mAudioName;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setBeatAuditionVolume(float beatAuditionVolume) {
        mBeatAuditionVolume = beatAuditionVolume;
    }

    public void setVocal1AuditionVolume(float vocal1AuditionVolume) {
        mVocal1AuditionVolume = vocal1AuditionVolume;
    }

    public void setVocal2AuditionVolume(float vocal2AuditionVolume) {
        mVocal2AuditionVolume = vocal2AuditionVolume;
    }

    public float getBeatAuditionVolume() {
        return mBeatAuditionVolume;
    }

    public float getVocal1AuditionVolume() {
        return mVocal1AuditionVolume;
    }

    public float getVocal2AuditionVolume() {
        return mVocal2AuditionVolume;
    }

    public void setRecordDuration(long recordDuration) {
        mRecordDuration = recordDuration;
    }

    public long getRecordDuration() {
        return mRecordDuration;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActName() {
        return actName;
    }
}
