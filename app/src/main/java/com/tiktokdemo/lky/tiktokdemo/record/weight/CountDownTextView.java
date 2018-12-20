package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by lky on 2017/7/4.
 */

public class CountDownTextView extends View {

    private int mTextSize;
    private int mTextColor;
    private String mTextStr = "";
    private long mAnimatorDuration = 1000;

    private float mTextSizeScale;

    public CountDownTextView(Context context) {
        this(context,null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountDownTextView,defStyleAttr,0);
        mTextSize = array.getDimensionPixelOffset(R.styleable.CountDownTextView_CountDownTextSize, DensityUtils
                .dp2px(100));
        mTextColor = array.getColor(R.styleable.CountDownTextView_CountDownTextColor,0xFFFFFFFF);
//        mTextStr = array.getString(R.styleable.CountDownTextView_CountDownText);
        init();
    }

    public void setText(String text){
        mTextStr = text;

        AnimatorSet set = new AnimatorSet();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(mAnimatorDuration/2);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextSizeScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(1f,0f);
        valueAnimator2.setDuration(mAnimatorDuration/2);
        valueAnimator2.setInterpolator(new AccelerateInterpolator());
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextSizeScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        set.playSequentially(valueAnimator,valueAnimator2);
        set.start();
    }

    public void setAnimatorDuration(long animatorDuration){
        mAnimatorDuration = animatorDuration;
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mTextColor);
        paint.setTextSize(mTextSize * mTextSizeScale);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int baseLineY = (int) (getHeight()/2 - top/2 - bottom/2);//文字居中

        canvas.drawText(mTextStr,getWidth()/2,baseLineY,paint);

    }
}
