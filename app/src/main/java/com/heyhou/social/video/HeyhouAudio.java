package com.heyhou.social.video;

import java.util.ArrayList;

/**
 * Created by DELL on 2017/8/17.
 */

public class HeyhouAudio {

    private static volatile HeyhouAudio instance = null;

    static {
        System.loadLibrary("openh264");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("yuv");
        System.loadLibrary("heyhou_video");

    }

    public static HeyhouAudio getInstance(){

        if(instance == null){
            synchronized (HeyhouAudio.class){
                if(instance == null){
                    instance = new HeyhouAudio();
                }
            }
        }
        return instance;
    }

    protected  HeyhouAudio(){

    }

    /**
     * 加载bgm
     * @param audioPath 要加载的mp3
     * @param wavePath 输出的wav 文件
     * @param listener 回调
     * @return 0 成功，not 0 fail
     */
    public native int audioRead(String audioPath, String wavePath, AudioListener listener);

    /**
     * 合成音乐
     * @param waveInfos 音轨文件信息数组
     * @param audioPath 要输出的文件 输出文件后缀为 aac
     */
    public native int audioWrite(ArrayList<WaveInfo> waveInfos, String audioPath);

    /**
     * 剪切音乐
     * @param wavePath
     * @param starttime 剪切的开始时间
     * @param endtime 结束时间
     */
    public native int audioCut(String wavePath, long starttime, long endtime);

    /**
     * 剪切音乐
     * @param wavePath
     * @param starttime 剪切的开始时间
     * @param endtime 结束时间
     */
    public native int audioMove(String wavePath, String newWavePath, long starttime, long endtime, long newstarttime, long newendtime);

    /**
     * 录音初始化
     * @param wavePath 录音输出的文件路径
     */
    public native int waveRecordInit(String wavePath);

    /**
     *
     * @param data audiorecorder录制的数据，采样双声道，16bit 采样
     * @param offset 录制音乐的起始位置
     * @param samples 采样个数
     * @return
     */
    public native short[] waveRecord(byte []data,long offset,int samples);

    /**
     * 停止录制
     */
    public native int waveRecordStop();


    /**
     * 播放合成的音乐
     * @param waveInfos
     */
    public native int wavePlayStart(ArrayList<WaveInfo> waveInfos, WavePlayListener listener);

    /**
     * 停止播放合成的音乐
     */
    public native int wavePlayStop();
}
