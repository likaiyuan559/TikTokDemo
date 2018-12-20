package com.tiktokdemo.lky.tiktokdemo.record.presenter;

import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;

import com.heyhou.social.video.FilterInfo;
import com.heyhou.social.video.HeyhouVideo;
import com.heyhou.social.video.VideoInfo;
import com.heyhou.social.video.VideoListener;
import com.tiktokdemo.lky.tiktokdemo.Constant;
import com.tiktokdemo.lky.tiktokdemo.HomeCallBack;
import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.bean.MusicBean;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsParentType;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsProgressBean;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsType;
import com.tiktokdemo.lky.tiktokdemo.record.bean.TidalPatRecordDraftBean;
import com.tiktokdemo.lky.tiktokdemo.record.manager.SpecialEffectsPlayManager;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.FileUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.ToastTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by lky on 2018/12/13
 */
public class VideoPlayPresenter implements VideoPlayContract.Presenter{


    private VideoPlayContract.View mView;
    private Handler mHandler;
    private TidalPatRecordDraftBean mTidalPatRecordDraftBean;
    private MusicBean mMusicBean;
    private String mBGMPath = "";
    private boolean isCutAudio;
    private boolean isAudioCuting;
    private MediaPlayer mCutAudioMediaPlayer;
    private String mCombineVideoPath;
    private String mSpecialEffectsTimeBackVideoPath="";
    private SpecialEffectsParentType mSpecialEffectsParentType = SpecialEffectsParentType.FILTER;//当前处在什么特效模式
    private boolean isCombining = false;
    private boolean isDidTimeBack;//做过时光倒流的特效的，本次特效不再做

    public VideoPlayPresenter(VideoPlayContract.View view, TidalPatRecordDraftBean tidalPatRecordDraftBean, MusicBean musicBean) {
        mView = view;
        mTidalPatRecordDraftBean = tidalPatRecordDraftBean;
        mMusicBean = musicBean;
        mHandler = new Handler();
    }

    public boolean isAudioCuting() {
        return isAudioCuting;
    }

    public boolean isCombining() {
        return isCombining;
    }

    public SpecialEffectsParentType getSpecialEffectsParentType() {
        return mSpecialEffectsParentType;
    }

    public String getSpecialEffectsTimeBackVideoPath() {
        return mSpecialEffectsTimeBackVideoPath;
    }

    public void setSpecialEffectsParentType(SpecialEffectsParentType specialEffectsParentType) {
        mSpecialEffectsParentType = specialEffectsParentType;
    }

    public void setSpecialEffectsTimeBackVideoPath(String specialEffectsTimeBackVideoPath) {
        mSpecialEffectsTimeBackVideoPath = specialEffectsTimeBackVideoPath;
    }

    public void pausePlayer(){
        if(mCutAudioMediaPlayer != null){
            mCutAudioMediaPlayer.pause();
        }
    }

    public void releasePlayer(){
        if(mCutAudioMediaPlayer != null){
            mCutAudioMediaPlayer.release();
            mCutAudioMediaPlayer = null;
        }
    }

    public void resetPlayer(){
        if(mCutAudioMediaPlayer != null){
            mCutAudioMediaPlayer.release();
            mCutAudioMediaPlayer = null;
        }
        try {
            mCutAudioMediaPlayer = new MediaPlayer();
            mCutAudioMediaPlayer.setDataSource(mBGMPath);
            mCutAudioMediaPlayer.setLooping(true);
            float volume = 0.5f;
            mCutAudioMediaPlayer.setVolume(volume,volume);
            mCutAudioMediaPlayer.prepare();
            mCutAudioMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查背景音乐并裁剪
     */
    public void checkBGMPathUpdata(){
        if(mMusicBean == null || TextUtils.isEmpty(mMusicBean.getUrl())
                || mMusicBean.getUrl().contains("http")){
            mView.checkMusicEmpty();
            return;
        }

        if(!isCutAudio){
            isCutAudio = true;
            final MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mMusicBean.getUrl());
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mView.checkMusicLength(mediaPlayer.getDuration());
                        mediaPlayer.release();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            cutAudio(mMusicBean.getUrl(), mTidalPatRecordDraftBean.getCutMusicPosition(), 15 * 1000000L, "cut_audio", new HomeCallBack() {
                @Override
                public void finish(Object obj) {
                    mBGMPath = obj.toString();
                }

                @Override
                public void error(String errorStr) {

                }
            });
        }
    }

    /**
     * 改变背景音乐
     * @param position 起始位置
     */
    public void changeCutAudio(float position){
        cutAudio(mMusicBean.getUrl(), (long) (position * 1000000L), 15 * 1000000L, "draft_cut_audio", new HomeCallBack() {
            @Override
            public void finish(Object obj) {
                mBGMPath = obj.toString();
                if(mCutAudioMediaPlayer != null){
                    mCutAudioMediaPlayer.release();
                    mCutAudioMediaPlayer = null;
                }
                try {
                    mCutAudioMediaPlayer = new MediaPlayer();
                    mCutAudioMediaPlayer.setDataSource(mBGMPath);
                    mCutAudioMediaPlayer.prepare();
                    float volume = 0.5f;
                    mCutAudioMediaPlayer.setVolume(volume,volume);
//                    if(mCutAudioLayout.getVisibility() == View.VISIBLE){
                        mCutAudioMediaPlayer.start();
//                    }else{
//                        mCutAudioMediaPlayer.stop();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String errorStr) {

            }
        });
    }

    /**
     * 完成
     */
    public void complete(){
        if(mSpecialEffectsParentType == SpecialEffectsParentType.TIME
                && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack
                && !TextUtils.isEmpty(mSpecialEffectsTimeBackVideoPath)) {
            mCombineVideoPath = mSpecialEffectsTimeBackVideoPath;
        }
        if(!TextUtils.isEmpty(mCombineVideoPath) && !mCombineVideoPath.equals(mTidalPatRecordDraftBean.getVideoLocalUrl())){
            FileUtils.deleteFile(mTidalPatRecordDraftBean.getVideoLocalUrl());
            mTidalPatRecordDraftBean.setVideoLocalUrl(mCombineVideoPath);
        }
        mView.completeFinish();
    }

    /**
     * 合成视频
     * @param originalVolume 原声音量
     * @param backgroundVolume 配乐音量
     */
    public void combineVideo(final float originalVolume, final float backgroundVolume){
        mView.showLoadingView(true,0);
        mView.combineVideoStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HeyhouVideo heyhouVideo = new HeyhouVideo();
                ArrayList<String> strPath = mTidalPatRecordDraftBean.getVideoLocalArrayFromList();
                ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
                for(int i=0;i<strPath.size();i++){
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.setTimes(1);
                    videoInfo.setVideoPath(strPath.get(i));
                    videoInfos.add(videoInfo);
                }
                File pathFile = new File(Constant.RECORD_VIDEO_PATH_TEMP);
                if(!pathFile.exists()){
                    pathFile.mkdirs();
                }
                heyhouVideo.combine(videoInfos, mBGMPath==null?"":mBGMPath,originalVolume,backgroundVolume, Constant.RECORD_VIDEO_PATH_TEMP + File.separator + "MIX_Video_" + System
                        .currentTimeMillis() + ".mp4", new VideoListener() {
                    @Override
                    public void onProgress(String outputPath, int percentage) {
                    }

                    @Override
                    public void onComplete(final String outputVideoPath) {
                        if(!TextUtils.isEmpty(mCombineVideoPath) && !mCombineVideoPath.equals(mSpecialEffectsTimeBackVideoPath)
                                && !TextUtils.isEmpty(mTidalPatRecordDraftBean.getVideoLocalUrl()) && !mCombineVideoPath.equals(mTidalPatRecordDraftBean.getVideoLocalUrl())
                                && !mTidalPatRecordDraftBean.getVideoLocalArrayFromList().contains(mCombineVideoPath)){
                            FileUtils.deleteFile(mCombineVideoPath);
                        }
                        mCombineVideoPath = outputVideoPath;
                        switch (mSpecialEffectsParentType){
                            case FILTER://灵魂出窍
                                ArrayList<SpecialEffectsProgressBean> progressBeen = SpecialEffectsPlayManager.getInstance().getFiltrationSpecialEffectsFilters();
                                if(progressBeen == null || progressBeen.isEmpty()){
                                    combineFinishPlayVideo(mCombineVideoPath);
                                }else{
                                    mTidalPatRecordDraftBean.setHasSpecialEffects(true);
                                    filterVideo(mCombineVideoPath, progressBeen, new HomeCallBack() {
                                        @Override
                                        public void finish(Object obj) {
                                            mCombineVideoPath = (String) obj;
                                            combineFinishPlayVideo(mCombineVideoPath);
                                        }

                                        @Override
                                        public void error(String errorStr) {
                                            mView.showToast(AppUtil.getString(R.string.tidal_pat_upload_combine_error));
                                            combineFinishPlayVideo(mCombineVideoPath);
                                        }
                                    });
                                }
                                break;
                            case TIME://时光倒流
                                SpecialEffectsType specialEffectsType = SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType();
                                if(specialEffectsType == SpecialEffectsType.Default){//如果没有做过特效，则直接播放
                                    combineFinishPlayVideo(mCombineVideoPath);
                                }else if(specialEffectsType == SpecialEffectsType.TimeBack){//如果做过时光倒流的特效，则再加工一次
                                    mTidalPatRecordDraftBean.setHasSpecialEffects(true);
                                    reverseVideo(mCombineVideoPath, new HomeCallBack() {
                                        @Override
                                        public void finish(Object obj) {
                                            mSpecialEffectsTimeBackVideoPath = (String) obj;
                                            combineFinishPlayVideo(mSpecialEffectsTimeBackVideoPath);
                                        }

                                        @Override
                                        public void error(String errorStr) {
                                            mView.showToast(AppUtil.getString(R.string.tidal_pat_upload_combine_error));
                                            combineFinishPlayVideo(mCombineVideoPath);
                                        }
                                    });
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(String outputPath, final String error) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                isCombining = false;
                                mView.showLoadingView(false,0);
                                mView.showToast(AppUtil.getString(R.string.personal_show_record_video_combine_fail) + error);
                                mView.combineVideoError(TextUtils.isEmpty(mCombineVideoPath)?mTidalPatRecordDraftBean.getVideoLocalUrl():mCombineVideoPath);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 进入特效编辑模式之前的准备
     * @param originalVolume 原声音量
     * @param backgroundVolume 配乐音量
     */
    public void inSpecialEffectsModeReady(final float originalVolume, final float backgroundVolume){
        if((!TextUtils.isEmpty(mTidalPatRecordDraftBean.getVideoLocalUrl()) && mTidalPatRecordDraftBean.getVideoLocalUrl().contains("SpecialEffects_") )){
            combineVideoFromNotSpecialEffects(new VideoListener() {
                @Override
                public void onProgress(String outputPath, int percentage) {
                }

                @Override
                public void onComplete(final String outputVideoPath) {
                    if(!TextUtils.isEmpty(mCombineVideoPath) && !mCombineVideoPath.equals(mSpecialEffectsTimeBackVideoPath)){
                        FileUtils.deleteFile(mCombineVideoPath);
                    }
                    mCombineVideoPath = outputVideoPath;
                    mTidalPatRecordDraftBean.setVideoLocalUrl(mCombineVideoPath);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            isCombining = false;
                            mView.showLoadingView(false,0);
                            changeSpecialEffectsMode(mSpecialEffectsParentType);
                        }
                    });
                }

                @Override
                public void onError(String outputPath, final String error) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            isCombining = false;
                            mView.showLoadingView(false,0);
                            mView.showToast(AppUtil.getString(R.string.personal_show_record_video_combine_fail) + error);
                            mView.combineVideoError(TextUtils.isEmpty(mCombineVideoPath)?mTidalPatRecordDraftBean.getVideoLocalUrl():mCombineVideoPath);
                        }
                    });
                }
            },originalVolume,backgroundVolume);
        }
        else if(!TextUtils.isEmpty(mTidalPatRecordDraftBean.getVideoLocalUrl()) && (!TextUtils.isEmpty(mCombineVideoPath)  && mCombineVideoPath.contains("SpecialEffects_"))){
            mCombineVideoPath = mTidalPatRecordDraftBean.getVideoLocalUrl();
            changeSpecialEffectsMode(mSpecialEffectsParentType);
        }
        else{
            changeSpecialEffectsMode(mSpecialEffectsParentType);
        }
    }

    /**
     * 改变特效模式
     * @param specialEffectsParentType 特效模式
     */
    public void changeSpecialEffectsMode(SpecialEffectsParentType specialEffectsParentType){
        mSpecialEffectsParentType = specialEffectsParentType;
        mView.resetSpecialEffectsSeekBar((mSpecialEffectsParentType == SpecialEffectsParentType.TIME
                && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack));
        switch (mSpecialEffectsParentType){
            case FILTER:
                mView.changeSpecialEffectsModeFilterFinish(TextUtils.isEmpty(mCombineVideoPath)?mTidalPatRecordDraftBean.getVideoLocalUrl():mCombineVideoPath);
                break;
            case TIME:
                if(SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack){
                    mView.inTimeBackState(!TextUtils.isEmpty(mSpecialEffectsTimeBackVideoPath)?
                            mSpecialEffectsTimeBackVideoPath:(!TextUtils.isEmpty(mCombineVideoPath)?
                            mCombineVideoPath:mTidalPatRecordDraftBean.getVideoLocalUrl()));
                }else{
                    mView.inTimeNotState(TextUtils.isEmpty(mCombineVideoPath)?mTidalPatRecordDraftBean.getVideoLocalUrl():mCombineVideoPath);
                }
                break;
        }
        mView.changeSpecialEffectsModeFinish(TextUtils.isEmpty(mCombineVideoPath)?mTidalPatRecordDraftBean.getVideoLocalUrl():mCombineVideoPath);
    }


    public void changeSpecialEffects(SpecialEffectsType specialEffectsType,final float originalVolume, final float backgroundVolume){
        SpecialEffectsPlayManager.getInstance().setCurrentSpecialEffectsFilterType(specialEffectsType);
        if(specialEffectsType == SpecialEffectsType.TimeBack){
            if(!isDidTimeBack && TextUtils.isEmpty(mSpecialEffectsTimeBackVideoPath)){
                mView.showLoadingView(true,0);
                mView.combineVideoStart();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HeyhouVideo heyhouVideo = new HeyhouVideo();
                        ArrayList<String> strPath = mTidalPatRecordDraftBean.getVideoLocalArrayFromList();
                        ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
                        for(int i=0;i<strPath.size();i++){
                            VideoInfo videoInfo = new VideoInfo();
                            videoInfo.setTimes(1);
                            videoInfo.setVideoPath(strPath.get(i));
                            videoInfos.add(videoInfo);
                        }
                        File pathFile = new File(Constant.RECORD_VIDEO_PATH_TEMP);
                        if(!pathFile.exists()){
                            pathFile.mkdirs();
                        }
                        heyhouVideo.combine(videoInfos, mBGMPath == null ? "" : mBGMPath, originalVolume, backgroundVolume, Constant.RECORD_VIDEO_PATH_TEMP + File.separator + "MIX_Video_" + System
                                .currentTimeMillis() + ".mp4", new VideoListener() {
                            @Override
                            public void onProgress(String outputPath, int percentage) {

                            }

                            @Override
                            public void onComplete(String outputPath) {
                                mCombineVideoPath = outputPath;
                                reverseVideo(TextUtils.isEmpty(mCombineVideoPath) ? mTidalPatRecordDraftBean.getVideoLocalUrl() : mCombineVideoPath, new HomeCallBack() {
                                    @Override
                                    public void finish(final Object obj) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                isDidTimeBack = true;
                                                mView.showLoadingView(false,0);
                                                mView.showLoadingView(false,0);
                                                mTidalPatRecordDraftBean.setHasSpecialEffects(true);
                                                mSpecialEffectsTimeBackVideoPath = (String) obj;
                                                mView.combineDidTimeBackFinish();
                                                combineFinishPlayVideoNotLooping(mSpecialEffectsTimeBackVideoPath);
                                            }
                                        });
                                    }

                                    @Override
                                    public void error(String errorStr) {
                                        mView.showToast(AppUtil.getString(R.string.tidal_pat_upload_combine_error));
                                        mView.showLoadingView(false,0);
                                    }
                                });
                            }

                            @Override
                            public void onError(String outputPath, String error) {
                                ToastTool.showShort(AppUtil.getApplicationContext(), R.string.tidal_pat_upload_combine_error);
                                mView.showLoadingView(false,0);
                            }
                        });

                    }
                }).start();
            }else{
                mView.inTimeBackState(!TextUtils.isEmpty(mSpecialEffectsTimeBackVideoPath)?
                        mSpecialEffectsTimeBackVideoPath:(!TextUtils.isEmpty(mCombineVideoPath)?
                        mCombineVideoPath:mTidalPatRecordDraftBean.getVideoLocalUrl()));
            }
        }else{
            mView.inTimeNotState(TextUtils.isEmpty(mCombineVideoPath)?mTidalPatRecordDraftBean.getVideoLocalUrl():mCombineVideoPath);
        }
    }

    /**
     * 合成视频成功播放视频但是不循环
     * @param videoPath 视频路径
     */
    public void combineFinishPlayVideoNotLooping(String videoPath){
        mView.showLoadingView(false,0);
        isCombining = false;
        mView.combineVideoFinish(false,videoPath);
    }

    /**
     * 合成视频，没有特效的处理
     * @param videoListener 回调
     * @param originalVolume 原声音量
     * @param backgroundVolume 配乐音量
     */
    public void combineVideoFromNotSpecialEffects(final VideoListener videoListener,final float originalVolume, final float backgroundVolume){
        mView.showLoadingView(true,0);
        mView.combineVideoStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HeyhouVideo heyhouVideo = new HeyhouVideo();
                ArrayList<String> strPath = mTidalPatRecordDraftBean.getVideoLocalArrayFromList();
                ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
                for(int i=0;i<strPath.size();i++){
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.setTimes(1);
                    videoInfo.setVideoPath(strPath.get(i));
                    videoInfos.add(videoInfo);
                }
                File pathFile = new File(Constant.RECORD_VIDEO_PATH_TEMP);
                if(!pathFile.exists()){
                    pathFile.mkdirs();
                }
                heyhouVideo.combine(videoInfos, mBGMPath==null?"":mBGMPath,originalVolume,backgroundVolume, Constant.RECORD_VIDEO_PATH_TEMP + File.separator + "MIX_Video_" + System
                        .currentTimeMillis() + ".mp4",videoListener);
            }
        }).start();
    }

    /**
     * 视频合成成功
     * @param videoPath
     */
    public void combineFinishPlayVideo(final String videoPath){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.showLoadingView(false,0);
                isCombining = false;
                mView.combineVideoFinish(true,videoPath);
            }
        });
    }

    /**
     * 合成灵魂出窍视频
     * @param videoPath 视频路径
     * @param beanArrayList 数据
     * @param callBack 回调
     */
    public void filterVideo(String videoPath, ArrayList<SpecialEffectsProgressBean> beanArrayList, final HomeCallBack callBack){
        ArrayList<FilterInfo> filterInfos = new ArrayList<>();
        if(beanArrayList != null && !beanArrayList.isEmpty()){
            for(SpecialEffectsProgressBean bean:beanArrayList){
                FilterInfo filterInfo = new FilterInfo();
                if(bean.getType() != null){
                    filterInfo.setFilter(bean.getType().getFilter());
                }else{
                    filterInfo.setFilter(SpecialEffectsType.Default.getFilter());
                }
                filterInfo.setStartTime(bean.getTimeStart()*1000);
                filterInfo.setEndTime(bean.getTimeEnd()*1000);
                filterInfos.add(filterInfo);
            }
        }
        new HeyhouVideo().filter(videoPath, filterInfos, Constant.RECORD_VIDEO_PATH_TEMP + File.separator + "SpecialEffects_" + System
                .currentTimeMillis() + ".mp4", new VideoListener() {
            @Override
            public void onProgress(String outputPath, int percentage) {

            }

            @Override
            public void onComplete(String outputPath) {
                callBack.finish(outputPath);
            }

            @Override
            public void onError(String outputPath, String error) {
                callBack.error(error);
            }
        });
    }

    /**
     * 合成时光倒流视频
     * @param videoPath 视频路径
     * @param callBack 回调
     */
    public void reverseVideo(String videoPath, final HomeCallBack callBack){
        new HeyhouVideo().reverse(videoPath, Constant.RECORD_VIDEO_PATH_TEMP + File.separator + "SpecialEffects_" + System
                .currentTimeMillis() + ".mp4", Constant.RECORD_VIDEO_PATH_TEMP, new VideoListener() {
            @Override
            public void onProgress(String outputPath, int percentage) {

            }

            @Override
            public void onComplete(String outputPath) {
                callBack.finish(outputPath);
            }

            @Override
            public void onError(String outputPath, String error) {
                callBack.error(error);
            }
        });
    }

    /**
     * 裁剪音频
     * @param filePath  文件路径
     * @param startTime 开始时间（！！！纳秒！！！）
     * @param endTime   结束时间（！！！纳秒！！！）
     * @param homeCallBack 回调（！！！！！！）
     */
    public void cutAudio(final String filePath, final long startTime, final long endTime, final String outputName, final HomeCallBack homeCallBack){
        mView.showLoadingView(true,R.string.tidal_pat_record_music_cutting);
        isAudioCuting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Constant.CUT_AUDIO_CACHE_PATH);
                if(!file.exists()){
                    file.mkdirs();
                }
                HeyhouVideo heyhouVideo = new HeyhouVideo();
                heyhouVideo.cut(filePath, startTime, endTime, Constant.CUT_AUDIO_CACHE_PATH + File.separator + outputName+ ".wav", new VideoListener() {

                    @Override
                    public void onProgress(String outputPath, int percentage) {

                    }

                    @Override
                    public void onComplete(final String outputPath) {
                        isAudioCuting = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                homeCallBack.finish(outputPath);
                                mView.showLoadingView(false,R.string.tidal_pat_record_combining);
                            }
                        });
                    }

                    @Override
                    public void onError(String outputPath, final String error) {
                        isAudioCuting = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                homeCallBack.error(error);
                                mView.showLoadingView(false,R.string.tidal_pat_record_combining);
                            }
                        });
                    }
                });
            }
        }).start();
    }

}
