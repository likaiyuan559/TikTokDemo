package com.tiktokdemo.lky.tiktokdemo.record;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.Constant;
import com.tiktokdemo.lky.tiktokdemo.MainActivity;
import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.bean.MusicBean;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsParentType;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsProgressBean;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsType;
import com.tiktokdemo.lky.tiktokdemo.record.bean.TidalPatRecordDraftBean;
import com.tiktokdemo.lky.tiktokdemo.record.effects.adapter.TidalPatSpecialEffectsFilterAdapter;
import com.tiktokdemo.lky.tiktokdemo.record.effects.adapter.TidalPatSpecialEffectsFilterClickListener;
import com.tiktokdemo.lky.tiktokdemo.record.effects.adapter.TidalPatSpecialEffectsTimeAdapter;
import com.tiktokdemo.lky.tiktokdemo.record.helper.RecordTimeType;
import com.tiktokdemo.lky.tiktokdemo.record.manager.SpecialEffectsPlayManager;
import com.tiktokdemo.lky.tiktokdemo.record.presenter.VideoPlayContract;
import com.tiktokdemo.lky.tiktokdemo.record.presenter.VideoPlayPresenter;
import com.tiktokdemo.lky.tiktokdemo.record.weight.ScaleRoundRectView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.SpecialEffectsPlayView;
import com.tiktokdemo.lky.tiktokdemo.record.weight.SpecialEffectsSeekBar;
import com.tiktokdemo.lky.tiktokdemo.record.weight.TidalPatAdjustSeekBar;
import com.tiktokdemo.lky.tiktokdemo.utils.AnimatorUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.FileUtils;
import com.tiktokdemo.lky.tiktokdemo.utils.StringUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.ToastTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by lky on 2018/12/13
 */

public class VideoPlayActivity extends Activity implements View.OnClickListener, VideoPlayContract.View {


    private SpecialEffectsPlayView mVideoPlayView;

    private ImageView mVideoCoverImg;
    private View mParentUploadLayout;
    private ImageView mCutMusicImg;
    private View mCutAudioScaleLayout;
    private View mCutAudioLayout;
    private ScaleRoundRectView mCutAudioScaleRoundRectView;
    private TextView mCutAudioCurrentTxt,mCutAudioMaxTxt;
    private View mVolumeLayout;
    private TidalPatAdjustSeekBar mOriginalSeekBar;
    private TidalPatAdjustSeekBar mBackgroundSeekBar;

    private String mUploadFilePath;
    private ArrayList<String> mLocalFilePaths;
    private MusicBean mMusicBean;
    private TidalPatRecordDraftBean mTidalPatRecordDraftBean;
    private boolean isDraft;
    private boolean isFromEdit;
    private boolean isFromCrop;

    private View mLoadingView;

    private boolean isActivityStop;

    private VideoPlayPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);
        setContentView(R.layout.activity_tidal_pat_record_upload);
        mUploadFilePath = getIntent().getStringExtra("mUploadFilePath");
        isFromEdit = getIntent().getBooleanExtra("isFromEdit",false);
        isFromCrop = getIntent().getBooleanExtra("isFromCrop",false);
        mMusicBean = (MusicBean) getIntent().getSerializableExtra("MusicBean");
        mTidalPatRecordDraftBean = (TidalPatRecordDraftBean) getIntent().getSerializableExtra("mTidalPatRecordDraftBean");

        mLoadingView = findViewById(R.id.personal_show_record_video_loading_layout);
        mLoadingView.setOnClickListener(this);
        Animation set = AnimationUtils.loadAnimation(this, R.anim.anim_center_rotate);
        set.setInterpolator(new LinearInterpolator());
        (findViewById(R.id.personal_show_loading_img)).startAnimation(set);
        if(mTidalPatRecordDraftBean == null){
            mTidalPatRecordDraftBean = new TidalPatRecordDraftBean();
            mTidalPatRecordDraftBean.setVideoLocalUrl(mUploadFilePath);
        }else{
            if(!isFromEdit){
                isDraft = true;
            }else{
                mTidalPatRecordDraftBean.setSpecialEffectsParentType(SpecialEffectsParentType.FILTER);
                mTidalPatRecordDraftBean.setSpecialEffectsType(null);
                mTidalPatRecordDraftBean.setSpecialEffectsFiltersFromList(new ArrayList<SpecialEffectsProgressBean>());
            }
            if(mMusicBean == null){
                mMusicBean = new MusicBean();
            }
            mMusicBean.setUrl(mTidalPatRecordDraftBean.getMusicLocalUrl());
            mMusicBean.setName(mTidalPatRecordDraftBean.getMusicName());
            mMusicBean.setCover(mTidalPatRecordDraftBean.getMusicCover());
            mMusicBean.setMusicId(mTidalPatRecordDraftBean.getMusicId());
            mUploadFilePath = mTidalPatRecordDraftBean.getVideoLocalUrl();
            mLocalFilePaths = mTidalPatRecordDraftBean.getVideoLocalArrayFromList();
        }
        if(mMusicBean != null){
            mTidalPatRecordDraftBean.setMusicCover(mMusicBean.getCover());
            mTidalPatRecordDraftBean.setMusicId(mMusicBean.getMusicId());
            mTidalPatRecordDraftBean.setMusicName(mMusicBean.getName());
            mTidalPatRecordDraftBean.setMusicLocalUrl(mMusicBean.getUrl());
        }

        if(TextUtils.isEmpty(mUploadFilePath)){
            ToastTool.showShort(this,R.string.home_data_error);
            finish();
            return ;
        }
        if(!new File(mUploadFilePath).exists()){
            ToastTool.showShort(this,R.string.tidal_detail_upload_video_not_find);
            finish();
            return ;
        }
        mPresenter = new VideoPlayPresenter(this,mTidalPatRecordDraftBean,mMusicBean);
        initView();
    }

    private void initView() {
        mParentUploadLayout = findViewById(R.id.tidal_pat_record_video_upload_layout);
        mVideoPlayView = findViewById(R.id.tidal_pat_record_video_upload_pv);
        SpecialEffectsPlayManager.startPlay(mVideoPlayView);
        mCutMusicImg = findViewById(R.id.tidal_pat_record_video_cut_music_img);
        mCutAudioScaleLayout = findViewById(R.id.tidal_pat_record_cut_audio_scale_layout);
        mCutAudioLayout = findViewById(R.id.tidal_pat_record_cut_audio_layout);
        mCutAudioScaleRoundRectView = findViewById(R.id.tidal_pat_record_cut_audio_scale_view);
        mCutAudioScaleRoundRectView.setMax(50);//假数据
        mCutAudioScaleRoundRectView.setOnDragListener(mOnDragListener);
        mCutAudioCurrentTxt = findViewById(R.id.tidal_pat_record_cut_audio_current_time_txt);
        mCutAudioMaxTxt = findViewById(R.id.tidal_pat_record_cut_audio_max_time_txt);
        mVolumeLayout = findViewById(R.id.tidal_pat_record_volume_layout);
        findViewById(R.id.tidal_pat_record_volume_wrap_layout).setOnClickListener(this);
        mVolumeLayout.setOnClickListener(this);
        mOriginalSeekBar = findViewById(R.id.tidal_pat_record_volume_original_sound_seek_bar);
        mBackgroundSeekBar = findViewById(R.id.tidal_pat_record_volume_background_music_seek_bar);
        if(mMusicBean != null && mMusicBean.getMusicId() != 0){
            mOriginalSeekBar.setDefaultProgress(isDraft?mTidalPatRecordDraftBean.getOriginalVolume():0f);
        }else{
            mOriginalSeekBar.setDefaultProgress(isDraft?mTidalPatRecordDraftBean.getOriginalVolume():50f);
        }
        mBackgroundSeekBar.setDefaultProgress(isDraft?mTidalPatRecordDraftBean.getBackgroundVolume():50f);
        mOriginalSeekBar.setOnAdjustSeekBarScrollListener(mOnAdjustSeekBarOriginalListener);
        mBackgroundSeekBar.setOnAdjustSeekBarScrollListener(mOnAdjustSeekBarBackgroundListener);
        findViewById(R.id.tidal_pat_record_cut_audio_confirm_img).setOnClickListener(this);
        findViewById(R.id.tidal_pat_upload_video_volume_img).setOnClickListener(this);
        ImageView volumeImg = findViewById(R.id.tidal_pat_upload_video_volume_img);
        volumeImg.setImageResource(mTidalPatRecordDraftBean.getRecordTimeType() != RecordTimeType.RECORD_TIME_15?R.mipmap.btn_volume_pre:R.mipmap.chaopai_yinliang);
        volumeImg.setOnClickListener(this);
        mCutMusicImg.setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_voice_complete_btn).setOnClickListener(this);
        findViewById(R.id.tidal_pat_record_video_back_img).setOnClickListener(this);
        mVideoCoverImg = findViewById(R.id.tidal_pat_upload_video_cover_select_img);
        mVideoCoverImg.setOnClickListener(this);

        mVideoPlayView.setLooping(true);
        mVideoPlayView.setVideoPath(mUploadFilePath);
        mCutMusicImg.setImageResource((mMusicBean == null || mTidalPatRecordDraftBean == null
                || TextUtils.isEmpty(mMusicBean.getUrl()) || TextUtils.isEmpty(mTidalPatRecordDraftBean.getMusicLocalUrl()))
                || mTidalPatRecordDraftBean.getRecordTimeType() != RecordTimeType.RECORD_TIME_15
                ?R.mipmap.btn_cut_dis:R.mipmap.chaopai_luzhi_jianyinyue);
        ImageView specialEffectsImg = findViewById(R.id.tidal_pat_upload_video_special_effects_img);
        specialEffectsImg.setImageResource(mTidalPatRecordDraftBean.getRecordTimeType() == RecordTimeType.RECORD_TIME_15?R.mipmap.chaopai_teixao:R.mipmap.chaopai_teixao_nor);
        specialEffectsImg.setOnClickListener(this);

        initSpecialEffectsView();
    }

    /**
     * 原声音量大小的seekBar回调
     */
    private TidalPatAdjustSeekBar.OnAdjustSeekBarScrollListener mOnAdjustSeekBarOriginalListener = new TidalPatAdjustSeekBar.OnAdjustSeekBarScrollListener() {
        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onEventUp(int progress) {
            mTidalPatRecordDraftBean.setOriginalVolume(progress);
            mPresenter.combineVideo(mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
        }

        @Override
        public void onEventDown() {

        }
    };

    /**
     * 配乐音量大小的seekBar回调
     */
    private TidalPatAdjustSeekBar.OnAdjustSeekBarScrollListener mOnAdjustSeekBarBackgroundListener = new TidalPatAdjustSeekBar.OnAdjustSeekBarScrollListener() {
        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onEventUp(int progress) {
            mTidalPatRecordDraftBean.setBackgroundVolume(progress);
            mPresenter.combineVideo(mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
        }

        @Override
        public void onEventDown() {

        }
    };
    /**
     * 剪音乐拖动的回调
     */
    private ScaleRoundRectView.OnDragListener mOnDragListener = new ScaleRoundRectView.OnDragListener() {
        @Override
        public void onPositionChange(int position) {
            mCutAudioCurrentTxt.setText(StringUtil.generateTimeFromSymbol(position*1000L));
        }

        @Override
        public void onChangeUp(float position) {
            if(mTidalPatRecordDraftBean != null){
                mTidalPatRecordDraftBean.setCutMusicPosition((long) (position * 1000000L));
            }
            mPresenter.changeCutAudio(position);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        isActivityStop = true;
        if(mCutAudioLayout.getVisibility() == View.VISIBLE){
            mCutAudioLayout.setVisibility(View.GONE);
            mParentUploadLayout.setVisibility(View.VISIBLE);
        }
        if(mVolumeLayout.getVisibility() == View.VISIBLE){
            mVolumeLayout.setVisibility(View.GONE);
            mParentUploadLayout.setVisibility(View.VISIBLE);
        }
        try{
            mVideoPlayView.pause();
            mPresenter.pausePlayer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityStop = false;
        mPresenter.checkBGMPathUpdata();
        try{
            if(!isSpecialEffectsEditMode){
                mVideoPlayView.play();
            }else{
                mVideoPlayView.play();
                mPlayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayView.pause();
                    }
                },20);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 保存后返回mainActivity并释放资源
     * @param isJumpHome
     */
    public void clearCacheDataAndStartMainActivity(boolean isJumpHome){
        try{
            mVideoPlayView.stop();
            mVideoPlayView.destroyRender();
        }catch (Exception e){
            e.printStackTrace();
        }
        SpecialEffectsPlayManager.stopPlay();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isJumpHome",isJumpHome);
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        if(mPresenter.isCombining() || mPresenter.isAudioCuting() || mLoadingView.getVisibility() == View.VISIBLE){
            return ;
        }
        if(isSpecialEffectsEditMode){
            outSpecialEffectsMode(false);
            return ;
        }
        if(mCutAudioLayout.getVisibility() == View.VISIBLE){
            hideCutAudioLayout();
            return ;
        }
        if(mVolumeLayout.getVisibility() == View.VISIBLE){
            hideVolumeLayout();
            return ;
        }
        if(!isDraft){
            if(mTidalPatRecordDraftBean.isHasSpecialEffects()){
                        FileUtils.deleteFile(mUploadFilePath);
                        finish();
            }else{
                finish();
            }

        }else if(isFromCrop){
                    FileUtils.deleteFile(mUploadFilePath);
                    finish();
        } else{
                    finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mVideoPlayView.stop();
            mVideoPlayView.destroyRender();
        }catch (Exception e){
            e.printStackTrace();
        }
        SpecialEffectsPlayManager.stopPlay();
        clearSpecialEffectsAndOut(false,false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tidal_pat_record_voice_complete_btn://完成
                mTidalPatRecordDraftBean.setCreateTime(Calendar.getInstance().getTimeInMillis());
                if(mTidalPatRecordDraftBean.getRecordTimeType() != RecordTimeType.RECORD_TIME_15){
                    mTidalPatRecordDraftBean.setMusicCover("");
                    mTidalPatRecordDraftBean.setMusicLocalUrl("");
                    mTidalPatRecordDraftBean.setMusicName("");
                    mTidalPatRecordDraftBean.setMusicId(0);
                }
                mPresenter.complete();

                break;
            case R.id.tidal_pat_record_video_back_img://返回
                onBackPressed();
                break;
            case R.id.tidal_pat_record_video_cut_music_img://剪音乐
                if(mTidalPatRecordDraftBean.getRecordTimeType() != RecordTimeType.RECORD_TIME_15){
                    return ;
                }
                if ((mMusicBean == null || TextUtils.isEmpty(mMusicBean.getUrl()))
                        && TextUtils.isEmpty(mTidalPatRecordDraftBean.getMusicLocalUrl())){
                    return ;
                }
                showCutAudioLayout();
                break;
            case R.id.tidal_pat_record_cut_audio_confirm_img://剪音乐完成
                if(mPresenter.isAudioCuting()){
                    return ;
                }
                hideCutAudioLayout();
                break;
            case R.id.tidal_pat_upload_video_volume_img://音量
                if(mTidalPatRecordDraftBean.getRecordTimeType() != RecordTimeType.RECORD_TIME_15){
                    return ;
                }
                showVolumeLayout();
                break;
            case R.id.tidal_pat_record_volume_layout://音量布局
                hideVolumeLayout();
                break;
            case R.id.tidal_pat_upload_video_special_effects_img://特效
                if(mTidalPatRecordDraftBean.getRecordTimeType() != RecordTimeType.RECORD_TIME_15){
                    return ;
                }
                inSpecialEffectsMode();
                break;
            case R.id.personal_show_record_video_loading_layout:
                break;
        }
    }

    /**
     * 显示音量布局
     */
    public void showVolumeLayout(){
        mVolumeLayout.setVisibility(View.VISIBLE);
        mParentUploadLayout.setVisibility(View.GONE);
        if(mMusicBean != null && mMusicBean.getMusicId() != 0){
            if(!mBackgroundSeekBar.isCanScroll()){
                mBackgroundSeekBar.setCanScroll(true);
            }
        }else{
            if(mBackgroundSeekBar.isCanScroll()){
                mBackgroundSeekBar.setCanScroll(false);
            }
        }
    }

    /**
     * 隐藏音量布局
     */
    public void hideVolumeLayout(){
        mVolumeLayout.setVisibility(View.GONE);
        mParentUploadLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示剪音乐布局
     */
    public void showCutAudioLayout(){
        mCutAudioLayout.setVisibility(View.VISIBLE);
        mParentUploadLayout.setVisibility(View.GONE);
        mVideoPlayView.stop();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mCutAudioScaleLayout, View.TRANSLATION_Y,mCutAudioScaleLayout.getHeight()==0? DensityUtils
                .dp2px(200):mCutAudioScaleLayout.getHeight(),0f);
        objectAnimator.setDuration(300);
        objectAnimator.start();
        mPresenter.resetPlayer();
    }

    /**
     * 隐藏剪音乐布局
     */
    public void hideCutAudioLayout(){
        mCutAudioLayout.setVisibility(View.GONE);
        mParentUploadLayout.setVisibility(View.VISIBLE);
        mPresenter.releasePlayer();
        mPresenter.combineVideo(mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
    }

    /**
     * 保存视频
     * @param tidalPatRecordDraftBean
     */
    public void saveVideo(final TidalPatRecordDraftBean tidalPatRecordDraftBean) {
        if(TextUtils.isEmpty(tidalPatRecordDraftBean.getVideoName())){
            FileUtils.copyFile(mTidalPatRecordDraftBean.getVideoLocalUrl(),Constant.RECORD_VIDEO_PATH,System.currentTimeMillis() + ".mp4");
            ToastTool.showShort(this,"视频已保存至：" + Constant.RECORD_VIDEO_PATH + File.separator + System.currentTimeMillis() + ".mp4");
            clearCacheDataAndStartMainActivity(true);
        }
    }


    /******************************************************* 特效 *******************************************************/

    private RecyclerView mSpecialEffectsSelectorRV;
    private TidalPatSpecialEffectsFilterAdapter mTidalPatSpecialEffectsFilterAdapter;
    private TidalPatSpecialEffectsTimeAdapter mTidalPatSpecialEffectsTimeAdapter;

    private SpecialEffectsSeekBar mSpecialEffectsSeekBar;

    private View mVideoPlayLayout;
    private View mVideoPlayBtn;

    private TextView mSEFilterTxt;
    private TextView mSETimeTxt;
    private TextView mSEPlayEndTxt;
    private TextView mSERemoveTxt;


    private Handler mPlayHandler = new Handler();

    /**
     * 初始化特效布局
     */
    private void initSpecialEffectsView(){
        SpecialEffectsPlayManager.getInstance().setSpecialEffectsFilters(mTidalPatRecordDraftBean.getSpecialEffectsFiltersFromList());
        SpecialEffectsPlayManager.getInstance().setCurrentSpecialEffectsFilterType(mTidalPatRecordDraftBean.getSpecialEffectsType());
        if(mTidalPatRecordDraftBean.getSpecialEffectsParentType() != null){
            mPresenter.setSpecialEffectsParentType(mTidalPatRecordDraftBean.getSpecialEffectsParentType());
        }
        if(mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack){
            mPresenter.setSpecialEffectsTimeBackVideoPath(mTidalPatRecordDraftBean.getVideoLocalUrl());
        }

        mSpecialEffectsSelectorRV = findViewById(R.id.tidal_pat_upload_se_recycler_view);
        mSpecialEffectsSelectorRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mTidalPatSpecialEffectsFilterAdapter = new TidalPatSpecialEffectsFilterAdapter();
        mTidalPatSpecialEffectsTimeAdapter = new TidalPatSpecialEffectsTimeAdapter();
        mTidalPatSpecialEffectsFilterAdapter.setTidalPatSpecialEffectsFilterClickListener(mTidalPatSpecialEffectsFilterClickListener);
        mTidalPatSpecialEffectsTimeAdapter.setTidalPatSpecialEffectsFilterClickListener(mTidalPatSpecialEffectsFilterClickListener);


        mSpecialEffectsSeekBar = findViewById(R.id.tidal_pat_upload_se_seek_bar);
        mVideoPlayLayout = findViewById(R.id.tidal_pat_record_video_upload_pv_layout);
        mVideoPlayBtn = findViewById(R.id.tidal_pat_record_video_upload_pv_btn);

        mSEFilterTxt = findViewById(R.id.tidal_pat_upload_se_bottom_filter_txt);
        mSETimeTxt = findViewById(R.id.tidal_pat_upload_se_bottom_time_txt);
        SpecialEffectsViewClickListener specialEffectsViewClickListener = new SpecialEffectsViewClickListener();
        mSEFilterTxt.setOnClickListener(specialEffectsViewClickListener);
        mSETimeTxt.setOnClickListener(specialEffectsViewClickListener);
        findViewById(R.id.tidal_pat_upload_se_save_txt).setOnClickListener(specialEffectsViewClickListener);
        findViewById(R.id.tidal_pat_upload_se_cancel_txt).setOnClickListener(specialEffectsViewClickListener);
        mSEPlayEndTxt = findViewById(R.id.tidal_pat_upload_se_time_current_txt);
        mSERemoveTxt = findViewById(R.id.tidal_pat_upload_se_remove_txt);

        mSERemoveTxt.setOnClickListener(specialEffectsViewClickListener);
        mVideoPlayView.setOnClickListener(specialEffectsViewClickListener);
        mSpecialEffectsSeekBar.setOnAdjustSeekBarScrollListener(new SpecialEffectsSeekBar.OnAdjustSeekBarScrollListener() {
            @Override
            public void onProgress(int progress) {
                if (mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack) {
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol((long) (mSpecialEffectsSeekBar.getMax()-progress)));
                    mVideoPlayView.seekTo((long) ((mSpecialEffectsSeekBar.getMax()-progress) * 1000L));
                }else{
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(progress));
                    mVideoPlayView.seekTo(progress * 1000);
                }
            }

            @Override
            public void onEventUp(int progress) {
                if (mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack) {
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol((long) (mSpecialEffectsSeekBar.getMax()-progress)));
                    long position = (long) ((mSpecialEffectsSeekBar.getMax()-progress) * 1000L);

                    if(position>=mVideoPlayView.getDuration()){
                        mVideoPlayView.seekTo(0);
                        mSpecialEffectsSeekBar.setProgress(0);
                    }else{
                        mVideoPlayView.seekTo(position);
                    }
                }else{
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(progress));
                    long position = progress * 1000;
                    if(position>=mVideoPlayView.getDuration()){
                        mVideoPlayView.seekTo(0);
                        mSpecialEffectsSeekBar.setProgress(0);
                    }else{
                        mVideoPlayView.seekTo(position);
                    }
                }
            }

            @Override
            public void onEventDown() {
                mVideoPlayView.pause();
            }
        });

        mVideoPlayView.setSpecialEffectsPlayViewListener(new SpecialEffectsPlayView.SpecialEffectsPlayViewListener() {
            @Override
            public void onPrepare(long timeDuration) {
                if (mSpecialEffectsSeekBar.getMax() == 0) {
                    mSpecialEffectsSeekBar.setMax(timeDuration);
                    ((TextView) findViewById(R.id.tidal_pat_upload_se_time_end_txt)).setText(StringUtil.generateTimeFromSymbol(timeDuration));
                }
            }

            @Override
            public void onPlayTime(long time) {
                if(!isSpecialEffectsEditMode){
                    return ;
                }
                if (mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack) {
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(time));
                    mSpecialEffectsSeekBar.setProgress(mSpecialEffectsSeekBar.getMax()-time);
                }else{
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(time));
                    mSpecialEffectsSeekBar.setProgress(time);
                }
                if (mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.FILTER && !isFilterTouch){
                    SpecialEffectsType specialEffectsType = SpecialEffectsPlayManager.getInstance().getTypeFromTime(time);
                    if(specialEffectsType != null){
                        mVideoPlayView.setFilter(specialEffectsType.getFilter());
                    }
                }

                if(isFilterTouch && isFilterStart){
                    isFilterStart = false;
                    SpecialEffectsProgressBean tempBean = mSpecialEffectsSeekBar.getOperationFilter();
                    if(tempBean != null){
                        if(Math.abs(tempBean.getTimeStart() - time) > 200){
                            tempBean.setTimeStart(time);
                        }else if(tempBean.getTimeStart() < 200){
                            tempBean.setTimeStart(0);
                        }
                    }
                }
            }

            @Override
            public void onPause() {
                if(isSpecialEffectsEditMode){
                    mVideoPlayBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlay() {
                mVideoPlayBtn.setVisibility(View.GONE);
            }

            @Override
            public void onStop() {
                if(isSpecialEffectsEditMode){
                    mVideoPlayBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                if(isSpecialEffectsEditMode){
                    mVideoPlayBtn.setVisibility(View.VISIBLE);
                    cancelItemTouch(true);
                    mPresenter.changeSpecialEffectsMode(mPresenter.getSpecialEffectsParentType());
                }
            }
        });
    }

    @Override
    public void showToast(String msg) {
        ToastTool.showShort(AppUtil.getApplicationContext(),msg);
    }

    @Override
    public void showLoadingView(boolean isShow, int loadingTxtRes) {
        if(mLoadingView != null){
            mLoadingView.setVisibility(isShow?View.VISIBLE:View.GONE);
            if(loadingTxtRes != 0){
                TextView textView = findViewById(R.id.tidal_pat_record_video_loading_txt);
                textView.setText(loadingTxtRes);
            }
        }
    }


    @Override
    public void checkMusicEmpty() {
        mCutMusicImg.setImageResource(R.mipmap.btn_cut_dis);
    }

    @Override
    public void checkMusicLength(int duration) {
        mCutAudioScaleRoundRectView.setMax((int) (duration/1000f));
        mCutAudioScaleRoundRectView.setProgress(0);
        mCutAudioCurrentTxt.setText(StringUtil.generateTimeFromSymbol(0));
        mCutAudioMaxTxt.setText(StringUtil.generateTimeFromSymbol(duration));
    }

    @Override
    public void combineVideoStart() {
        mVideoPlayView.stop();
    }

    @Override
    public void combineVideoFinish(boolean isLooping,String path) {
        mVideoPlayView.setLooping(isLooping);
        if(!isActivityStop){
            mVideoPlayView.setVideoPath(path);
        }else{
            mVideoPlayView.setVideoPathNotPlay(path);
        }
    }

    @Override
    public void combineVideoError(String path) {
        mVideoPlayView.setLooping(true);
        mVideoPlayView.setVideoPath(path);
    }

    @Override
    public void inTimeBackState(String path){
        mVideoPlayView.stop();
        mVideoPlayView.setVideoPathNotPlay(path);
        mTidalPatSpecialEffectsTimeAdapter.setCurrentType(SpecialEffectsType.TimeBack);
        ArrayList<SpecialEffectsProgressBean> specialEffectsProgressBeen = new ArrayList<>();
        SpecialEffectsProgressBean specialEffectsProgressBean = new SpecialEffectsProgressBean();
        specialEffectsProgressBean.setTimeEnd((long) mSpecialEffectsSeekBar.getMax());
        specialEffectsProgressBean.setTimeStart(0);
        specialEffectsProgressBean.setType(SpecialEffectsType.TimeBack);
        specialEffectsProgressBean.setShowColor(0xFF5543D0);
        specialEffectsProgressBeen.add(specialEffectsProgressBean);
        mSpecialEffectsSeekBar.setSpecialEffectsProgressBeen(specialEffectsProgressBeen);
        mSpecialEffectsSeekBar.setProgress(mSpecialEffectsSeekBar.getMax());
        mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(0));
    }

    @Override
    public void inTimeNotState(String path){
        mTidalPatSpecialEffectsTimeAdapter.setCurrentType(SpecialEffectsType.Default);
        ArrayList<SpecialEffectsProgressBean> specialEffectsProgressBeen = new ArrayList<>();
        mSpecialEffectsSeekBar.setSpecialEffectsProgressBeen(specialEffectsProgressBeen);
        mVideoPlayView.stop();
        mVideoPlayView.setVideoPathNotPlay(path);
        mSpecialEffectsSeekBar.setProgress(0);
        mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(0));
    }

    @Override
    public void resetSpecialEffectsSeekBar(boolean isMax) {
        mSpecialEffectsSeekBar.setProgress(isMax?mSpecialEffectsSeekBar.getMax():0);
    }

    @Override
    public void changeSpecialEffectsModeFilterFinish(String path) {
        mSpecialEffectsSeekBar.setSpecialEffectsProgressBeen(SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters());
        mVideoPlayView.stop();
        mVideoPlayView.setVideoPathNotPlay(path);
    }

    @Override
    public void changeSpecialEffectsModeFinish(String path) {
        mVideoPlayView.setLooping(false);
        mPlayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(0));
            }
        },100);
    }

    @Override
    public void combineDidTimeBackFinish() {
        ArrayList<SpecialEffectsProgressBean>
                specialEffectsProgressBeen = new ArrayList<>();
        SpecialEffectsProgressBean specialEffectsProgressBean = new SpecialEffectsProgressBean();
        specialEffectsProgressBean.setTimeEnd((long) mSpecialEffectsSeekBar.getMax());
        specialEffectsProgressBean.setTimeStart(0);
        specialEffectsProgressBean.setType(SpecialEffectsType.TimeBack);
        specialEffectsProgressBean.setShowColor(0xFF5543D0);
        specialEffectsProgressBeen.add(specialEffectsProgressBean);
        mSpecialEffectsSeekBar.setSpecialEffectsProgressBeen(specialEffectsProgressBeen);
    }

    @Override
    public void completeFinish() {
        saveVideo(mTidalPatRecordDraftBean);
    }

    class SpecialEffectsViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(isFilterTouch){
                return ;
            }
            switch (v.getId()){
                case R.id.tidal_pat_upload_se_bottom_filter_txt:
                    if(mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.FILTER){
                        return;
                    }
                    mPresenter.changeSpecialEffectsMode(SpecialEffectsParentType.FILTER);
                    selectedFilterMode();
                    break;
                case R.id.tidal_pat_upload_se_bottom_time_txt:
                    if(mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME){
                        return ;
                    }
                    mPresenter.changeSpecialEffectsMode(SpecialEffectsParentType.TIME);
                    selectedTimeMode();
                    break;
                case R.id.tidal_pat_upload_se_save_txt:
                    outSpecialEffectsMode(true);
                    break;
                case R.id.tidal_pat_upload_se_cancel_txt:
                    outSpecialEffectsMode(false);
                    break;
                case R.id.tidal_pat_record_video_upload_pv:
                    if(isSpecialEffectsEditMode){
                        mVideoPlayView.changeState();
                    }
                    break;
                case R.id.tidal_pat_upload_se_remove_txt:
                    SpecialEffectsPlayManager.getInstance().removeLastFilter();
                    mSERemoveTxt.setVisibility(SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().size() <= 0?
                            View.GONE: View.VISIBLE);
                    long lastTime = 0;
                    if(SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().size() > 0){
                        lastTime = SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().get(SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().size()-1).getTimeEnd();
                    }
                    mSpecialEffectsSeekBar.setProgress(lastTime);
                    mSpecialEffectsSeekBar.clearOperationFilter();
                    mVideoPlayView.seekTo(lastTime * 1000L);
                    mSEPlayEndTxt.setText(StringUtil.generateTimeFromSymbol(lastTime));
                    mSpecialEffectsSeekBar.setSpecialEffectsProgressBeen(SpecialEffectsPlayManager.getInstance().getFiltrationSpecialEffectsFilters());
                    break;
            }
        }
    }

    /**
     * 选择灵魂出窍模式
     */
    private void selectedFilterMode(){
        final View seekLayout = findViewById(R.id.tidal_pat_upload_se_seek_layout);
        final TextView hintTxt = findViewById(R.id.tidal_pat_upload_se_hint_txt);

        AnimatorUtils.viewAlphaAnimator(findViewById(R.id.tidal_pat_upload_se_bottom_time_view),false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(seekLayout,false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(hintTxt,false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(mSpecialEffectsSelectorRV, false, 200, new AnimatorUtils.FreeAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSpecialEffectsSelectorRV.setAdapter(mTidalPatSpecialEffectsFilterAdapter);
                hintTxt.setText(R.string.tidal_pat_upload_position_selected_hint);
                AnimatorUtils.viewAlphaAnimator(findViewById(R.id.tidal_pat_upload_se_bottom_filter_view),true,200,null).start();
                AnimatorUtils.viewAlphaAnimator(seekLayout,true,200,null).start();
                AnimatorUtils.viewAlphaAnimator(hintTxt,true,200,null).start();
                AnimatorUtils.viewAlphaAnimator(mSERemoveTxt,SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().size() > 0,
                        SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().size() <= 10?0:200,null).start();
                AnimatorUtils.viewAlphaAnimator(mSpecialEffectsSelectorRV,true,200,null).start();
            }
        }).start();

        AnimatorUtils.textViewColorChangeAnimator(mSETimeTxt, getResources().getColor(R.color.white), getResources().getColor(R.color.few_60_transparency_white), 200, new AnimatorUtils.FreeAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorUtils.textViewColorChangeAnimator(mSEFilterTxt,getResources().getColor(R.color.few_60_transparency_white),getResources().getColor(R.color.white), 200,null).start();
            }
        }).start();
    }

    /**
     * 选择时光倒流模式
     */
    private void selectedTimeMode(){

        final View seekLayout = findViewById(R.id.tidal_pat_upload_se_seek_layout);
        final TextView hintTxt = (TextView) findViewById(R.id.tidal_pat_upload_se_hint_txt);

        AnimatorUtils.viewAlphaAnimator(findViewById(R.id.tidal_pat_upload_se_bottom_filter_view),false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(seekLayout,false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(hintTxt,false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(mSERemoveTxt,false,200,null).start();
        AnimatorUtils.viewAlphaAnimator(mSpecialEffectsSelectorRV, false, 200, new AnimatorUtils.FreeAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSpecialEffectsSelectorRV.setAdapter(mTidalPatSpecialEffectsTimeAdapter);
                mSERemoveTxt.setVisibility(View.GONE);
                hintTxt.setText(R.string.tidal_pat_upload_time_back_hint);
                AnimatorUtils.viewAlphaAnimator(findViewById(R.id.tidal_pat_upload_se_bottom_time_view),true,200,null).start();
                AnimatorUtils.viewAlphaAnimator(seekLayout,true,200,null).start();
                AnimatorUtils.viewAlphaAnimator(hintTxt,true,200,null).start();
                AnimatorUtils.viewAlphaAnimator(mSpecialEffectsSelectorRV,true,200,null).start();
            }
        }).start();

        AnimatorUtils.textViewColorChangeAnimator(mSEFilterTxt, getResources().getColor(R.color.white), getResources().getColor(R.color.few_60_transparency_white), 200, new AnimatorUtils.FreeAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorUtils.textViewColorChangeAnimator(mSETimeTxt,getResources().getColor(R.color.few_60_transparency_white),getResources().getColor(R.color.white), 200,null).start();
            }
        }).start();
    }

    private int mPlayViewWidth;
    private int mPlayViewHeight;
    private boolean isSpecialEffectsEditMode;//是否进入特效编辑模式
    private boolean isFilterTouch;//正在触摸特效
    private boolean isFilterStart;//滤镜特效开始的标识，Touch时为true，如果进度条走过将会置为false
    private long mFilterTouchTime;

    /**
     * 进入特效编辑模式
     */
    private void inSpecialEffectsMode(){
        mPresenter.inSpecialEffectsModeReady(mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
        if(mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME){
            selectedTimeMode();
        }else{
            selectedFilterMode();
            mSERemoveTxt.setVisibility(SpecialEffectsPlayManager.getInstance().getSpecialEffectsFilters().size() <= 0?
                    View.GONE: View.VISIBLE);
        }
        isSpecialEffectsEditMode = true;
        AnimatorSet animatorSet = new AnimatorSet();

        Animator animator = AnimatorUtils.viewAlphaAnimator(mParentUploadLayout, false, 200, new AnimatorUtils.FreeAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParentUploadLayout.setVisibility(View.GONE);
                mSpecialEffectsSeekBar.setProgress((mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME
                        && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack)?mSpecialEffectsSeekBar.getMax():0);
            }
        });

        final View titleLayout  = findViewById(R.id.tidal_pat_upload_se_title_layout);
        final View bottomLayout = findViewById(R.id.tidal_pat_upload_se_bottom_layout);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f,0f);
        valueAnimator.setDuration(400);
        final LinearLayout.LayoutParams titleLayoutParams = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
        final LinearLayout.LayoutParams bottomLayoutParams = (LinearLayout.LayoutParams) bottomLayout.getLayoutParams();
        final LinearLayout.LayoutParams playViewParams = (LinearLayout.LayoutParams) mVideoPlayLayout.getLayoutParams();
        mPlayViewWidth = mVideoPlayLayout.getWidth();
        mPlayViewHeight = mVideoPlayLayout.getHeight();
        final int minPlayViewHeight = mPlayViewHeight - DensityUtils.dp2px(57) - DensityUtils.dp2px(200);
        final float playViewScale = mPlayViewWidth/(float)mPlayViewHeight;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                titleLayoutParams.topMargin = (int) (-DensityUtils.dp2px(57) * (float)animation.getAnimatedValue());
                titleLayout.setLayoutParams(titleLayoutParams);
                bottomLayoutParams.bottomMargin = (int) (-DensityUtils.dp2px(200) * (float)animation.getAnimatedValue());
                bottomLayout.setLayoutParams(bottomLayoutParams);
                playViewParams.width = (int) ((minPlayViewHeight + ((mPlayViewHeight - minPlayViewHeight)* (float)animation.getAnimatedValue())) * playViewScale);
                playViewParams.bottomMargin = (int) (DensityUtils.dp2px(18) * (1f-(float)animation.getAnimatedValue()));
                mVideoPlayLayout.setLayoutParams(playViewParams);
            }
        });
        animatorSet.playSequentially(animator,valueAnimator);
        animatorSet.start();
    }

    /**
     * 推出特效编辑模式
     * @param isSave
     */
    private void outSpecialEffectsMode(boolean isSave){
        mVideoPlayView.setLooping(true);
        if(isSave){
            saveSpecialEffectsAndOut();
        }else{
            clearSpecialEffectsAndOut(true,true);
        }
    }

    /**
     * 保存特效并退出
     */
    private void saveSpecialEffectsAndOut(){
        mVideoPlayView.stop();
        if(mPresenter.getSpecialEffectsParentType() == SpecialEffectsParentType.TIME
                && SpecialEffectsPlayManager.getInstance().getCurrentSpecialEffectsFilterType() == SpecialEffectsType.TimeBack
                && !TextUtils.isEmpty(mPresenter.getSpecialEffectsTimeBackVideoPath())){
            mVideoPlayView.setVideoPath(
                    TextUtils.isEmpty(mPresenter.getSpecialEffectsTimeBackVideoPath())?mTidalPatRecordDraftBean.getVideoLocalUrl():mPresenter.getSpecialEffectsTimeBackVideoPath());
        }else{
            mPresenter.combineVideo(mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
        }
        outSpecialEffectsAnimator();
    }

    /**
     * 清除特效并退出
     * @param isOut 退出
     * @param isCombineVideo 是否合成视频
     */
    private void clearSpecialEffectsAndOut(boolean isOut,boolean isCombineVideo){
        if(mTidalPatSpecialEffectsTimeAdapter == null){
            return ;
        }
        mTidalPatRecordDraftBean.setHasSpecialEffects(false);
        mTidalPatSpecialEffectsTimeAdapter.setCurrentType(SpecialEffectsType.Default);
        SpecialEffectsPlayManager.getInstance().setCurrentSpecialEffectsFilterType(SpecialEffectsType.Default);
        mSpecialEffectsSeekBar.clearData();
        SpecialEffectsPlayManager.getInstance().clearFilters();
        if(isOut){
            outSpecialEffectsAnimator();
        }
        if(isCombineVideo){
            mPresenter.combineVideo(mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
        }
    }

    /**
     * 动画
     */
    private void outSpecialEffectsAnimator(){
        mVideoPlayBtn.setVisibility(View.GONE);
        isSpecialEffectsEditMode = false;
        AnimatorSet animatorSet = new AnimatorSet();
        Animator animator = AnimatorUtils.viewAlphaAnimator(mParentUploadLayout,true,200,null);


        final View titleLayout  = findViewById(R.id.tidal_pat_upload_se_title_layout);
        final View bottomLayout = findViewById(R.id.tidal_pat_upload_se_bottom_layout);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(400);
        final LinearLayout.LayoutParams titleLayoutParams = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
        final LinearLayout.LayoutParams bottoLayoutmParams = (LinearLayout.LayoutParams) bottomLayout.getLayoutParams();
        final LinearLayout.LayoutParams playViewParams = (LinearLayout.LayoutParams) mVideoPlayLayout.getLayoutParams();
        final int minPlayViewHeight = mPlayViewHeight - DensityUtils.dp2px(57) - DensityUtils.dp2px(200);
        final float playViewScale = mPlayViewWidth/(float)mPlayViewHeight;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                titleLayoutParams.topMargin = (int) (-DensityUtils.dp2px(57) * (float)animation.getAnimatedValue());
                titleLayout.setLayoutParams(titleLayoutParams);
                bottoLayoutmParams.bottomMargin = (int) (-DensityUtils.dp2px(200) * (float)animation.getAnimatedValue());
                bottomLayout.setLayoutParams(bottoLayoutmParams);
                playViewParams.width = (int) ((minPlayViewHeight + ((mPlayViewHeight - minPlayViewHeight)* (float)animation.getAnimatedValue())) * playViewScale);
                playViewParams.bottomMargin = (int) (DensityUtils.dp2px(18) * (1f-(float)animation.getAnimatedValue()));
                mVideoPlayLayout.setLayoutParams(playViewParams);

            }
        });
        animatorSet.playSequentially(valueAnimator,animator);
        animatorSet.start();
    }

    private void cancelItemTouch(boolean isFinish){
        if(!isFilterTouch){
            return ;
        }
        isFilterTouch = false;
        mVideoPlayView.setFilter(SpecialEffectsType.Default.getFilter());
        SpecialEffectsProgressBean specialEffectsProgressBean = mSpecialEffectsSeekBar.setOperationFilter(false,null);
        if(isFinish){
            if(System.currentTimeMillis() - mFilterTouchTime > 50){
                specialEffectsProgressBean.setTimeEnd((long) mSpecialEffectsSeekBar.getMax());
            }
        }
        SpecialEffectsPlayManager.getInstance().addSpecialEffectsFilter(specialEffectsProgressBean);
        mSpecialEffectsSeekBar.setSpecialEffectsProgressBeen(SpecialEffectsPlayManager.getInstance().getFiltrationSpecialEffectsFilters());
        mSERemoveTxt.setAlpha(1.0f);
        mSERemoveTxt.setVisibility(View.VISIBLE);
    }

    /**
     * 灵魂出窍的按钮回调
     */
    TidalPatSpecialEffectsFilterClickListener mTidalPatSpecialEffectsFilterClickListener = new TidalPatSpecialEffectsFilterClickListener() {
        @Override
        public void onItemTouchDown(int position, SpecialEffectsType specialEffectsType) {
            if(specialEffectsType == SpecialEffectsType.SoulOut){
                isFilterTouch = true;
                isFilterStart = true;
                mFilterTouchTime = System.currentTimeMillis();
                SpecialEffectsProgressBean specialEffectsProgressBean = new SpecialEffectsProgressBean();
                specialEffectsProgressBean.setType(SpecialEffectsType.SoulOut);
                specialEffectsProgressBean.setShowColor(0xFFEB4293);
                mSpecialEffectsSeekBar.setOperationFilter(true,specialEffectsProgressBean);
                mVideoPlayView.setFilter(specialEffectsType.getFilter());
                mVideoPlayView.play();
            }
        }

        @Override
        public void onItemTouchUp(int position) {
            if(isFilterTouch){
                mVideoPlayView.pause();
                isFilterStart = false;
                cancelItemTouch(false);
            }
        }

        @Override
        public void onItemStateChange(int position, boolean isSelected, SpecialEffectsType specialEffectsType) {
            mPresenter.changeSpecialEffects(specialEffectsType,mOriginalSeekBar.getProgress()/mOriginalSeekBar.getMax(),mBackgroundSeekBar.getProgress()/mBackgroundSeekBar.getMax());
        }
    };


}
