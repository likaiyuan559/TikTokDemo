package com.tiktokdemo.lky.tiktokdemo.record.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.utils.DensityUtils;
import com.tiktokdemo.lky.tiktokdemo.R;

/**
 * Created by lky on 2017/5/2.
 */

public class ScaleRoundRectView extends View {

    private final int ROUND_RECT_COUNT = 45;
    private int[] mHeights = { DensityUtils.dp2px(20),DensityUtils.dp2px(27),DensityUtils.dp2px(23),
            DensityUtils.dp2px(34),DensityUtils.dp2px(42),DensityUtils.dp2px(36),DensityUtils.dp2px(32),
            DensityUtils.dp2px(41),DensityUtils.dp2px(21),DensityUtils.dp2px(27),DensityUtils.dp2px(16)};

    private int mMaxCount = 50;
    private int mSelectedCount = 15;

    private int mSelectedColor = 0x80F8CE17;
    private int mDefaultColor = 0x80FFFFFF;


    private boolean isDragging;


    private int mImageWidth;
    private int mImageHeight;
    private int mImagePositonX;
    private int mImagePositonY;

    private Bitmap mImageBitmap;

    private int mProgress;

    private OnDragListener mOnDragListener;


    public ScaleRoundRectView(Context context) {
        this(context,null);
    }

    public ScaleRoundRectView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleRoundRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleRoundRectView,defStyleAttr,0);
        mSelectedColor = array.getColor(R.styleable.ScaleRoundRectView_ScaleRoundRectSelectedColor, 0x80F8CE17);
        mDefaultColor = array.getColor(R.styleable.ScaleRoundRectView_ScaleRoundRectDefaultColor,0x80FFFFFF);
        mImageWidth = array.getDimensionPixelOffset(R.styleable.ScaleRoundRectView_ScaleRoundRectImageWidth,DensityUtils.dp2px(44));
        mImageHeight = array.getDimensionPixelOffset(R.styleable.ScaleRoundRectView_ScaleRoundRectImageHeight,DensityUtils.dp2px(22));
        mImageBitmap = BitmapFactory.decodeResource(context.getResources(), array.getResourceId(R.styleable.ScaleRoundRectView_ScaleRoundRectImageRes,R.mipmap.chaopai_luzhi_jianyinyuedian));
    }

    public void setMax(int maxCount) {
        mMaxCount = maxCount;
        mImagePositonX = 0;
        invalidate();
    }

    public void setSelectedCount(int selectedCount){
        mSelectedCount = selectedCount;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mImagePositonX = (int) (mProgress/(float)mMaxCount*mRoundViewWidth);
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        mOnDragListener = onDragListener;
    }

    int mParentMargin;
    float mRoundViewWidth;
    private float mMoveX;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mParentMargin = mImageWidth/2;//全部刻度和整个控件外面的边距
        mRoundViewWidth = getMeasuredWidth()-mParentMargin*2;//全部刻度的占有区域的大小
        mImagePositonX = (int) (mProgress/(float)mMaxCount*mRoundViewWidth);
        mImagePositonY = getMeasuredHeight()-mImageHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eX = event.getX();
        float eY = event.getY();
        if(event.getAction() == MotionEvent.ACTION_DOWN
                && (eX > mImagePositonX && eX < mImagePositonX+mImageWidth)
                && (eY > mImagePositonY && eY < mImagePositonY+mImageHeight)){
            isDragging = true;
            mMoveX = eX;
        }
        if(isDragging){
            switch (event.getAction()){
                case MotionEvent.ACTION_MOVE:
                    mImagePositonX += event.getX() - mMoveX;
                    if(mImagePositonX < 0){
                        mImagePositonX = 0;
                    }
                    if(mOnDragListener != null){
                        float selectedWidth = (getMeasuredWidth()-mParentMargin*2)*mSelectedCount/(float)mMaxCount;//整个选中区域的宽度
                        if(mImagePositonX + selectedWidth + mParentMargin > mRoundViewWidth + mParentMargin){//超过右边区域，限制
                            mProgress = (int) ((mRoundViewWidth - selectedWidth)/mRoundViewWidth*mMaxCount);
                            mOnDragListener.onPositionChange(mProgress);
                        }else{
                            mProgress = (int) (mImagePositonX/mRoundViewWidth*mMaxCount);
                            mOnDragListener.onPositionChange(mProgress);
                        }
                    }
                    mMoveX = event.getX();
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isDragging = false;
                    float selectedWidth = (getMeasuredWidth()-mParentMargin*2)*mSelectedCount/(float)mMaxCount;//整个选中区域的宽度
                    if(mImagePositonX + selectedWidth + mParentMargin > mRoundViewWidth + mParentMargin){//超过右边区域，限制
                        mImagePositonX = (int) (mRoundViewWidth - selectedWidth);
                    }
                    if(mOnDragListener != null){
                        mOnDragListener.onChangeUp(mImagePositonX/mRoundViewWidth*mMaxCount);
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        int parentMargin = mImageWidth/2;//全部刻度和整个控件外面的边距
//        float roundViewWidth = getMeasuredWidth()-parentMargin*2;//全部刻度的占有区域的大小
        float selectedWidth = (getMeasuredWidth()-mParentMargin*2)*mSelectedCount/(float)mMaxCount;//整个选中区域的宽度

        int tempImagePositionX = mImagePositonX;//超过左边区域，限制
        if(tempImagePositionX + selectedWidth + mParentMargin > mRoundViewWidth + mParentMargin){//超过右边区域，限制
            tempImagePositionX = (int) (mRoundViewWidth  - selectedWidth);
        }

        float baseWidth = mRoundViewWidth/ROUND_RECT_COUNT;//每个刻度应该分配的大小
        float roundRectMargin = mRoundViewWidth/ROUND_RECT_COUNT/4f;//每个刻度分配的大小的间距

//        int centerY = (getMeasuredHeight()-mImageHeight)/2 + mImageHeight;
        int centerY = (getMeasuredHeight()-mImageHeight)/2;

        Rect rect = new Rect(tempImagePositionX,mImagePositonY,tempImagePositionX+mImageWidth,mImagePositonY+mImageHeight);
        canvas.drawBitmap(mImageBitmap,null,rect,new Paint());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //画刻度
        for(int i=0;i<ROUND_RECT_COUNT;i++){
            int index = i%mHeights.length;

            float positionPx = baseWidth*i+mParentMargin;
            RectF rectF = new RectF(positionPx,centerY - mHeights[index]/2,positionPx + baseWidth - roundRectMargin,centerY + mHeights[index]/2);
            float centerPositionX = tempImagePositionX + mParentMargin;

            if(rectF.left < centerPositionX && rectF.right > centerPositionX){//选中区域刚好分隔的rect，左边
                float colorDivision = (centerPositionX-rectF.left)/(baseWidth-roundRectMargin);
                Shader shader = new LinearGradient(rectF.left, rectF.bottom, rectF.right, rectF.bottom,
                        new int[] { mDefaultColor, mSelectedColor},
                        new float[]{colorDivision,colorDivision}, Shader.TileMode.CLAMP);
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(shader);
            }else if(rectF.left < centerPositionX+selectedWidth && rectF.right > centerPositionX+selectedWidth){//选中区域刚好分隔的rect，右边
                float colorDivision = (centerPositionX+selectedWidth-rectF.left)/(baseWidth-roundRectMargin);
                Shader shader = new LinearGradient(rectF.left, rectF.bottom, rectF.right, rectF.bottom,
                        new int[] { mSelectedColor,mDefaultColor},
                        new float[]{colorDivision,colorDivision}, Shader.TileMode.CLAMP);
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(shader);
            }else if(rectF.left >= centerPositionX && rectF.right <= centerPositionX + selectedWidth){//选中区域里边
                paint.setShader(null);

                paint.setColor(mSelectedColor);
            }else{//选中区域外边
                paint.setShader(null);
                paint.setColor(mDefaultColor);
            }
            canvas.drawRoundRect(rectF,positionPx/2,positionPx/2,paint);
        }
    }

    public interface OnDragListener{
        void onPositionChange(int position);
        void onChangeUp(float position);
    }
}
