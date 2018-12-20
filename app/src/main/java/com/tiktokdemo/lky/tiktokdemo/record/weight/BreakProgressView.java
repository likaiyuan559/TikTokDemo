package com.tiktokdemo.lky.tiktokdemo.record.weight;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by lky on 2016/12/5.
 */

public class BreakProgressView extends View {


    private int mProgressWidth;
    private int mProgressHeight;

    private int mMaxProgress = 100;
    private int mCurrentProgress;

    private int mProgressColor = 0xffff0000;
    private int mSegmentationLineColor = 0xffffffff;

    private ArrayList<Integer> mBreakProgress;

    private OnBreakProgressListener mOnBreakProgressListener;
//    private boolean isRemoving = true;

    public BreakProgressView(Context context) {
        this(context,null);
    }

    public BreakProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BreakProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBreakProgress = new ArrayList<>();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BreakProgressView,defStyleAttr,0);
        mProgressColor = array.getColor(
                R.styleable.BreakProgressView_SelectedProgressColor,context.getResources().getColor(R.color.tidal_pat_yellow));
        mSegmentationLineColor = array.getColor(R.styleable.BreakProgressView_SegmentationLineColor,context.getResources().getColor(R.color.white));
        mMaxProgress = array.getInt(R.styleable.BreakProgressView_MaxProgress,100);
    }

    public void setOnBreakProgressListener(OnBreakProgressListener onBreakProgressListener) {
        mOnBreakProgressListener = onBreakProgressListener;
    }


    public void setMax(int max){
        mMaxProgress = max;
    }

    public int getMax() {
        return mMaxProgress;
    }

    public void setProgress(int progress){
//        int offsetProgress = 0;
//        if(mBreakProgress != null && mBreakProgress.size() > 0){
//            offsetProgress = mBreakProgress.get(mBreakProgress.size()-1);
//        }
        mCurrentProgress = progress;
        if(mCurrentProgress > mMaxProgress){
            mCurrentProgress = mMaxProgress;
        }
        invalidate();
        if(mOnBreakProgressListener != null){
            if(mCurrentProgress >= mMaxProgress){
                mOnBreakProgressListener.complete();
            }else{
                mOnBreakProgressListener.progress(mCurrentProgress);
            }
        }
    }

    public void setBreakProgress(ArrayList<Integer> breakProgress) {
        mBreakProgress = breakProgress;
        invalidate();
    }

    public int getLastBreakProgress(){
        if(mBreakProgress == null || mBreakProgress.size() < 1){
            return 0;
        }
        return mBreakProgress.get(mBreakProgress.size()-1);
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void addBreakProgress(int breakProgress){
        mBreakProgress.add(breakProgress);
        invalidate();
    }

    public ArrayList<Integer> getBreakProgress() {
        return mBreakProgress;
    }

    public void removeLastBreakProgress(){
        if(mBreakProgress != null && mBreakProgress.size() > 0){
//            if(isRemoving){
//                isRemoving = false;
                mCurrentProgress = mBreakProgress.size()>=1?mBreakProgress.get(mBreakProgress.size()-1):0;
                mBreakProgress.remove(mBreakProgress.size()-1);
                if(mOnBreakProgressListener != null){
                    mOnBreakProgressListener.remove();
                }
//            }
//            else{
//                isRemoving = true;
//            }
        }else if(mCurrentProgress > 0){
//            if(isRemoving){
//                isRemoving = false;
                mCurrentProgress = 0;
                if(mOnBreakProgressListener != null){
                    mOnBreakProgressListener.remove();
                }
//            }
//            else{
//                isRemoving = true;
//            }
        }
        invalidate();
    }

    public void resetRemoveStatus(){
//        isRemoving = false;
        invalidate();
    }

    public void resetAllStatus(){
//        isRemoving = false;
        mBreakProgress.clear();
        mCurrentProgress = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mProgressColor);
        paint.setStyle(Paint.Style.FILL);

//        if(mBreakProgress == null || mBreakProgress.size() <= 0){
//
//        }    RectF selectRectF = new RectF(getPaddingLeft(),getMeasuredHeight()/2-10,mCurrentProgress/(float)mMaxProgress*mProgressWidth,getMeasuredHeight()/2+10);
//            canvas.drawRect(selectRectF,paint);
//        }else{
//            for(int i=0;i<mBreakProgress.size();i++){
//                RectF selectRectF = new RectF((i==0?getPaddingLeft():((mBreakProgress.get(i-1)/(float)mMaxProgress)*mProgressWidth)),
//                        getMeasuredHeight()/2-10,(mBreakProgress.get(i)/(float)mMaxProgress)*mProgressWidth,getMeasuredHeight()/2+10);
//                canvas.drawRoundRect(selectRectF,10,10,paint);
//            }
//            RectF selectRectF = new RectF(mBreakProgress.get(mBreakProgress.size()-1)/(float)mMaxProgress*mProgressWidth,getMeasuredHeight()/2-10,mCurrentProgress/(float)mMaxProgress*mProgressWidth,getMeasuredHeight()/2+10);
//            canvas.drawRoundRect(selectRectF,10,10,paint);

        RectF progressRectF = new RectF(getPaddingLeft(),0,mCurrentProgress/(float)mMaxProgress*mProgressWidth,getMeasuredHeight());
        canvas.drawRect(progressRectF,paint);
        paint.setColor(mSegmentationLineColor);
        int breakWidth = DensityUtils.dp2px(1.5f);
        paint.setStrokeWidth(breakWidth);
        if(mBreakProgress != null && mBreakProgress.size() > 0){
            for(int i=0;i<mBreakProgress.size();i++){
                canvas.drawLine((mBreakProgress.get(i)/(float)mMaxProgress)*mProgressWidth-breakWidth/2,0,(mBreakProgress.get(i)/(float)mMaxProgress)*mProgressWidth-breakWidth/2,getMeasuredHeight(),paint);
            }
        }
//        if(isRemoving && mCurrentProgress > 0){
//            if(mBreakProgress.size() >= 1){
//                paint.setColor(0xFF999999);
//                RectF rectF = new RectF((mBreakProgress.get(mBreakProgress.size() - 1)/(float)mMaxProgress)*mProgressWidth,0,mCurrentProgress/(float)mMaxProgress*mProgressWidth,getMeasuredHeight());
//                canvas.drawRect(rectF,paint);
//            }else{
//                paint.setColor(0xFF666666);
//                RectF rectF = new RectF(0,0,mCurrentProgress/(float)mMaxProgress*mProgressWidth,getMeasuredHeight());
//                canvas.drawRect(rectF,paint);
//            }
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mProgressWidth == 0){
            mProgressWidth = getMeasuredWidth();
        }

        if(mProgressHeight == 0){
            mProgressHeight = getMeasuredHeight();
        }
    }


    public interface OnBreakProgressListener{
        void progress(int progress);
        void complete();
        void remove();
    }
}
