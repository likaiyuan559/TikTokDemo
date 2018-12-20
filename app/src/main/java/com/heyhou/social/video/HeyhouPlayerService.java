package com.heyhou.social.video;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;


/**
 * Created by Administrator on 2015/10/30 0030.
 */
final public class HeyhouPlayerService implements Runnable
{
    static private String TAG = "HeyhouPlayerService";
    static public HeyhouPlayerService instance = new HeyhouPlayerService();
    HeyhouPlayer mMediaPlayer;
    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
    String mUrl;
    Surface mSurface;
    VideoPlayListener mlistener;
    long mPosition;
    int mFilter;
    int mSpeed;

    private boolean isRunning;

    private HeyhouPlayerService() {
        super();
        mMediaPlayer = new HeyhouPlayer(AppUtil.getApplicationContext());
//        mHeyhouPlayerRender = new HeyhouPlayerRender(AppUtil.getApplicationContext(),glSurfaceView);
    }

    public void running(){
        if(isRunning){
            return ;
        }
        isRunning = true;
    }

    public void stopRun(){
        if(!isRunning){
            return ;
        }
        isRunning = false;

    }


    public void pause() {
        mMediaPlayer.pause();
    }

//    public void attachGLSurfaceView(GLSurfaceView glSurfaceView){
////        if(mHeyhouPlayerRender == null){
////
////            glSurfaceView.setRenderer(mHeyhouPlayerRender);
////        }
//        mMediaPlayer.attachRender(mHeyhouPlayerRender);
//    }
//
//
//    public void detachGLSurface(){
//        mMediaPlayer.detachRender();
//    }

//    public void attachSurface(Surface surface)
//    {
//        try
//        {
//            mSurface = surface;
//            queue.add("attachSurface");
//        }
//        catch (Exception e)
//        {
//
//        }
//
//    }

//    public void detachSurface()
//    {
//        try
//        {
//            queue.add("detachSurface");
//        }
//        catch (Exception e)
//        {
//
//        }
//
//    }

    public void prepareM(String url)
    {
        try {
            mUrl = url;
            queue.add("prepare");
        }
        catch (Exception e)
        {}
    }

    public void playM()
    {
        try {
            queue.add("play");
        }
        catch (Exception e)
        {}
    }

    public void stopM()
    {
        try {
            queue.add("stop");
        }
        catch (Exception e)
        {}
    }

    public void pauseM()
    {
        try {
            queue.add("pause");
        }
        catch (Exception e)
        {}
    }

    public void resumeM()
    {
        try {
            queue.add("resume");
        }
        catch (Exception e)
        {}
    }

    public void setListener(VideoPlayListener listener)
    {
        try {
            mlistener = listener;
            queue.add("setListener");

        }
        catch (Exception e)
        {}
    }

    public void removeListener()
    {
        try {
            queue.add("removeListener");

        }
        catch (Exception e)
        {}
    }

    public void setPosition(long position)
    {
        try {
            mPosition = position;
            queue.add("setPosition");
        }
        catch (Exception e)
        {}
    }

    public void setFilter(int filter)
    {
        try {
            mFilter = filter;
            queue.add("setFilter");
        }
        catch (Exception e)
        {}
    }

    public void setSpeed(int speed)
    {
        try {
            mSpeed = speed;
            queue.add("setSpeed");
        }
        catch (Exception e)
        {}
    }

    @Override
    public void run() {
        while (isRunning)
        {
            try
            {
                String event = queue.poll(100000, TimeUnit.MICROSECONDS);

                if(event == null){
                    continue;
                }

                if(event.equals("prepare"))
                {
                    Log.d(TAG,"------------prepare--------------");
                    mMediaPlayer.prepare(mUrl);
                }
                else if(event.equals("play"))
                {
                    Log.d(TAG,"play------------");
                    mMediaPlayer.play();
                }
                else if(event.equals("pause"))
                {
                    Log.d(TAG,"pause------------");
                    mMediaPlayer.pause();
                }
                else if(event.equals("resume"))
                {
                    Log.d(TAG,"resume------------");
                    mMediaPlayer.resume();
                }
                else if(event.equals("stop"))
                {
                    Log.d(TAG,"-------------stop------------");
                    mMediaPlayer.stop();
                }
//                else if(event.equals("attachSurface"))
//                {
//                    Log.d(TAG,"attach surface-----------");
//                    mMediaPlayer.attachSurface(mSurface);
//                }
//                else if(event.equals("detachSurface"))
//                {
//                    Log.d(TAG,"detach surface-----------");
//                    mMediaPlayer.detachSurface();
//                }
                else if(event.equals("setListener"))
                {
                    Log.d(TAG,"set listener-----------");
                    mMediaPlayer.setListener(mlistener);
                }
                else if(event.equals("removeListener"))
                {
                    Log.d(TAG,"remove listener-----------");
                    mMediaPlayer.removeListener();
                }
                else if(event.equals("setPosition"))
                {
                    Log.d(TAG,"set position-----------");
                    mMediaPlayer.setPosition(mPosition);
                }
                else if(event.equals("setFilter"))
                {
                    Log.d(TAG,"set filter-----------");
                    mMediaPlayer.setFilter(mFilter);
                }
                else if(event.equals("setSpeed"))
                {
                    Log.d(TAG,"set speed-----------");
                    mMediaPlayer.setSpeed(mSpeed);
                }
            }
            catch (Exception e)
            {
//                e.printStackTrace();
//                Log.d(TAG, e.getMessage());

            }
        }
    }

    public void attachRender(GLSurfaceView.Renderer render){
        mMediaPlayer.attachRender(render);
    }

    public void detachRender(){
        mMediaPlayer.detachRender();
    }

    public long getDuration()
    {
        return mMediaPlayer.getDuration();
    }

    public long getCurrentTime()
    {
        return mMediaPlayer.getCurrentTime();
    }
    public void setBufferTime(long bufferTime)
    {
        mMediaPlayer.setBufferTime(bufferTime);
    }
    public boolean isSeekable()
    {
        return  mMediaPlayer.isSeekable();
    }
    public boolean isPlaying()
    {
        return mMediaPlayer.isPlaying();
    }

}