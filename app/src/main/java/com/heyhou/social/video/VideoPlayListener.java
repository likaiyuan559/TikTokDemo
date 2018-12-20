package com.heyhou.social.video;

/**
 * Created by Administrator on 2015/11/10 0010.
 */

public interface VideoPlayListener {

    /**
     * @param percentage buffering progress between 0-1
     */
    public void onBufferingEvent(float percentage);

    public void onPrepareEvent();
    /**
     * called when video playing,
     */
    public void onPlayEvent();

    public void onPauseEvent();

    public void onResumeEvent();

    public void onStopEvent();

    /**
     * called when stream end
     */
    public void onEndEvent();

    /**
     * called when have error
     */
    public void onErrorEvent(String msg);

    /**
     *
     * @param time current playing time,time unit:us
     */
    public void onPlayTimeEvent(long time);

}