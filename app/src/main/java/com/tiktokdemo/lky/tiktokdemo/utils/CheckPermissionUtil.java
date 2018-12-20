package com.tiktokdemo.lky.tiktokdemo.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by lky on 2017/5/16.
 */

public class CheckPermissionUtil {

    /**
     * 判断是是否有录音权限
     */
    public static boolean isHasAudioPermission(final Context context){
        int bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord =  new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
        //开始录制音频
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
//            AVLogUtils.e(TAG, Log.getStackTraceString(e));
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING
                && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
//            AVLogUtils.e(TAG, "audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING : " + audioRecord.getRecordingState());
            return false;
        }

        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            //如果短时间内频繁检测，会造成audioRecord还未销毁完成，此时检测会返回RECORDSTATE_STOPPED状态，再去read，会读到0的size，可以更具自己的需求返回true或者false
            return false;
        }

        byte[] bytes = new byte[1024];
        int readSize = audioRecord.read(bytes, 0, 1024);
        if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize <= 0) {
//            AVLogUtils.e(TAG, "readSize illegal : " + readSize);
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }

}
