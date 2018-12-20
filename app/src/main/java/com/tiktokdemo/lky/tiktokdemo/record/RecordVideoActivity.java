package com.tiktokdemo.lky.tiktokdemo.record;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.adapter.TidalPatRecordFilterAdapter;
import com.tiktokdemo.lky.tiktokdemo.record.bean.MusicBean;
import com.tiktokdemo.lky.tiktokdemo.record.bean.TidalPatRecordDraftBean;
import com.tiktokdemo.lky.tiktokdemo.record.camera.camera.CameraEngine;
import com.tiktokdemo.lky.tiktokdemo.record.camera.encoder.video.ImageEncoderCore;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.MagicCameraView;
import com.tiktokdemo.lky.tiktokdemo.record.helper.RecordTimeType;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatFilterType;
import com.tiktokdemo.lky.tiktokdemo.record.presenter.RecordVideoContract;
import com.tiktokdemo.lky.tiktokdemo.record.presenter.RecordVideoPresenter;
import com.tiktokdemo.lky.tiktokdemo.record.weight.BreakProgressView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.CircleRecordView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.CountDownTextView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.NewSpeedLevelControllerView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.ScaleRoundRectView;
import com.tiktokdemo.lky.tiktokdemo.utils.AnimatorUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.CheckPermissionUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.CommonSelectDialog;
import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.FileUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.StringUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.ToastTool;
import com.heyhou.social.video.VideoInfo;
import com.heyhou.social.video.VideoTimeType;


/**
 * Created by lky on 2018/12/13
 */

public class RecordVideoActivity extends Activity implements View.OnClickListener,RecordVideoContract.View {


    private final float AUDIO_PLAY_DURATION = 15f;
    private final int VIDEO_COUNT_DOWN_HANDLER_WHAT = 102;
    private final int VIDEO_COUNT_DOWN_TIME_3 = 3;
    private final int VIDEO_COUNT_DOWN_TIME_6 = 6;
    private final int VIDEO_COUNT_DOWN_TIME_9 = 9;


    private MagicCameraView mMagicCameraView;


    private BreakProgressView mBreakProgressView;

    private NewSpeedLevelControllerView mSpeedLevelControllerView;

    private View mBtnsLayout;
    private ImageView mFlashImg;
    private ImageView mFlipImg;
    private ImageView mSaveImg;

    private ImageView mCutMusicImg;
    private ImageView mCountDownImg;
    private CountDownTextView mCountDownTxt;


    private View mFilterLayout;
    private RecyclerView mFilterRecyclerView;
    private CircleRecordView mCircleRecordView;
    private View mRemoveView;

    private View mCutAudioLayout;
    private View mCutAudioScaleLayout;
    private ScaleRoundRectView mCutAudioScaleRoundRectView;
    private TextView mCutAudioCurrentTxt,mCutAudioMaxTxt;


    private VideoTimeType mVideoTimeType = VideoTimeType.SPEED_N1;


    private boolean isCanRecord;

    private boolean isCountDowing;


    private boolean isWindowGone;


    private MusicBean mMusicBean;

    private RecordVideoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);
        setContentView(R.layout.activity_tidal_pat_record_video);
        mMusicBean = (MusicBean) getIntent().getSerializableExtra("MusicBean");
        mPresenter = new RecordVideoPresenter(this,mMusicBean);
        initView();
        boolean isAudioPermission = CheckPermissionUtil.isHasAudioPermission(this);
        if(!isAudioPermission){
            mCircleRecordView.setCanTouch(isAudioPermission);
            ToastTool.showShort(AppUtil.getApplicationContext(),R.string.tidal_pat_record_check_audio_permission);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isWindowGone = false;
        mPresenter.checkBGMPathUpdata();
        try{
            mPresenter.startCutAudioPlay();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPresenter.startAudioRecord();
    }


    @Override
    protected void onStop() {
        super.onStop();
        isWindowGone = true;
        mPresenter.stopAudioRecord();
        mFlashImg.setImageResource(R.mipmap.chaopai_luzhi_guanshanguangdeng);
        if(mPresenter.isRecording()){
            mCircleRecordView.cancelTouch();
        }
        try{
            mPresenter.pauseMusic();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.releaseAll();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("MusicBean", mMusicBean);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMusicBean = (MusicBean) savedInstanceState.getSerializable("MusicBean");
        super.onRestoreInstanceState(savedInstanceState);
    }



    public void initView(){
        mMagicCameraView = findViewById(R.id.tidal_pat_record_camera_view);
        mMagicCameraView.setOnImageEncoderListener(mOnImageEncoderListener);
        mMagicCameraView.setOnMagicCameraOpenListener(mOnMagicCameraOpenListener);
        mPresenter.initMagicEngine(mMagicCameraView);
        mBreakProgressView = findViewById(R.id.tidal_pat_record_video_break_progress);
        mBreakProgressView.setMax(mPresenter.getMaxRecordTime());
        mBreakProgressView.setProgress(0);
//        mBreakProgressView.setBreakProgress(mTidalPatRecordDraftBean.getBreakTimeArraysFromList());
//        mRecordTimeCount = mTidalPatRecordDraftBean.getRecordContinueTime();
        mBreakProgressView.setOnBreakProgressListener(mOnBreakProgressListener);
        mBtnsLayout = findViewById(R.id.tidal_pat_record_video_btn_layout);
        mFlashImg = findViewById(R.id.tidal_pat_record_video_flash_img);
        mFlipImg = findViewById(R.id.tidal_pat_record_video_flip_img);
        mSaveImg = findViewById(R.id.tidal_pat_record_video_save_img);

        mCutMusicImg = findViewById(R.id.tidal_pat_record_video_cut_music_img);
        mCountDownImg = findViewById(R.id.tidal_pat_record_video_count_down_img);
        mCountDownTxt = findViewById(R.id.tidal_pat_record_video_count_down_txt);

        mSpeedLevelControllerView = findViewById(R.id.tidal_pat_record_speed_controller_view);
        mSpeedLevelControllerView.setOnSpeedLevelChangeListener(mSpeedLevelChangeListener);
        mCircleRecordView = findViewById(R.id.tidal_pat_record_start_img);
        mCircleRecordView.setOnRecordChangeListener(mOnRecordChangeListener);

        mFilterLayout = findViewById(R.id.tidal_pat_record_filter_layout);
        mFilterLayout.setOnClickListener(this);
        mFilterRecyclerView = findViewById(R.id.tidal_pat_record_filter_recycler);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        TidalPatRecordFilterAdapter tidalPatRecordFilterAdapter = new TidalPatRecordFilterAdapter();
        tidalPatRecordFilterAdapter.setOnTidalPatFilterItemClickListener(mOnTidalPatFilterItemClickListener);
        mFilterRecyclerView.setAdapter(tidalPatRecordFilterAdapter);


        mCutAudioLayout = findViewById(R.id.tidal_pat_record_cut_audio_layout);
        mCutAudioScaleLayout = findViewById(R.id.tidal_pat_record_cut_audio_scale_layout);
        mCutAudioScaleRoundRectView = findViewById(R.id.tidal_pat_record_cut_audio_scale_view);
        mCutAudioScaleRoundRectView.setMax(50);//假数据
        mCutAudioScaleRoundRectView.setOnDragListener(mOnDragListener);
        mCutAudioCurrentTxt = findViewById(R.id.tidal_pat_record_cut_audio_current_time_txt);
        mCutAudioMaxTxt = findViewById(R.id.tidal_pat_record_cut_audio_max_time_txt);
        findViewById(R.id.tidal_pat_record_cut_audio_confirm_img).setOnClickListener(this);

        mLoadingView = findViewById(R.id.personal_show_record_video_loading_layout);
        mLoadingView.setOnClickListener(this);
        mLoadingTxt = findViewById(R.id.tidal_pat_record_video_loading_txt);
        Animation set = AnimationUtils.loadAnimation(this, R.anim.anim_center_rotate);
        set.setInterpolator(new LinearInterpolator());
        ((ImageView)(findViewById(R.id.personal_show_loading_img))).startAnimation(set);


//        findViewById(R.id.tidal_pat_record_filter_confirm_img).setOnClickListener(this);


        mCutMusicImg.setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_beauty_img).setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_filter_img).setOnClickListener(this);
        mCountDownImg.setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_count_down_img_3).setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_count_down_img_6).setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_count_down_img_9).setOnClickListener(this);

        mFlashImg.setOnClickListener(this);
        mFlipImg.setOnClickListener(this);
        mSaveImg.setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_back_img).setOnClickListener(this);
        mRemoveView = findViewById(R.id.tidal_pat_record_delete_view);
        mRemoveView.setOnClickListener(this);
//        mMagicCameraView.setOnClickListener(this);
        initRecordTimeSelectorView();


        mPresenter.checkLocalFrameInfo();
        changeAllBtnStatus(RECORD_STATUS, true);
//        if(mRecordTimeCount+mTempRecordTimeCount >= VIDEO_RECORD_MIN_TIME && !isCanSaveVideo){
//            isCanSaveVideo = true;
//            mSaveImg.setImageResource(R.mipmap.chaopai_luzhi_wancheng);
//        }
    }


    /**
     * 录制速度控件的点击回调
     */
    private NewSpeedLevelControllerView.OnSpeedLevelChangeListener mSpeedLevelChangeListener = new NewSpeedLevelControllerView.OnSpeedLevelChangeListener() {
        @Override
        public void onChange(VideoTimeType level) {
            mVideoTimeType = level;
            mPresenter.resetSpeedAudioMediaPlayer(mSpeedLevelControllerView.getSpeedLevel(),mBreakProgressView.getCurrentProgress(),mBreakProgressView.getMax());
        }
    };

    /**
     * 滤镜条目的点击回调
     */
    private TidalPatRecordFilterAdapter.OnTidalPatFilterItemClickListener mOnTidalPatFilterItemClickListener = new TidalPatRecordFilterAdapter.OnTidalPatFilterItemClickListener() {
        @Override
        public void onFilterItemClick(TidalPatFilterType type) {
            mPresenter.resetMagicEngineFilter(type);
        }
    };

    /**
     * 相机开启的回调
     */
    private MagicCameraView.OnMagicCameraOpenListener mOnMagicCameraOpenListener = new MagicCameraView.OnMagicCameraOpenListener() {
        @Override
        public void onCameraOpen() {
            mPresenter.magicEngineStartRecord();
        }
    };

    /**
     * 每一帧的图像的回调
     */
    private ImageEncoderCore.OnImageEncoderListener mOnImageEncoderListener = new ImageEncoderCore.OnImageEncoderListener() {
        @Override
        public void onImageEncoder(ImageEncoderCore.ImageInfo imageInfo) {
            mPresenter.imageEncoder(imageInfo);
//            }
            //这里是将回调的图像直接转化后显示在屏幕上，用于调试，有需要的可以参考
//            byte[] data = new byte[imageInfo.rowStride * imageInfo.height];
//            imageInfo.data.get(data);
//            final int pixelStride = imageInfo.pixelStride;
//            final int rowPadding = imageInfo.rowPadding;
//            int[] mPixelData = new int[CameraEngine.RECORD_HEIGHT*CameraEngine.RECORD_WIDTH];
//            int offset = 0;
//            int index = 0;
//            for (int i = 0; i < imageInfo.height; ++i) {
//                for (int j = 0; j < imageInfo.width; ++j) {
//                    int pixel = 0;
//                    pixel |= (data[offset] & 0xff) << 16;     // R
//                    pixel |= (data[offset + 1] & 0xff) << 8;  // G
//                    pixel |= (data[offset + 2] & 0xff);       // B
//                    pixel |= (data[offset + 3] & 0xff) << 24; // A
//                    mPixelData[index++] = pixel;
//                    offset += pixelStride;
//                }
//                offset += rowPadding;
//            }
//
//            final Bitmap bitmap = Bitmap.createBitmap(mPixelData,
//                    imageInfo.width, imageInfo.height,
//                    Bitmap.Config.ARGB_8888);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);
//                }
//            });
        }
    };
    /**
     * 录制按钮按下及松开的回调
     */
    private CircleRecordView.OnRecordChangeListener mOnRecordChangeListener = new CircleRecordView.OnRecordChangeListener() {
        @Override
        public void onEventDown() {//按下录制
            changeAllBtnStatus(RECORD_STATUS,false);
            mBreakProgressView.resetRemoveStatus();
            mPresenter.startRecord(mSpeedLevelControllerView.getSpeedLevel());
        }

        @Override
        public void onEventUp() {//松开录制
            changeAllBtnStatus(RECORD_STATUS,true);
            mPresenter.stopRecord();
            mPresenter.pauseMusic();
            mPresenter.seekToAudioMediaPlayer(mSpeedLevelControllerView.getSpeedLevel(),mBreakProgressView.getCurrentProgress(),mBreakProgressView.getMax());
        }

        @Override
        public void onDontTouch() {

        }
    };

    /**
     * 剪音乐控件的回调
     */
    private ScaleRoundRectView.OnDragListener mOnDragListener = new ScaleRoundRectView.OnDragListener() {
        @Override
        public void onPositionChange(int position) {//移动回调
            mCutAudioCurrentTxt.setText(StringUtil.generateTimeFromSymbol(position*1000L));
            mPresenter.pauseCutAudioMusic();
        }

        @Override
        public void onChangeUp(float position) {//松手回调
            mPresenter.resetCutMusic(position);
        }
    };

    /**
     * 录制进度条的回调
     */
    private BreakProgressView.OnBreakProgressListener mOnBreakProgressListener = new BreakProgressView.OnBreakProgressListener() {
        @Override
        public void progress(int progress) {

        }

        @Override
        public void complete() {

        }

        @Override
        public void remove() {//删除最后一段
            mPresenter.deleteRecordVideo(mBreakProgressView.getCurrentProgress());
        }
    };


    @Override
    public void onBackPressed() {//返回复写
        if(isCountDowing || mPresenter.isSpeedAudio() || mPresenter.isRecording() || mPresenter.isAudioCuting()){
            return ;
        }
        if(mFilterLayout.getVisibility() == View.VISIBLE){
            changeAllBtnStatus(FILTER_SHOW_STATUS,true);
            return ;
        }
        if(mCutAudioLayout.getVisibility() == View.VISIBLE){
            cutAudioFinish();
            return ;
        }
        if(mPresenter.isCombining()){
            ToastTool.showShort(this, R.string.personal_show_combining_hint);
            return ;
        }
        if(!mPresenter.isRecordVideoInfoEmpty()){
            CommonSelectDialog.show(this, -1, new CommonSelectDialog.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int which) {
                    switch (which){
                        case 0:
                            for(int i=0;i<mPresenter.getRecordVideoInfos().size();i++){
                                FileUtils.deleteFile(mPresenter.getRecordVideoInfos().get(i).getVideoPath());
                            }
                            RecordVideoActivity.this.finish();
                            break;
                        case 1:
                            mPresenter.resetRecord();
                            break;
                    }
                }
            },getString(R.string.tidal_pat_record_delete_video_out),getString(R.string.tidal_pat_record_reset));
        }else{
            finish();
        }
    }





    @Override
    public void onClick(View v) {
        if(v.getId() != R.id.tidal_pat_record_delete_view){
            mBreakProgressView.resetRemoveStatus();
        }
        switch (v.getId()){
            case R.id.tidal_pat_record_video_back_img://返回
                onBackPressed();
                break;
            case R.id.tidal_pat_record_video_flash_img://闪光灯
                if(CameraEngine.getCameraInfo().isFront){
                    return ;
                }
                mPresenter.changeFlashMode();
                break;
            case R.id.tidal_pat_record_video_flip_img://镜头切换
                mPresenter.switchCamera();
                break;
            case R.id.tidal_pat_record_video_save_img://保存按钮
                if(mPresenter.isCombining()){
                    return ;
                }
                if(mPresenter.isCanSaveVideo()){
                    if(mPresenter.isRecording()){
                        mCircleRecordView.cancelTouch();
                    }
                    mPresenter.combineVideo();
                }
                break;
            case R.id.tidal_pat_record_video_cut_music_img://剪音乐
                if (mMusicBean == null || TextUtils.isEmpty(mMusicBean.getUrl()) || mRemoveView.getVisibility() == View.VISIBLE
                        || mPresenter.getRecordTimeType() != RecordTimeType.RECORD_TIME_15){
                    return ;
                }
                showCutAudioLayout();
                break;
            case R.id.tidal_pat_record_video_beauty_img://美颜
                mPresenter.changeBeautyOpen();

                break;
            case R.id.tidal_pat_record_video_filter_img://滤镜
                showFilterRecycler();
                break;
            case R.id.tidal_pat_record_video_count_down_img://倒计时
                if(mPresenter.isCurrentRecordMax() || !isCanRecord){
                    return ;
                }
                if(findViewById(R.id.tidal_pat_record_video_count_down_img_3).getVisibility() == View.GONE){
                    showCountDownLayout();
                }else{
                    hideCountDownLayout(0);
                }
                break;
            case R.id.tidal_pat_record_video_count_down_img_3://倒计时3
                if(mPresenter.isCurrentRecordMax() || !isCanRecord){
                    return ;
                }
                hideCountDownLayout(VIDEO_COUNT_DOWN_TIME_3);
                break;
            case R.id.tidal_pat_record_video_count_down_img_6://倒计时6
                if(mPresenter.isCurrentRecordMax() || !isCanRecord){
                    return ;
                }
                hideCountDownLayout(VIDEO_COUNT_DOWN_TIME_6);
                break;
            case R.id.tidal_pat_record_video_count_down_img_9://倒计时9
                if(mPresenter.isCurrentRecordMax() || !isCanRecord){
                    return ;
                }
                hideCountDownLayout(VIDEO_COUNT_DOWN_TIME_9);
                break;
            case R.id.personal_show_record_video_loading_layout://dialog

                break;
            case R.id.tidal_pat_record_cut_audio_confirm_img://剪音乐完成
                if(mPresenter.isAudioCuting()){
                    return ;
                }
                cutAudioFinish();
                break;
            case R.id.tidal_pat_record_delete_view://删除按钮
                if(mBreakProgressView.getCurrentProgress() <= 0 && !mPresenter.isRecordVideoInfoEmpty()){//不正常数据
                    mPresenter.getRecordVideoInfos().clear();
                    mRemoveView.setVisibility(View.GONE);
                    return ;
                }
                mBreakProgressView.removeLastBreakProgress();
                break;
            case R.id.tidal_pat_record_filter_layout:
                hideFilterRecycler();
                break;
            case R.id.tidal_pat_record_camera_view:

                break;
        }
    }

    /**
     * 倒计时动画handler
     */
    public Handler mCountDownHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 <= 0){
                isCountDowing = false;
                if(isWindowGone){
                    changeAllBtnStatus(RECORD_COUNT_DOWN,false);
                    return ;
                }
                mCountDownTxt.setText("" + VIDEO_COUNT_DOWN_TIME_3);
                mCountDownTxt.setVisibility(View.GONE);
                mCircleRecordView.setVisibility(View.VISIBLE);
                mSaveImg.setVisibility(View.VISIBLE);
                mCircleRecordView.autoAnimatorStart();
                return ;
            }
            mCountDownTxt.setText("" + msg.arg1);
            Message msg1 = mCountDownHandler.obtainMessage();
            msg1.what = VIDEO_COUNT_DOWN_HANDLER_WHAT;
            msg1.arg1 = msg.arg1 - 1;
            mCountDownHandler.sendMessageDelayed(msg1,1000);
        }
    };

    public void showCountDownLayout(){//显示倒计时动画
        View mCountDown3 = findViewById(R.id.tidal_pat_record_video_count_down_img_3);
        View mCountDown6 = findViewById(R.id.tidal_pat_record_video_count_down_img_6);
        View mCountDown9 = findViewById(R.id.tidal_pat_record_video_count_down_img_9);
        mCountDown3.setVisibility(View.VISIBLE);
        mCountDown6.setVisibility(View.VISIBLE);
        mCountDown9.setVisibility(View.VISIBLE);

        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(AnimatorUtils
                .translationAnimator(mCountDown3,0,-DensityUtils.dp2px(50),0,0,400,new BounceInterpolator(),null));
        animators.add(AnimatorUtils.translationAnimator(mCountDown6,0,-DensityUtils.dp2px(100),0,0,400,new BounceInterpolator(),null));
        animators.add(AnimatorUtils.translationAnimator(mCountDown9,0,-DensityUtils.dp2px(150),0,0,400,new BounceInterpolator(),null));
        animators.add(AnimatorUtils.rotationAnimator(mCountDown3,360,0,400,null));
        animators.add(AnimatorUtils.rotationAnimator(mCountDown6,360,0,400,null));
        animators.add(AnimatorUtils.rotationAnimator(mCountDown9,360,0,400,null));
        AnimatorUtils.playAnimatorArray(animators, AnimatorUtils.AnimatorPlayType.Together);
    }

    public void hideCountDownLayout(final int time){//隐藏倒计时动画
        final View mCountDown3 = findViewById(R.id.tidal_pat_record_video_count_down_img_3);
        final View mCountDown6 = findViewById(R.id.tidal_pat_record_video_count_down_img_6);
        final View mCountDown9 = findViewById(R.id.tidal_pat_record_video_count_down_img_9);

        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(AnimatorUtils.translationAnimator(mCountDown3,-DensityUtils.dp2px(50),0,0,0,400,new AccelerateInterpolator(),null));
        animators.add(AnimatorUtils.translationAnimator(mCountDown6,-DensityUtils.dp2px(100),0,0,0,400,new AccelerateInterpolator(),null));
        animators.add(AnimatorUtils.translationAnimator(mCountDown9,-DensityUtils.dp2px(150),0,0,0,400,new AccelerateInterpolator(),null));
        animators.add(AnimatorUtils.rotationAnimator(mCountDown3,0,360,400,null));
        animators.add(AnimatorUtils.rotationAnimator(mCountDown6,0,360,400,null));
        animators.add(AnimatorUtils.rotationAnimator(mCountDown9,0,360,400,null));
        AnimatorUtils.playAnimatorArray(animators, AnimatorUtils.AnimatorPlayType.Together, new AnimatorUtils.FreeAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCountDown3.setVisibility(View.GONE);
                mCountDown6.setVisibility(View.GONE);
                mCountDown9.setVisibility(View.GONE);
                if(time != 0){
                    startCountDown(time);
                }
            }
        });

    }

    public void startCountDown(int time){//开始倒计时
        isCountDowing = true;
        mCountDownTxt.setText("" + time);
        changeAllBtnStatus(RECORD_COUNT_DOWN,true);
        if(!mCountDownHandler.hasMessages(VIDEO_COUNT_DOWN_HANDLER_WHAT)){
            Message msg = mCountDownHandler.obtainMessage();
            msg.what = VIDEO_COUNT_DOWN_HANDLER_WHAT;
            msg.arg1 = time;
            mCountDownHandler.sendMessage(msg);
        }
    }

    public void showFilterRecycler(){//显示滤镜布局
        changeAllBtnStatus(FILTER_SHOW_STATUS,false);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mFilterRecyclerView, View.TRANSLATION_Y,mFilterRecyclerView.getHeight()==0? DensityUtils.dp2px(70):mFilterRecyclerView.getHeight(),0f);
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    public void hideFilterRecycler(){//隐藏滤镜布局
        changeAllBtnStatus(FILTER_SHOW_STATUS,true);
    }

    public void showCutAudioLayout(){//显示剪音乐布局
        changeAllBtnStatus(RECORD_CUT_AUDIO,false);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mCutAudioScaleLayout, View.TRANSLATION_Y,mCutAudioScaleLayout.getHeight()==0? DensityUtils.dp2px(200):mCutAudioScaleLayout.getHeight(),0f);
        objectAnimator.setDuration(300);
        objectAnimator.start();
        mPresenter.resetCutAudioMusic();
    }

    public void cutAudioFinish(){//剪音乐完成
        mPresenter.createSpeedAudioFiles();
        changeAllBtnStatus(RECORD_CUT_AUDIO,true);
        mPresenter.releaseCutAudioMusic();
    }


    private final int RECORD_STATUS = 1001;
    private final int FILTER_SHOW_STATUS = 1002;
    private final int RECORD_COUNT_DOWN = 1004;
    private final int RECORD_CUT_AUDIO = 1005;

    /**
     * 按钮状态改变
     * @param status 按钮类型
     * @param isShow 是否显示
     */
    public void changeAllBtnStatus(int status,boolean isShow){
        switch (status){
            case FILTER_SHOW_STATUS:
                mBtnsLayout.setVisibility(isShow? View.VISIBLE: View.GONE);
                mRecordTimeSelectorLayout.setVisibility(isShow?
                        View.VISIBLE: View.GONE);
                mSaveImg.setVisibility(isShow? View.VISIBLE: View.GONE);
                mRemoveView.setVisibility(isShow?(mPresenter.isRecordVideoInfoEmpty()? View.GONE:View.VISIBLE):
                        View.GONE);
                mCutMusicImg.setImageResource((!mPresenter.isRecordVideoInfoEmpty() || mMusicBean == null || TextUtils
                        .isEmpty(mMusicBean.getUrl()))?R.mipmap.btn_cut_dis:R.mipmap.chaopai_luzhi_jianyinyue);
                mCircleRecordView.setVisibility(isShow? View.VISIBLE: View.GONE);
                mSpeedLevelControllerView.setVisibility((isShow && mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_15)?
                        View.VISIBLE: View.GONE);
                mFilterLayout.setVisibility(isShow? View.GONE: View.VISIBLE);
                break;
            case RECORD_STATUS:
                mBtnsLayout.setVisibility(isShow? View.VISIBLE: View.GONE);
                mRecordTimeSelectorLayout.setVisibility(isShow?
                        View.VISIBLE: View.GONE);
                mTimeCountTxt.setVisibility((isShow || mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_15)?
                        View.GONE: View.VISIBLE);
                mRemoveView.setVisibility(isShow?(mPresenter.isRecordVideoInfoEmpty()? View.GONE:View.VISIBLE):
                        View.GONE);
                mCutMusicImg.setImageResource((!mPresenter.isRecordVideoInfoEmpty() || mMusicBean == null || TextUtils
                        .isEmpty(mMusicBean.getUrl()))?R.mipmap.btn_cut_dis:R.mipmap.chaopai_luzhi_jianyinyue);
                mSpeedLevelControllerView.setVisibility((isShow && mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_15)?
                        View.VISIBLE: View.GONE);
                break;
            case RECORD_COUNT_DOWN:
                mBtnsLayout.setVisibility(isShow? View.GONE: View.VISIBLE);
                mRecordTimeSelectorLayout.setVisibility(!isShow?
                        View.VISIBLE: View.GONE);
                mSaveImg.setVisibility(isShow? View.GONE: View.VISIBLE);
                mRemoveView.setVisibility(isShow? View.GONE: View.VISIBLE);
                mCircleRecordView.setVisibility(isShow? View.GONE: View.VISIBLE);
                mSpeedLevelControllerView.setVisibility((isShow || mPresenter.getRecordTimeType() != RecordTimeType.RECORD_TIME_15)?
                        View.GONE: View.VISIBLE);
                mCountDownTxt.setVisibility(isShow? View.VISIBLE: View.GONE);
                break;
            case RECORD_CUT_AUDIO:
                mBtnsLayout.setVisibility(isShow? View.VISIBLE: View.GONE);
                mRecordTimeSelectorLayout.setVisibility(isShow?
                        View.VISIBLE: View.GONE);
                mSaveImg.setVisibility(isShow? View.VISIBLE: View.GONE);
                mRemoveView.setVisibility(isShow?(mPresenter.isRecordVideoInfoEmpty()? View.GONE:View.VISIBLE):
                        View.GONE);
                mCutMusicImg.setImageResource((!mPresenter.isRecordVideoInfoEmpty() || mMusicBean == null || TextUtils
                        .isEmpty(mMusicBean.getUrl()))?R.mipmap.btn_cut_dis:R.mipmap.chaopai_luzhi_jianyinyue);
                mCircleRecordView.setVisibility(isShow? View.VISIBLE: View.GONE);
                mSpeedLevelControllerView.setVisibility((isShow && mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_15)?
                        View.VISIBLE: View.GONE);
                mCutAudioLayout.setVisibility(isShow? View.GONE: View.VISIBLE);
                break;
        }
    }

    @Override
    public void showToast(String msg) {
        ToastTool.showShort(AppUtil.getApplicationContext(),msg);
    }

    @Override public void showLoadingView(boolean isShow, int loadingTxtRes) {
        if(mLoadingView == null){
            return ;
        }
        if(loadingTxtRes != 0){
            mLoadingTxt.setText(loadingTxtRes);
        }
        mLoadingView.setVisibility(isShow?View.VISIBLE:View.GONE);
        ImageView imgProgress = mLoadingView.findViewById(R.id.personal_show_loading_img);
        Animation set = AnimationUtils.loadAnimation(this, R.anim.anim_center_rotate);
        set.setInterpolator(new LinearInterpolator());
        imgProgress.startAnimation(set);
    }

    @Override
    public void canRecord(boolean isCan) {
        isCanRecord = isCan;
        mCircleRecordView.setCanTouch(isCan);
    }


    @Override public void checkMusicEmpty() {
        mCutMusicImg.setImageResource(R.mipmap.btn_cut_dis);
    }


    @Override public void checkMusicLength(int duration) {
        mCutAudioScaleRoundRectView.setMax((int) (duration/1000f));
        mCutAudioScaleRoundRectView.setProgress(0);
        mCutAudioCurrentTxt.setText(StringUtil.generateTimeFromSymbol(0));
        mCutAudioMaxTxt.setText(StringUtil.generateTimeFromSymbol(duration));
    }


    @Override public void createSpeedAudioSuccess() {
        mPresenter.resetSpeedAudioMediaPlayer(mSpeedLevelControllerView.getSpeedLevel(),mBreakProgressView.getCurrentProgress(),mBreakProgressView.getMax());
    }


    @Override public void combineVideoSuccess(String outputVideoPath) {
        Intent intent = new Intent(RecordVideoActivity.this,VideoPlayActivity.class);
        intent.putExtra("isFromEdit",true);
        //                                if(mTidalPatRecordDraftBean != null){
        TidalPatRecordDraftBean mTidalPatRecordDraftBean = new TidalPatRecordDraftBean();
        mTidalPatRecordDraftBean.setVideoLocalUrl(outputVideoPath);
        mTidalPatRecordDraftBean.setRecordTimeType(RecordTimeType.RECORD_TIME_15);
        mTidalPatRecordDraftBean.setMusicId(1);
        mTidalPatRecordDraftBean.setMusicName(mMusicBean.getName());
        mTidalPatRecordDraftBean.setMusicLocalUrl(mMusicBean.getUrl());
        mTidalPatRecordDraftBean.setOriginalVolume(mTidalPatRecordDraftBean.getMusicId() != 0?0:50f);
        mTidalPatRecordDraftBean.setBackgroundVolume(mTidalPatRecordDraftBean.getMusicId() != 0?50f:0);
        ArrayList<String> localFiles = new ArrayList<>();
        for(VideoInfo videoInfo:mPresenter.getRecordVideoInfos()){
            localFiles.add(videoInfo.getVideoPath());
        }
        mTidalPatRecordDraftBean.setVideoLocalArraysFromList(localFiles);
        intent.putExtra("mTidalPatRecordDraftBean",mTidalPatRecordDraftBean);
        //                                }
        startActivity(intent);
    }


    @Override public void combineVideoFail(String errorMsg) {
        ToastTool.showShort(RecordVideoActivity.this,getString(R.string.personal_show_record_video_combine_fail) + errorMsg);
    }

    @Override
    public void startRecordSuccess(float progress) {
        if(mBreakProgressView.getCurrentProgress() != 0){
            mBreakProgressView.addBreakProgress((int)progress);
        }
    }

    @Override
    public void recordProgress(boolean isMinProgress, float progress) {
        if(isMinProgress){
            mSaveImg.setImageResource(R.mipmap.chaopai_luzhi_wancheng);
        }
        mBreakProgressView.setProgress((int)(progress));
    }

    @Override
    public void recordProgressForm120(float progress) {
        if(mTimeCountTxt != null){
            mTimeCountTxt.setText(
                    String.format("%.1f",(progress)/1000f)+AppUtil.getString(R.string.tidal_pat_record_time_second));
        }
    }

    @Override
    public void recordProgressMax() {
        mCircleRecordView.setCanTouch(false);
        mCountDownImg.setImageResource(R.mipmap.chaopa_daojishi_zhihui);
    }

    @Override
    public void resetRecordFinish() {
        mBreakProgressView.resetAllStatus();
        mSaveImg.setImageResource(R.mipmap.chaopai_luzhi_wanchenmoren);
        mRemoveView.setVisibility(View.GONE);
        if(mMusicBean == null || TextUtils.isEmpty(mMusicBean.getUrl()) || mPresenter.getRecordTimeType() != RecordTimeType.RECORD_TIME_15){
            mCutMusicImg.setImageResource(R.mipmap.btn_cut_dis);
        }else{
            mCutMusicImg.setImageResource(R.mipmap.chaopai_luzhi_jianyinyue);
        }
        mCountDownImg.setImageResource(R.mipmap.chaopai_luzhi_daojishi);
        mCircleRecordView.setCanTouch(true);
        mPresenter.resetSpeedAudioMediaPlayer(mSpeedLevelControllerView.getSpeedLevel(),mBreakProgressView.getCurrentProgress(),mBreakProgressView.getMax());
    }

    @Override
    public void deleteRecordVideoFinish(boolean isCanSave) {
        mCircleRecordView.setCanTouch(true);
        mCountDownImg.setImageResource(R.mipmap.chaopai_luzhi_daojishi);
        mSpeedLevelControllerView.setCanTouch(true);
//        mPresenter.setRecordTimeCount(mBreakProgressView.getCurrentProgress());
        mPresenter.resetSpeedAudioMediaPlayer(mSpeedLevelControllerView.getSpeedLevel(),mBreakProgressView.getCurrentProgress(),mBreakProgressView.getMax());
        if(!mPresenter.isRecordVideoInfoEmpty()){
            mCutMusicImg.setImageResource(R.mipmap.btn_cut_dis);
            mRemoveView.setVisibility(View.VISIBLE);
        }else{
            if(mMusicBean != null && !TextUtils.isEmpty(mMusicBean.getUrl()) && mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_15){
                mCutMusicImg.setImageResource(R.mipmap.chaopai_luzhi_jianyinyue);
            }
            mRemoveView.setVisibility(View.GONE);
        }
        if(isCanSave){
            mSaveImg.setImageResource(R.mipmap.chaopai_luzhi_wancheng);
        }else{
            mSaveImg.setImageResource(R.mipmap.chaopai_luzhi_wanchenmoren);
        }
    }

    @Override
    public void changeFlashModeFinish(String flashMode) {
        if(flashMode.equals(Camera.Parameters.FLASH_MODE_ON) || flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)){
            mFlashImg.setImageResource(R.mipmap.chaopai_luzhi_kaishanguangdeng);
        }else if(flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)){
            mFlashImg.setImageResource(R.mipmap.chaopai_luzhi_guanshanguangdeng);
        }
    }

    @Override
    public void switchCameraFinish() {
        mFlashImg.setVisibility(CameraEngine.getCameraInfo().isFront? View.GONE: View.VISIBLE);
        mFlashImg.setImageResource(R.mipmap.chaopai_luzhi_guanshanguangdeng);
    }

    @Override
    public void changeBeautyOpenFinish(boolean isOpen) {
        ((ImageView)findViewById(R.id.tidal_pat_record_video_beauty_img)).setImageResource(isOpen?R.mipmap.chaopai_luzhi_kaimeiyan:R.mipmap.chaopai_luzhi_guangmeiyan);
    }


    private View mLoadingView;
    private TextView mLoadingTxt;
    private View mRecordTimeSelectorLayout;
    private TextView mTimeCountTxt;

    private void initRecordTimeSelectorView(){
        refreshAllFromRecordTime(mPresenter.getRecordTimeType());
        mRecordTimeSelectorLayout = findViewById(R.id.item_record_time_selector_layout);
        mTimeCountTxt = (TextView) findViewById(R.id.item_record_time_count_txt);

        findViewById(R.id.item_record_time_selector_layout_15).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_15){
                    return ;
                }
                if(!mPresenter.isRecordVideoInfoEmpty()){
                    ToastTool.showShort(AppUtil.getApplicationContext(),R.string.tidal_pat_record_change_time_mode_hint);
                    return ;
                }
                refreshAllFromRecordTime(RecordTimeType.RECORD_TIME_15);
            }
        });

        findViewById(R.id.item_record_time_selector_layout_120).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPresenter.getRecordTimeType() == RecordTimeType.RECORD_TIME_120){
                    return ;
                }
                if(!mPresenter.isRecordVideoInfoEmpty()){
                    ToastTool.showShort(AppUtil.getApplicationContext(),R.string.tidal_pat_record_change_time_mode_hint);
                    return ;
                }
                refreshAllFromRecordTime(RecordTimeType.RECORD_TIME_120);
            }
        });
    }

    private void refreshAllFromRecordTime(RecordTimeType recordTimeType){
        mPresenter.setRecordTimeType(recordTimeType);
        mVideoTimeType = VideoTimeType.SPEED_N1;
        mSpeedLevelControllerView.setSpeedLevel(mVideoTimeType);
        notifyRecordTime();
//        resetRecord();
        switch (recordTimeType){
            case RECORD_TIME_15:
                mPresenter.setMaxRecordTime(mPresenter.VIDEO_RECORD_MAX_TIME);
                mSpeedLevelControllerView.setVisibility(View.VISIBLE);
                mBreakProgressView.setMax(mPresenter.getMaxRecordTime());
                break;
            case RECORD_TIME_120:
                mPresenter.setMaxRecordTime(mPresenter.VIDEO_RECORD_MAX_TIME_120);
                mSpeedLevelControllerView.setVisibility(View.GONE);
                mCutMusicImg.setImageResource(R.mipmap.btn_cut_dis);
                mBreakProgressView.setMax(mPresenter.getMaxRecordTime());
                if(mMusicBean != null && !TextUtils.isEmpty(mMusicBean.getUrl())){
                    ToastTool.showShort(AppUtil.getApplicationContext(),R.string.tidal_pat_record_time_120_not_background_music);
                }
                break;
        }
    }


    private void notifyRecordTime(){
        View hintView15 = findViewById(R.id.item_record_time_selector_hint_view_15);
        TextView textView15 = (TextView) findViewById(R.id.item_record_time_selector_txt_15);
        View hintView120 = findViewById(R.id.item_record_time_selector_hint_view_120);
        TextView textView120 = (TextView) findViewById(R.id.item_record_time_selector_txt_120);
        switch (mPresenter.getRecordTimeType()){
            case RECORD_TIME_15:
                hintView15.setBackgroundResource(R.drawable.bg_tidal_pat_record_time_red_cricle);
                textView15.setTextColor(getResources().getColor(R.color.theme_pink));
                hintView120.setBackgroundColor(getResources().getColor(R.color.transparency));
                textView120.setTextColor(getResources().getColor(R.color.white));
                break;
            case RECORD_TIME_120:
                hintView15.setBackgroundColor(getResources().getColor(R.color.transparency));
                textView15.setTextColor(getResources().getColor(R.color.white));
                hintView120.setBackgroundResource(R.drawable.bg_tidal_pat_record_time_red_cricle);
                textView120.setTextColor(getResources().getColor(R.color.theme_pink));
                break;
        }
    }
}
