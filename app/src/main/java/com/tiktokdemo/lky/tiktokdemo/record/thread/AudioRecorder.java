package com.tiktokdemo.lky.tiktokdemo.record.thread;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.heyhou.social.video.HeyhouRecorder;
import com.tiktokdemo.lky.tiktokdemo.utils.AppUtil;
import com.tiktokdemo.lky.tiktokdemo.utils.CheckPermissionUtil;
/**
 * Created by lky on 2018/12/13
 * 音频录制的线程
 * ！！！一开始就会启动，为了实时的进行录制，录制进度以这个音频为准！！！
 * ！！！如果没有录音权限，将会限制录制！！！
 */
public class AudioRecorder extends Thread {
    final int audioSampleRate = 44100;//采样率
    private boolean isRecord = true;//是否进行录制
    private boolean isAudioRecordWrite;//是否写入文件
    private OnAudioRecorderListener mOnAudioRecorderListener;
    private boolean isAudioPermission;//是否有权限

    public AudioRecorder() {
        isAudioPermission = CheckPermissionUtil.isHasAudioPermission(AppUtil.getApplicationContext());
    }

    public void setOnAudioRecorderListener(OnAudioRecorderListener onAudioRecorderListener) {
        mOnAudioRecorderListener = onAudioRecorderListener;
    }

    public void setAudioRecordWrite(boolean audioRecordWrite) {
        isAudioRecordWrite = audioRecordWrite;
    }

    public void startRecord(){
        start();
    }

    public void stopRecord(){
        isRecord = false;
    }

    public boolean isAudioPermission() {
        return isAudioPermission;
    }

    @Override
    public void run() {
        try {
            //初始化音频
            int bufferSizeInBytes = AudioRecord
                    .getMinBufferSize(audioSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            final AudioRecord
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, audioSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
            if(audioRecord == null){
                mOnAudioRecorderListener.onNotPermission();
                return ;
            }
            audioRecord.startRecording();

            /**
             * 根据开始录音判断是否有录音权限
             */
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING
                    && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
//            AVLogUtils.e(TAG, "audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING : " + audioRecord.getRecordingState());
                isAudioPermission = false;
            }

            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
                //如果短时间内频繁检测，会造成audioRecord还未销毁完成，此时检测会返回RECORDSTATE_STOPPED状态，再去read，会读到0的size，可以更具自己的需求返回true或者false
                isAudioPermission = false;
            }

            if(!isAudioPermission){
                mOnAudioRecorderListener.onNotPermission();
                return ;
            }
            mOnAudioRecorderListener.onCanRecord(isAudioPermission);

            byte[] data = new byte[2048];
            while(isRecord){
                if(audioRecord == null){
                    return ;
                }
                int offset = 0;
                while(offset < 2048) {
                    int readSize = audioRecord.read(data, offset, data.length-offset);
                    offset+=readSize;
                }
                if(isAudioRecordWrite){//写入文件
                    HeyhouRecorder.getInstance().recordAudioNHW(data,audioSampleRate,HeyhouRecorder.FORMAT_S16,1024);
                }

            }
            audioRecord.stop();
            audioRecord.release();
        }catch (Exception e) {
            e.printStackTrace();
            mOnAudioRecorderListener.onRecordError("录音失败");
        }
    }

    public interface OnAudioRecorderListener{
        void onNotPermission();
        void onRecordError(String msg);
        void onCanRecord(boolean isCan);
    }
}
