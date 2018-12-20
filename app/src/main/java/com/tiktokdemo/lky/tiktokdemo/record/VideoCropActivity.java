package com.tiktokdemo.lky.tiktokdemo.record;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.Constant;
import com.tiktokdemo.lky.tiktokdemo.HomeCallBack;
import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.bean.TidalPatRecordDraftBean;
import com.tiktokdemo.lky.tiktokdemo.record.helper.RecordTimeType;
import com.tiktokdemo.lky.tiktokdemo.record.manager.SpecialEffectsPlayManager;
import com.tiktokdemo.lky.tiktokdemo.record.weight.CircleProgressView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.NewSpeedLevelControllerView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.SpecialEffectsPlayView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.VideoCropViewBar;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.ToastTool;
import com.heyhou.social.video.HeyhouVideo;
import com.heyhou.social.video.VideoListener;
import com.heyhou.social.video.VideoTimeType;


/**
 * Created by lky on 2018/12/13
 */

public class VideoCropActivity extends Activity implements View.OnClickListener {

    private final int MIN_TIME = 3000;

    private final int VIDEO_RECORD_MAX_TIME = 15000;
    private final int VIDEO_RECORD_MAX_TIME_120 = 120000;

    private int mMaxRecordTime = VIDEO_RECORD_MAX_TIME;

    private NewSpeedLevelControllerView mSpeedLevelControllerView;
    private VideoCropViewBar mVideoCropViewBar;
    private SpecialEffectsPlayView mPlayView;


    private VideoTimeType mVideoTimeType = VideoTimeType.SPEED_N1;

    private String mCurrentVideoPath;

    private ImageView mSaveImg;
    private TextView mSelectedTxt;

    private View mLoadingView;
    private TextView mLoadingTxt;

    private long mCurrentCropTime;
    private long mCurrentRange;

    private boolean isEdit;
    private boolean isStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);
        setContentView(R.layout.activity_tidal_pat_video_crop);
        mCurrentVideoPath = getIntent().getStringExtra("mCurrentVideoPath");

        mPlayView = (SpecialEffectsPlayView) findViewById(R.id.tidal_pat_crop_video_pv);
        mPlayView.setLooping(true);
        mPlayView.setNeedCallBackFinish(true);
        SpecialEffectsPlayManager.startPlay(mPlayView);



        mVideoCropViewBar = findViewById(R.id.tidal_pat_video_crop_crop_view);
        mSpeedLevelControllerView =  findViewById(R.id.tidal_pat_video_crop_speed_controller_view);
        mSaveImg = findViewById(R.id.tidal_pat_crop_video_save_img);
        mSelectedTxt = findViewById(R.id.tidal_pat_video_crop_selected_time_txt);


        mVideoCropViewBar.setVideoCropViewBarListener(mVideoCropViewBarListener);
        mVideoCropViewBar.setVideoPath(mCurrentVideoPath);
        mPlayView.setVideoPath(mCurrentVideoPath);


        mSpeedLevelControllerView.setOnSpeedLevelChangeListener(mSpeedLevelChangeListener);



        mSaveImg.setOnClickListener(this);
        findViewById(R.id.tidal_pat_crop_video_back_img).setOnClickListener(this);

        mPlayView.setSpecialEffectsPlayViewListener(mPlayViewListener);

        mLoadingView = findViewById(R.id.personal_show_record_video_loading_layout);
        mLoadingView.setOnClickListener(this);
        mLoadingTxt = (TextView) findViewById(R.id.tidal_pat_record_video_loading_txt);
        initRecordTimeSelectorView();
    }

    @Override
    public void finish() {
        mVideoCropViewBar.releaseData();
        super.finish();
    }

    private VideoCropViewBar.VideoCropViewBarListener mVideoCropViewBarListener = new VideoCropViewBar.VideoCropViewBarListener() {
        @Override
        public void touchDown() {
            mPlayView.pause();
        }

        @Override
        public void touchUp() {
            mPlayView.play();
        }

        @Override
        public void touchChange(long time) {
            mCurrentCropTime = time * 1000L;
            mPlayView.seekTo(time * 1000L);
        }

        @Override
        public void rangeChange(long time, long range) {
            mCurrentCropTime = time * 1000L;
            mCurrentRange = range * 1000L;
            mSelectedTxt.setText(AppUtil.getApplicationContext().getString(R.string.tidal_pat_crop_video_selected_time,(int)(range/1000L)));
            mSaveImg.setImageResource(range>=MIN_TIME?R.mipmap.chaopai_luzhi_wancheng:R.mipmap.chaopai_luzhi_wanchenmoren);
            mPlayView.seekTo(time * 1000L);
        }

        @Override
        public void makeDataFail(String error) {
            ToastTool.showShort(VideoCropActivity.this,R.string.tidal_pat_crop_video_make_data_fail);
            finish();
        }
    };

    private SpecialEffectsPlayView.SpecialEffectsPlayViewListener mPlayViewListener = new SpecialEffectsPlayView.SpecialEffectsPlayViewListener() {
        @Override
        public void onPrepare(long timeDuration) {
            mPlayView.setSpeed(mVideoTimeType);
            mPlayView.seekTo(mCurrentCropTime);
        }

        @Override
        public void onPlayTime(long time) {
            if(time > (mCurrentCropTime + mCurrentRange * mVideoCropViewBar.getCurrentSpeed())/1000){
                mPlayView.seekTo(mCurrentCropTime);
            }
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onPlay() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tidal_pat_crop_video_back_img:
                onBackPressed();
                break;
            case R.id.tidal_pat_crop_video_save_img:
                if(mCurrentRange/1000L < MIN_TIME){
                    return ;
                }
                makeVideo();
                break;
            case R.id.personal_show_record_video_loading_layout://dialog

                break;
        }
    }

    public void makeVideo(){
        mLoadingView.setVisibility(View.VISIBLE);
        editReleaseData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                cropVideo(mCurrentVideoPath, mCurrentCropTime, mCurrentRange, mVideoTimeType, new HomeCallBack() {
                    @Override
                    public void finish(final Object obj) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingView.setVisibility(View.GONE);
                                String outputVideoPath = (String) obj;
                                Intent intent = new Intent(VideoCropActivity.this,VideoPlayActivity.class);
                                intent.putExtra("isFromCrop",true);
                                TidalPatRecordDraftBean tidalPatRecordDraftBean = new TidalPatRecordDraftBean();
                                tidalPatRecordDraftBean.setVideoLocalUrl(outputVideoPath);
                                ArrayList<String> strPaths = tidalPatRecordDraftBean.getVideoLocalArrayFromList();
                                strPaths.add(outputVideoPath);
                                tidalPatRecordDraftBean.setRecordContinueTime((int) mCurrentCropTime);
                                tidalPatRecordDraftBean.setRecordTimeType(mRecordTimeType);
                                tidalPatRecordDraftBean.setVideoLocalArraysFromList(strPaths);
                                tidalPatRecordDraftBean.setOriginalVolume(tidalPatRecordDraftBean.getMusicId() != 0?0:50f);
                                tidalPatRecordDraftBean.setBackgroundVolume(tidalPatRecordDraftBean.getMusicId() != 0?50f:0);
                                tidalPatRecordDraftBean.setFromCropVideo(true);
                                intent.putExtra("mTidalPatRecordDraftBean",tidalPatRecordDraftBean);
                                startActivity(intent);
                                if(!isEdit){
                                    isEdit = true;
                                    VideoCropActivity.this.finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void error(String errorStr) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void cropVideo(String videoPath, long cropStartTime, long cropRange, VideoTimeType speed, final HomeCallBack homeCallBack){
        if(TextUtils.isEmpty(videoPath) || cropStartTime < 0 || cropRange < 0){
            homeCallBack.error("data error");
            return ;
        }

        switch (speed){
            case SPEED_M4:
                cropRange /= 3;
                break;
            case SPEED_M2:
                cropRange /= 2;
                break;
            case SPEED_N1:
                break;
            case SPEED_P2:
                cropRange *= 2;
                break;
            case SPEED_P4:
                cropRange *= 3;
                break;
        }

        HeyhouVideo heyhouVideo = new HeyhouVideo();
        String filePath = Constant.RECORD_VIDEO_TEMP_PATH;
        String fileName = "record_" + System.currentTimeMillis() + ".mp4";
        File pathFile = new File(filePath);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }

        final CircleProgressView circleProgressView = findViewById(R.id.personal_show_loading_img);
        heyhouVideo.cutVideo(videoPath, cropStartTime, cropRange, speed.getValue(), filePath + File.separator + fileName, new VideoListener() {
            @Override
            public void onProgress(String outputPath, final int percentage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circleProgressView.setProgress(percentage);
                    }
                });

            }

            @Override
            public void onComplete(String outputPath) {
                homeCallBack.finish(outputPath);
            }

            @Override
            public void onError(String outputPath, String error) {
                homeCallBack.error(error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mLoadingView.getVisibility() == View.VISIBLE){
            return ;
        }
        if(isEdit){
            return ;
        }
        isEdit = true;
        editReleaseData();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isStop){
            mPlayView.play();
        }
        isStop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
        if(isEdit){
            return ;
        }
        mPlayView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void editReleaseData(){
        try{
            mPlayView.stop();
            mPlayView.destroyRender();
        }catch (Exception e){
            e.printStackTrace();
        }
        SpecialEffectsPlayManager.stopPlay();
    }

    private NewSpeedLevelControllerView.OnSpeedLevelChangeListener mSpeedLevelChangeListener = new NewSpeedLevelControllerView.OnSpeedLevelChangeListener() {
        @Override
        public void onChange(VideoTimeType level) {
            mVideoTimeType = level;
            mVideoCropViewBar.setSpeed(mVideoTimeType);
            mPlayView.setSpeed(mVideoTimeType);
        }
    };


    private RecordTimeType mRecordTimeType = RecordTimeType.RECORD_TIME_15;

    private void initRecordTimeSelectorView(){
        notifyRecordTime();

        findViewById(R.id.tidal_pat_video_crop_time_selector_15_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecordTimeType == RecordTimeType.RECORD_TIME_15){
                    return ;
                }
                refreshAllFromRecordTime(RecordTimeType.RECORD_TIME_15);


            }
        });

        findViewById(R.id.tidal_pat_video_crop_time_selector_120_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecordTimeType == RecordTimeType.RECORD_TIME_120){
                    return ;
                }
                refreshAllFromRecordTime(RecordTimeType.RECORD_TIME_120);
            }
        });
    }

    private void refreshAllFromRecordTime(RecordTimeType recordTimeType){
        mRecordTimeType = recordTimeType;
        mVideoTimeType = VideoTimeType.SPEED_N1;
        mSpeedLevelControllerView.setSpeedLevel(mVideoTimeType);
        mVideoCropViewBar.setSpeed(mVideoTimeType);
        mPlayView.setSpeed(mVideoTimeType);
        notifyRecordTime();
        switch (mRecordTimeType){
            case RECORD_TIME_15:
                mMaxRecordTime = VIDEO_RECORD_MAX_TIME;
                mSpeedLevelControllerView.setVisibility(View.VISIBLE);
                mVideoCropViewBar.setFinalMaxTime(mMaxRecordTime);
                break;
            case RECORD_TIME_120:
                mMaxRecordTime = VIDEO_RECORD_MAX_TIME_120;
                mSpeedLevelControllerView.setVisibility(View.GONE);
                mVideoCropViewBar.setFinalMaxTime(mMaxRecordTime);
                break;
        }
    }

    private void notifyRecordTime(){
        TextView textView15 = (TextView) findViewById(R.id.tidal_pat_video_crop_time_selector_15_txt);
        TextView textView120 = (TextView) findViewById(R.id.tidal_pat_video_crop_time_selector_120_txt);
        switch (mRecordTimeType){
            case RECORD_TIME_15:
                textView15.setTextColor(getResources().getColor(R.color.few_transparency));
                textView15.setBackgroundResource(R.drawable.bg_personal_show_video_speed_level_left);
                textView120.setTextColor(getResources().getColor(R.color.white));
                textView120.setBackgroundColor(getResources().getColor(R.color.transparency));
                break;
            case RECORD_TIME_120:
                textView15.setTextColor(getResources().getColor(R.color.white));
                textView15.setBackgroundColor(getResources().getColor(R.color.transparency));
                textView120.setTextColor(getResources().getColor(R.color.few_transparency));
                textView120.setBackgroundResource(R.drawable.bg_personal_show_video_speed_level_right);
                break;
        }

    }
}
