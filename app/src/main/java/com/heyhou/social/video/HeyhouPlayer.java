package com.heyhou.social.video;

import android.content.Context;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;

/**
 * Created by Administrator on 2015/11/10 0010.
 */
public class HeyhouPlayer {
    static {

        System.loadLibrary("openh264");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("yuv");
        System.loadLibrary("heyhou_video");

    }
    private Context context;
    private AudioTrack audioTrack = null;

    public HeyhouPlayer(Context context)
    {
        this.context = context;
    }

    /**
     * prepare media,when media start play MoListener.onPlay() will called
     * @param path play url
     *@return void
     */
    public native void prepare(String path);

    /**
     * play the video
     */
    public native void play();

    /**
     * puase play, MoListener.onPause() will be called
     */
    public native void pause();

    /**
     * resume play,MoListener.onResume will be called
     */
    public native void resume();

    /**
     * stop play,MoListener.onStop() will be called
     */
    public native void stop();

    /**
     * @return  true or false,only media is playing return value is true,or is false
     *
     */
    public native boolean isPlaying();

//    /**
//     * attach surface
//     * @param surface,render Surface,used by video output
//     */
//    public native void attachSurface(Surface surface);
//
//    /**
//     * detath surface
//     */
//    public native void detachSurface();


    /**
     * attach surface
     * @param render,render GLSurfaceView render,used by video output
     */
    public native void attachRender(GLSurfaceView.Renderer render);

    /**
     * detath surface
     */
    public native void detachRender();

    /**
     * setListener,when play event happen,listener callback function will be called
     * @param listener
     */
    public native void setListener(VideoPlayListener listener);

    /**
     * remove the listener
     */
    public native void removeListener();

    /**
     * check the media if can seekable
     * @return true or false,true can seekable,false can not seekable
     */
    public native  boolean isSeekable();

    /**
     * set media posistion
     * @param position,time unit is micro second
     */
    public native void setPosition(long position);

    /**
     * set media filter
     * @param filter
     */
    public native void setFilter(int filter);

    /**
     * set media speed
     * @param speed
     */
    public native void setSpeed(int speed);

    /**
     * get media current postion
     * @return media postion,time unit is microsecond
     */
    public native long getPosition();

    /**
     * get media current play time
     * @return time unit is micro second
     */
    public native long getCurrentTime();

    /**
     * get media duration
     * @return time unit is micro second
     */
    public native long getDuration();

    /**
     * set network buffering time
     * @param bufferTime time unit is millisecond
     */
    public native void setBufferTime(long bufferTime);

}
