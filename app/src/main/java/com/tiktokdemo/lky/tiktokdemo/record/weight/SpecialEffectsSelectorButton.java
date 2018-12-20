package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by lky on 2017/6/23.
 */

public class SpecialEffectsSelectorButton extends View {

    private TouchMode mTouchMode = TouchMode.TOUCH;

    private Context mContext;

    private int mDefaultRes;
    private int mSelectedRes;

    private int mDefaultViewWidth;
    private int mDefaultViewHeight;

    private Bitmap mDefaultBitmap;
    private Bitmap mSelectedBitmap;

    private float mViewScale;

    private boolean isTouching;

    private SpecialEffectsSelectorListener mSpecialEffectsSelectorListener;

    public SpecialEffectsSelectorButton(Context context) {
        this(context,null);
    }

    public SpecialEffectsSelectorButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpecialEffectsSelectorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SpecialEffectsSelectorButton,defStyleAttr,0);
        switch (array.getInt(R.styleable.SpecialEffectsSelectorButton_SpecialEffectsTouchMode,0)){
            case 0:
                mTouchMode = TouchMode.TOUCH;
                break;
            case 1:
                mTouchMode = TouchMode.SELECTOR;
                break;
        }
        mDefaultRes = array.getResourceId(R.styleable.SpecialEffectsSelectorButton_SpecialEffectsDefaultRes,0);
        mSelectedRes = array.getResourceId(R.styleable.SpecialEffectsSelectorButton_SpecialEffectsSelectedRes,0);
        mDefaultViewWidth = array.getDimensionPixelOffset(R.styleable.SpecialEffectsSelectorButton_SpecialEffectsDefaultViewWidth, DensityUtils
                .dp2px(50));
        mDefaultViewHeight = array.getDimensionPixelOffset(R.styleable.SpecialEffectsSelectorButton_SpecialEffectsDefaultViewHeight, DensityUtils.dp2px(50));

        init(context);


    }

    private void init(Context context) {
        if(mDefaultRes != 0){
            mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(),mDefaultRes);
        }
        if(mSelectedRes != 0){
            mSelectedBitmap = BitmapFactory.decodeResource(context.getResources(),mSelectedRes);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mDefaultBitmap != null && mSelectedBitmap != null){

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            Rect rect = new Rect((int) ((getWidth()-mDefaultViewWidth)/2*(1.0f-mViewScale)),(int) ((getHeight()-mDefaultViewHeight)/2*(1.0f-mViewScale)),
                    (int) (mDefaultViewWidth+(getWidth()-mDefaultViewWidth)/2*mViewScale),(int) (mDefaultViewHeight+(getHeight()-mDefaultViewHeight)/2*mViewScale));
            canvas.drawBitmap(isTouching?mSelectedBitmap:mDefaultBitmap,null,rect,paint);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mTouchMode == TouchMode.TOUCH){
                    isTouching = true;
                    if(mSpecialEffectsSelectorListener != null){
                        mSpecialEffectsSelectorListener.onTouchDown();
                    }
                    touchAnimator();
                }else if(mTouchMode == TouchMode.SELECTOR){
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mTouchMode == TouchMode.TOUCH && isTouching){
                    isTouching = false;
                    unTouchAnimator();
                    if(mSpecialEffectsSelectorListener != null){
                        mSpecialEffectsSelectorListener.onTouchUp();
                    }
                }else if(mTouchMode == TouchMode.SELECTOR && !isTouching){
                    isTouching = !isTouching;
                    if(mSpecialEffectsSelectorListener != null){
                        mSpecialEffectsSelectorListener.onStateChange(isTouching);
                    }
                    invalidate();
                }
                break;
        }

        return true;
    }

    private void touchAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1.0f);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mViewScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void unTouchAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f,0f);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mViewScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }


    public interface SpecialEffectsSelectorListener{
        void onTouchDown();
        void onTouchUp();
        void onStateChange(boolean isSelector);
    }

    public void setTouching(boolean isTouching){
        this.isTouching = isTouching;
        invalidate();
    }


    public void setTouchMode(TouchMode touchMode) {
        mTouchMode = touchMode;
    }

    public TouchMode getTouchMode() {
        return mTouchMode;
    }

    public void setSpecialEffectsSelectorListener(SpecialEffectsSelectorListener specialEffectsSelectorListener) {
        mSpecialEffectsSelectorListener = specialEffectsSelectorListener;
    }

    public SpecialEffectsSelectorListener getSpecialEffectsSelectorListener() {
        return mSpecialEffectsSelectorListener;
    }

    public void setDefaultRes(int defaultRes) {
        mDefaultRes = defaultRes;
        if(mDefaultRes != 0){
            mDefaultBitmap = BitmapFactory.decodeResource(mContext.getResources(),mDefaultRes);
        }
    }

    public void setSelectedRes(int selectedRes) {
        mSelectedRes = selectedRes;
        if(mSelectedRes != 0){
            mSelectedBitmap = BitmapFactory.decodeResource(mContext.getResources(),mSelectedRes);
        }
    }

    public void setDefaultViewWidth(int defaultViewWidth) {
        mDefaultViewWidth = defaultViewWidth;
    }

    public void setDefaultViewHeight(int defaultViewHeight) {
        mDefaultViewHeight = defaultViewHeight;
    }


    public enum TouchMode{
        TOUCH,
        SELECTOR,
    }
}
