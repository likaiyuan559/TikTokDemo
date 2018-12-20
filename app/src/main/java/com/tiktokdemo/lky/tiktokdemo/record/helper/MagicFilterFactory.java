package com.tiktokdemo.lky.tiktokdemo.record.helper;

import java.util.ArrayList;

import android.graphics.BitmapFactory;

import com.tiktokdemo.lky.tiktokdemo.BaseApplication;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageFilterGroup;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageLookupFilter;


public class MagicFilterFactory{

    private static volatile MagicFilterFactory mInstance;
    private ArrayList<TidalPatFilterType> mTidalPatRecordFilterTypes;
    private TidalPatFilterType mCurrentFilterType = TidalPatFilterType.original;
    private GPUImageLookupFilter mGPUImageLookupFilter;
    private GPUImageFilterGroup mGPUImageFilterGroup;
    private GPUImageFilterGroup mNewGPUImageFilterGroup;

    public static MagicFilterFactory getInstance(){
        if(mInstance == null){
            synchronized (MagicFilterFactory.class){
                if(mInstance == null){
                    mInstance = new MagicFilterFactory();
                }
            }
        }
        return mInstance;
    }

    public ArrayList<TidalPatFilterType> getTidalPatRecordFilterTypes() {
        return mTidalPatRecordFilterTypes;
    }

    private MagicFilterFactory(){
        mCurrentFilterType = TidalPatFilterType.original;
        mTidalPatRecordFilterTypes = new ArrayList<>();
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.original);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.cf_17);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.sf_03);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.fm_05);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.fs_10);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.fm_10);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.mod_09);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.re_03);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.cf_19);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.ins_02);
        mTidalPatRecordFilterTypes.add(TidalPatFilterType.bw_03);
    }

    public void setFilterType(TidalPatFilterType type){
        if(mCurrentFilterType == type){
            return ;
        }
        mCurrentFilterType = type;
        mGPUImageLookupFilter = new GPUImageLookupFilter();
        mGPUImageLookupFilter.setBitmap(BitmapFactory
                .decodeResource(BaseApplication.mContext.getResources(), mCurrentFilterType.getFilterRes()));
        resetFilterGroup();
    }

    public boolean isNotInit(){
        return mGPUImageFilterGroup == null;
    }

    public void clearFilter(){
        mGPUImageLookupFilter = null;
        mGPUImageFilterGroup = null;
        mCurrentFilterType = TidalPatFilterType.original;
    }

    public void resetFilterGroup(){
        mGPUImageFilterGroup = new GPUImageFilterGroup();
        if(mGPUImageLookupFilter != null){
            mGPUImageFilterGroup.addFilter(mGPUImageLookupFilter);
        }
    }

    public TidalPatFilterType getCurrentFilterType(){
        return mCurrentFilterType;
    }

    public GPUImageLookupFilter getCurrentFilter(){
        if(mGPUImageLookupFilter == null){
            mGPUImageLookupFilter = new GPUImageLookupFilter();
            mGPUImageLookupFilter.setBitmap(BitmapFactory
                    .decodeResource(BaseApplication.mContext.getResources(), mCurrentFilterType.getFilterRes()));
        }
        return mGPUImageLookupFilter;
    }

    public GPUImageFilterGroup newGPUImageFilterGroup() {
        mNewGPUImageFilterGroup = new GPUImageFilterGroup();
//        if(mCurrentFilterType != TidalPatFilterType.original){
            GPUImageLookupFilter imageLookupFilter = new GPUImageLookupFilter();
            imageLookupFilter.setBitmap(BitmapFactory
                    .decodeResource(BaseApplication.mContext.getResources(),mCurrentFilterType.getFilterRes()));
            mNewGPUImageFilterGroup.addFilter(imageLookupFilter);
//        }

//        PointF centerPoint = new PointF();
//        centerPoint.x = 0.5f;
//        centerPoint.y = 0.5f;
//        GPUImageVignetteFilter gpuImageVignetteFilter = new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.4f, 1.0f);
//        mNewGPUImageFilterGroup.addFilter(gpuImageVignetteFilter);
//        GPUImageOverlayBlendFilter gpuImageAddBlendFilter = new GPUImageOverlayBlendFilter();
//        gpuImageAddBlendFilter.setBitmap(BitmapFactory.decodeResource(AppUtil.getApplicationContext().getResources(),R.drawable.b_2));
//        mGPUImageFilterGroup.addFilter(gpuImageAddBlendFilter);
        return mNewGPUImageFilterGroup;
    }

    public GPUImageFilterGroup getGPUImageFilterGroup() {
        mGPUImageFilterGroup = new GPUImageFilterGroup();
//        if(mCurrentFilterType != TidalPatFilterType.original){
            mGPUImageLookupFilter = new GPUImageLookupFilter();
            mGPUImageLookupFilter.setBitmap(BitmapFactory
                    .decodeResource(BaseApplication.mContext.getResources(), mCurrentFilterType.getFilterRes()));
            mGPUImageFilterGroup.addFilter(mGPUImageLookupFilter);
//        }


//        PointF centerPoint = new PointF();
//        centerPoint.x = 0.5f;
//        centerPoint.y = 0.5f;
//        GPUImageVignetteFilter gpuImageVignetteFilter = new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.4f, 1.0f);
//        mGPUImageFilterGroup.addFilter(gpuImageVignetteFilter);
//        GPUImageOverlayBlendFilter gpuImageAddBlendFilter = new GPUImageOverlayBlendFilter();
//        gpuImageAddBlendFilter.setBitmap(BitmapFactory.decodeResource(AppUtil.getApplicationContext().getResources(),R.drawable.b_2));
//        mGPUImageFilterGroup.addFilter(gpuImageAddBlendFilter);
        return mGPUImageFilterGroup;
    }
}
