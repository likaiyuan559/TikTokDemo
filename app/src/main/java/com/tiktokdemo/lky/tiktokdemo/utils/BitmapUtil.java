package com.tiktokdemo.lky.tiktokdemo.utils;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Base64;

import com.tiktokdemo.lky.tiktokdemo.Constant;
import com.tiktokdemo.lky.tiktokdemo.R;

public class BitmapUtil {
    private static final String TAG = "BitmapUtil";
    public static Bitmap stringtoBitmap(String string){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }



    public static String saveOverlayBitmapToLocal(){
        String fileName = "overlayFile.png";
        File file = new File(Constant.IMAGE_SAVE_PATH + File.separator + fileName);
        if(file.exists()){
            return file.getAbsolutePath();
        }

        Bitmap bitmap = BitmapFactory
                .decodeResource(AppUtil.getApplicationContext().getResources(), R.drawable.shuiying);
        if(FileUtils.saveBitmapToPNG(bitmap,fileName, Constant.IMAGE_SAVE_PATH)){
            return Constant.IMAGE_SAVE_PATH + File.separator + fileName;
        }else{
            return "";
        }
    }

    public static Bitmap getVideoFrame(String videoPath, int frameNumber){
        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            return retriever.getFrameAtTime(frameNumber,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888);
    }

}
