package com.tiktokdemo.lky.tiktokdemo.record.camera.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.camera.camera.CameraEngine;


/**
 * Created by lky on 2017/4/18.
 * 用于对焦提示的layout
 */

public class MagicCameraLayout extends FrameLayout {
    private MagicCameraView mMagicCameraView;
    private CameraFocusHintView mCameraFocusHintView;

    public MagicCameraLayout(Context context) {
        this(context,null);
    }

    public MagicCameraLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MagicCameraLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
//        mCameraFocusHintView = new CameraFocusHintView(context);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        addView(mCameraFocusHintView,layoutParams);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMagicCameraView = (MagicCameraView) findViewById(R.id.tidal_pat_record_camera_view);
        mCameraFocusHintView = (CameraFocusHintView) findViewById(R.id.tidal_pat_record_focus_view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!CameraEngine.getCameraInfo().isFront){
            if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN){
                if(mMagicCameraView != null){
                    mMagicCameraView.handleFocusMetering(event);
                }
                mCameraFocusHintView.startFocus(event);
            }
        }

        return super.onTouchEvent(event);


    }
}
