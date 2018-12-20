package com.tiktokdemo.lky.tiktokdemo.record.presenter;

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;

import com.heyhou.social.video.HeyhouRecorder;
import com.heyhou.social.video.HeyhouVideo;
import com.heyhou.social.video.VideoInfo;
import com.heyhou.social.video.VideoListener;
import com.heyhou.social.video.VideoTimeType;
import com.tiktokdemo.lky.tiktokdemo.Constant;
import com.tiktokdemo.lky.tiktokdemo.HomeCallBack;
import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.RecordVideoActivity;
import com.tiktokdemo.lky.tiktokdemo.record.bean.MusicBean;
import com.tiktokdemo.lky.tiktokdemo.record.camera.MagicEngine;
import com.tiktokdemo.lky.tiktokdemo.record.camera.camera.CameraEngine;
import com.tiktokdemo.lky.tiktokdemo.record.camera.encoder.video.ImageEncoderCore;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.MagicCameraView;
import com.tiktokdemo.lky.tiktokdemo.record.helper.MagicFilterFactory;
import com.tiktokdemo.lky.tiktokdemo.record.helper.RecordTimeType;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatFilterType;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatPropFactory;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatPropType;
import com.tiktokdemo.lky.tiktokdemo.record.thread.AudioRecorder;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.CacheUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.FileUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.ToastTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Created by lky on 2018/12/13
 */
public class RecordVideoPresenter implements RecordVideoContract.Presenter {

    private final float AUDIO_PLAY_DURATION = 15f;
    private RecordTimeType mRecordTimeType = RecordTimeType.RECORD_TIME_15;
    private final String SPEED_AUDIO_FILE_NAME = "SpeedAudioFile";
    private final int VIDEO_RECORD_MIN_TIME = 3000;
    public final int VIDEO_RECORD_MAX_TIME = 15000;
    public final int VIDEO_RECORD_MAX_TIME_120 = 120000;

    private int mMaxRecordTime = VIDEO_RECORD_MAX_TIME;

    private RecordVideoContract.View mView;
    private MusicBean mMusicBean;
    private boolean isCutAudio;
    private boolean isAudioCuting;
    private Handler mHandler;
    private String mBGMPath = "";
    private int mSpeedAudioCreateCount = 0;
    private boolean isSpeedAudio;
    private boolean isCombining = false;
    private MediaPlayer mMediaPlayer;//录制时的Player
    private MediaPlayer mCutAudioMediaPlayer;//剪音乐时候的Player
    private HashMap<VideoTimeType,String> mBGMSpeedPaths;
    private ArrayList<VideoInfo> mRecordVideoInfos;
    private MagicEngine mMagicEngine;
    private boolean isRecording;
    private boolean isVideoRecordInit;

    private float mTempRecordTimeCount;
    private float mRecordTimeCount;//当前录制时常
    private boolean isCanSaveVideo;//是否达到最小录制时长
    private AudioRecorder mAudioRecorder;//音频录制

    public RecordVideoPresenter(RecordVideoContract.View view, MusicBean musicBean) {
        mView = view;
        mMusicBean = musicBean;
        mHandler = new Handler();
        mBGMSpeedPaths = new HashMap<>();
        mRecordVideoInfos = new ArrayList<>();
    }


    public void setMusicBean(MusicBean musicBean) {
        mMusicBean = musicBean;
    }

    public void setRecordTimeCount(float recordTimeCount) {
        mRecordTimeCount = recordTimeCount;
    }

    public void setRecordTimeType(RecordTimeType recordTimeType) {
        mRecordTimeType = recordTimeType;
    }

    public void setMaxRecordTime(int maxRecordTime) {
        mMaxRecordTime = maxRecordTime;
    }

    public boolean isAudioCuting() {
        return isAudioCuting;
    }


    public boolean isSpeedAudio() {
        return isSpeedAudio;
    }


    public boolean isCombining() {
        return isCombining;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isCanSaveVideo() {
        return isCanSaveVideo;
    }

    public boolean isCurrentRecordMax(){//当前的录制时长是否已经达到最大
        return mRecordTimeCount + mTempRecordTimeCount >= mMaxRecordTime;
    }

    public boolean isRecordVideoInfoEmpty(){
        return mRecordVideoInfos == null || mRecordVideoInfos.isEmpty();
    }


    public RecordTimeType getRecordTimeType() {
        return mRecordTimeType;
    }

    public int getMaxRecordTime() {
        return mMaxRecordTime;
    }

    public ArrayList<VideoInfo> getRecordVideoInfos() {
        return mRecordVideoInfos;
    }

    public void startCutAudioPlay(){
        if(mCutAudioMediaPlayer != null){
            mCutAudioMediaPlayer.start();
        }
    }

    public void startMusicPlay(){
        if(mMediaPlayer != null){
            mMediaPlayer.start();
        }
    }

    public void pauseMusic(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
        if(mCutAudioMediaPlayer != null){
            mCutAudioMediaPlayer.pause();
        }
    }

    public void pauseCutAudioMusic(){
        if(mCutAudioMediaPlayer != null && mCutAudioMediaPlayer.isPlaying()){
            mCutAudioMediaPlayer.pause();
        }
    }

    public void resetCutAudioMusic(){
        if(mCutAudioMediaPlayer != null){
            mCutAudioMediaPlayer.release();
            mCutAudioMediaPlayer = null;
        }
        try {
            mCutAudioMediaPlayer = new MediaPlayer();
            mCutAudioMediaPlayer.setDataSource(mBGMPath);
            mCutAudioMediaPlayer.setLooping(true);
            mCutAudioMediaPlayer.setVolume(0.5f,0.5f);
            mCutAudioMediaPlayer.prepare();
            mCutAudioMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void releaseAll(){
        CameraEngine.setCameraID(Camera.getNumberOfCameras()>1?1:0);//界面销毁，重置摄像头id为前置摄像头
        if(!mMagicEngine.isBeautyOpen()){
            mMagicEngine.setBeautyOpenStatus(!mMagicEngine.isBeautyOpen());
        }
        MagicFilterFactory.getInstance().clearFilter();
        TidalPatPropFactory.getInstance().changeType(TidalPatPropType.DEFAULT);
        releaseMusic();
    }

    public void releaseMusic(){
        if(mMediaPlayer != null){
            try{
                mMediaPlayer.release();
                mMediaPlayer = null;
            }catch(Exception e){

            }
        }
    }
    public void releaseCutAudioMusic(){
        if(mCutAudioMediaPlayer != null){
            try{
                mCutAudioMediaPlayer.release();
                mCutAudioMediaPlayer = null;
            }catch(Exception e){

            }
        }
    }

    public void initMagicEngine(MagicCameraView magicCameraView){
        mMagicEngine = new MagicEngine.Builder().build(magicCameraView);
    }

    /**
     * 检查BGM状态并生成适用的背景音乐
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
                //初始化音乐并回调音乐时长
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
            cutAudio(mMusicBean.getUrl(), 0, 15 * 1000000L, "cut_audio", new HomeCallBack() {
                @Override
                public void finish(Object obj) {//15s的音频文件生成成功
                    mBGMPath = obj.toString();
                    createSpeedAudioFiles();//开始创建各个不同的速率下的音频文件
                }

                @Override
                public void error(String errorStr) {

                }
            });
        }
    }

    /**
     * 创建各个录制速率下的音频文件，比如0.5倍、2倍等
     */
    public void createSpeedAudioFiles(){
        if(!TextUtils.isEmpty(mBGMPath)){
            isSpeedAudio = true;
            mView.showLoadingView(true,R.string.personal_loading);
            createSpeedAudioPaths();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (RecordVideoActivity.class){
                        synchronized (mBGMSpeedPaths){
                            Iterator<Map.Entry<VideoTimeType,String>>
                                    iterator = mBGMSpeedPaths.entrySet().iterator();
                            while (iterator.hasNext()){
                                Map.Entry<VideoTimeType,String> map = iterator.next();
                                HeyhouVideo heyhouVideo = new HeyhouVideo();
                                heyhouVideo.speed(mBGMPath, map.getKey().getValue(), map.getValue(), new VideoListener() {

                                    @Override
                                    public void onProgress(String outputPath, int percentage) {

                                    }

                                    @Override
                                    public void onComplete(String outputPath) {
                                        mSpeedAudioCreateCount++;
                                        createSpeedAudioFinish();
                                    }

                                    @Override
                                    public void onError(String outputPath, String error) {
                                        mSpeedAudioCreateCount++;
                                        createSpeedAudioFinish();
                                    }

                                });
                            }
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * 创建不同速率音频文件的路径
     */
    public void createSpeedAudioPaths(){
        synchronized (mBGMSpeedPaths){
            mBGMSpeedPaths.clear();
            File file = new File(Constant.SPEED_AUDIO_CACHE_PATH);
            if(!file.exists()){
                file.mkdirs();
            }
            mBGMSpeedPaths.put(VideoTimeType.SPEED_M4,Constant.SPEED_AUDIO_CACHE_PATH + File.separator + SPEED_AUDIO_FILE_NAME + "_1.wav");
            mBGMSpeedPaths.put(VideoTimeType.SPEED_M2,Constant.SPEED_AUDIO_CACHE_PATH + File.separator + SPEED_AUDIO_FILE_NAME + "_2.wav");
            mBGMSpeedPaths.put(VideoTimeType.SPEED_N1,Constant.SPEED_AUDIO_CACHE_PATH + File.separator + SPEED_AUDIO_FILE_NAME + "_3.wav");
            mBGMSpeedPaths.put(VideoTimeType.SPEED_P2,Constant.SPEED_AUDIO_CACHE_PATH + File.separator + SPEED_AUDIO_FILE_NAME + "_4.wav");
            mBGMSpeedPaths.put(VideoTimeType.SPEED_P4,Constant.SPEED_AUDIO_CACHE_PATH + File.separator + SPEED_AUDIO_FILE_NAME + "_5.wav");
        }
    }

    /**
     * 不同速率的音频文件创建完成
     */
    public void createSpeedAudioFinish(){
        if(mSpeedAudioCreateCount >= 5){
            isSpeedAudio = false;
            mSpeedAudioCreateCount = 0;
            mHandler.post(new Runnable() {
                @Override public void run() {
                    mView.showLoadingView(false,0);
                    mView.createSpeedAudioSuccess();
                }
            });
        }
    }

    /**
     * 重置裁剪的音乐
     * @param position 裁剪的起始长度
     */
    public void resetCutMusic(float position){
        cutAudio(mMusicBean.getUrl(), (long) (position * 1000000L), 15 * 1000000L, "cut_audio", new HomeCallBack() {
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
                    mCutAudioMediaPlayer.setLooping(true);
                    mCutAudioMediaPlayer.setVolume(0.5f,0.5f);
                    mCutAudioMediaPlayer.prepare();
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

    private boolean isMediaPlayerResetting;

    /**
     * 根据不同速率来初始化player
     * @param videoTimeType 速率
     * @param progress 位置
     * @param max 最大
     */
    public void resetSpeedAudioMediaPlayer(VideoTimeType videoTimeType,int progress,int max){
        if(TextUtils.isEmpty(mBGMPath) || isMediaPlayerResetting){
            return ;
        }
        isMediaPlayerResetting = true;
        int playPosition = 0;
        if(mMediaPlayer != null){
            playPosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.release();
        }
        float scale = progress/(float)max;
        switch (videoTimeType){
            case SPEED_M4:
            case SPEED_M2:
                playPosition = (int) Math
                        .abs((scale * AUDIO_PLAY_DURATION / videoTimeType.getValue()) * 1000);
                break;
            case SPEED_N1:
                playPosition = (int) Math.abs(scale * AUDIO_PLAY_DURATION * 1000);
                break;
            case SPEED_P2:
            case SPEED_P4:
                playPosition = (int) Math
                        .abs((scale * AUDIO_PLAY_DURATION * videoTimeType.getValue()) * 1000);
                break;
        }

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mBGMSpeedPaths.get(videoTimeType));
            mMediaPlayer.setVolume(0.5f,0.5f);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(playPosition+200);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isMediaPlayerResetting = false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * seekToPlayer
     */
    public void seekToAudioMediaPlayer(VideoTimeType videoTimeType,int progress,int max){
        if(TextUtils.isEmpty(mBGMPath) || isMediaPlayerResetting){
            return ;
        }
        isMediaPlayerResetting = true;
        int playPosition = 0;
        if(mMediaPlayer != null){
            playPosition = mMediaPlayer.getCurrentPosition();
        }
        float scale = progress/(float)max;
        switch (videoTimeType){
            case SPEED_M4:
            case SPEED_M2:
                playPosition = (int) Math
                        .abs((scale * AUDIO_PLAY_DURATION / videoTimeType.getValue()) * 1000);
                break;
            case SPEED_N1:
                playPosition = (int) Math.abs(scale * AUDIO_PLAY_DURATION * 1000);
                break;
            case SPEED_P2:
            case SPEED_P4:
                playPosition = (int) Math
                        .abs((scale * AUDIO_PLAY_DURATION * videoTimeType.getValue()) * 1000);
                break;
        }
        if(mMediaPlayer != null){
            try{
                final int finalPlayPosition = playPosition;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMediaPlayer.seekTo(finalPlayPosition+200);
                        isMediaPlayerResetting = false;
                    }
                },200);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始音频录制及初始化
     */
    public void startAudioRecord(){
        if(mAudioRecorder == null){
            mAudioRecorder = new AudioRecorder();
            mAudioRecorder.setOnAudioRecorderListener(new AudioRecorder.OnAudioRecorderListener() {
                @Override
                public void onNotPermission() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.showToast(AppUtil.getString(R.string.tidal_pat_record_check_audio_permission));
                        }
                    });
                }

                @Override
                public void onRecordError(String msg) {
                    mView.showToast(msg);
                }

                @Override
                public void onCanRecord(boolean isCan) {
                    mView.canRecord(isCan);
                }
            });
            mAudioRecorder.startRecord();
        }
    }

    /**
     * 停止录制音频
     */
    public void stopAudioRecord(){
        if(mAudioRecorder != null){
            mAudioRecorder.stopRecord();
            mAudioRecorder = null;
        }
    }

    /**
     * 开启相机录制，捕获画面
     */
    public void magicEngineStartRecord(){
        if(!isFrameReal && !CameraEngine.isFrameRateSure){
            mMagicEngine.startRecord();
        }
    }

    /**
     * 重置滤镜
     * @param type 滤镜类型
     */
    public void resetMagicEngineFilter(TidalPatFilterType type){
        MagicFilterFactory.getInstance().setFilterType(type);
        mMagicEngine.setFilter(MagicFilterFactory.getInstance().getGPUImageFilterGroup());
    }

    /**
     * 图像编码
     * @param imageInfo 图像数据
     */
    public void imageEncoder(ImageEncoderCore.ImageInfo imageInfo){
        if(!CameraEngine.isFrameRateSure){//帧率计算未完成
            calculateFrame();
            readSureCalculateFrame();
        }
        if(!isFrameReal && !CameraEngine.isFrameRateSure){
            return ;
        }

        if(!isVideoRecordInit && isRecording){
            isVideoRecordInit = true;

            mAudioRecorder.setAudioRecordWrite(true);
            if(mRecordTimeType == RecordTimeType.RECORD_TIME_15){
                startMusicPlay();
            }
        }
        if(isVideoRecordInit){
            float count = HeyhouRecorder
                    .getInstance().recordVideoNHW(imageInfo.data,imageInfo.width,imageInfo.height,imageInfo.rowPadding/4,HeyhouRecorder.FORMAT_ABGR,0)/1000f;
            if(count > 0 && isRecording){
                mTempRecordTimeCount = count;
                recordProgress();
            }
        }
    }

    /**
     * 开始录制
     * @param videoTimeType 速率
     */
    public void startRecord(VideoTimeType videoTimeType){
        isRecording = true;
        if(MagicFilterFactory.getInstance().isNotInit()){
            mMagicEngine.setFilter(MagicFilterFactory.getInstance().getGPUImageFilterGroup());
        }
        if(!mAudioRecorder.isAudioPermission()){
            mView.showToast(AppUtil.getString(R.string.tidal_pat_record_check_audio_permission));
            return ;
        }
        if (!CameraEngine.isOpenCameraSucceed()) {
            ToastTool.showShort(AppUtil.getApplicationContext(), R.string.personal_show_camera_open_fail);
            return ;
        }
        String filePath = Constant.RECORD_VIDEO_TEMP_PATH;
        String fileName = "record_" + System.currentTimeMillis() + ".mp4";
        File pathFile = new File(filePath);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoPath(filePath + File.separator + fileName);
        videoInfo.setTimes(videoTimeType.getValue());
        mRecordVideoInfos.add(videoInfo);
        HeyhouRecorder.getInstance().startRecord(videoInfo.getVideoPath(),CameraEngine.mRealFrameRate,2500*1000,CameraEngine.RECORD_WIDTH,CameraEngine.RECORD_HEIGHT,videoInfo.getTimes());

        mMagicEngine.startRecord();
        if(!CameraEngine.isFrameRateSure){
            startSureCalculateFrame();
        }

        mView.startRecordSuccess(mRecordTimeCount);
    }

    /**
     * 停止录制
     */
    public void stopRecord(){
        isRecording = false;
        mRecordTimeCount += HeyhouRecorder.getInstance().stopRecord()/1000f;
        mTempRecordTimeCount = 0;
        mMagicEngine.stopRecord();
        isVideoRecordInit = false;
        mAudioRecorder.setAudioRecordWrite(false);
        if(!CameraEngine.isFrameRateSure){
            stopSureCalculateFrame();
        }
    }

    /**
     * 录制进度
     */
    public void recordProgress(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mRecordTimeCount + mTempRecordTimeCount >= VIDEO_RECORD_MIN_TIME && !isCanSaveVideo){
                    isCanSaveVideo = true;
                }
                mView.recordProgress(isCanSaveVideo,mRecordTimeCount + mTempRecordTimeCount);
                if(isCurrentRecordMax()){
                    mView.recordProgressMax();
                    combineVideo();
                }

                if(mRecordTimeType == RecordTimeType.RECORD_TIME_120){
                    mView.recordProgressForm120(mRecordTimeCount + mTempRecordTimeCount);
                }
            }
        });
    }

    /**
     * 重置录制，删除文件，状态恢复等
     */
    public void resetRecord(){
        mRecordTimeCount = 0;
        mTempRecordTimeCount = 0;
        isCanSaveVideo = false;
        for(int i=0;i<mRecordVideoInfos.size();i++){
            FileUtils.deleteFile(mRecordVideoInfos.get(i).getVideoPath());
        }
        mRecordVideoInfos.clear();
        mView.resetRecordFinish();
    }

    /**
     * 删除录制内容
     * @param progress 进度
     */
    public void deleteRecordVideo(int progress){
        boolean isDeleteSucceed = FileUtils.deleteFile(mRecordVideoInfos.get(mRecordVideoInfos.size()-1).getVideoPath());
        if(isDeleteSucceed){
            mRecordVideoInfos.remove(mRecordVideoInfos.size()-1);
            mRecordTimeCount = progress;
            isCanSaveVideo = mRecordTimeCount + mTempRecordTimeCount >= VIDEO_RECORD_MIN_TIME;
            mView.deleteRecordVideoFinish(isCanSaveVideo);
        }else{
            mView.showToast(AppUtil.getString(R.string.cache_delete_fail_hint));
        }
    }

    /**
     * 闪光灯
     */
    public void changeFlashMode(){
        mMagicEngine.changeFlashMode();
        mView.changeFlashModeFinish(CameraEngine.getCameraInfo().flashMode);
    }

    /**
     * 切换镜头
     */
    public void switchCamera(){
        mMagicEngine.switchCamera();
        mView.switchCameraFinish();
    }

    /**
     * 切换美颜开关
     */
    public void changeBeautyOpen(){
        mMagicEngine.setBeautyOpenStatus(!mMagicEngine.isBeautyOpen());
        mView.showToast(AppUtil.getString(mMagicEngine.isBeautyOpen()?R.string.tidal_pat_beauty_open:R.string.tidal_pat_beauty_close));
        mView.changeBeautyOpenFinish(mMagicEngine.isBeautyOpen());
    }

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓ 计算回调的帧率 ↓↓↓↓↓↓↓↓↓↓↓
     * 帧率检查，为了适配低端手机，检查在流畅的情况下的最高帧率是多少
     */
    private long mFrameStartTime;
    private int mFrameCount;
    private boolean isFrameStart;
    private boolean isFrameReal;

    /**
     * 计算帧率
     */
    public void calculateFrame(){
        if(!isFrameReal){
            if(!isFrameStart){
                isFrameStart = true;
                mFrameStartTime = System.currentTimeMillis();
            }
            mFrameCount++;
            if(mFrameCount >= 20){
                long countFrameTime = System.currentTimeMillis() - mFrameStartTime;
                int frameTime = (int) (countFrameTime/mFrameCount);
                int rate = 1000/frameTime;
                CameraEngine.mRealFrameRate = CameraEngine.getSureFrameRate(rate);
                isFrameReal = true;
                mMagicEngine.stopRecord();
                return ;
            }
        }
    }
    private long mSureFrameStartTime;
    private int mSureFrameCount;
    private boolean isSureFrameStart;
    private boolean isSureFrameReal;
    public void startSureCalculateFrame(){
        if(!isSureFrameReal){
            if(!isSureFrameStart){
                isSureFrameStart = true;
                mSureFrameStartTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * 帧率计数
     */
    public void readSureCalculateFrame(){
        if(!isSureFrameReal && isSureFrameStart){
            mSureFrameCount++;
        }
    }

    public void stopSureCalculateFrame(){
        if(!isSureFrameReal && isSureFrameStart){
            if(mSureFrameCount<20){
                isSureFrameStart = false;
                mSureFrameCount = 0;
                mSureFrameStartTime = 0;
                return ;
            }

            long countFrameTime = System.currentTimeMillis() - mSureFrameStartTime;
            int frameTime = (int) (countFrameTime/mSureFrameCount);
            int rate = 1000/frameTime;
            CameraEngine.mRealFrameRate = CameraEngine.getSureFrameRate(rate);
            isSureFrameReal = true;
            CameraEngine.isFrameRateSure = true;
//            CacheUtil.putInt(AppUtil.getApplicationContext(),CameraEngine.FRAME_RATE_KEY,CameraEngine.mRealFrameRate);
            return ;
        }
    }

    public void checkLocalFrameInfo() {//帧率读取，为了适配部分低端手机，读取缓存->在流畅的情况下最高帧率是多少
        isFrameReal = CacheUtil.getInt(AppUtil.getApplicationContext(),CameraEngine.FRAME_RATE_KEY,0) != 0;
        if(isFrameReal){
            CameraEngine.mRealFrameRate = CacheUtil.getInt(AppUtil.getApplicationContext(),CameraEngine.FRAME_RATE_KEY,15);
        }
    }
    /**
     * ↑↑↑↑↑↑↑↑↑↑↑↑ 计算回调的帧率 ↑↑↑↑↑↑↑↑↑↑↑↑
     */


    /**
     * 裁剪音频
     * @param filePath  文件路径
     * @param startTime 开始时间（！！！纳秒！！！）
     * @param endTime   结束时间（！！！纳秒！！！）
     * @param homeCallBack 回调（！！！！！！）
     */
    public void cutAudio(final String filePath, final long startTime, final long endTime, final String outputName, final HomeCallBack homeCallBack){
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
                            @Override public void run() {
                                homeCallBack.finish(outputPath);
                            }
                        });
                    }

                    @Override
                    public void onError(String outputPath, final String error) {
                        mHandler.post(new Runnable() {
                            @Override public void run() {
                                homeCallBack.error(error);
                            }
                        });
                        isAudioCuting = false;
                    }
                });
            }
        }).start();
    }

    /**
     * 合成视频
     */
    public void combineVideo(){
        if(isCombining){
            return ;
        }
        isCombining = true;
        mView.showLoadingView(true,R.string.tidal_pat_record_combining);
        new Thread(new Runnable() {
            @Override
            public void run() {
                File pathFile = new File(Constant.RECORD_VIDEO_PATH_TEMP);
                if(!pathFile.exists()){
                    pathFile.mkdirs();
                }
                HeyhouVideo heyhouVideo = new HeyhouVideo();
                heyhouVideo.combine(mRecordVideoInfos, (mBGMPath==null||mRecordTimeType != RecordTimeType.RECORD_TIME_15)?"":mBGMPath,
                        mRecordTimeType != RecordTimeType.RECORD_TIME_15?1.0d:(TextUtils.isEmpty(mBGMPath)?0.5d:0d),0.5d,
                        Constant.RECORD_VIDEO_PATH_TEMP + File.separator + "MIX_Video_" + System.currentTimeMillis() + ".mp4", new VideoListener() {

                            @Override
                            public void onProgress(String outputPath, int percentage) {
                            }

                            @Override
                            public void onComplete(final String outputVideoPath) {
                                mHandler.post(new Runnable() {
                                    @Override public void run() {
                                        mView.showLoadingView(false,0);
                                        mView.combineVideoSuccess(outputVideoPath);
                                        isCombining = false;
                                    }
                                });
                            }

                            @Override
                            public void onError(String outputPath, final String error) {
                                mHandler.post(new Runnable() {
                                    @Override public void run() {
                                        mView.showLoadingView(false,0);
                                        isCombining = false;
                                        mView.combineVideoFail(error);
                                    }
                                });
                            }

                        });
            }
        }).start();
    }

}
