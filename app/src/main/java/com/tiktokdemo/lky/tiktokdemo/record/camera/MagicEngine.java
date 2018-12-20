package com.tiktokdemo.lky.tiktokdemo.record.camera;


import java.io.File;

import android.hardware.Camera;

import com.tiktokdemo.lky.tiktokdemo.record.camera.camera.CameraEngine;
import com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage.GPUImageFilter;
import com.tiktokdemo.lky.tiktokdemo.record.camera.helper.SavePictureTask;
import com.tiktokdemo.lky.tiktokdemo.record.camera.utils.MagicParams;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.MagicCameraView;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.base.MagicBaseView;


/**
 * Created by why8222 on 2016/2/25.
 */
public class MagicEngine {
    private static MagicEngine magicEngine;

    private static boolean isBeautyOpen = true;

    public static MagicEngine getInstance(){
        if(magicEngine == null)
            throw new NullPointerException("MagicEngine must be built first");
        else
            return magicEngine;
    }

    private MagicEngine(Builder builder){

    }

//    public void setFilter(MagicFilterType type){
//        MagicParams.magicBaseView.setFilter(type);
//    }

    public void setFilter(GPUImageFilter filter){
        MagicParams.magicBaseView.setFilter(filter);
    }

    public void savePicture(File file, SavePictureTask.OnPictureSaveListener listener){
        SavePictureTask savePictureTask = new SavePictureTask(file, listener);
        MagicParams.magicBaseView.savePicture(savePictureTask);
    }

    public void startRecord(){
        if(MagicParams.magicBaseView instanceof MagicCameraView)
            ((MagicCameraView)MagicParams.magicBaseView).changeRecordingState(true);
    }

    public void stopRecord(){
        if(MagicParams.magicBaseView instanceof MagicCameraView)
            ((MagicCameraView)MagicParams.magicBaseView).changeRecordingState(false);
    }

    public static void setBeautyOpenStatus(boolean isOpen){
        if(isBeautyOpen == isOpen){
            return ;
        }
        int beauty;
        if(isOpen){
            beauty = 5;
        }else{
            beauty = 0;
        }
        if(MagicParams.magicBaseView instanceof MagicCameraView) {
            MagicParams.beautyLevel = beauty;
            ((MagicCameraView) MagicParams.magicBaseView).onBeautyLevelChanged();
        }
        isBeautyOpen = isOpen;
    }

    public static boolean isBeautyOpen() {
        return isBeautyOpen;
    }

    public void setBeautyLevel(int level){

    }

    public boolean switchCamera(){
        return ((MagicCameraView)MagicParams.magicBaseView).switchCamera();
    }


    public void changeFlashMode(){
        try{
            String flashMode = CameraEngine.getCameraInfo().flashMode;
            if(flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)){
                CameraEngine.setFlashMode(CameraEngine.FLASH_MODE_ON);
            }else if(flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH) || flashMode.equals(
                    Camera.Parameters.FLASH_MODE_ON)){
                CameraEngine.setFlashMode(CameraEngine.FLASH_MODE_OFF);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static class Builder{

        public MagicEngine build(MagicBaseView magicBaseView) {
            MagicParams.context = magicBaseView.getContext();
            MagicParams.magicBaseView = magicBaseView;
            return new MagicEngine(this);
        }

        public Builder setVideoPath(String path){
            MagicParams.videoPath = path;
            return this;
        }

        public Builder setVideoName(String name){
            MagicParams.videoName = name;
            return this;
        }

    }
}
