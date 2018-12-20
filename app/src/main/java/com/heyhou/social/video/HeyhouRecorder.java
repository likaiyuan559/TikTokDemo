package com.heyhou.social.video;

import java.nio.ByteBuffer;

/**
 * Created by starjiang on 4/14/17.
 */

public class HeyhouRecorder {

    public final static int FORMAT_NV21 = 1;
    public final static int FORMAT_RGBA = 2;
    public final static int FORMAT_ABGR = 3;
    public final static int FORMAT_BGRA = 4;
    public final static int FORMAT_ARGB = 5;
    public final static int FORMAT_YUV420 = 6;
    public final static int FORMAT_NV12 = 7;
    public final static int FORMAT_YV12 = 8;

    public final static int FORMAT_S16 = 1;
    public final static int FORMAT_FLTP = 2;

    public final static int SPEED_SLOWEST = -3;
    public final static int SPEED_SLOWER = -2;
    public final static int SPEED_NORMAL = 1;
    public final static int SPEED_FASTER = 2;
    public final static int SPEED_FASTEST = 3;

    public final static int ROTATE_NONE = 0;
    public final static int ROTATE_90 = 90;
    public final static int ROTATE_180 = 180;
    public final static int ROTATE_270 = 270;

    private int videoWidth;
    private int videoHeight;
    private int videoSpeed;
    private int videoFrameCount;
    private int colorFormat;

    private static volatile HeyhouRecorder instance = null;

    static {
        System.loadLibrary("openh264");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("yuv");
        System.loadLibrary("heyhou_video");

    }

    private void HeyhouRecorder(){

    }

    public static HeyhouRecorder getInstance(){

        if(instance == null){
            synchronized (HeyhouRecorder.class){
                if(instance == null){
                    instance = new HeyhouRecorder();
                }
            }
        }
        return instance;
    }

    /**
     * @param output recording video output path
     * @param videoFrameRate video framerate
     * @param videoBitRate video bitrate bps
     * @param width video width
     * @param height video height
     * @param speed video speed -4 very slow -2 slow 1 normal 2 fast 4 very fast
     * @return 0 start success <0 fail
     */
    public int startRecord(String output, int videoFrameRate, int videoBitRate, int width, int height, int speed){

        return  HeyhouRecorder.start(output,videoFrameRate,videoBitRate,width,height,speed);

    }

    public int stopRecord(){
        return HeyhouRecorder.stop();
    }

    public int recordStatus(){
        return HeyhouRecorder.getRecordStatus();
    }

    /**
     * software encode by ffmpeg libx264
     * @param data rgba data ByteBuffer
     * @param width picture width
     * @param height picture height
     * @param rowPadding picture rowPadding in some mobile phone
     * @param fmt 1 NV21 2 RGBA 3 ABGR
     * @param rotate video need rotate
     * @return 0 record success
     */
    public int recordVideoNHW(ByteBuffer data, int width, int height, int rowPadding, int fmt, int rotate){
        return HeyhouRecorder.recordVideo(data,width,height,rowPadding,fmt,rotate);
    }

    /**
     * @param output recording video output path
     * @param videoFrameRate video framerate
     * @param videoBitRate video bitrate bps
     * @param width video width
     * @param height video height
     * @param speed video speed -4 very slow -2 slow 1 normal 2 fast 4 very fast
     * @return 0 start success <0 fail
     */
    static private native int start(String output, int videoFrameRate, int videoBitRate, int width, int height, int speed);

    /**
     *
     * @param data rgba data ByteBuffer
     * @param width picture width
     * @param height picture height
     * @param rowPadding picture rowPadding in some mobile phone
     * @param fmt 1 NV21 2 RGBA 3 ABGR
     * @param rotate video need rotate
     * @return 0 record success
     */
    static private native int recordVideo(ByteBuffer data, int width, int height, int rowPadding, int fmt, int rotate);

    static private native int writeVideo(ByteBuffer data, int keyFrame);

    static public native int convertTo420(ByteBuffer src, byte [] dst, int srcWidth, int srcHeight, int dstWidth, int dstHeight, int rowPadding, int rotate, int srcFmt);

    static public native int convertFrom420(ByteBuffer src, byte [] dst, int srcWidth, int srcHeight, int dstWidth, int dstHeight, int dstFmt);


    /**
     *
     * @param data 16bit 1channel 44100hz audio pcm data
     * @param samples sample number data.length/2
     * @param sampleRate audio sample rate
     * @param fmt 1 ENCODING_PCM_16BIT 2 ENCODING_PCM_FLOAT
     * @return 0 record success
     */
    public int recordAudioNHW(byte []data,int sampleRate,int fmt,int samples){
        return HeyhouRecorder.recordAudio(data,sampleRate,fmt,samples);
    }


    /**
     *
     * @param data 16bit 1channel 44100hz audio pcm data
     * @param samples sample number data.length/2
     * @param sampleRate audio sample rate
     * @param fmt 1 ENCODING_PCM_16BIT 2 ENCODING_PCM_FLOAT
     * @return 0 record success
     */
    static private native int recordAudio(byte []data,int sampleRate,int fmt,int samples);

    /**
     * stop recrod
     */
    static private native int stop();

    /**
     * get recorder status
     */
    static private native int getRecordStatus();

}
