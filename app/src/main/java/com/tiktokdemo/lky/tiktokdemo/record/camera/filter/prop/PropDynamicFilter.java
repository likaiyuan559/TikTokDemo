package com.tiktokdemo.lky.tiktokdemo.record.camera.filter.prop;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.os.SystemClock;

import com.tiktokdemo.lky.tiktokdemo.BaseApplication;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageNormalBlendFilter;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.helper.FilterInfo;
import com.tiktokdemo.lky.tiktokdemo.record.camera.utils.OpenGlUtils;
import com.tiktokdemo.lky.tiktokdemo.record.camera.utils.PointParseUtil;

/**
 * Created by lky on 2017/4/24.
 */

public class PropDynamicFilter extends GPUImageNormalBlendFilter {


    private ArrayList<Bitmap> mBitmaps;
    private int mCurrentBitmapPosition;
    private FilterInfo mFilterInfo;
    private boolean isKill;

    public PropDynamicFilter(int[] res, final FilterInfo filterInfo){
        mBitmaps = new ArrayList<>();
        for(int i=0;i<res.length;i++){
            mBitmaps.add(
                    BitmapFactory.decodeResource(BaseApplication.mContext.getResources(),res[i]));
        }
        mBitmap = mBitmaps.get(0);
        mFilterInfo = filterInfo;
        if(filterInfo.getFrameDuration() == 0){
            return ;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isKill){
                    SystemClock.sleep(filterInfo.getFrameDuration());
                    mCurrentBitmapPosition++;
                    if(mBitmaps == null || mBitmaps.size() <= 0){
                        break;
                    }
                    if(mCurrentBitmapPosition >= mBitmaps.size()){
                        mCurrentBitmapPosition = 0;
                    }
                    setBitmap(mBitmaps.get(mCurrentBitmapPosition));
                }
            }
        }).start();
    }

    @Override
    public void setBitmap(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
                if (bitmap == null || bitmap.isRecycled()) {
                    return;
                }
                GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                mFilterSourceTexture2 = OpenGlUtils
                        .loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBitmaps != null){
            mBitmaps.clear();
            mBitmaps = null;
        }
        isKill = true;
    }

    public void setFacePosition(int width,int height,PointF[] pointFs){
        float[] array = PointParseUtil.parseCoordinatesData(width,height,pointFs,mFilterInfo);
        setFilterSecondTextureCoordinateAttribute(array);
    }


}
