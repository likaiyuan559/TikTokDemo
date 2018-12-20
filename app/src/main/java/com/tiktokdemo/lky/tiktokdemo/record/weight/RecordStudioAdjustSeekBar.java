package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;

/**
 * Created by ydc on 2017/8/28.
 */

public class RecordStudioAdjustSeekBar extends View {

    private int mTidalPatAdjustThumbSize;
    private int mTidalPatAdjustThumbColor;
    private int mTidalPatAdjustThumbDefaultColor;
    private int mTidalPatAdjustProgressColor;
    private int mTidalPatAdjustProgressSelectedColor;
    private int mTidalPatAdjustProgressDefaultColor;
    private int mTidalPatAdjustProgressHeight;

    private int mTextColor = 0xff333333;

    private OnAdjustSeekBarScrollListener mOnAdjustSeekBarScrollListener;

    private float mMax = 100f;

    private float mProgress = 50f;

    private float mDefaultProgress = 50f;

    private boolean isCanScroll = true;
    private int mParentMargin;
    private float mRoundViewWidth;
    private int mImagePositionX;

    public RecordStudioAdjustSeekBar(Context context) {
        this(context, null);
    }

    public RecordStudioAdjustSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordStudioAdjustSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RecordStudioAdjustSeekBar, defStyleAttr, 0);
        mTidalPatAdjustThumbSize = array.getDimensionPixelSize(R.styleable.RecordStudioAdjustSeekBar_RecordStudioAdjustThumbSize, DensityUtils
                .dp2px(27));
        mTidalPatAdjustThumbColor = array.getColor(R.styleable.RecordStudioAdjustSeekBar_RecordStudioAdjustThumbColor, context.getResources().getColor(R.color.white));
        mTidalPatAdjustThumbDefaultColor = array.getColor(R.styleable.RecordStudioAdjustSeekBar_RecordStudioAdjustThumbDefaultColor, context.getResources().getColor(R.color.gray_normal));
        mTidalPatAdjustProgressColor = array.getColor(R.styleable.RecordStudioAdjustSeekBar_RecordStudioAdjustProgressColor, context.getResources().getColor(R.color.white));
        mTidalPatAdjustProgressSelectedColor = array.getColor(R.styleable.RecordStudioAdjustSeekBar_RecordStudioAdjustProgressSelectedColor, 0xFFFACE15);
        mTidalPatAdjustProgressDefaultColor = array.getColor(R.styleable.RecordStudioAdjustSeekBar_TRecordStudioAdjustProgressDefaultColor, context.getResources().getColor(R.color.few_10_transparency_white));
        mTidalPatAdjustProgressHeight = array.getDimensionPixelOffset(R.styleable.RecordStudioAdjustSeekBar_RecordStudioAdjustProgressHeight, DensityUtils.dp2px(3));
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
        if (!isCanScroll) {
            mProgress = 0f;
        } else {
            setProgress(mDefaultProgress);
        }
        invalidate();
    }

    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        mImagePositionX = (int) (mRoundViewWidth * mProgress / mMax);
    }

    public void setDefaultProgress(float progress) {
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
        if (!isCanScroll) {
            return super.onTouchEvent(event);
        }
        float eX = event.getX();
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && (eX > mImagePositionX && eX < mImagePositionX + mTidalPatAdjustThumbSize)) {
            isDragging = true;
            mMoveX = eX;
            mOnAdjustSeekBarScrollListener.onEventDown();
        }
        if (isDragging) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mImagePositionX += event.getX() - mMoveX;
                    if (mImagePositionX < 0) {
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
                    if (mImagePositionX + mParentMargin > mRoundViewWidth + mParentMargin) {//超过右边区域，限制
                        mImagePositionX = (int) (mRoundViewWidth);
                    }
                    mProgress = (int) (mImagePositionX / mRoundViewWidth * mMax);
                    if (mOnAdjustSeekBarScrollListener != null) {
                        mOnAdjustSeekBarScrollListener.onEventUp((int) (mImagePositionX / mRoundViewWidth * mMax));
                    }
                    invalidate();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mParentMargin = mTidalPatAdjustThumbSize / 2;//全部刻度和整个控件外面的边距
        mRoundViewWidth = getMeasuredWidth() - mParentMargin * 2;//全部刻度的占有区域的大小
        mImagePositionX = (int) (mRoundViewWidth * mProgress / mMax);
    }

    private int textBackWidth = DensityUtils.dp2px(72);
    private int textBackHeight = DensityUtils.dp2px(44);
    private int textBackMargin = DensityUtils.dp2px(6);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(isCanScroll ? mTidalPatAdjustProgressColor : mTidalPatAdjustProgressDefaultColor);
        paint.setStrokeWidth(mTidalPatAdjustProgressHeight);
        int height = getMeasuredHeight();

        canvas.drawLine(mParentMargin, height / 2, mParentMargin + mRoundViewWidth, height / 2, paint);
        if (isCanScroll) {
            paint.setColor(mTidalPatAdjustProgressSelectedColor);
            canvas.drawLine(mParentMargin, height / 2, mParentMargin + (mRoundViewWidth * mProgress / mMax), height / 2, paint);
        }

        paint.setColor(isCanScroll ? mTidalPatAdjustThumbColor : mTidalPatAdjustThumbDefaultColor);
        canvas.drawCircle(mParentMargin + (mRoundViewWidth * mProgress / mMax), height / 2, mTidalPatAdjustThumbSize / 2, paint);
        if (isDragging) {
            int left = (int) (mParentMargin + (mRoundViewWidth * mProgress / mMax) - textBackWidth / 2);

            Rect rectF1 = new Rect(0, 0, textBackWidth, textBackHeight);
            RectF rectF2 = new RectF(left, -textBackHeight - textBackMargin, left + textBackWidth, -textBackMargin);
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bg_1), rectF1, rectF2, paint);

//            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bg_1), left, -DensityUtils.dp2px(44), paint);
            paint.setColor(mTextColor);
            paint.setTextSize(DensityUtils.sp2px(getContext(), 12f));
            paint.setTextAlign(Paint.Align.CENTER);
//            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
//            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
//            int baseLineY = (int) (-DensityUtils.dp2px(44) / 2 - top / 2 - bottom / 2);//基线中间点的y轴计算公式

            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            // 转载请注明出处：http://blog.csdn.net/hursing
            int baseline = (int) ((rectF2.bottom + rectF2.top - DensityUtils.dp2px(6) - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText((int)mProgress + "", left + textBackWidth / 2, baseline, paint);
        }
    }

    public interface OnAdjustSeekBarScrollListener {
        void onProgress(int progress);

        void onEventUp(int progress);

        void onEventDown();
    }

}
