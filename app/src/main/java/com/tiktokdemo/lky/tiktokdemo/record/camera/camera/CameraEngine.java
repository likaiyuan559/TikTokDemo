package com.tiktokdemo.lky.tiktokdemo.record.camera.camera;

import java.io.IOException;
import java.util.List;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceView;

import com.tiktokdemo.lky.tiktokdemo.record.camera.camera.utils.CameraUtils;

public class CameraEngine {

    public static final String FRAME_RATE_KEY = "CAMERA_FRAME_RATE_KEY";
    public static final int RECORD_WIDTH = 360, RECORD_HEIGHT = 640;
    public static final int PREVIEW_WIDTH = 1280;
    public static final int PREVIEW_HEIGHT = 720;
    public static final int CAMERA_FRAME_RATE = 15;
    public static final int CAMERA_FRAME_RATE_MID = 25;
    public static final int CAMERA_FRAME_RATE_MAX = 30;
    public static int mRealFrameRate = CAMERA_FRAME_RATE;
    public static boolean isFrameRateSure = false;

    public static final int FLASH_MODE_AUTO = 1;//闪光灯自动
    public static final int FLASH_MODE_ON = 2;//闪光灯开启
    public static final int FLASH_MODE_OFF = 3;//闪光灯关闭
    public static final int FLASH_MODE_TORCH = 4;//闪光灯常亮

    private static Camera camera = null;
    private static int cameraID = Camera.getNumberOfCameras()>1?1:0;//得到摄像头的个数;
    private static int surfaceRotation = 1;
    private static SurfaceTexture surfaceTexture;
    private static SurfaceView surfaceView;

    private static CameraInfo mCameraInfo;

    public static Camera getCamera(){
        return camera;
    }

    public static boolean openCamera(){
        if(camera == null){
            try{
                camera = Camera.open(cameraID);
                setDefaultParameters();
                return true;
            }catch(RuntimeException e){
                return false;
            }
        }
        return false;
    }

    public static boolean openCamera(int id){
        if(camera == null){
            try{
                camera = Camera.open(id);
                cameraID = id;
                setDefaultParameters();
                return true;
            }catch(RuntimeException e){
                return false;
            }
        }
        return false;
    }

    public static void releaseCamera(){
        if(camera != null){
            camera.setPreviewCallback(null);
            camera.setPreviewCallbackWithBuffer(null);
            camera.cancelAutoFocus();
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(null);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
            camera.release();
            camera = null;
        }
    }

    public static int getCameraID(){
        return cameraID;
    }

    public static void setCameraID(int cameraID) {
        CameraEngine.cameraID = cameraID;
    }

    public static void setSurfaceRotation(int surfaceRotation) {
        CameraEngine.surfaceRotation = surfaceRotation;
    }

    public void resumeCamera(){
        openCamera();
    }

    public void setParameters(Parameters parameters){
        camera.setParameters(parameters);
    }

    public Parameters getParameters(){
        if(camera != null)
            camera.getParameters();
        return null;
    }

    public static boolean switchCamera(){
        releaseCamera();
        cameraID = cameraID == 0 && Camera.getNumberOfCameras()>1 ? 1 : 0;
        boolean isSucceed = openCamera(cameraID);
        startPreview(surfaceTexture);
        return isSucceed;
    }

    /**
     * ------------- 设置闪光灯模式 ------------------
     * @param flashMode
     */
    public static void setFlashMode(int flashMode){
        try{
            Camera.Parameters parameters = camera.getParameters();
            if(flashMode == FLASH_MODE_OFF){
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }else if(flashMode == FLASH_MODE_ON){
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }else{
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
//        CacheUtil.putInt(AppUtil.getApplicationContext(), FLASH_MODE, flashMode);
            camera.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void setDefaultParameters(){
        if(camera == null){
            return ;
        }
        Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        Size previewSize = CameraUtils.getLargePreviewSize(camera);
        parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        if(!isFrameRateSure && mRealFrameRate == CAMERA_FRAME_RATE){
            List<int[]> fpsList =  parameters.getSupportedPreviewFpsRange();
            int tempFps = 30001;
            for(int i=0;i<fpsList.size();i++){
                int[] fps = fpsList.get(i);
                if(fps[0] == fps[1]){
                    if(fps[0] >= (CAMERA_FRAME_RATE * 1000) && fps[0]<= tempFps){
                        tempFps = fps[0];
                    }
                }
            }
            if(tempFps == 30001){
                mRealFrameRate = CAMERA_FRAME_RATE;
            }else{
                mRealFrameRate = tempFps/1000;
            }
        }
        parameters.setPreviewFpsRange(mRealFrameRate * 1000,mRealFrameRate * 1000);
        parameters.setPreviewFrameRate(mRealFrameRate);
        Size pictureSize = CameraUtils.getLargePictureSize(camera);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setRotation(getRightCameraOrientation(CameraEngine.cameraID));
//        parameters.setExposureCompensation(-1);
        camera.setParameters(parameters);
//        DebugTool.warn("CameraEngineExposureCompensation:" + parameters.getMinExposureCompensation() + "," + parameters.getMaxExposureCompensation());
//        camera.setDisplayOrientation(getRightCameraOrientation(CameraEngine.cameraID));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int[] fpsa = new int[2];
//                camera.getParameters().getPreviewFpsRange(fpsa);
//                DebugTool.warn("CameraEnginePreviewFpsAA:" + fpsa[0] + "," + fpsa[1] + "," + camera.getParameters().getPreviewFrameRate());
//            }
//        },2000);
//        if(!isFrameReal && CacheUtil.getInt(AppUtil.getApplicationContext(),FRAME_RATE_KEY,0) == 0){
//            byte[] data = new byte[PREVIEW_WIDTH*PREVIEW_HEIGHT*3/2];
//            camera.addCallbackBuffer(data);
//            camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] data, Camera camera) {
//                    mFrameCount++;
//                    DebugTool.warn("CameraPreviewCallback:" + mFrameCount);
//                    CameraEngine.camera.addCallbackBuffer(data);
//                    if(mFrameStartTime > 0 && System.currentTimeMillis()-mFrameStartTime >= 2000){//开始确定帧率
//                        camera.setPreviewCallbackWithBuffer(null);
//                        isFrameStart = false;
//                        isFrameReal = true;
//
//                        if(mFrameCount>=mRealFrameRate){
//                            if(mFrameCount/2-mRealFrameRate<4){
//                                return ;
//                            }else{
//                                mRealFrameRate = mFrameCount/2 + (5-mFrameCount%5);
//                            }
//                        }
//                        DebugTool.warn("CameraPreviewCallback:mRealFrameRate:" + mRealFrameRate);
//                        return ;
//                    }
//                    if(!isFrameStart){
//                        isFrameStart = true;
//                        mFrameStartTime = System.currentTimeMillis();
//                    }
//                }
//            });
//        }
    }
//    private static long mFrameStartTime;
//    private static int mFrameCount;
//    private static boolean isFrameStart;
//    private static boolean isFrameReal;


    public static int getSureFrameRate(int unknownFrameRate){
        try{

            Parameters parameters = camera.getParameters();
            List<Integer> frames = parameters.getSupportedPreviewFrameRates();
            int maxFrameRate = 0;
            for(int i=0;i<frames.size();i++){
                if(frames.get(i) > maxFrameRate){
                    maxFrameRate = frames.get(i);
                }
            }
            if(maxFrameRate != 0 && unknownFrameRate > maxFrameRate){
                unknownFrameRate = maxFrameRate;
            }
            int tempSureFrameRate = 15;
            int offsetFrame = 20;
            for(int i=0;i<frames.size();i++){
                if(frames.get(i)>=unknownFrameRate && frames.get(i)>=15){
                    if(frames.get(i) - unknownFrameRate < offsetFrame){
                        offsetFrame = frames.get(i) - unknownFrameRate;
                        tempSureFrameRate = frames.get(i);
                    }
                }
            }
            return tempSureFrameRate;
        }catch (Exception e){
            e.printStackTrace();
        }

        if(unknownFrameRate > 25){
            return CameraEngine.CAMERA_FRAME_RATE_MAX;
        }else if(unknownFrameRate > 20){
            return CameraEngine.CAMERA_FRAME_RATE_MID;
        }else{
            return CameraEngine.CAMERA_FRAME_RATE;
        }
    }

    /**
     * --------------- 获得系统默认摄像头旋转的角度 ------------------
     * @param cameraId 摄像头Id
     * @return
     */
    public static int getRightCameraOrientation(int cameraId) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

//        int rotation = context.getWindowManager().getDefaultDisplay()
//                .getRotation();
        int degrees = 90;
        switch (surfaceRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        //
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static Size getPreviewSize(){
        if(camera == null){
            return null;
        }
        try{
            return camera.getParameters().getPreviewSize();
        }catch (Exception e){
            return null;
        }
    }

    private static Size getPictureSize(){
        if(camera == null){
            return null;
        }
        try{
            return camera.getParameters().getPictureSize();
        }catch (Exception e){
            return null;
        }
    }

    public static void startPreview(SurfaceTexture surfaceTexture){
        if(camera != null)
            try {
                camera.setPreviewTexture(surfaceTexture);
                CameraEngine.surfaceTexture = surfaceTexture;
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void setPreviewCallback(Camera.PreviewCallback previewCallback, byte[] data){
        if(camera == null){
            return ;
        }
        camera.addCallbackBuffer(data);
        camera.setPreviewCallbackWithBuffer(previewCallback);
//        camera.setPreviewCallback(previewCallback);
    }

    public static void addCallbackBuffer(byte[] data){
        camera.addCallbackBuffer(data);
    }

    public static void startPreview(){
        if(camera != null)
            camera.startPreview();
    }

    public static void stopPreview(){
        camera.stopPreview();
    }

    public static void setRotation(int rotation){
        if(camera == null){
            return ;
        }
        Parameters params = camera.getParameters();
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                                   Camera.PictureCallback jpegCallback){
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public static boolean isOpenCameraSucceed(){
        return camera != null;
    }


    public static com.tiktokdemo.lky.tiktokdemo.record.camera.camera.utils.CameraInfo   getCameraInfo(){
        com.tiktokdemo.lky.tiktokdemo.record.camera.camera.utils.CameraInfo  info = new com.tiktokdemo.lky.tiktokdemo.record.camera.camera.utils.CameraInfo();

        try{
        Size size = getPreviewSize();
        CameraInfo cameraInfo = new CameraInfo();
        mCameraInfo = cameraInfo;
            Camera.getCameraInfo(cameraID, cameraInfo);
        info.previewWidth = PREVIEW_WIDTH;
        info.previewHeight = PREVIEW_HEIGHT;
        info.orientation = cameraInfo.orientation;
        info.isFront = cameraID == 1 ? true : false;
        size = getPictureSize();
        if (size != null){
            info.pictureWidth = size.width;
            info.pictureHeight = size.height;
        }
        if(camera != null){
            info.flashMode = camera.getParameters().getFlashMode();
        }
        return info;
        }catch (Exception e){
            e.printStackTrace();
            return info;
        }
    }
}