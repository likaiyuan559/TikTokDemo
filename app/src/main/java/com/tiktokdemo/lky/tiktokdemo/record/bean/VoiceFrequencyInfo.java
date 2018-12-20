package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.Serializable;

import android.graphics.Path;

/**
 * Created by lky on 2017/8/21.
 */

public class VoiceFrequencyInfo implements Serializable {

    private final int MIN_CAPACITY_INCREMENT = 6;

    private long mStartPosition;
    private long mEndPosition;
    private long mPositionSize;
    private short[] mAudioData;
    private transient Path mPath;

    public VoiceFrequencyInfo() {
        mAudioData = new short[0];
    }

    public VoiceFrequencyInfo(long startPosition, long endPosition) {
        mStartPosition = startPosition;
        mEndPosition = endPosition;
        mPositionSize = mEndPosition - mStartPosition;
        mAudioData = new short[0];
    }

    public void setStartPosition(long startPosition) {
        mStartPosition = startPosition;
    }

    public void setEndPosition(long endPosition) {
        mEndPosition = endPosition;
    }

    public void setPositionSize(long positionSize) {
        mPositionSize = positionSize;
    }

    public Long getStartPositionFromLong() {
        return mStartPosition;
    }

    public long getStartPosition() {
        return mStartPosition;
    }

    public long getEndPosition() {
        return mEndPosition;
    }

    public long getPositionSize() {
        return mPositionSize;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public Path getPath() {
        return mPath;
    }

    public void setAudioData(short[] audioData) {
        mAudioData = audioData;
        size = mAudioData.length;
    }

    public short[] getAudioData() {
        return mAudioData;
    }

    private int size;

    public void addData(short[] data){
        int newPartSize = data.length;
        if (newPartSize == 0) {
            return ;
        }
        short[] a = mAudioData;
        int s = size;
        int newSize = s + newPartSize; // If add overflows, arraycopy will fail
        if (newSize > a.length) {
            int newCapacity = newCapacity(newSize - 1);  // ~33% growth room
            short[] newArray = new short[newCapacity];
            System.arraycopy(a, 0, newArray, 0, s);
            mAudioData = a = newArray;
        }
        System.arraycopy(data, 0, a, s, newPartSize);
        size = newSize;
    }

//    public void addDataFromMagnify(short[] data){
//        int newPartSize = data.length;
//        if (newPartSize == 0) {
//            return ;
//        }
//        short[] a = mAudioData;
//        int s = mAudioData.length;
//        short[] newArray = new short[mAudioData.length + data.length];
//        System.arraycopy(a, 0, newArray, 0, s);
//        mAudioData = a = newArray;
//        System.arraycopy(data, 0, a, s, newPartSize);
//        size = mAudioData.length;
//    }

    public int getDataSize(){
        return size;
    }

    private int newCapacity(int currentCapacity) {
        int increment = (currentCapacity < (MIN_CAPACITY_INCREMENT / 2) ?
                MIN_CAPACITY_INCREMENT : currentCapacity >> 1);
        return currentCapacity + increment;
    }
}
