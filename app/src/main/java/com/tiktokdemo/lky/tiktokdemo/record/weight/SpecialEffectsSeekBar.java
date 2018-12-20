package com.tiktokdemo.lky.tiktokdemo.record.weight;

import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsProgressBean;


/**
 * Created by lky on 2017/6/20.
 */

public class SpecialEffectsSeekBar extends View {

    private final int DELAYED_TIME = 150;

    private float mTidalPatAdjustThumbWidth;
    private float mTidalPatAdjustThumbHeight;
    private int mTidalPatAdjustThumbColor;
    private int mTidalPatAdjustProgressColor;
    private int mTidalPatAdjustProgressSelectedColor;
    private int mTidalPatAdjustProgressHeight;

    private Context mContext;

    private OnAdjustSeekBarScrollListener mOnAdjustSeekBarScrollListener;

    private float mMax = 0;

    private float mProgress = 50f;

    private int mParentMargin;
    private float mRoundViewWidth;
    private int mImagePositionX;

    private int mTargetThumbImgRes = 0;
    private int mTargetThumbWidth = 100;
    private int mTargetThumbHeight = 100;

    private int mTargetMargin;
    private float mTargetRoundViewWidth;
    private int mTargetPositionX;
    private float mTargetProgress = 0f;

    private boolean isExistTarget = false;

    private float mTouchRangeAppend = DensityUtils.dp2px(4);
    private float mTouchChangeBigWidth = DensityUtils.dp2px(2);
    private float mTouchChangeBigHeight = DensityUtils.dp2px(4);


    private boolean isOperationFilter;//是否正在操作滤镜

    private SpecialEffectsProgressBean mTempProgressBean;
    private ArrayList<SpecialEffectsProgressBean> mSpecialEffectsProgressBeen;
    private boolean isCanListener = true;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            isCanListener = true;
        }
    };

    public SpecialEffectsSeekBar(Context context) {
        this(context,null);
    }

    public SpecialEffectsSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpecialEffectsSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TidalPatAdjustSeekBar,defStyleAttr,0);
        mTidalPatAdjustThumbWidth = DensityUtils.dp2px(10);
        mTidalPatAdjustThumbHeight = DensityUtils.dp2px(24);
        mTidalPatAdjustThumbColor = 0xFFFFD217;
        mTidalPatAdjustProgressColor = 0xFFFFFFFF;
        mTidalPatAdjustProgressSelectedColor = 0xFFFACE15;
        mTidalPatAdjustProgressHeight = DensityUtils.dp2px(4);
        mOnAdjustSeekBarScrollListener = new OnAdjustSeekBarScrollListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onEventUp(int progress) {

            }

            @Override
            public void onEventDown() {

            }
        };
        mSpecialEffectsProgressBeen = new ArrayList<>();
    }

    public void setProgress(float progress) {
        if(isDragging){
            return ;
        }
        mProgress = progress;
        mImagePositionX = (int) (mRoundViewWidth * mProgress/mMax);
        if(mTempProgressBean != null){
            mTempProgressBean.setTimeEnd((long) mProgress);
        }
        invalidate();
    }

    public SpecialEffectsProgressBean setOperationFilter(boolean isOperationFilter, @Nullable
            SpecialEffectsProgressBean specialEffectsProgressBean){
        this.isOperationFilter = isOperationFilter;
        if(isOperationFilter){
            mTempProgressBean = specialEffectsProgressBean;
            mTempProgressBean.setTimeStart((long) mProgress);
//            if(mSpecialEffectsProgressBeen != null){
//                mSpecialEffectsProgressBeen.clear();
//                mSpecialEffectsProgressBeen.add(mTempProgressBean);
//            }
        }else{
            specialEffectsProgressBean = mTempProgressBean;
            mTempProgressBean = null;
        }
        return specialEffectsProgressBean;
    }

    public void clearOperationFilter(){
        mTempProgressBean = null;
    }

    public SpecialEffectsProgressBean getOperationFilter(){
        return mTempProgressBean;
    }


    public void setOnAdjustSeekBarScrollListener(OnAdjustSeekBarScrollListener onAdjustSeekBarScrollListener) {
        mOnAdjustSeekBarScrollListener = onAdjustSeekBarScrollListener;
    }

    public void setSpecialEffectsProgressBeen(ArrayList<SpecialEffectsProgressBean> specialEffectsProgressBeen) {
        mSpecialEffectsProgressBeen = specialEffectsProgressBeen;
        invalidate();
    }

    public void clearData(){
        mTempProgressBean = null;
        mSpecialEffectsProgressBeen = new ArrayList<>();
        setProgress(0);
    }

    public float getProgress() {
        return mProgress;
    }

    public float getMax() {
        return mMax;
    }

    public void setMax(float max) {
        mMax = max;
        invalidate();
    }

    private boolean isDragging;
    private float mMoveX;
    private boolean isTargetDragging;
    private float mTargetMoveX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eX = event.getX();
        if(event.getAction() == MotionEvent.ACTION_DOWN
                && (eX > mImagePositionX-mTouchRangeAppend && eX < mImagePositionX+mTidalPatAdjustThumbWidth+mTouchRangeAppend)){
            isDragging = true;
            mMoveX = eX;
            touchThumbAnimator();
            if (mOnAdjustSeekBarScrollListener != null) {
                mOnAdjustSeekBarScrollListener.onEventDown();
            }
            return true;
        }else if(isExistTarget && event.getAction() == MotionEvent.ACTION_DOWN
                && (eX > mTargetPositionX && eX < mTargetPositionX+mTargetThumbWidth)){
            isTargetDragging = true;
            mTargetMoveX = eX;
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    mImagePositionX += event.getX() - mMoveX;
                    if (mImagePositionX < 0) {
                        mImagePositionX = 0;
                    }
                    if (mImagePositionX + mParentMargin > mRoundViewWidth + mParentMargin) {//超过右边区域，限制
                        int progress = (int) ((mRoundViewWidth) / mRoundViewWidth * mMax);
                        if (mOnAdjustSeekBarScrollListener != null && progress != mProgress && isCanListener) {
                            isCanListener = false;
                            mHandler.sendEmptyMessageDelayed(0,DELAYED_TIME);
                            mOnAdjustSeekBarScrollListener.onProgress(progress>mMax? (int) mMax :progress);
                        }
                        mProgress = progress;
                    } else {
                        int progress = (int) (mImagePositionX / mRoundViewWidth * mMax);
                        if (mOnAdjustSeekBarScrollListener != null && progress != mProgress && isCanListener) {
                            isCanListener = false;
                            mHandler.sendEmptyMessageDelayed(0,DELAYED_TIME);
                            mOnAdjustSeekBarScrollListener.onProgress(progress>mMax? (int) mMax :progress);
                        }
                        mProgress = progress;
                    }
                    mMoveX = event.getX();
                    invalidate();
                    return true;
                }else if(isTargetDragging){
                    mTargetPositionX += event.getX() - mTargetMoveX;
                    if(mTargetPositionX < 0){
                        mTargetPositionX = 0;
                    }
                    if (mTargetPositionX + mTargetMargin > mTargetRoundViewWidth + mTargetMargin) {//超过右边区域，限制
//                        if (mOnAdjustSeekBarScrollListener != null) {
//                            mOnAdjustSeekBarScrollListener.onProgress((int) ((mRoundViewWidth) / mRoundViewWidth * mMax));
//                        }
                        mTargetProgress = (int) mMax;
                    } else {
//                        if (mOnAdjustSeekBarScrollListener != null) {
//                            mOnAdjustSeekBarScrollListener.onProgress((int) (mImagePositionX / mRoundViewWidth * mMax));
//                        }
                        mTargetProgress = (int) (mTargetPositionX / mTargetRoundViewWidth * mMax);
                    }
                    mTargetMoveX = event.getX();
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(isDragging){
                    unTouchThumbAnimator();
                    isDragging = false;
                    if (mImagePositionX + mParentMargin > mRoundViewWidth + mParentMargin) {//超过右边区域，限制
                        mImagePositionX = (int) (mRoundViewWidth);
                    }
                    mProgress = (int) (mImagePositionX / mRoundViewWidth * mMax);
                    if (mOnAdjustSeekBarScrollListener != null) {
                        int progress = (int) (mImagePositionX / mRoundViewWidth * mMax);
                        mOnAdjustSeekBarScrollListener.onEventUp(progress>mMax? (int) mMax :progress);
                    }
                    return true;
                }else if(isTargetDragging){
                    isTargetDragging = false;
                    if (mTargetPositionX + mTargetMargin > mTargetRoundViewWidth + mTargetMargin) {//超过右边区域，限制
                        mTargetPositionX = (int) (mTargetRoundViewWidth);
                    }
                    mTargetProgress = (int) (mTargetPositionX / mTargetRoundViewWidth * mMax);
//                    if (mOnAdjustSeekBarScrollListener != null) {
//                        mOnAdjustSeekBarScrollListener.onEventUp((int) (mImagePositionX / mRoundViewWidth * mMax));
//                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mParentMargin = (int) (mTidalPatAdjustThumbWidth/2);//全部刻度和整个控件外面的边距
        mRoundViewWidth = getMeasuredWidth()-mParentMargin*2 - mTouchChangeBigWidth*2;//全部刻度的占有区域的大小
        if(mImagePositionX == 0){
            mImagePositionX = (int) (mRoundViewWidth * mProgress/mMax);
        }

        mTargetMargin = mTargetThumbWidth/2;//全部刻度和整个控件外面的边距
        mTargetRoundViewWidth = getMeasuredWidth()-mTargetMargin*2;//全部刻度的占有区域的大小
        if(mTargetPositionX == 0){
            mTargetPositionX = (int) (mTargetRoundViewWidth * mTargetProgress/mMax);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mTidalPatAdjustProgressColor);
        paint.setStrokeWidth(mTidalPatAdjustProgressHeight);
        int height = getMeasuredHeight();
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        RectF lineRectF = new RectF(mParentMargin,height/2-mTidalPatAdjustProgressHeight/2,mParentMargin+mRoundViewWidth,height/2+mTidalPatAdjustProgressHeight/2);
        canvas.drawRoundRect(lineRectF,mTidalPatAdjustProgressHeight/2,mTidalPatAdjustProgressHeight/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        for(SpecialEffectsProgressBean bean:mSpecialEffectsProgressBeen){
            paint.setColor(bean.getShowColor());
            RectF selectLineRectF = new RectF(mParentMargin+(mRoundViewWidth*bean.getTimeStart()/mMax),height/2-mTidalPatAdjustProgressHeight/2,
                    mParentMargin+(mRoundViewWidth*bean.getTimeEnd()/mMax),height/2+mTidalPatAdjustProgressHeight/2);
//            canvas.drawRoundRect(selectLineRectF,mTidalPatAdjustProgressHeight/2,mTidalPatAdjustProgressHeight/2,paint);
            canvas.drawRect(selectLineRectF,paint);
        }
        if(mTempProgressBean != null){
            paint.setColor(mTempProgressBean.getShowColor());
            RectF selectLineRectF = new RectF(mParentMargin+(mRoundViewWidth*mTempProgressBean.getTimeStart()/mMax),height/2-mTidalPatAdjustProgressHeight/2,
                    mParentMargin+(mRoundViewWidth*mTempProgressBean.getTimeEnd()/mMax),height/2+mTidalPatAdjustProgressHeight/2);
//            canvas.drawRoundRect(selectLineRectF,mTidalPatAdjustProgressHeight/2,mTidalPatAdjustProgressHeight/2,paint);
            canvas.drawRect(selectLineRectF,paint);
        }
        paint.setXfermode(null);
        canvas.restoreToCount(layerId);

        if(isExistTarget && mTargetThumbImgRes != 0){
            Rect rect = new Rect((int)(mTargetRoundViewWidth*mTargetProgress/mMax),height/2-mTargetThumbHeight/2,
                    (int) (mTargetRoundViewWidth*mTargetProgress/mMax+mTargetThumbWidth),height/2+mTargetThumbHeight/2);
            canvas.drawBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(),mTargetThumbImgRes),null,rect,paint);
        }

        paint.setColor(mTidalPatAdjustThumbColor);
        RectF
                rectF = new RectF(mRoundViewWidth*mProgress/mMax,height/2-mTidalPatAdjustThumbHeight/2,mRoundViewWidth*mProgress/mMax+mTidalPatAdjustThumbWidth,height/2+mTidalPatAdjustThumbHeight/2);
        canvas.drawRoundRect(rectF,20,20,paint);
    }

    private void touchThumbAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTidalPatAdjustThumbWidth = DensityUtils.dp2px(10) + (float) animation.getAnimatedValue() * DensityUtils.dp2px(2);
                mTidalPatAdjustThumbHeight = DensityUtils.dp2px(24) + (float) animation.getAnimatedValue() * DensityUtils.dp2px(4);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void unTouchThumbAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f,0f);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTidalPatAdjustThumbWidth = DensityUtils.dp2px(10) + (float) animation.getAnimatedValue() * mTouchChangeBigWidth;
                mTidalPatAdjustThumbHeight = DensityUtils.dp2px(24) + (float) animation.getAnimatedValue() * mTouchChangeBigHeight;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public interface OnAdjustSeekBarScrollListener{
        void onProgress(int progress);
        void onEventUp(int progress);
        void onEventDown();
    }
}
