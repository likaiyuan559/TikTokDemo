package com.heyhou.social.video;


import java.util.ArrayList;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2015/11/10 0010.
 */
public class HeyhouVideo {
    static {
        System.loadLibrary("openh264");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("yuv");
        System.loadLibrary("heyhou_video");

    }
    /**
     * combine video and audio to new video
     * @param videoInfos video list
     * @param audioPath audio path
     * @param weight1 video audio volume
     * @param weight2 audio volume
     * @param outputVideoPath output path
     *@return void
     */
    public native void combine(ArrayList<VideoInfo> videoInfos, String audioPath, double weight1, double weight2, String outputVideoPath, VideoListener listener);


    /**
     * reverse video
     * @param videoPath video path
     * @param outPath output path
     * @param tmpPath frame tmp cache path must be a directory
     *@return void
     */
    public native void reverse(String videoPath, String outPath, String tmpPath, VideoListener listener);


    /**
     * filter video
     * @param videoPath video path
     * @param outPath output path
     * @param filterinfos array
     *@return void
     */
    public native void filter(String videoPath, ArrayList<FilterInfo> filterInfos, String outPath, VideoListener listener);

    /**
     * overlay video
     * @param videoPath video path
     * @param outPath output path
     * @param x video audio volume
     * @param y audio volume
     * @param overlayPath overlay file path
     *@return void
     */
    public native void overlay(String videoPath, String outPath, String overlayPath, int x, int y, VideoListener listener);


    /**
     * get video frame
     * @param videoPath video path
     * @param seconds get the seconds frame
     * @param bitmap video frame bitmap
     *@return int 0 success not 0 fail
     */
    public native void getFrameBitmap(String videoPath, long seconds, Bitmap bitmap, int rotate);



    /**
     * get video meta info
     * @param videoPath video path
     * @param metaInfo video frame bitmap
     *@return int 0 success not 0 fail
     */
    public native void getMetaInfo(String videoPath, VideoMetaInfo metaInfo);


    /**
     * cut video
     * @param videoPath video path
     * @param outPath output path
     * @param starttime cut starttime
     * @param seconds need micro seconds
     * @param speed dst video speed
     *@return void
     */
    public native void cutVideo(String videoPath, long starttime, long seconds, int speed, String outPath, VideoListener listener);

    /**
     * speed audio
     * @param audioPath audio path
     * @param speed audio speed -4 -2 1 2 4
     *@return void
     */
    public native void speed(String audioPath, int speed, String outputPath, VideoListener listener);

    /**
     * cut audio
     * @param audioPath audio path
     * @param starttime audio starttime micro second
     * @param needtime audio needtime micro second
     *@return void
     */
    public native void cut(String audioPath, long starttime, long needtime, String outputPath, VideoListener listener);

}
