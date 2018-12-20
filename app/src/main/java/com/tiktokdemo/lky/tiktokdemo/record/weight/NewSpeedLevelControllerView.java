package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.R;
import com.heyhou.social.video.VideoTimeType;


/**
 * Created by lky on 2016/12/28.
 */

public class NewSpeedLevelControllerView extends LinearLayout {

//    private VideoTimeType mSpeedLevel = VideoTimeType.SPEED_N1;

    private String[] mLevelStrs;

    private SparseArray<TextView> mLevelTexts;

    private int mCurrentPosition = 2;

    private boolean isTouch = true;//是否允许点击

    private OnSpeedLevelChangeListener mOnSpeedLevelChangeListener;

    public NewSpeedLevelControllerView(Context context) {
        this(context, null);
    }

    public NewSpeedLevelControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewSpeedLevelControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLevelStrs = context.getResources().getStringArray(R.array.tidal_pat_speed);
        mLevelTexts = new SparseArray<>();
        initView(context);
    }

    public void setOnSpeedLevelChangeListener(OnSpeedLevelChangeListener onSpeedLevelChangeListener) {
        mOnSpeedLevelChangeListener = onSpeedLevelChangeListener;
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundResource(R.drawable.bg_personal_show_video_speed_level);

        mLevelTexts.clear();
        for (int i = 0; i < mLevelStrs.length; i++) {
            TextView textView = new TextView(context);
            textView.setTextSize(15);
            textView.setTextColor(0x80ffffff);
            textView.setGravity(Gravity.CENTER);
            textView.setText(mLevelStrs[i]);

            final int finalI = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isTouch || mCurrentPosition == finalI) {
                        return;
                    }
                    mCurrentPosition = finalI;
                    refreshViewState();
                    mOnSpeedLevelChangeListener.onChange(getSpeedLevel());
                }
            });

            LayoutParams textViewParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            textViewParams.gravity = Gravity.CENTER;
            this.addView(textView, textViewParams);
            mLevelTexts.append(i, textView);
        }
        refreshViewState();
    }

    public void refreshViewState() {
        for (int i = 0; i < mLevelTexts.size(); i++) {
            TextView textView = mLevelTexts.get(i);
            if (i == mCurrentPosition) {
                textView.setTextColor(0x80000000);
                if (i == 0) {
                    textView.setBackgroundResource(R.drawable.bg_personal_show_video_speed_level_left);
                } else if (i == mLevelTexts.size() - 1) {
                    textView.setBackgroundResource(R.drawable.bg_personal_show_video_speed_level_right);
                } else {
                    textView.setBackgroundColor(0xffface15);
                }
            } else {
                textView.setBackgroundColor(0x00000000);
                textView.setTextColor(0xffffffff);
            }
        }
    }

    public void setCanTouch(boolean touch) {
        isTouch = touch;
    }

    public VideoTimeType getSpeedLevel() {
        switch (mCurrentPosition) {
            case 0:
                return VideoTimeType.SPEED_M4;
            case 1:
                return VideoTimeType.SPEED_M2;
            case 2:
                return VideoTimeType.SPEED_N1;
            case 3:
                return VideoTimeType.SPEED_P2;
            case 4:
                return VideoTimeType.SPEED_P4;
        }
        return VideoTimeType.SPEED_N1;
    }

    public void setSpeedLevel(VideoTimeType videoTimeType){
        switch (videoTimeType) {
            case SPEED_M4:
                mCurrentPosition = 0;
                break;
            case SPEED_M2:
                mCurrentPosition = 1;
                break;
            case SPEED_N1:
                mCurrentPosition = 2;
                break;
            case SPEED_P2:
                mCurrentPosition = 3;
                break;
            case SPEED_P4:
                mCurrentPosition = 4;
                break;
        }
        refreshViewState();
    }

    //    SpeedLevelTxtClickListener mListener = new SpeedLevelTxtClickListener() {
//        @Override
//        public void onClick(View v, int position) {
//
//        }
//    };
//
//    private interface SpeedLevelTxtClickListener{
//        void onClick(View v,int position);
//    }
    public interface OnSpeedLevelChangeListener {
        void onChange(VideoTimeType level);
    }
}
