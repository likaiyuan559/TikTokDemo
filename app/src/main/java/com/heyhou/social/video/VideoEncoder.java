package com.heyhou.social.video;

import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

/**
 * Created by starjiang on 4/28/17.
 */


public class VideoEncoder {
    private static final String TAG = "VideoEncoder";
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private MediaCodec mediaCodec;
    private MediaCodec.BufferInfo bufferInfo;
    private static final int IFRAME_INTERVAL = 1;           // 1 seconds between I-frames
    private int videoWidth;
    private int videoHeight;
    private int frameRate;
    private int bitRate;
    private VideoEncodeCallback callback;

    private boolean isStart;

    private byte[] info;
    private ByteBuffer encodeBuffer;
    private int frameCount;


    public VideoEncoder(int frameRate,int bitRate, int width, int height,VideoEncodeCallback callback) {

        bufferInfo = new MediaCodec.BufferInfo();
        videoWidth = width;
        videoHeight = height;
        this.callback = callback;
        this.frameRate = frameRate;
        this.bitRate = bitRate;
        this.isStart = false;
        encodeBuffer = ByteBuffer.allocateDirect(1024*1024);
        getSupportColorFormat();
        frameCount = 0;


    }

    static public int getSupportColorFormat() {

        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals("video/avc")) {
                    found = true;
                }
            }
            if (!found)
                continue;
            codecInfo = info;
        }

        // Find a color profile that the codec supports
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType("video/avc");

        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            Log.e(TAG,"format="+capabilities.colorFormats[i]);
            switch (capabilities.colorFormats[i]) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                    Log.e(TAG, "supported color format " + capabilities.colorFormats[i]);
                    return capabilities.colorFormats[i];
                default:
                    Log.e(TAG, "unsupported color format " + capabilities.colorFormats[i]);
                    break;
            }
        }

        return -1;
    }


    public void startEncode(){
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, videoWidth, videoHeight);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, getSupportColorFormat());
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mediaCodec.start();
            isStart = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void offerData(byte [] buffer) {

        if(!isStart) return;

        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);

        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buffer);
            long timepts = 1000000*frameCount / frameRate;
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length, timepts, 0);
            frameCount++;
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

        while (outputBufferIndex >= 0) {

            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            Log.i(TAG, "outBufferIndex:" + outputBufferIndex+",size="+bufferInfo.size+",flag="+bufferInfo.flags+",buffer limit="+outputBuffer.limit());

            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                Log.e(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                info = new byte[bufferInfo.size];
                outputBuffer.get(info);
            } else if((bufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0){

                encodeBuffer.clear();
                encodeBuffer.put(info);
                encodeBuffer.put(outputBuffer);
                encodeBuffer.flip();

                Log.e(TAG,"encode buffer="+encodeBuffer.limit());
                if(callback != null) {
                    callback.onEncodeData(encodeBuffer, MediaCodec.BUFFER_FLAG_KEY_FRAME);
                }
            } else{
                if(callback != null) {
                    callback.onEncodeData(outputBuffer,0);
                }
            }

            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.e(TAG,"++++++++stream is end+++++++++++");
                break;      // out of while
            }
        }
    }

    public void stopEncode(){
        isStart = false;
        mediaCodec.stop();
        mediaCodec.release();

    }

    public interface VideoEncodeCallback{
        public void onEncodeData(ByteBuffer buffer, int keyFrame);
    }
}
