package com.heyhou.social.video;

/**
 * Created by Administrator on 2015/11/10 0010.
 */

public interface VideoListener {

    void onProgress(String outputPath, int percentage);
    void onComplete(String outputPath);
    void onError(String outputPath, String error);
 }