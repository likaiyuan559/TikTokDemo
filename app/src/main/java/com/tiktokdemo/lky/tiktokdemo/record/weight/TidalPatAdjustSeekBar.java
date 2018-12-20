package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by lky on 2017/5/22.
 */

public class TidalPatAdjustSeekBar extends View {

    private int mTidalPatAdjustThumbSize;
    private int mTidalPatAdjustThumbColor;
    private int mTidalPatAdjustThumbDefaultColor;
    private int mTidalPatAdjustProgressColor;
    private int mTidalPatAdjustProgressSelectedColor;
    private int mTidalPatAdjustProgressDefaultColor;
    private int mTidalPatAdjustProgressHeight;

    private OnAdjustSeekBarScrollListener mOnAdjustSeekBarScrollListener;

    private float mMax = 100f;

    private float mProgress = 50f;

    private float mDefaultProgress = 50f;

    private boolean isCanScroll = true;
    private int mParentMargin;
    private float mRoundViewWidth;
    private int mImagePositionX;

    public TidalPatAdjustSeekBar(Context context) {
        this(context,null);
    }

    public TidalPatAdjustSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TidalPatAdjustSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TidalPatAdjustSeekBar,defStyleAttr,0);
        mTidalPatAdjustThumbSize = array.getDimensionPixelSize(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustThumbSize, DensityUtils
                .dp2px(27));
        mTidalPatAdjustThumbColor = array.getColor(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustThumbColor, context.getResources().getColor(R.color.white));
        mTidalPatAdjustThumbDefaultColor = array.getColor(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustThumbDefaultColor, context.getResources().getColor(R.color.gray_normal));
        mTidalPatAdjustProgressColor = array.getColor(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustProgressColor, context.getResources().getColor(R.color.white));
        mTidalPatAdjustProgressSelectedColor = array.getColor(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustProgressSelectedColor, 0xFFFACE15);
        mTidalPatAdjustProgressDefaultColor = array.getColor(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustProgressSelectedColor, context.getResources().getColor(R.color.theme_dim_white_new));
        mTidalPatAdjustProgressHeight = array.getDimensionPixelOffset(R.styleable.TidalPatAdjustSeekBar_TidalPatAdjustProgressHeight, DensityUtils.dp2px(3));
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
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
        if(!isCanScroll){
            mProgress = 0f;
        }else{
            setProgress(mDefaultProgress);
        }
        invalidate();
    }

    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        mImagePositionX = (int) (mRoundViewWidth * mProgress/mMax);
    }

    public void setDefaultProgress(float progress){
        mDefaultProgress = progress;
        setProgress(mDefaultProgress);
    }

    public void setOnAdjustSeekBarScrollListener(OnAdjustSeekBarScrollListener onAdjustSeekBarScrollListener) {
        mOnAdjustSeekBarScrollListener = onAdjustSeekBarScrollListener;
    }

    public float getProgress() {
        return mProgress;
    }

    public float getMax() {
        return mMax;
    }

    private boolean isDragging;
    private float mMoveX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isCanScroll){
            return super.onTouchEvent(event);
        }
        float eX = event.getX();
        if(event.getAction() == MotionEvent.ACTION_DOWN
                && (eX > mImagePositionX && eX < mImagePositionX+mTidalPatAdjustThumbSize)){
            isDragging = true;
            mMoveX = eX;
        }
        if(isDragging){
            switch (event.getAction()){
                case MotionEvent.ACTION_MOVE:
                    mImagePositionX += event.getX() - mMoveX;
                    if(mImagePositionX < 0){
                        mImagePositionX = 0;
                    }

                    if (mImagePositionX + mParentMargin > mRoundViewWidth + mParentMargin) {//超过右边区域，限制
                        if (mOnAdjustSeekBarScrollListener != null) {
                            mOnAdjustSeekBarScrollListener.onProgress((int) ((mRoundViewWidth) / mRoundViewWidth * mMax));
                        }
                        mProgress = (int) ((mRoundViewWidth) / mRoundViewWidth * mMax);
                    } else {
                        if (mOnAdjustSeekBarScrollListener != null) {
                            mOnAdjustSeekBarScrollListener.onProgress((int) (mImagePositionX / mRoundViewWidth * mMax));
                        }
                        mProgress = (int) (mImagePositionX / mRoundViewWidth * mMax);
                    }
                    mMoveX = event.getX();
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isDragging = false;
                    if(mImagePositionX + mParentMargin > mRoundViewWidth + mParentMargin){//超过右边区域，限制
                        mImagePositionX = (int) (mRoundViewWidth);
                    }
                    mProgress = (int) (mImagePositionX/mRoundViewWidth*mMax);
                    if(mOnAdjustSeekBarScrollListener != null){
                        mOnAdjustSeekBarScrollListener.onEventUp((int) (mImagePositionX/mRoundViewWidth*mMax));
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mParentMargin = mTidalPatAdjustThumbSize/2;//全部刻度和整个控件外面的边距
        mRoundViewWidth = getMeasuredWidth()-mParentMargin*2;//全部刻度的占有区域的大小
        mImagePositionX = (int) (mRoundViewWidth * mProgress/mMax);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(isCanScroll?mTidalPatAdjustProgressColor:mTidalPatAdjustProgressDefaultColor);
        paint.setStrokeWidth(mTidalPatAdjustProgressHeight);
        int height = getMeasuredHeight();

        canvas.drawLine(mParentMargin,height/2,mParentMargin+mRoundViewWidth,height/2,paint);
        if(isCanScroll){
            paint.setColor(mTidalPatAdjustProgressSelectedColor);
            canvas.drawLine(mParentMargin,height/2,mParentMargin+(mRoundViewWidth*mProgress/mMax),height/2,paint);
        }

        paint.setColor(isCanScroll?mTidalPatAdjustThumbColor:mTidalPatAdjustThumbDefaultColor);
        canvas.drawCircle(mParentMargin+(mRoundViewWidth*mProgress/mMax),height/2,mTidalPatAdjustThumbSize/2,paint);
    }

    public interface OnAdjustSeekBarScrollListener{
        void onProgress(int progress);
        void onEventUp(int progress);
        void onEventDown();
    }

}
