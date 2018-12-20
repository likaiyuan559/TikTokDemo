package com.tiktokdemo.lky.tiktokdemo.record.camera.utils;

import android.graphics.PointF;
import android.hardware.Camera;
import android.util.Log;

import com.tiktokdemo.lky.tiktokdemo.record.camera.camera.CameraEngine;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.helper.FilterInfo;


/**
 * Created by lky on 2017/4/25.
 */

public class PointParseUtil {


    public static float[] parseCoordinatesData2(int width, int height, PointF[] pointFs, FilterInfo filterInfo){
        PointF eyeLeft = pointFs[58];
        PointF eyeRight = pointFs[55];

        float eyeDist = getSqrtDistence(eyeLeft,eyeRight);
        float singnx = 1.0f * (eyeRight.y - eyeLeft.y)/eyeDist;
        float cosignx = 1.0f * (eyeRight.x - eyeLeft.x)/eyeDist;

        PointF leftPointF = pointFs[filterInfo.getAlignIndexes().get(0)];
        PointF centerPointF1 = pointFs[filterInfo.getAlignIndexes().get(1)];
        PointF rightPointF = pointFs[filterInfo.getAlignIndexes().get(2)];

        float dist = getSqrtDistence(leftPointF,rightPointF);

        float itemWidth = dist + eyeDist * filterInfo.getScaleWidth();
        float itemHeight = itemWidth * filterInfo.getHeight() / filterInfo.getWidth();

        float temp_left = centerPointF1.x - itemWidth / 2.f + eyeDist * filterInfo.getOffsetX();
        float temp_right = centerPointF1.x + itemWidth / 2.f + eyeDist * filterInfo.getOffsetX();
        float temp_top = centerPointF1.y - itemHeight / 2.f + eyeDist * filterInfo.getOffsetY();
        float temp_bottom = centerPointF1.y + itemHeight / 2.f + eyeDist * filterInfo.getOffsetY();

        float widthScale = width/itemWidth;
        float heightScale = height/itemHeight;
////        Log.e("MagicFace","MagicFace:widthScale:" + widthScale + ",heightScale:" + heightScale);


//        float left = -temp_left/(float) width*widthScale;
//        float top = heightScale-temp_top/(float)height*heightScale;
//        float right = widthScale-temp_left/(float)width*widthScale;
//        float bottom = -temp_top/(float)height*heightScale;
//        DebugTool.warn("PointParseUtil,left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom);


//        float v1x = -v1p.x/(float) width*widthScale;
//        float v1y = -heightScale+v1p.y/(float)height*heightScale+1f;
//        float v2x = widthScale-v2p.x/(float)width*widthScale+1f;
//        float v2y = -heightScale+v2p.y/(float)height*heightScale+1f;
//        float v3x = -v3p.x/(float)width*widthScale;
//        float v3y = v3p.y/(float)height*heightScale;
//        float v4x = widthScale-v4p.x/(float)width*widthScale+1f;
//        float v4y = v4p.y/(float)height*heightScale;
        float ver1x = ((temp_left-centerPointF1.x)*cosignx-(temp_bottom-centerPointF1.y)*singnx+centerPointF1.x);
        float ver1y = ((temp_left-centerPointF1.x)*singnx+(temp_bottom-centerPointF1.y)*cosignx+centerPointF1.y);
        float ver2x = ((temp_right-centerPointF1.x)*cosignx-(temp_bottom-centerPointF1.y)*singnx+centerPointF1.x);
        float ver2y = ((temp_right-centerPointF1.x)*singnx+(temp_bottom-centerPointF1.y)*cosignx+centerPointF1.y);
        float ver3x = ((temp_left-centerPointF1.x)*cosignx-(temp_top-centerPointF1.y)*singnx+centerPointF1.x);
        float ver3y = ((temp_left-centerPointF1.x)*singnx+(temp_top-centerPointF1.y)*cosignx+centerPointF1.y);
        float ver4x = ((temp_right-centerPointF1.x)*cosignx-(temp_top-centerPointF1.y)*singnx+centerPointF1.x);
        float ver4y = ((temp_right-centerPointF1.x)*singnx+(temp_top-centerPointF1.y)*cosignx+centerPointF1.y);

//        PointF centerPointF = new PointF();
//        centerPointF.x = (Math.abs(right-left)/2)/widthScale;
//        centerPointF.y = (Math.abs(bottom-top)/2)/heightScale;


//        float v3x = ((left-centerPointF.x)*cosignx-(bottom-centerPointF.y)*singnx+centerPointF.x);
//        float v3y = ((left-centerPointF.x)*singnx+(bottom-centerPointF.y)*cosignx+centerPointF.y);
//        float v4x =((right-centerPointF.x)*cosignx-(bottom-centerPointF.y)*singnx+centerPointF.x);
//        float v4y = ((right-centerPointF.x)*singnx+(bottom-centerPointF.y)*cosignx+centerPointF.y);
//        float v1x = ((left-centerPointF.x)*cosignx-(top-centerPointF.y)*singnx+centerPointF.x);
//        float v1y = ((left-centerPointF.x)*singnx+(top-centerPointF.y)*cosignx+centerPointF.y);
//        float v2x = ((right-centerPointF.x)*cosignx-((top-centerPointF.y)*singnx+centerPointF.x));
//        float v2y = ((right-centerPointF.x)*singnx+(top-centerPointF.y)*cosignx+centerPointF.y);

//        float C = (float) Math.sqrt(Math.pow(top-centerPointF.y,2)+Math.pow(-left+centerPointF.x,2));
//        float r1Sin = Math.abs(-left+centerPointF.x)/C;
//
//        float rotation_r = (float) Math.asin(singnx);
//        float d_90_r = (float) (90f * Math.PI /180f);
//        float a_r = (float) Math.asin(r1Sin);
//        float new_r = d_90_r - (a_r + rotation_r);
//
//        float sinr = (float) Math.sin(new_r);
//        float cosr = (float) Math.cos(new_r);
//
//
//
//        Log.e("Magic","Coordinates:left:" + left + ",top:" + top + "right:" + right + ",bottom:" + bottom + ",centerPointF.x:" + centerPointF.x + ",centerPointF.y:" +centerPointF.y + ",singnx:" + singnx + ",cosignx:" + cosignx);
//        Log.e("Magic","Coordinates:r1Sin:" + r1Sin + ",r1D:" + C);

        float v1x = ver1x/(float)width*widthScale - centerPointF1.x/width*widthScale+0.5f;
        float v1y = ver1y/(float)height*heightScale - centerPointF1.y/height*heightScale+0.5f;
        float v2x = ver2x/(float)width*widthScale - centerPointF1.x/width*widthScale+0.5f;
        float v2y = ver2y/(float)height*heightScale - centerPointF1.y/height*heightScale+0.5f;
        float v3x = ver3x/(float)width*widthScale - centerPointF1.x/width*widthScale+0.5f;
        float v3y = ver3y/(float)height*heightScale - centerPointF1.y/height*heightScale+0.5f;
        float v4x = ver4x/(float)width*widthScale - centerPointF1.x/width*widthScale+0.5f;
        float v4y = ver4y/(float)height*heightScale - centerPointF1.y/height*heightScale+0.5f;
        float[] testArray = {
                v1x, v1y,
                v2x, v2y,
                v3x, v3y,
                v4x, v4y
        };
        return testArray;
    }


    public static float[] parseCoordinatesData(int width, int height, PointF[] pointFs, FilterInfo filterInfo){
        PointF eyeLeft = pointFs[58];
        PointF eyeRight = pointFs[55];

        float eyeDist = getSqrtDistence(eyeLeft,eyeRight);
        float singnx = 1.0f * (eyeLeft.y - eyeRight.y)/eyeDist;
        float cosignx = 1.0f * (eyeRight.x - eyeLeft.x)/eyeDist;
        if(CameraEngine.getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK){
            singnx = 1.0f * (eyeRight.y - eyeLeft.y)/eyeDist;
            cosignx = 1.0f * (eyeLeft.x - eyeRight.x)/eyeDist;
        }

        PointF leftPointF = pointFs[filterInfo.getAlignIndexes().get(0)];
        PointF centerPointF1 = pointFs[filterInfo.getAlignIndexes().get(1)];
        PointF rightPointF = pointFs[filterInfo.getAlignIndexes().get(2)];

        float dist = getSqrtDistence(leftPointF,rightPointF);

        float itemWidth = dist + eyeDist * filterInfo.getScaleWidth();
        float itemHeight = itemWidth * filterInfo.getHeight() / filterInfo.getWidth();

        float temp_left = centerPointF1.x - itemWidth / 2.f + eyeDist * filterInfo.getOffsetX();
        float temp_right = centerPointF1.x + itemWidth / 2.f + eyeDist * filterInfo.getOffsetX();
        float temp_top = centerPointF1.y - itemHeight / 2.f + eyeDist * filterInfo.getOffsetY();
        float temp_bottom = centerPointF1.y + itemHeight / 2.f + eyeDist * filterInfo.getOffsetY();


        float widthScale = width/itemWidth;
        float heightScale = height/itemHeight;
////        Log.e("MagicFace","MagicFace:widthScale:" + widthScale + ",heightScale:" + heightScale);


        float left = -temp_left/(float) width*widthScale;
        float top = heightScale-temp_top/(float)height*heightScale;
        float right = widthScale-temp_left/(float)width*widthScale;
        float bottom = -temp_top/(float)height*heightScale;


//        float v1x = -v1p.x/(float) width*widthScale;
//        float v1y = -heightScale+v1p.y/(float)height*heightScale+1f;
//        float v2x = widthScale-v2p.x/(float)width*widthScale+1f;
//        float v2y = -heightScale+v2p.y/(float)height*heightScale+1f;
//        float v3x = -v3p.x/(float)width*widthScale;
//        float v3y = v3p.y/(float)height*heightScale;
//        float v4x = widthScale-v4p.x/(float)width*widthScale+1f;
//        float v4y = v4p.y/(float)height*heightScale;

        PointF centerPointF = new PointF();
        centerPointF.x = (Math.abs(right-left)/2)/widthScale;
        centerPointF.y = (Math.abs(bottom-top)/2)/heightScale;


//        float v3x = ((left-centerPointF.x)*cosignx-(bottom-centerPointF.y)*singnx+centerPointF.x);
//        float v3y = ((left-centerPointF.x)*singnx+(bottom-centerPointF.y)*cosignx+centerPointF.y);
//        float v4x =((right-centerPointF.x)*cosignx-(bottom-centerPointF.y)*singnx+centerPointF.x);
//        float v4y = ((right-centerPointF.x)*singnx+(bottom-centerPointF.y)*cosignx+centerPointF.y);
//        float v1x = ((left-centerPointF.x)*cosignx-(top-centerPointF.y)*singnx+centerPointF.x);
//        float v1y = ((left-centerPointF.x)*singnx+(top-centerPointF.y)*cosignx+centerPointF.y);
//        float v2x = ((right-centerPointF.x)*cosignx-((top-centerPointF.y)*singnx+centerPointF.x));
//        float v2y = ((right-centerPointF.x)*singnx+(top-centerPointF.y)*cosignx+centerPointF.y);

        float C = (float) Math.sqrt(Math.pow(top-centerPointF.y,2)+ Math.pow(-left+centerPointF.x,2));
        float r1Sin = Math.abs(-left+centerPointF.x)/C;

        float rotation_r = (float) Math.asin(singnx);
        float d_90_r = (float) (90f * Math.PI /180f);
        float a_r = (float) Math.asin(r1Sin);
        float new_r = d_90_r - (a_r + rotation_r);

        float sinr = (float) Math.sin(new_r);
        float cosr = (float) Math.cos(new_r);



        Log.e("Magic","Coordinates:left:" + left + ",top:" + top + "right:" + right + ",bottom:" + bottom + ",centerPointF.x:" + centerPointF.x + ",centerPointF.y:" +centerPointF.y + ",singnx:" + singnx + ",cosignx:" + cosignx);
        Log.e("Magic","Coordinates:r1Sin:" + r1Sin + ",r1D:" + C);

        float v1x = -C*cosr+centerPointF.x;
        float v1y = C*sinr+centerPointF.y;
        float v2x = widthScale*cosignx+v1x;
        float v2y = widthScale*singnx+v1y;
        float v3x = v1x + heightScale*singnx;
        float v3y = v1y - heightScale*cosignx;
        float v4x = v2x + singnx*heightScale;
        float v4y = v2y - cosignx*heightScale;
        Log.e("Magic","Coordinates:\nv1:" + v1x + "," + v1y + "\nv2:" + v2x + "," + v2y + "\nv3:" + v3x + "," + v3y + "\nv4:" + v4x + "," + v4y);
        float[] testArray = {
                v1x, v1y,
                v2x, v2y,
                v3x, v3y,
                v4x, v4y
        };
        return testArray;
    }


    /**
     * 获取两点间的距离
     * @param pointF0
     * @param pointF1
     * @return
     */
    public static float getSqrtDistence(PointF pointF0, PointF pointF1){
        return getSqrtDistence(Math.abs(pointF0.x-pointF1.x), Math.abs(pointF0.y-pointF1.y));
    }

    /**
     * 获取三角形斜边的距离
     * @param width 直角边
     * @param height 直角边2
     * @return
     */
    public static float getSqrtDistence(float width,float height){
        return (float) Math.sqrt(width*width+height*height);
    }

}
