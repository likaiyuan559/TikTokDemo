package com.tiktokdemo.lky.tiktokdemo.record.camera.filter.prop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;

import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageFilterGroup;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageNormalBlendFilter;


/**
 * Created by lky on 2017/4/24.
 */

public class PropStaticGroupFilter extends GPUImageFilterGroup {


//    public HashMap<PropStaticType,GPUImageNormalBlendFilter> mFilters;
//
//    public enum PropStaticType{
//        NECKLACE(R.drawable.necklace),//项链
//        HAT(R.drawable.hat),//帽子
//        FACECLOTH(R.drawable.facecloth),//面巾
//        GLASSES(R.drawable.glasses),//眼镜
//        GUN(R.drawable.gun),;//枪
//
//        int res;
//        PropStaticType(int res) {
//            this.res = res;
//        }
//        public int getRes(){
//            return this.res;
//        }
//    }
//
//    public PropStaticGroupFilter(){
//        mFilters = new HashMap<>();
//    }
//
//    public void release(){
//        mFilters.clear();
//    }
//
//    public void addFilter(Context context, PropStaticType propStaticType){
//        if(mFilters.size() == 1){
//            GPUImageNormalBlendFilter normalBlendFilter = new GPUImageNormalBlendFilter();
//            normalBlendFilter.setBitmap(Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888));
//            addFilter(normalBlendFilter);
//        }
//        GPUImageNormalBlendFilter normalBlendFilter = new GPUImageNormalBlendFilter();
//        normalBlendFilter.setBitmap(
//                BitmapFactory.decodeResource(context.getResources(),propStaticType.getRes()));
//        mFilters.put(propStaticType,normalBlendFilter);
//        addFilter(normalBlendFilter);
//    }
//
//    public void removeFilter(PropStaticType propStaticType){
//        mFilters.remove(propStaticType);
//    }
//
//    public void setFaceData(STMobileFaceAction faceData){
//        Iterator<Map.Entry<PropStaticType,GPUImageNormalBlendFilter>>
//                iterator = mFilters.entrySet().iterator();
//        PointF[] pointFs = faceData.getFace().getPointsArray();
//        Rect rect = faceData.getFace().getRect();
//        while (iterator.hasNext()){
//            Map.Entry<PropStaticType,GPUImageNormalBlendFilter> entry = iterator.next();
//            switch (entry.getKey()){
//                case HAT:
//                    entry.getValue().setFilterSecondTextureCoordinateAttribute(480,640,480-rect.bottom,(int)(640-pointFs[40].x - (
//                            Math.abs(rect.right-rect.left)*0.3) - (Math.abs(rect.bottom-rect.top)*0.634f)),480-rect.top+50, (int)(640-pointFs[40].x - (
//                            Math.abs(rect.right-rect.left)*0.3)));
//                    break;
//                case FACECLOTH:
//                    entry.getValue().setFilterSecondTextureCoordinateAttribute(480,640,(int)(480-pointFs[28].y - 10),(int)(640-pointFs[81].x - 10),(int)(480-pointFs[4].y + 10),(int)(640-pointFs[16].x + 20));
//                    break;
//                case GLASSES:
//                    entry.getValue().setFilterSecondTextureCoordinateAttribute(480,640,(int)(480-pointFs[61].y - 20),(int)(640-pointFs[70].x - 20),(int)(480-pointFs[52].y + 20),(int)(640-pointFs[76].x + 20));
//                    break;
//                case GUN:
//                    break;
//                case NECKLACE:
//                    entry.getValue().setFilterSecondTextureCoordinateAttribute(480,640,480-rect.bottom,rect.left - 30,480-rect.top,rect.left - 30 - (int) (
//                            Math.abs(rect.top-rect.bottom)*1.674f));
//                    break;
//            }
//        }
//    }

}
