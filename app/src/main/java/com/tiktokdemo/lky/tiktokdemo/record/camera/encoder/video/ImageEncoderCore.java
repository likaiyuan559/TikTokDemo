package com.tiktokdemo.lky.tiktokdemo.record.camera.encoder.video;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

/**
 * ImageReader录制，我在这里将图片传回了主界面显示，你需要自己改装
 *
 * @author Created by jz on 2017/4/8 14:53
 */
public class ImageEncoderCore {
    private static final String TAG = "ImageEncoderCore";
    private static final boolean VERBOSE = true;

    private static final int MAX_IMAGE_NUMBER = 25;//这个值代表ImageReader最大的存储图像
    private static final int ENCODER_BITMAP = 0;

    private int mWidth;
    private int mHeight;

    private int[] mPixelData;
    private List<byte[]> mReusableBuffers;

    private ImageReader mImageReader;
    private Surface mInputSurface;

//    private EncoderThread mEncoderThread;

    private List<ImageInfo> mList;

    private OnImageEncoderListener mOnImageEncoderListener;

    //这里的width=240，height=320。为了测试实时浏览，尽可能的小，防止转换消耗时间
    public ImageEncoderCore(int width, int height, OnImageEncoderListener l) {
        this.mWidth = width;
        this.mHeight = height;
        this.mPixelData = new int[width * height];
        this.mReusableBuffers = Collections.synchronizedList(new ArrayList<byte[]>());

        this.mOnImageEncoderListener = l;
        this.mImageReader = ImageReader
                .newInstance(width, height, PixelFormat.RGBA_8888, MAX_IMAGE_NUMBER);

        mList = Collections.synchronizedList(new ArrayList<ImageInfo>());

        mInputSurface = mImageReader.getSurface();

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (VERBOSE) Log.i(TAG, "in onImageAvailable:"+ Thread.currentThread().getId());
                Image image = reader.acquireNextImage();//获取下一个
                Image.Plane[] planes = image.getPlanes();
                int width = image.getWidth();//设置的宽
                int height = image.getHeight();//设置的高
                int pixelStride = planes[0].getPixelStride();//内存对齐参数
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Log.e(TAG,"rowStride="+rowStride+",bufferlen="+planes[0].getBuffer().limit()+",rowPadding="+rowPadding + ",imageFormat:" + image.getFormat());
                ByteBuffer buffer = planes[0].getBuffer();//获得buffer
                //buffer.get(data);//将buffer数据写入byte中

//                mList.add(new ImageInfo(data, pixelStride, rowPadding));

                mOnImageEncoderListener.onImageEncoder(new ImageInfo(buffer,width,height, pixelStride, rowPadding,rowStride));

                image.close();//用完需要关闭
            }
        }, null);

//        mEncoderThread = new EncoderThread();
//        mEncoderThread.start();
    }

    private class EncoderThread extends Thread {//这里把byte转换为bitmap，实际效率比较低下，这里只是展示用

        @Override
        public void run() {
            while (mImageReader != null) {
                if (mList.isEmpty()) {
                    SystemClock.sleep(1);
                    continue;
                }

                final ImageInfo info = mList.remove(0);

                byte[] data = info.data.array();
                final int pixelStride = info.pixelStride;
                final int rowPadding = info.rowPadding;

                int offset = 0;
                int index = 0;
                for (int i = 0; i < mHeight; ++i) {
                    for (int j = 0; j < mWidth; ++j) {
                        int pixel = 0;
                        pixel |= (data[offset] & 0xff) << 16;     // R
                        pixel |= (data[offset + 1] & 0xff) << 8;  // G
                        pixel |= (data[offset + 2] & 0xff);       // B
                        pixel |= (data[offset + 3] & 0xff) << 24; // A
                        mPixelData[index++] = pixel;
                        offset += pixelStride;
                    }
                    offset += rowPadding;
                }

                Bitmap bitmap = Bitmap.createBitmap(mPixelData,
                        mWidth, mHeight,
                        Bitmap.Config.ARGB_8888);
                Message message = Message.obtain();
                message.what = ENCODER_BITMAP;
                message.obj = bitmap;
                mHandler.sendMessage(message);

                mReusableBuffers.add(data);
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ENCODER_BITMAP:
                    if (mOnImageEncoderListener != null) {

                    }
                    break;
            }
        }
    };

    /**
     * Returns the encoder's input surface.
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }

    /**
     * Releases encoder resources.
     */
    public void release() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
//        try {
//            mEncoderThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        mList.clear();
        mReusableBuffers.clear();
        mHandler.removeMessages(ENCODER_BITMAP);
        mHandler = null;
        mOnImageEncoderListener = null;
    }

    private byte[] getBuffer(int length) {
        if (mReusableBuffers.isEmpty()) {
            return new byte[length];
        } else {
            return mReusableBuffers.remove(0);
        }
    }

    public interface OnImageEncoderListener {
        void onImageEncoder(ImageInfo imageInfo);
    }

    public static class ImageInfo {
        public final ByteBuffer data;
        public final int pixelStride;
        public final int rowPadding;
        public final int width;
        public final int height;
        public final int rowStride;

        ImageInfo(ByteBuffer data, int width, int height, int pixelStride, int rowPadding, int rowStride) {
            this.data = data;
            this.pixelStride = pixelStride;
            this.rowPadding = rowPadding;
            this.width = width;
            this.height = height;
            this.rowStride = rowStride;
        }
    }

}
