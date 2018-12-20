package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by lky on 2017/4/28.
 */

public class CircleRecordView extends View {

    private final int ANIMATOR_DURATION = 200;
    private final int ANIMATOR_DURATION_CIRCLE = 400;
    private final int HANDLER_WHAT = 101;
    private final int HANDLER_TIME = 40;


    private int mCircleStatusSize;
    private int mCircleDynamicSize;
    private float mCircleImageSize;
    private int mCircleBackgroundStartColor;
    private int mCircleBackgroundEndColor;
    private int mCircleNotTouchStartColor;
    private int mCircleNotTouchEndColor;
//    private int mBackgroundColor;


    private float mCircleSize;
    private float mCircleImageDrawSize;

    private Bitmap mImageBitmap;

    private OnRecordChangeListener mOnRecordChangeListener;

    private boolean isTouching;
    private boolean isCanTouch = true;

    private boolean isCircleAlignParentBottom;
    private int mCircleMarginBottom;
    private float mCircleMaxStrokeWidth = DensityUtils.dp2px(20);
    private float mCircleMinStrokeWidth = DensityUtils.dp2px(10);

    private float mCircleCurrentStrokeWidth = mCircleMinStrokeWidth;

    private int mCenterX;
    private int mCenterY;

    public CircleRecordView(Context context) {
        this(context,null);
    }

    public CircleRecordView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleRecordView,defStyleAttr,0);
        mCircleStatusSize = array.getDimensionPixelSize(R.styleable.CircleRecordView_CircleStatusSize, DensityUtils.dp2px(80));
        mCircleDynamicSize = array.getDimensionPixelSize(R.styleable.CircleRecordView_CircleDynamicSize,DensityUtils.dp2px(95));
        mCircleImageSize = array.getDimensionPixelSize(R.styleable.CircleRecordView_CircleImageSize,DensityUtils.dp2px(80));
        mCircleBackgroundStartColor = array.getColor(R.styleable.CircleRecordView_CircleBackgroundStartColor,0xFFFF2665);
        mCircleBackgroundEndColor = array.getColor(R.styleable.CircleRecordView_CircleBackgroundEndColor,0xFFFF5735);
        mCircleNotTouchStartColor = array.getColor(R.styleable.CircleRecordView_CircleNotTouchStartColor,0x80FF2665);
        mCircleNotTouchEndColor = array.getColor(R.styleable.CircleRecordView_CircleNotTouchEndColor,0x80FF5735);
        isCircleAlignParentBottom = array.getBoolean(R.styleable.CircleRecordView_CircleAlignParentBottom,true);
        mCircleMarginBottom = array.getDimensionPixelSize(R.styleable.CircleRecordView_CircleMarginBottom,DensityUtils.dp2px(15));
        mCircleMaxStrokeWidth = array.getDimensionPixelSize(R.styleable.CircleRecordView_CircleMaxStrokeWidth,DensityUtils.dp2px(20));
        mCircleMinStrokeWidth = array.getDimensionPixelSize(R.styleable.CircleRecordView_CircleMinStrokeWidth,DensityUtils.dp2px(10));

        mImageBitmap = BitmapFactory.decodeResource(context.getResources(),array.getResourceId(R.styleable.CircleRecordView_CircleImageRes,R.mipmap.shipinicon));
        mCircleImageDrawSize = mCircleImageSize;
        mCircleSize = mCircleStatusSize;
        mCircleCurrentStrokeWidth = mCircleSize/2;
//        mBackgroundColor = mCircleBackgroundColor;
    }


    public void setOnRecordChangeListener(OnRecordChangeListener onRecordChangeListener) {
        mOnRecordChangeListener = onRecordChangeListener;
    }

    public void setCanTouch(boolean canTouch) {
        isCanTouch = canTouch;
        if(!isCanTouch){
//            mBackgroundColor = mCircleNotTouchColor;
            if(mOnRecordChangeListener != null){
                mOnRecordChangeListener.onEventUp();
            }
            shrinkAnimatorStart();
        }else{
//            mBackgroundColor = mCircleBackgroundColor;
            invalidate();
        }
    }

    public void cancelTouch(){
        if(!isTouching){
            return ;
        }
        if(mOnRecordChangeListener != null){
            mOnRecordChangeListener.onEventUp();
        }
        shrinkAnimatorStart();
    }

    public boolean isTouching() {
        return isTouching;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mCenterX == 0 && !isTouching){
            mCenterX = getMeasuredWidth()/2;
        }
        if(mCenterY == 0 && !isTouching){
            mCenterY = getMeasuredHeight() - mCircleMarginBottom - mCircleDynamicSize/2;
        }

        Shader shader = new LinearGradient(mCenterX-(int)(mCircleSize/2), mCenterY+(int)(mCircleSize/2), mCenterX+(int)(mCircleSize/2), mCenterY-(int)(mCircleSize/2),
                new int[] { isCanTouch?mCircleBackgroundStartColor:mCircleNotTouchStartColor,isCanTouch?mCircleBackgroundEndColor:mCircleNotTouchEndColor},
                null, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float radius = mCircleSize/2;
        if(isTouching){
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mCircleCurrentStrokeWidth);
            radius = mCircleSize/2 - mCircleCurrentStrokeWidth/2;
            radius = (radius==0?1:radius);
        }else{
            paint.setStyle(Paint.Style.FILL);
        }
//        paint.setColor(mBackgroundColor);
        paint.setShader(shader);
        canvas.drawCircle(mCenterX,mCenterY,radius,paint);

        if(mImageBitmap != null){
            Rect rect = new Rect(mCenterX-(int)(mCircleImageDrawSize/2),mCenterY-(int)(mCircleImageDrawSize/2),mCenterX+(int)(mCircleImageDrawSize/2),mCenterY+(int)(mCircleImageDrawSize/2));
            canvas.drawBitmap(mImageBitmap,null,rect,paint);
        }

//        float colorDivision = (centerPositionX+selectedWidth-rectF.left)/(baseWidth-roundRectMargin);
    }


    float mMoveX;
    float mMoveY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isCanTouch){
            if(mOnRecordChangeListener != null){
                mOnRecordChangeListener.onDontTouch();
            }
            return super.onTouchEvent(event);
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(Math.abs(getMeasuredWidth()/2-event.getX()) > mCircleSize/2//手指在圆的X范围内
                        || Math.abs(getMeasuredHeight() - mCircleMarginBottom - mCircleSize/2-event.getY()) > mCircleSize/2){
                    return false;
                }
                mMoveX = event.getX();
                mMoveY = event.getY();
//                magnifyAnimatorStart();
                if(mOnRecordChangeListener != null && !isTouching){
                    magnifyAnimatorStart();
                    mOnRecordChangeListener.onEventDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isTouching){
                    return true;
                }
                mCenterX -= mMoveX-event.getX();
                mCenterY -= mMoveY-event.getY();
                mMoveX = event.getX();
                mMoveY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mOnRecordChangeListener != null){
                    mOnRecordChangeListener.onEventUp();
                }
                if(isTouching){
                    shrinkAnimatorStart();
                }
                break;
        }
        return true;
    }

    private AnimatorSet mMagnifyAnimator;
    private AnimatorSet mShrinkAnimator;

    public void autoAnimatorStart(){
        if(mOnRecordChangeListener != null && !isTouching){
            magnifyAnimatorStart();
            mOnRecordChangeListener.onEventDown();
            isTouching = true;
        }
    }

    public void magnifyAnimatorStart(){
        isTouching = true;
        if(mShrinkAnimator != null && mShrinkAnimator.isRunning()){
            mShrinkAnimator.pause();
        }
//        if(mMagnifyAnimator == null){
//            mMagnifyAnimator = ValueAnimator.ofFloat(0f,1f);
//        }
//        if(mMagnifyAnimator == null){
        mMagnifyAnimator = new AnimatorSet();
//        }

        ValueAnimator magnifyAnimator = ValueAnimator.ofFloat(0f,1f);
//        AnimatorSet animatorSet = new AnimatorSet();
        magnifyAnimator.setDuration(ANIMATOR_DURATION);
//        magnifyAnimator.removeAllUpdateListeners();
        magnifyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changeSizeAndDraw((float) animation.getAnimatedValue());
            }
        });

        ValueAnimator magnifyCircleAnimator = ValueAnimator.ofFloat(1f,0f);
        magnifyCircleAnimator.setDuration(ANIMATOR_DURATION_CIRCLE);
        magnifyCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircleSize = (mCircleDynamicSize-mCircleStatusSize)*(1f-(float)animation.getAnimatedValue()) + mCircleStatusSize;
                mCircleCurrentStrokeWidth = (mCircleSize / 2) * (float)animation.getAnimatedValue();
                mCircleCurrentStrokeWidth = (mCircleCurrentStrokeWidth<mCircleMinStrokeWidth?mCircleMinStrokeWidth:mCircleCurrentStrokeWidth);
                invalidate();
            }
        });
        mMagnifyAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                mCircleCurrentStrokeWidth = mCircleSize / 2;
                Message message = mHandler.obtainMessage();
                message.what = HANDLER_WHAT;
                message.arg1 = 1;
                mHandler.sendMessage(message);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mMagnifyAnimator.playSequentially(magnifyAnimator,magnifyCircleAnimator);
        mMagnifyAnimator.start();

//        mMagnifyAnimator.start();
    }

    public void shrinkAnimatorStart(){
        if(mMagnifyAnimator != null && mMagnifyAnimator.isRunning()){
            mMagnifyAnimator.pause();
        }
//        if(mShrinkAnimator == null){
//            mShrinkAnimator = ValueAnimator.ofFloat(1f,0f);
//        }

        if(mHandler.hasMessages(HANDLER_WHAT)){
            mHandler.removeMessages(HANDLER_WHAT);
        }

        mShrinkAnimator = new AnimatorSet();
        ValueAnimator shrinkAnimator = ValueAnimator.ofFloat(1f,0f);
        shrinkAnimator.setDuration(ANIMATOR_DURATION);
//        shrinkAnimator.removeAllUpdateListeners();
        shrinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changeSizeAndDraw((float) animation.getAnimatedValue());
            }
        });


        ValueAnimator shrinkCircleAnimator = ValueAnimator.ofFloat(0f,1f);
        shrinkCircleAnimator.setDuration(ANIMATOR_DURATION_CIRCLE);
        shrinkCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircleSize = (mCircleDynamicSize-mCircleStatusSize)*(1f-(float)animation.getAnimatedValue()) + mCircleStatusSize;
                mCircleCurrentStrokeWidth = (mCircleSize / 2) * (float)animation.getAnimatedValue();
                mCircleCurrentStrokeWidth = (mCircleCurrentStrokeWidth<mCircleMinStrokeWidth?mCircleMinStrokeWidth:mCircleCurrentStrokeWidth);
                mCenterX = (int) ((mCenterX-getMeasuredWidth()/2)*(1f-(float)animation.getAnimatedValue()) + getMeasuredWidth()/2);
                mCenterY = (int) ((mCenterY - (getMeasuredHeight() - mCircleMarginBottom - mCircleDynamicSize/2))*(1f-(float)animation.getAnimatedValue()) + (getMeasuredHeight() - mCircleMarginBottom - mCircleDynamicSize/2));
                invalidate();
            }
        });
        mShrinkAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCircleCurrentStrokeWidth = mCircleSize / 2;
                mCenterX = 0;
                mCenterY = 0;
                isTouching = false;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mShrinkAnimator.playSequentially(shrinkCircleAnimator,shrinkAnimator);
        mShrinkAnimator.start();

//        shrinkAnimator.start();
    }

    private void changeSizeAndDraw(float value){
//        mCircleSize = (mCircleDynamicSize-mCircleStatusSize)*value + mCircleStatusSize;
        mCircleImageDrawSize = mCircleImageSize * (1f-value);
        invalidate();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mCircleCurrentStrokeWidth+=msg.arg1;
            invalidate();
            Message message = mHandler.obtainMessage();
            message.what = HANDLER_WHAT;
            if(mCircleCurrentStrokeWidth > mCircleMaxStrokeWidth){
                message.arg1 = -1;
            }else if(mCircleCurrentStrokeWidth < mCircleMinStrokeWidth){
                message.arg1 = 1;
            }else{
                message.arg1 = msg.arg1;
            }
            mHandler.sendMessageDelayed(message,HANDLER_TIME);
        }
    };


    public interface OnRecordChangeListener{
        void onEventDown();
        void onEventUp();
        void onDontTouch();
    }

}
