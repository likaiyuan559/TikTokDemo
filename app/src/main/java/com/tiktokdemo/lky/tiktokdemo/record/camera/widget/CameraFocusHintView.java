package com.tiktokdemo.lky.tiktokdemo.record.camera.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;


/**
 * Created by lky on 2017/7/12.
 */

public class CameraFocusHintView extends View {

    private final int HANDLER_WHAT = 101;

    private float mTouchX;
    private float mTouchY;


    public CameraFocusHintView(Context context) {
        super(context);
    }

    public CameraFocusHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraFocusHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mTouchX = 0f;
            mTouchY = 0f;
            invalidate();
        }
    };


    public void startFocus(MotionEvent event){
        mTouchX = event.getX();
        mTouchY = event.getY();
        invalidate();
        if(mHandler.hasMessages(HANDLER_WHAT)){
            mHandler.removeMessages(HANDLER_WHAT);
        }
        mHandler.sendEmptyMessageDelayed(HANDLER_WHAT,2000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mTouchX != 0 && mTouchY != 0){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(DensityUtils.dp2px(1));

            float dp10 = DensityUtils.dp2px(20);
            float dp2 = DensityUtils.dp2px(4);
            canvas.drawRect(mTouchX-dp10,mTouchY-dp10,mTouchX+dp10,mTouchY+dp10,paint);

            canvas.drawLine(mTouchX,mTouchY-dp10,mTouchX,mTouchY-dp10+dp2,paint);
            canvas.drawLine(mTouchX-dp10,mTouchY,mTouchX-dp10+dp2,mTouchY,paint);
            canvas.drawLine(mTouchX,mTouchY+dp10-dp2,mTouchX,mTouchY+dp10,paint);
            canvas.drawLine(mTouchX+dp10-dp2,mTouchY,mTouchX+dp10,mTouchY,paint);

        }
    }
}
