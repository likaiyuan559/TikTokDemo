package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by ydc on 2017/8/28.
 */
public class RecordStudioAdjustSeekBarLayout extends LinearLayout {
    private RecordStudioAdjustSeekBar studioAdjustSeekBar;
    private TextView tvhead;
    private Context mContext;
    private OnAdjustSeekBarScrollListener mOnAdjustSeekBarScrollListener;

    public RecordStudioAdjustSeekBarLayout(Context context) {
        this(context, null);
    }

    public RecordStudioAdjustSeekBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RecordStudioAdjustSeekBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void setmOnAdjustSeekBarScrollListener(OnAdjustSeekBarScrollListener mOnAdjustSeekBarScrollListener) {
        this.mOnAdjustSeekBarScrollListener = mOnAdjustSeekBarScrollListener;
    }

    public float getProgress(){
        return studioAdjustSeekBar.getProgress();
    }

    public float getMax(){
        return studioAdjustSeekBar.getMax();
    }

    public void setCanScroll(boolean canScroll){
        studioAdjustSeekBar.setCanScroll(canScroll);
    }

    public void setProgress(float progress){
        studioAdjustSeekBar.setProgress(progress);
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.textview_seekhead, this);
        layoutInflater.inflate(R.layout.seekbar_record, this);
        tvhead = (TextView) findViewById(R.id.tv_head_tip);
        tvhead.setVisibility(INVISIBLE);
        studioAdjustSeekBar = (RecordStudioAdjustSeekBar) findViewById(R.id.tidal_pat_record_volume_original4_sound_seek_bar);
//        studioAdjustSeekBar = new RecordStudioAdjustSeekBar(getContext());
//        tvhead = new TextView(getContext());
//        tvhead.setBackgroundResource(R.mipmap.bg_1);
//        LayoutParams paramsHead = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtils.dp2px(25));
//        LayoutParams paramsSeekBar = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(mContext, 22));
//        paramsHead.setMargins(DensityUtils.dp2px(mContext, -22), DensityUtils.dp2px(mContext, -25), 0, 0);
//        this.addView(tvhead, paramsHead);
//        this.addView(studioAdjustSeekBar, paramsSeekBar);
//        tvhead.setText("000");
//        tvhead.setPadding(0, DensityUtils.dp2px(mContext, 3), 0, 0);
//        tvhead.setTextColor(getResources().getColor(R.color.pure_black));
        studioAdjustSeekBar.setOnAdjustSeekBarScrollListener(new RecordStudioAdjustSeekBar.OnAdjustSeekBarScrollListener() {
            @Override
            public void onProgress(int progress) {
                tvhead.setVisibility(VISIBLE);
                tvhead.setText(progress + "");
                LinearLayout.LayoutParams params = (LayoutParams) tvhead.getLayoutParams();
                int width = studioAdjustSeekBar.getWidth();
                params.leftMargin = (int) (width * progress / studioAdjustSeekBar.getMax() - DensityUtils
                        .dp2px(20));
                tvhead.setLayoutParams(params);
                if (mOnAdjustSeekBarScrollListener != null) {
                    mOnAdjustSeekBarScrollListener.onProgress(RecordStudioAdjustSeekBarLayout.this,progress);
                }
            }

            @Override
            public void onEventUp(int progress) {
                tvhead.setVisibility(INVISIBLE);
                if (mOnAdjustSeekBarScrollListener != null) {
                    mOnAdjustSeekBarScrollListener.onEventUp(RecordStudioAdjustSeekBarLayout.this,progress);
                }
            }

            @Override
            public void onEventDown() {
                tvhead.setVisibility(VISIBLE);
                if (mOnAdjustSeekBarScrollListener != null) {
                    mOnAdjustSeekBarScrollListener.onEventDown(RecordStudioAdjustSeekBarLayout.this);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnAdjustSeekBarScrollListener {
        void onProgress(View view, int progress);

        void onEventUp(View view, int progress);

        void onEventDown(View view);
    }

}
