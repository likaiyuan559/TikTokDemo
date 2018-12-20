package com.tiktokdemo.lky.tiktokdemo.record.camera.filter.helper;

import java.util.List;

/**
 * Created by lky on 2017/4/25.
 */

public class FilterInfo {


    /**
     * type : 0
     * scaleWidth : 12
     * scaleHight : 0
     * offsetX : 0.5
     * offsetY : 0.8
     * alignIndexes : [15,19,2]
     * folder : smoking
     * frames : 11
     * frameDuration : 110
     * width : 400
     * height : 249
     * triggerType : 1
     */

    private int type;
    private float scaleWidth;
    private float scaleHight;
    private float offsetX;
    private float offsetY;
    private String folder;
    private int frames;
    private int frameDuration;
    private int width;
    private int height;
    private int triggerType;
    private List<Integer> alignIndexes;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(float scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public float getScaleHight() {
        return scaleHight;
    }

    public void setScaleHight(float scaleHight) {
        this.scaleHight = scaleHight;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public int getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public List<Integer> getAlignIndexes() {
        return alignIndexes;
    }

    public void setAlignIndexes(List<Integer> alignIndexes) {
        this.alignIndexes = alignIndexes;
    }
}
