package com.heyhou.social.video;

/**
 * Created by DELL on 2017/8/31.
 */

public interface WavePlayListener {

    public void onPlayEvent();

    public void onPauseEvent();

    public void onResumeEvent();

    public void onStopEvent();

    public void onErrorEvent(String msg);

    public void onPlayTimeEvent(long time);
}
