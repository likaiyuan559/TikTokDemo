package com.heyhou.social.video;

/**
 * Created by starjiang on 12/8/16.
 */

public class VideoInfo {
    private String videoPath;
    private int times;//-4 -2 1 2 4

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
