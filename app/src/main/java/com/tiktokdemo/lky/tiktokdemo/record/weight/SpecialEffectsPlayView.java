package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.heyhou.social.video.HeyhouPlayerRender;
import com.heyhou.social.video.HeyhouPlayerService;
import com.heyhou.social.video.VideoPlayListener;
import com.heyhou.social.video.VideoTimeType;


/**
 * Created by lky on 2017/6/26.
 */

public class SpecialEffectsPlayView extends GLSurfaceView implements VideoPlayListener {

    private Handler mHandler;

    private SurfaceHolder mSurfaceHolder;
    private HeyhouPlayerService mService;

    private boolean isPause = false;

    private String mVideoPath;

    private boolean isLooping;

    private boolean isNeedCallBackFinish;

    private SpecialEffectsPlayViewListener mSpecialEffectsPlayViewListener;

    private boolean isResetNeedPlay = true;

    private HeyhouPlayerRender mHeyhouPlayerRender;

    public SpecialEffectsPlayView(Context context) {
        this(context,null);
    }

    public SpecialEffectsPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        setEGLContextClientVersion(2);
        mSurfaceHolder = getHolder();
        mService = HeyhouPlayerService.instance;
        mHeyhouPlayerRender = new HeyhouPlayerRender(context,this);
//        mHeyhouPlayerRender.setRotation(Rotation.ROTATION_90);
        setRenderer(mHeyhouPlayerRender);

//        mSurfaceHolder.addCallback(this);
        mHandler = new Handler();
    }

    public void setSpecialEffectsPlayViewListener(SpecialEffectsPlayViewListener specialEffectsPlayViewListener) {
        mSpecialEffectsPlayViewListener = specialEffectsPlayViewListener;
    }

    public void setNeedCallBackFinish(boolean needCallBackFinish) {
        isNeedCallBackFinish = needCallBackFinish;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        //m.attachSurface(holder.getSurface());
        mService.attachRender(mHeyhouPlayerRender);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder,format,width,height);
//        Log.d(TAG,"surface changed +++++++++++");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
//        Log.d(TAG,"surface destroyed +++++++++++");
        //m.detachSurface();
//        mService.detachRender();
        if(mHeyhouPlayerRender != null){
            mService.detachRender();
        }

    }


//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
////        mService.attachSurface(holder.getSurface());
////        mService.attachRender(new r);
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        mService.detachSurface();
//    }


    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public void stop(){
        isPause = false;
        mService.stopM();
    }

    public void destroyRender(){
        if(mHeyhouPlayerRender != null){
            mHeyhouPlayerRender.destroy();
            mHeyhouPlayerRender = null;
            mService.detachRender();
        }
    }

    public void setFilter(int filter){
        mService.setFilter(filter);
    }


    public void pause(){
        if(mService.isPlaying()){
            mService.pause();
            isPause = true;
        }
    }

    public void play(){
        if(isPause && !mService.isPlaying()){
            mService.resumeM();
        }else if(!TextUtils.isEmpty(mVideoPath)){
            mService.prepareM(mVideoPath);
        }
    }

    public void changeState(){
        if(mService.isPlaying()){
            pause();
        }else{
            play();
        }
    }

    public void setVideoPath(String videoPath){
        isResetNeedPlay = true;
        isPause = false;
        mVideoPath = videoPath;
        play();
    }

    public void setVideoPathNotPlay(String videoPath){
        isResetNeedPlay = false;
        mVideoPath = videoPath;
        mService.prepareM(mVideoPath);
    }

    public void setSpeed(VideoTimeType videoTimeType){
        mService.setSpeed(videoTimeType.getValue());
    }

    public long getDuration(){
        return mService.getDuration();
    }

    public void seekTo(long position){
        mService.setPosition(position);
    }


    @Override
    public void onBufferingEvent(float percentage) {

    }

    @Override
    public void onPrepareEvent() {
        mService.playM();
        if(!isResetNeedPlay){
            isResetNeedPlay = true;
            mService.pauseM();
            mService.setPosition(0);
            isPause = true;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mSpecialEffectsPlayViewListener != null){
                    mSpecialEffectsPlayViewListener.onPrepare(mService.getDuration()/1000);
                }
            }
        });

    }

    @Override
    public void onPlayEvent() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mSpecialEffectsPlayViewListener != null){
                    mSpecialEffectsPlayViewListener.onPlay();
                }
            }
        });

    }

    @Override
    public void onPauseEvent() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mSpecialEffectsPlayViewListener != null){
                    mSpecialEffectsPlayViewListener.onPause();
                }
            }
        });


    }

    @Override
    public void onResumeEvent() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mSpecialEffectsPlayViewListener != null){
                    mSpecialEffectsPlayViewListener.onPlay();
                }
            }
        });

    }

    @Override
    public void onStopEvent() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mSpecialEffectsPlayViewListener != null){
                    mSpecialEffectsPlayViewListener.onStop();
                }
            }
        });

    }

    @Override
    public void onEndEvent() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(isLooping && !TextUtils.isEmpty(mVideoPath)){
                    mService.prepareM(mVideoPath);
                    if(isNeedCallBackFinish){
                        if(mSpecialEffectsPlayViewListener != null){
                            mSpecialEffectsPlayViewListener.onFinish();
                        }
                    }
                }else{
                    if(mSpecialEffectsPlayViewListener != null){
                        mSpecialEffectsPlayViewListener.onFinish();
                    }
                }
            }
        });

    }

    @Override
    public void onErrorEvent(String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                ToastTool.showShort(AppUtil.getApplicationContext(), R.string.tidal_pat_upload_play_error);
            }
        });

    }

    @Override
    public void onPlayTimeEvent(final long time) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mSpecialEffectsPlayViewListener != null){
                    mSpecialEffectsPlayViewListener.onPlayTime(time/1000);
                }
            }
        });

    }

    public interface SpecialEffectsPlayViewListener{
        void onPrepare(long timeDuration);
        void onPlayTime(long time);
        void onPause();
        void onPlay();
        void onStop();
        void onFinish();
    }
}
