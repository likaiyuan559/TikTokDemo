package com.tiktokdemo.lky.tiktokdemo.record.helper;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageFilterGroup;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.helper.FilterInfo;

/**
 * Created by lky on 2017/5/3.
 */

public class TidalPatPropFactory {

    private ArrayList<GPUImageFilterGroup> mFilterGroups = new ArrayList<>();
    private ArrayList<GPUImageFilterGroup> mRecordFilterGroups = new ArrayList<>();
//    private static GPUImageFilterGroup mFilterGroups;
//    private static GPUImageFilterGroup mRecordFilterGroups;
    private TidalPatPropType mPropType = TidalPatPropType.DEFAULT;
    private int mFaceCount;

    private static volatile TidalPatPropFactory mInstance;

    private TidalPatPropFactory(){}

    public static TidalPatPropFactory getInstance(){
        if(mInstance == null){
            synchronized (TidalPatPropFactory.class){
                if(mInstance == null){
                    mInstance = new TidalPatPropFactory();
                }
            }
        }
        return mInstance;
    }

    public ArrayList<GPUImageFilterGroup> getPropFilter(int count){
        synchronized (mFilterGroups){
            mFilterGroups.clear();
            mRecordFilterGroups.clear();
            for(int i=0;i<count;i++){
                mFilterGroups.add(getPropFilter());
                mRecordFilterGroups.add(getPropFilter());
            }
            mFaceCount = count;
            return mFilterGroups;
        }
    }

    public ArrayList<GPUImageFilterGroup> getFilterGroups() {
        synchronized (mFilterGroups){
            return mFilterGroups;
        }
    }

    public ArrayList<GPUImageFilterGroup> getRecordFilterGroups() {
        return mRecordFilterGroups;
    }

    public ArrayList<GPUImageFilterGroup> newFilterGroups(){
//        mRecordFilterGroups.clear();
//        for(int i=0;i<mFaceCount;i++){
//            mRecordFilterGroups.add(getPropFilter());
//        }
        return mRecordFilterGroups;
    }

    public ArrayList<TidalPatPropType> getAllPropList(){
        ArrayList<TidalPatPropType> tidalPatPropTypes = new ArrayList<>();
        tidalPatPropTypes.add(TidalPatPropType.DEFAULT);
        tidalPatPropTypes.add(TidalPatPropType.CIGAR);
        tidalPatPropTypes.add(TidalPatPropType.GLASSES);
        tidalPatPropTypes.add(TidalPatPropType.FACECLOTH);
        tidalPatPropTypes.add(TidalPatPropType.GROUP2);
        tidalPatPropTypes.add(TidalPatPropType.GROUP1);
        return tidalPatPropTypes;
    }

    public void changeType(TidalPatPropType propType){
        if(propType == mPropType){
            return ;
        }
        synchronized (mFilterGroups){
            mPropType = propType;
            mFilterGroups.clear();
            mRecordFilterGroups.clear();
            for(int i=0;i<mFaceCount;i++){
                mFilterGroups.add(getPropFilter());
                mRecordFilterGroups.add(getPropFilter());
            }
        }
    }

    public TidalPatPropType getPropType() {
        return mPropType;
    }

    private GPUImageFilterGroup getPropFilter(){
        GPUImageFilterGroup imageFilterGroup = new GPUImageFilterGroup();
        Gson gson = new Gson();
        switch (mPropType){
            case CIGAR:
                FilterInfo cigarInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": -0.4,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0.7,\n" +
                        "            \"offsetY\": 0,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                90, 98, 84\n" +
                        "            ],\n" +
                        "            \"folder\": \"ciga\",\n" +
                        "            \"frames\": 1,\n" +
                        "            \"frameDuration\": 0,\n" +
                        "            \"width\": 139,\n" +
                        "            \"height\": 64,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
                FilterInfo smokingInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 12,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0.5,\n" +
                        "            \"offsetY\": 1.5,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                83, 49, 82\n" +
                        "            ],\n" +
                        "            \"folder\": \"smoking\",\n" +
                        "            \"frames\": 11,\n" +
                        "            \"frameDuration\": 110,\n" +
                        "            \"width\": 400,\n" +
                        "            \"height\": 249,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] cigarRes = {R.drawable.cigar};
//                int[] smokingRes = {R.drawable.smoking_000,R.drawable.smoking_001,R.drawable.smoking_002,R.drawable.smoking_003,R.drawable.smoking_004,R.drawable.smoking_005,
//                        R.drawable.smoking_006,R.drawable.smoking_007,R.drawable.smoking_008,R.drawable.smoking_009,R.drawable.smoking_010};
//                GPUImageNormalBlendFilter normalBlendFilter = new GPUImageNormalBlendFilter();
//                normalBlendFilter.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
//                imageFilterGroup.addFilter(normalBlendFilter);
//                PropDynamicFilter cigarFilter = new PropDynamicFilter(cigarRes,cigarInfo);
//                PropDynamicFilter smokingFilter = new PropDynamicFilter(smokingRes,smokingInfo);
//                imageFilterGroup.addFilter(cigarFilter);
//                imageFilterGroup.addFilter(smokingFilter);
                break;
            case GLASSES:
                FilterInfo glassesInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 3,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0,\n" +
                        "            \"offsetY\": 0,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                61, 43, 52\n" +
                        "            ],\n" +
                        "            \"folder\": \"sunglasses\",\n" +
                        "            \"frames\": 16,\n" +
                        "            \"frameDuration\": 100,\n" +
                        "            \"width\": 400,\n" +
                        "            \"height\": 297,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] glassesRes = {R.drawable.sunglasses_000,R.drawable.sunglasses_001,R.drawable.sunglasses_002,R.drawable.sunglasses_003,R.drawable.sunglasses_004,
//                        R.drawable.sunglasses_005, R.drawable.sunglasses_006,R.drawable.sunglasses_007,R.drawable.sunglasses_008,R.drawable.sunglasses_009,
//                        R.drawable.sunglasses_010,R.drawable.sunglasses_011,R.drawable.sunglasses_012,R.drawable.sunglasses_013,R.drawable.sunglasses_014,R.drawable.sunglasses_015};
//                PropDynamicFilter glassesFilter = new PropDynamicFilter(glassesRes,glassesInfo);
//                imageFilterGroup.addFilter(glassesFilter);
                break;
            case FACECLOTH:
                FilterInfo faceClothInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 1.8,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0,\n" +
                        "            \"offsetY\": 3.5,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                27, 46, 5\n" +
                        "            ],\n" +
                        "            \"folder\": \"faceCloth\",\n" +
                        "            \"frames\": 20,\n" +
                        "            \"frameDuration\": 100,\n" +
                        "            \"width\": 193,\n" +
                        "            \"height\": 316,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] faceClothRes = {R.drawable.face_cloth_000,R.drawable.face_cloth_001,R.drawable.face_cloth_002,R.drawable.face_cloth_003,R.drawable.face_cloth_004,
//                        R.drawable.face_cloth_005, R.drawable.face_cloth_006,R.drawable.face_cloth_007,R.drawable.face_cloth_008,R.drawable.face_cloth_009,
//                        R.drawable.face_cloth_010,R.drawable.face_cloth_011,R.drawable.face_cloth_012,R.drawable.face_cloth_013,R.drawable.face_cloth_014,
//                        R.drawable.face_cloth_015,R.drawable.face_cloth_016,R.drawable.face_cloth_017,R.drawable.face_cloth_018,R.drawable.face_cloth_019};
//                PropDynamicFilter faceClothFilter = new PropDynamicFilter(faceClothRes,faceClothInfo);
//                imageFilterGroup.addFilter(faceClothFilter);
                break;
            case GROUP1:
                FilterInfo groupFaceClothInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 1.8,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0,\n" +
                        "            \"offsetY\": 3.5,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                27, 46, 5\n" +
                        "            ],\n" +
                        "            \"folder\": \"faceCloth\",\n" +
                        "            \"frames\": 20,\n" +
                        "            \"frameDuration\": 100,\n" +
                        "            \"width\": 193,\n" +
                        "            \"height\": 316,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] groupFaceClothRes = {R.drawable.face_cloth_000,R.drawable.face_cloth_001,R.drawable.face_cloth_002,R.drawable.face_cloth_003,R.drawable.face_cloth_004,
//                        R.drawable.face_cloth_005, R.drawable.face_cloth_006,R.drawable.face_cloth_007,R.drawable.face_cloth_008,R.drawable.face_cloth_009,
//                        R.drawable.face_cloth_010,R.drawable.face_cloth_011,R.drawable.face_cloth_012,R.drawable.face_cloth_013,R.drawable.face_cloth_014,
//                        R.drawable.face_cloth_015,R.drawable.face_cloth_016,R.drawable.face_cloth_017,R.drawable.face_cloth_018,R.drawable.face_cloth_019};
//                PropDynamicFilter groupFaceClothFilter = new PropDynamicFilter(groupFaceClothRes,groupFaceClothInfo);
//                imageFilterGroup.addFilter(groupFaceClothFilter);
//                GPUImageNormalBlendFilter normalBlendFilter = new GPUImageNormalBlendFilter();
//                normalBlendFilter.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
//                imageFilterGroup.addFilter(normalBlendFilter);
                FilterInfo groupGlassesInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 3,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0,\n" +
                        "            \"offsetY\": 0,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                61, 43, 52\n" +
                        "            ],\n" +
                        "            \"folder\": \"sunglasses\",\n" +
                        "            \"frames\": 16,\n" +
                        "            \"frameDuration\": 100,\n" +
                        "            \"width\": 400,\n" +
                        "            \"height\": 297,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] groupGlassesRes = {R.drawable.sunglasses_000,R.drawable.sunglasses_001,R.drawable.sunglasses_002,R.drawable.sunglasses_003,R.drawable.sunglasses_004,
//                        R.drawable.sunglasses_005, R.drawable.sunglasses_006,R.drawable.sunglasses_007,R.drawable.sunglasses_008,R.drawable.sunglasses_009,
//                        R.drawable.sunglasses_010,R.drawable.sunglasses_011,R.drawable.sunglasses_012,R.drawable.sunglasses_013,R.drawable.sunglasses_014,R.drawable.sunglasses_015};
//                PropDynamicFilter groupGlassesFilter = new PropDynamicFilter(groupGlassesRes,groupGlassesInfo);
//                imageFilterGroup.addFilter(groupGlassesFilter);
//                GPUImageNormalBlendFilter normalBlendFilter2 = new GPUImageNormalBlendFilter();
//                normalBlendFilter2.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
//                imageFilterGroup.addFilter(normalBlendFilter2);
                FilterInfo groupHatInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 4.5,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 1,\n" +
                        "            \"offsetY\": -2,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                61, 43, 52\n" +
                        "            ],\n" +
                        "            \"folder\": \"hat\",\n" +
                        "            \"frames\": 1,\n" +
                        "            \"frameDuration\": 0,\n" +
                        "            \"width\": 635,\n" +
                        "            \"height\": 403,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] groupHatRes = {R.drawable.hat};
//                PropDynamicFilter groupHatFilter = new PropDynamicFilter(groupHatRes,groupHatInfo);
//                imageFilterGroup.addFilter(groupHatFilter);
                FilterInfo groupGunInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 5,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0.0,\n" +
                        "            \"offsetY\": 3,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                90, 98, 84\n" +
                        "            ],\n" +
                        "            \"folder\": \"ciga\",\n" +
                        "            \"frames\": 1,\n" +
                        "            \"frameDuration\": 0,\n" +
                        "            \"width\": 700,\n" +
                        "            \"height\": 409,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] groupGunRes = {R.drawable.gun};
//                PropDynamicFilter groupGunFilter = new PropDynamicFilter(groupGunRes,groupGunInfo);
//                imageFilterGroup.addFilter(groupGunFilter);
                break;
            case GROUP2:
                FilterInfo group2CigarInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": -0.4,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0.7,\n" +
                        "            \"offsetY\": 0,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                90, 98, 84\n" +
                        "            ],\n" +
                        "            \"folder\": \"ciga\",\n" +
                        "            \"frames\": 1,\n" +
                        "            \"frameDuration\": 0,\n" +
                        "            \"width\": 139,\n" +
                        "            \"height\": 64,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
                FilterInfo group2SmokingInfo = gson.fromJson("{\n" +
                        "            \"type\": 0,\n" +
                        "            \"scaleWidth\": 12,\n" +
                        "            \"scaleHight\": 0,\n" +
                        "            \"offsetX\": 0.5,\n" +
                        "            \"offsetY\": 1.5,\n" +
                        "            \"alignIndexes\": [\n" +
                        "                83, 49, 82\n" +
                        "            ],\n" +
                        "            \"folder\": \"smoking\",\n" +
                        "            \"frames\": 11,\n" +
                        "            \"frameDuration\": 110,\n" +
                        "            \"width\": 400,\n" +
                        "            \"height\": 249,\n" +
                        "            \"triggerType\": 1\n" +
                        "        }",FilterInfo.class);
//                int[] group2CigarRes = {R.drawable.cigar};
//                int[] group2SmokingRes = {R.drawable.smoking_000,R.drawable.smoking_001,R.drawable.smoking_002,R.drawable.smoking_003,R.drawable.smoking_004,R.drawable.smoking_005,
//                        R.drawable.smoking_006,R.drawable.smoking_007,R.drawable.smoking_008,R.drawable.smoking_009,R.drawable.smoking_010};
////                GPUImageNormalBlendFilter normalBlendFilter = new GPUImageNormalBlendFilter();
////                normalBlendFilter.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
////                imageFilterGroup.addFilter(normalBlendFilter);
//                PropDynamicFilter group2CigarFilter = new PropDynamicFilter(group2CigarRes,group2CigarInfo);
//                PropDynamicFilter group2SmokingFilter = new PropDynamicFilter(group2SmokingRes,group2SmokingInfo);
//                imageFilterGroup.addFilter(group2CigarFilter);

//                GPUImageNormalBlendFilter normalBlendFilter1 = new GPUImageNormalBlendFilter();
//                normalBlendFilter1.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
//                imageFilterGroup.addFilter(normalBlendFilter1);
//                imageFilterGroup.addFilter(group2SmokingFilter);
//                GPUImageNormalBlendFilter normalBlendFilter3 = new GPUImageNormalBlendFilter();
//                normalBlendFilter3.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
//                imageFilterGroup.addFilter(normalBlendFilter3);
//                FilterInfo group2GunInfo = gson.fromJson("{\n" +
//                        "            \"type\": 0,\n" +
//                        "            \"scaleWidth\": 5,\n" +
//                        "            \"scaleHight\": 0,\n" +
//                        "            \"offsetX\": 0.0,\n" +
//                        "            \"offsetY\": 3,\n" +
//                        "            \"alignIndexes\": [\n" +
//                        "                90, 98, 84\n" +
//                        "            ],\n" +
//                        "            \"folder\": \"ciga\",\n" +
//                        "            \"frames\": 1,\n" +
//                        "            \"frameDuration\": 0,\n" +
//                        "            \"width\": 700,\n" +
//                        "            \"height\": 409,\n" +
//                        "            \"triggerType\": 1\n" +
//                        "        }",FilterInfo.class);
//                int[] group2GunRes = {R.drawable.gun};
//                PropDynamicFilter group2GunFilter = new PropDynamicFilter(group2GunRes,group2GunInfo);
//                imageFilterGroup.addFilter(group2GunFilter);
//                FilterInfo group2HatInfo = gson.fromJson("{\n" +
//                        "            \"type\": 0,\n" +
//                        "            \"scaleWidth\": 4.5,\n" +
//                        "            \"scaleHight\": 0,\n" +
//                        "            \"offsetX\": 1,\n" +
//                        "            \"offsetY\": -2,\n" +
//                        "            \"alignIndexes\": [\n" +
//                        "                61, 43, 52\n" +
//                        "            ],\n" +
//                        "            \"folder\": \"hat\",\n" +
//                        "            \"frames\": 1,\n" +
//                        "            \"frameDuration\": 0,\n" +
//                        "            \"width\": 635,\n" +
//                        "            \"height\": 403,\n" +
//                        "            \"triggerType\": 1\n" +
//                        "        }",FilterInfo.class);
//                int[] group2HatRes = {R.drawable.hat};
//                PropDynamicFilter group2HatFilter = new PropDynamicFilter(group2HatRes,group2HatInfo);
//                imageFilterGroup.addFilter(group2HatFilter);

                break;
            case DEFAULT:
            default:

                break;
        }
        return imageFilterGroup;
    }

}
