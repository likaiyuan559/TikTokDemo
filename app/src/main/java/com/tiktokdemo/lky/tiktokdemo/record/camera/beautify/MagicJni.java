package com.tiktokdemo.lky.tiktokdemo.record.camera.beautify;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;

/**
 * Created by why8222 on 2016/2/29.
 */
public class MagicJni {
    static{
        System.loadLibrary("hh_magic_camera");
    }

    public static native void jniInitMagicBeautify(ByteBuffer handler);
    public static native void jniUnInitMagicBeautify();

    public static native void jniStartSkinSmooth(float denoiseLevel);
    public static native void jniStartWhiteSkin(float whitenLevel);

    public static native ByteBuffer jniStoreBitmapData(Bitmap bitmap);
    public static native void jniFreeBitmapData(ByteBuffer handler);
    public static native Bitmap jniGetBitmapFromStoredBitmapData(ByteBuffer handler);
}
