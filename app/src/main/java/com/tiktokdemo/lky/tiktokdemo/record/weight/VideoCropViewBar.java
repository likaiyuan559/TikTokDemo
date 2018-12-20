package com.tiktokdemo.lky.tiktokdemo.record.weight;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.tiktokdemo.lky.tiktokdemo.record.bean.VideoFrameBean;
import com.heyhou.social.video.HeyhouVideo;
import com.heyhou.social.video.VideoMetaInfo;
import com.heyhou.social.video.VideoTimeType;


/**
 * Created by lky on 2017/7/14.
 */

public class VideoCropViewBar extends View {
//    /storage/emulated/0/360Videos/vrtest.mp4

    private final int SELECTED_MAX_TIME = 15000;
    private final int SELECTED_MAX_TIME_120 = 120000;

    private final int SELECTED_MIN_TIME = 3000;

    private int mFinalMaxTime = SELECTED_MAX_TIME;

    private long mFrameTime = 1000L;
    private final float SPEED_N1 = 1f;
    private final float SPEED_M3 = 0.33f;
    private final float SPEED_M2 = 0.5f;
    private final float SPEED_P3 = 3f;
    private final float SPEED_P2 = 2f;

    private float mCurrentSpeed = SPEED_N1;

    private String mVideoPath;
    private boolean isLoadEnd;

    private ArrayList<VideoFrameBean> mWaitLoadBitmapFrames;

    private VideoFrameBean mCurrentFrameBean;
    private VideoFrameBean mFirstFrameBean;
    private VideoFrameBean mLastFrameBean;

    private VideoFrameBean mReleaseFrameBean;

    private int mVideoWidth;        //视频宽度
    private int mVideoHeight;       //视频高度
    private int mVideoRotation;     //视频旋转参数
    private long mVideoDuration;    //视频持续时常
    private float mVideoScale;      //视频宽高比

    private float mViewFrameWidth;

    private boolean isMakeFail;     //是否数据解析失败了

    private VideoCropViewBarListener mVideoCropViewBarListener;


    //选取拖动参数，只与View的宽相关
    private float mSelectedMaxTime = SELECTED_MAX_TIME;//最大允许选取时间
    private float mSelectedStartTime = 0;//选取的开始时间
    private float mSelectedEndTime = mSelectedMaxTime;//选取的结束时间


    private HeyhouVideo mHeyhouVideo;


    public VideoCropViewBar(Context context) {
        this(context,null);
    }

    public VideoCropViewBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoCropViewBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeyhouVideo = new HeyhouVideo();
        mWaitLoadBitmapFrames = new ArrayList<>();
        new LoadThread().start();

    }

    public void releaseData(){
        mReleaseFrameBean = mCurrentFrameBean;
        synchronized (mWaitLoadBitmapFrames){
            mWaitLoadBitmapFrames.clear();
        }
        new ReleaseDataThread().start();
    }

    public float getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
        mReleaseFrameBean = mCurrentFrameBean;
        synchronized (mWaitLoadBitmapFrames){
            mWaitLoadBitmapFrames.clear();
        }
        resetData();
        try{
            VideoMetaInfo mateInfo = new VideoMetaInfo();
            mHeyhouVideo.getMetaInfo(mVideoPath,mateInfo);

            mVideoWidth = (mateInfo.rotation%180 != 0)?mateInfo.height:mateInfo.width;
            mVideoHeight = (mateInfo.rotation%180 != 0)?mateInfo.width:mateInfo.height;
            mVideoRotation = mateInfo.rotation;
            mVideoDuration = mateInfo.duration/1000L;
            if(mSelectedMaxTime > mVideoDuration * mCurrentSpeed){
                mSelectedMaxTime = mVideoDuration * mCurrentSpeed;
                mSelectedEndTime = mSelectedMaxTime;
            }
            if(mVideoCropViewBarListener != null){
                mVideoCropViewBarListener.rangeChange(0, (long) (mSelectedEndTime-mSelectedStartTime));
            }
            mVideoScale = mVideoWidth/(float)mVideoHeight;
            mViewFrameWidth = getMeasuredHeight()*mVideoScale;
            if(mVideoDuration == 0 || mVideoWidth == 0 || mVideoHeight == 0){
                if(mVideoCropViewBarListener != null){
                    mVideoCropViewBarListener.makeDataFail("data make fail");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            isMakeFail = true;
            if(mVideoCropViewBarListener != null){
                mVideoCropViewBarListener.makeDataFail(e.getMessage() == null?"not error message":e.getMessage());
            }
        }

        if(mVideoDuration == 0 || mVideoWidth == 0 || mVideoHeight == 0){
            isMakeFail = true;
        }
    }

    public void setSpeed(VideoTimeType videoTimeType){
        switch (videoTimeType){
            case SPEED_M4:
                mCurrentSpeed = SPEED_M3;
                break;
            case SPEED_M2:
                mCurrentSpeed = SPEED_M2;
                break;
            case SPEED_N1:
                mCurrentSpeed = SPEED_N1;
                break;
            case SPEED_P2:
                mCurrentSpeed = SPEED_P2;
                break;
            case SPEED_P4:
                mCurrentSpeed = SPEED_P3;
                break;
        }
        if(mVideoDuration / mCurrentSpeed > mFinalMaxTime){
            mSelectedMaxTime = mFinalMaxTime;
        }else{
            mSelectedMaxTime = mVideoDuration / mCurrentSpeed;
        }
        mSelectedEndTime = mSelectedMaxTime;
        mSelectedStartTime = 0;

        if(mVideoCropViewBarListener != null){
            mVideoCropViewBarListener.rangeChange(0, (long) (mSelectedEndTime-mSelectedStartTime));
        }
        resetData();
    }

    public void setFinalMaxTime(int finalMaxTime) {
        mFinalMaxTime = finalMaxTime;
        if(mViewFrameWidth != 0){
            mFrameTime = (long) (mFinalMaxTime/(getMeasuredWidth()/mViewFrameWidth * 1000L) * 1000L);//view的宽度表示15秒，算出每一帧应该截取的时间
        }
        if(mVideoDuration / mCurrentSpeed > mFinalMaxTime){
            mSelectedMaxTime = mFinalMaxTime;
        }else{
            mSelectedMaxTime = mVideoDuration / mCurrentSpeed;
        }
        mSelectedEndTime = mSelectedMaxTime;
        mSelectedStartTime = 0;

        if(mVideoCropViewBarListener != null){
            mVideoCropViewBarListener.rangeChange(0, (long) (mSelectedEndTime-mSelectedStartTime));
        }
        resetData();
    }

    public void setVideoCropViewBarListener(VideoCropViewBarListener videoCropViewBarListener) {
        mVideoCropViewBarListener = videoCropViewBarListener;
    }


    private void touchMoveEditData(){
        if(isMakeFail){
            return ;
        }

        if(mCurrentFrameBean != null && mCurrentFrameBean.getFrameTime() == 0 && mExcursionX < 0){

            mExcursionX = 0;
            if(mVideoCropViewBarListener != null){
                mVideoCropViewBarListener.touchChange((long) ((mCurrentFrameBean == null?0: mCurrentFrameBean.getFrameTime() + (mExcursionX / mViewFrameWidth * (mFrameTime*mCurrentSpeed)))+mSelectedStartTime*mCurrentSpeed));
            }
            if(mSrcollHandler.hasMessages(SRCOLL_MESSAGE)){
                mSrcollHandler.removeMessages(SRCOLL_MESSAGE);
            }
            return ;
        }
        if(mVideoDuration != 0 && ((mLastFrameBean != null && mLastFrameBean.getFrameTime() + (mFrameTime * mCurrentSpeed) > mVideoDuration && mExcursionX > 0)
                || (mCurrentFrameBean != null && mCurrentFrameBean.getFrameTime() + (mFrameTime * mCurrentSpeed) > mVideoDuration && mExcursionX > 0))
                || (mCurrentFrameBean != null && mCurrentFrameBean.getFrameTime() + (mSelectedEndTime-mSelectedStartTime) * mCurrentSpeed + (mFrameTime * mCurrentSpeed) > mVideoDuration && mExcursionX > 0)){
            mExcursionX = 0;
            if(mVideoCropViewBarListener != null){
                mVideoCropViewBarListener.touchChange((long) ((mCurrentFrameBean == null?0: mCurrentFrameBean.getFrameTime() + (mExcursionX / mViewFrameWidth * (mFrameTime*mCurrentSpeed)))+mSelectedStartTime*mCurrentSpeed));
            }
            if(mSrcollHandler.hasMessages(SRCOLL_MESSAGE)){
                mSrcollHandler.removeMessages(SRCOLL_MESSAGE);
            }
            return ;
        }


        if(mCurrentFrameBean == null){
            return ;
        }
        if(mViewFrameWidth >= 0){
            if(mExcursionX > mViewFrameWidth){
                synchronized (mCurrentFrameBean){
                    if(mCurrentFrameBean.next() == null){
                        VideoFrameBean frameBean = new VideoFrameBean();
                        frameBean.setFrameTime((long) (mCurrentFrameBean.getFrameTime() + (mFrameTime * mCurrentSpeed)));
                        frameBean.setPrev(mCurrentFrameBean);
                        mCurrentFrameBean.setNext(frameBean);
                        if(frameBean.getFrameTime() <= mVideoDuration) {
                            if (mLastFrameBean == null) {
                                mLastFrameBean = frameBean;
                            } else {
                                synchronized (mLastFrameBean) {
                                    if(mLastFrameBean.getFrameTime() < frameBean.getFrameTime()){
                                        frameBean.setPrev(mLastFrameBean);//关联链表，替换链表尾
                                        mLastFrameBean.setNext(frameBean);
                                        mLastFrameBean = frameBean;
                                    }
                                }
                            }
                        }
                        synchronized (mWaitLoadBitmapFrames){
                                mWaitLoadBitmapFrames.add(frameBean);
                        }
                    }
                    mCurrentFrameBean = mCurrentFrameBean.next();
                    if(mFirstFrameBean != null && mCurrentFrameBean != mFirstFrameBean.next() && mCurrentFrameBean.getFrameTime() > mFirstFrameBean.getFrameTime()){
                        removeDataHeader();
                    }
                }
                mExcursionX -= mViewFrameWidth;
            }else if(mExcursionX < -mViewFrameWidth && mCurrentFrameBean.getFrameTime() != 0){
                synchronized (mCurrentFrameBean){
                    if(mCurrentFrameBean.prev() == null){
                        VideoFrameBean frameBean = new VideoFrameBean();
                        frameBean.setFrameTime((long) (mCurrentFrameBean.getFrameTime() - (mFrameTime * mCurrentSpeed)));
                        if(frameBean.getFrameTime() < 0){
                            mExcursionX = 0;
                            return ;
                        }
                        frameBean.setNext(mCurrentFrameBean);
                        mCurrentFrameBean.setPrev(frameBean);
                        if(mFirstFrameBean == null){
                            mFirstFrameBean = frameBean;
                        }else{
                            synchronized (mFirstFrameBean){
                                if(mFirstFrameBean.getFrameTime() > frameBean.getFrameTime()){
                                    frameBean.setNext(mFirstFrameBean);//关联链表，替换链表头
                                    mFirstFrameBean.setPrev(frameBean);
                                    mFirstFrameBean = frameBean;
                                }
                            }
                        }



                        synchronized (mWaitLoadBitmapFrames){
                            mWaitLoadBitmapFrames.add(frameBean);
                        }
                    }
                    mCurrentFrameBean = mCurrentFrameBean.prev();
                        removeDataFooter();
                }
                mExcursionX += mViewFrameWidth;
            }
        }
        if(mVideoCropViewBarListener != null){
            mVideoCropViewBarListener.touchChange((long) ((mCurrentFrameBean == null?0: mCurrentFrameBean.getFrameTime() + (mExcursionX / mViewFrameWidth * (mFrameTime*mCurrentSpeed)))+mSelectedStartTime*mCurrentSpeed));
        }
    }

    private void removeDataHeader(){
        if(mFirstFrameBean == null){
            return ;
        }
        synchronized (mFirstFrameBean){
            VideoFrameBean videoFrameBean = mFirstFrameBean.next();
            if(videoFrameBean == null){
                return ;
            }
            videoFrameBean.setPrev(null);
            mFirstFrameBean.setPrev(null);
            mFirstFrameBean.setNext(null);
            if(mFirstFrameBean.getBitmap() != null && !mFirstFrameBean.getBitmap().isRecycled()){
                mFirstFrameBean.getBitmap().recycle();
                mFirstFrameBean.setBitmap(null);
            }
            mFirstFrameBean = videoFrameBean;
        }
    }

    private void removeDataFooter(){
        if(mLastFrameBean == null){
            return ;
        }
        synchronized (mLastFrameBean){
            VideoFrameBean videoFrameBean = mLastFrameBean.prev();
            if(videoFrameBean == null){
                return ;
            }
            videoFrameBean.setNext(null);
            mLastFrameBean.setPrev(null);
            mLastFrameBean.setNext(null);
            if(mLastFrameBean.getBitmap() != null && !mLastFrameBean.getBitmap().isRecycled()){
                mLastFrameBean.getBitmap().recycle();
                mLastFrameBean.setBitmap(null);
            }
            mLastFrameBean = videoFrameBean;
        }
    }


    private void resetData(){
        if(mFirstFrameBean == null){
            return ;
        }
        synchronized (mWaitLoadBitmapFrames){
            mWaitLoadBitmapFrames.clear();
        }
        VideoFrameBean videoFrameBean = mFirstFrameBean;
        if(mCurrentFrameBean != null){
            synchronized (mCurrentFrameBean){
                mCurrentFrameBean = null;
                mFirstFrameBean = null;
                mLastFrameBean = null;
                mExcursionX = 0;
            }
        }
        while (videoFrameBean != null){
            if(videoFrameBean.getBitmap() != null && !videoFrameBean.getBitmap().isRecycled()){
                videoFrameBean.getBitmap().recycle();
            }
            VideoFrameBean tempFrameBean = videoFrameBean.next();
            videoFrameBean.setNext(null);
            videoFrameBean = tempFrameBean;
            if(tempFrameBean != null){
                tempFrameBean.setPrev(null);
            }
        }
        if(mSrcollHandler.hasMessages(SRCOLL_MESSAGE)){
            mSrcollHandler.removeMessages(SRCOLL_MESSAGE);
        }
        invalidate();
    }

    private VelocityTracker vt; //手势加速检测
    private final int SRCOLL_MESSAGE = 501;
    private int mExcursionX;    //偏移的x
    private float mMoveX;

    private boolean isTouchSelected;//是否是选中了选取的区域
    private boolean isTouchRight = false;//true:选中了右边 false:选中了左边

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mMoveX = event.getX();
                if(mVideoCropViewBarListener != null){
                    mVideoCropViewBarListener.touchDown();
                }
                int startPx = (int) (mSelectedStartTime * (getMeasuredWidth()/(float)mFinalMaxTime));
                int endPx = (int) (mSelectedEndTime * (getMeasuredWidth()/(float)mFinalMaxTime));
                if(mMoveX >= startPx && mMoveX <= startPx+25){//拖动左边
                    isTouchSelected = true;
                    isTouchRight = false;
                    return true;
                }else if(mMoveX <= endPx && mMoveX >= endPx-25){//拖动右边
                    isTouchSelected = true;
                    isTouchRight = true;
                    return true;
                }


                if(vt==null){
                    //初始化velocityTracker的对象 vt 用来监测motionevent的动作
                    vt= VelocityTracker.obtain();
                }else{
                    vt.clear();
                }
                vt.addMovement(event);
                if(mSrcollHandler.hasMessages(SRCOLL_MESSAGE)){
                    mSrcollHandler.removeMessages(SRCOLL_MESSAGE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isTouchSelected){
                    if(isTouchRight){
                        mSelectedEndTime -= (mMoveX - event.getX()) / (getMeasuredWidth()/(float)mFinalMaxTime) ;
//                        Log.e("SelectedTime","mSelectedEndTime:" + mSelectedEndTime);
                        if(mSelectedEndTime <= mSelectedStartTime+SELECTED_MIN_TIME){
                            mSelectedEndTime = mSelectedStartTime+SELECTED_MIN_TIME;
                        }
                        if(mSelectedEndTime > mSelectedMaxTime){
                            mSelectedEndTime = mSelectedMaxTime;
                        }
                    }else{
                        mSelectedStartTime -= (mMoveX - event.getX()) / (getMeasuredWidth()/(float)mFinalMaxTime) ;
//                        Log.e("SelectedTime","mSelectedStartTime:" + mSelectedStartTime);
                        if(mSelectedStartTime+SELECTED_MIN_TIME >= mSelectedEndTime){
                            mSelectedStartTime = mSelectedEndTime-SELECTED_MIN_TIME;
                        }
                        if(mSelectedStartTime < 0){
                            mSelectedStartTime = 0;
                        }
                    }
                    if(mVideoCropViewBarListener != null){
                        mVideoCropViewBarListener.rangeChange((long) ((mCurrentFrameBean == null?0: mCurrentFrameBean.getFrameTime() + (mExcursionX / mViewFrameWidth * (mFrameTime*mCurrentSpeed)))+mSelectedStartTime*mCurrentSpeed), (long) (mSelectedEndTime-mSelectedStartTime));
                    }
                    mMoveX = event.getX();
                    invalidate();
                    return true;
                }

                vt.addMovement(event);
                //代表的是监测每1000毫秒手指移动的距离（像素）即m/px，这是用来控制vt的单位，若括号内为1，则代表1毫秒手指移动过的像素数即ms/px
                vt.computeCurrentVelocity(1000);
                mExcursionX += (int) (mMoveX - event.getX());
                touchMoveEditData();
//                Log.e("LoadThread","mExcursionX:" + mExcursionX + "," + mMoveX);
                mMoveX = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mVideoCropViewBarListener != null){
                    mVideoCropViewBarListener.touchUp();
                }
                if(isTouchSelected){
                    isTouchSelected = false;
                    return true;
                }
                if(Math.abs(vt.getXVelocity()) > 1000){
                    Message message = mSrcollHandler.obtainMessage();
                    int xVelocity = (int) vt.getXVelocity();
                    if(xVelocity > 8000){//限制速率，不至于太快或太慢
                        xVelocity = 8000;
                    }else if(xVelocity < -8000){
                        xVelocity = -8000;
                    }
                    message.arg1 = xVelocity;
                    message.what = SRCOLL_MESSAGE;
                    mSrcollHandler.sendMessage(message);
                }
                break;
        }
        return true;
    }


    /**
     * 加速度爆发后持续滚动多一段距离的Handler
     */
    private SrcollHandler mSrcollHandler = new SrcollHandler();
    class SrcollHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int srcollVt = msg.arg1;
            mExcursionX += -srcollVt / 100;
            touchMoveEditData();
//            Log.e("LoadThread","srcollVt:" + srcollVt + ",mExcursionX:" + mExcursionX + ",mMoveX:" + mMoveX);
            invalidate();
            if(srcollVt < 0){//左划
                Message message = mSrcollHandler.obtainMessage();
                message.arg1 = srcollVt + 100;
                message.what = SRCOLL_MESSAGE;
                if(srcollVt + 100 < -500){
                    mSrcollHandler.sendMessageDelayed(message,5);
                }else{
                }
            }else if(srcollVt > 0){//右划
                Message message = mSrcollHandler.obtainMessage();
                message.arg1 = srcollVt - 100;
                message.what = SRCOLL_MESSAGE;
                if(srcollVt - 100 > 500){
                    mSrcollHandler.sendMessageDelayed(message,5);
                }
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mVideoScale != 0f){
            mViewFrameWidth = getMeasuredHeight()*mVideoScale;
        }
        if(mVideoScale != 0){
            mViewFrameWidth = getMeasuredHeight()*mVideoScale;
        }
        if(mViewFrameWidth != 0){
            mFrameTime = (long) (mFinalMaxTime/(getMeasuredWidth()/mViewFrameWidth * 1000L) * 1000L);//view的宽度表示15秒，算出每一帧应该截取的时间
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isMakeFail){
            return ;
        }

        if(mCurrentFrameBean == null){
            VideoFrameBean videoFrameBean = new VideoFrameBean();
            videoFrameBean.setFrameTime(0);
//            mCurrentFrameBean = videoFrameBean;
            synchronized (mWaitLoadBitmapFrames){
                mWaitLoadBitmapFrames.add(videoFrameBean);
            }
            mCurrentFrameBean = videoFrameBean;
            mFirstFrameBean = videoFrameBean;
            synchronized (mWaitLoadBitmapFrames){
                mWaitLoadBitmapFrames.add(videoFrameBean);
            }
        }
        mViewFrameWidth = getMeasuredHeight()*mVideoScale;
        int frameSize = (int) (getMeasuredWidth()/mViewFrameWidth) + 2;//加2，预加载帧
        VideoFrameBean videoFrameBean;
        synchronized (mCurrentFrameBean){
            videoFrameBean = mCurrentFrameBean.prev();
            if(videoFrameBean == null){
                videoFrameBean = new VideoFrameBean();
                videoFrameBean.setFrameTime((long) (mCurrentFrameBean.getFrameTime() - mFrameTime * mCurrentSpeed));
                if(videoFrameBean.getFrameTime() <= 0){
                    videoFrameBean.setFrameTime(0);
                }
                videoFrameBean.setNext(mCurrentFrameBean);
                mCurrentFrameBean.setPrev(videoFrameBean);

                if(mFirstFrameBean == null){
                    mFirstFrameBean = videoFrameBean;
                }else{
                    synchronized (mFirstFrameBean){
                        if(mFirstFrameBean.getFrameTime() > videoFrameBean.getFrameTime()){
                            videoFrameBean.setNext(mFirstFrameBean);//关联链表，替换链表头
                            mFirstFrameBean.setPrev(videoFrameBean);
                            mFirstFrameBean = videoFrameBean;
                        }
                    }
                }
                synchronized (mWaitLoadBitmapFrames){
                    mWaitLoadBitmapFrames.add(videoFrameBean);
                }

            }
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        for(int i=-1;i<frameSize;i++){
            if(videoFrameBean == null){
                break;
            }
            if(videoFrameBean.getBitmap() != null && !videoFrameBean.getBitmap().isRecycled()){
                int left = (int) (mViewFrameWidth*i-mExcursionX);
                int right = (int) (mViewFrameWidth * (i+1)-mExcursionX);
                Rect srcRect = null;
                if(videoFrameBean.getFrameTime() + mFrameTime * mCurrentSpeed > mVideoDuration){
                    right = (int) (left + (mViewFrameWidth * (mVideoDuration-videoFrameBean.getFrameTime())/(mFrameTime*mCurrentSpeed)));
                    srcRect = new Rect(0,0, (int) (mViewFrameWidth * (mVideoDuration-videoFrameBean.getFrameTime())/(mFrameTime*mCurrentSpeed)),getMeasuredHeight());
                }
                Rect rect = new Rect(left,0,right,getMeasuredHeight());
                canvas.drawBitmap(videoFrameBean.getBitmap(),srcRect,rect,paint);
            }
            if(videoFrameBean.getFrameTime() > mVideoDuration){
                break;
            }
            if(videoFrameBean.next() == null){
                VideoFrameBean frameBean = new VideoFrameBean();
                frameBean.setFrameTime((long) (videoFrameBean.getFrameTime() + (mFrameTime * mCurrentSpeed)));
                videoFrameBean.setNext(frameBean);
                frameBean.setPrev(videoFrameBean);
                if(frameBean.getFrameTime() <= mVideoDuration){
                    synchronized (mCurrentFrameBean) {
                        if(mLastFrameBean == null){
                            mLastFrameBean = videoFrameBean;
                        }else{
                            synchronized (mLastFrameBean){
                                if(mLastFrameBean.getFrameTime() < videoFrameBean.getFrameTime()){
                                    videoFrameBean.setPrev(mLastFrameBean);//关联链表，替换链表尾
                                    mLastFrameBean.setNext(videoFrameBean);
                                    mLastFrameBean = videoFrameBean;
                                }
                            }
                        }
                    }
                    synchronized (mWaitLoadBitmapFrames){
                        mWaitLoadBitmapFrames.add(frameBean);
                    }
                }
                continue;
            }

            videoFrameBean = videoFrameBean.next();
        }


        paint.setColor(0xFFFFD217);
        int startPx = (int) (mSelectedStartTime * (getMeasuredWidth()/(float)mFinalMaxTime));
        int endPx = (int) (mSelectedEndTime * (getMeasuredWidth()/(float)mFinalMaxTime));

        int touchSize = 25;

        Rect leftRect = new Rect(startPx,0,startPx+touchSize,getMeasuredHeight());
        canvas.drawRect(leftRect,paint);

        Rect topRect = new Rect(startPx,0,endPx,10);
        canvas.drawRect(topRect,paint);

        Rect rightRect = new Rect(endPx-touchSize,0,endPx,getMeasuredHeight());
        canvas.drawRect(rightRect,paint);

        Rect bottomRect = new Rect(startPx,getMeasuredHeight()-10,endPx,getMeasuredHeight());
        canvas.drawRect(bottomRect,paint);

        paint.setColor(0xFFFFFFFF);
        for(int i=-1;i<3;i++){
            canvas.drawLine(startPx+touchSize/5,(getMeasuredHeight()/2+i*10),startPx+touchSize-touchSize/5,(getMeasuredHeight()/2+i*10),paint);
        }
        for(int i=-1;i<3;i++){
            canvas.drawLine(endPx-touchSize/5,(getMeasuredHeight()/2+i*10),endPx-touchSize+touchSize/5,(getMeasuredHeight()/2+i*10),paint);
        }

    }


    public Bitmap getVideoFrame(String videoPath, long frameNumber){
        if(isMakeFail){
            return Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
        }
        Bitmap bitmap = Bitmap
                .createBitmap((int)mViewFrameWidth, (int) (mViewFrameWidth/mVideoScale), Bitmap.Config.RGB_565);
        try{
            mHeyhouVideo.getFrameBitmap(videoPath,frameNumber,bitmap,mVideoRotation);
        }catch (Exception e){
              e.printStackTrace();
        }

        return bitmap;
    }


    /**
     * 加载线程
     */
    class LoadThread extends Thread {
        @Override
        public void run() {


            while (!isLoadEnd){
                if(isMakeFail){
                    return ;
                }
                synchronized (mWaitLoadBitmapFrames){
                    if(mWaitLoadBitmapFrames.isEmpty()){
                        continue;
                    }
                }

                VideoFrameBean videoFrameBean = null;

                synchronized (mWaitLoadBitmapFrames){
                    if(!mWaitLoadBitmapFrames.isEmpty()){
                        videoFrameBean = mWaitLoadBitmapFrames.remove(0);
                    }else{
                        videoFrameBean = null;
                    }
                }
                if(videoFrameBean != null){
                    //load bitmap
                    videoFrameBean.setBitmap(getVideoFrame(mVideoPath, videoFrameBean.getFrameTime() * 1000L));
                    postInvalidate();
                }
            }
        }
    }


    /**
     * 释放资源的线程
     */
    class ReleaseDataThread extends Thread {
        @Override
        public void run() {
            if(mReleaseFrameBean == null){
                return ;
            }
            synchronized (mReleaseFrameBean){
                if(mReleaseFrameBean == null){
                    return ;
                }
                VideoFrameBean prevBean = mReleaseFrameBean.prev();
                while (prevBean != null){
                    if(prevBean.getBitmap() != null && !prevBean.getBitmap().isRecycled()){
                        prevBean.getBitmap().recycle();
                        prevBean.setBitmap(null);
                    }
                    VideoFrameBean tempBean = prevBean;
                    prevBean = prevBean.prev();
                    tempBean.setPrev(null);
                    if(prevBean != null){
                        prevBean.setNext(null);
                    }
                }

                VideoFrameBean nextBean = mReleaseFrameBean.next();
                while (nextBean != null){
                    if(nextBean.getBitmap() != null && !nextBean.getBitmap().isRecycled()){
                        nextBean.getBitmap().recycle();
                        nextBean.setBitmap(null);
                    }
                    VideoFrameBean tempBean = nextBean;
                    nextBean = nextBean.next();
                    tempBean.setNext(null);
                    if(nextBean != null){
                        nextBean.setPrev(null);
                    }
                }
                mReleaseFrameBean = null;
            }
            isLoadEnd = true;
            System.gc();
        }
    }


    public interface VideoCropViewBarListener{
        void touchDown();
        void touchUp();
        void touchChange(long time);
        void rangeChange(long time, long range);
        void makeDataFail(String error);//解析数据失败
    }


}
