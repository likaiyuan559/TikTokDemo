package com.heyhou.social.video;

/**
 * Created by DELL on 2017/8/21.
 */

public class WaveInfo {
    private String wavePath;
    private long startTime;
    private double volume;

    private long jump_start_time;
    private long jump_end_time;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public String getWavePath() {
        return wavePath;
    }

    public void setWavePath(String path) {
        this.wavePath = path;
    }

    public long getJump_start_time() {
        return jump_start_time;
    }

    public void setJump_start_time(long time) {
        this.jump_start_time = time;
    }

    public long getJump_end_time() {
        return jump_end_time;
    }

    public void setJump_end_time(long time) {
        this.jump_end_time = time;
    }
}
