package com.tiktokdemo.lky.tiktokdemo.record.presenter;

/**
 * Created by lky on 2018/12/13
 */
public class RecordVideoContract {

    public interface View{
        void showToast(String msg);//显示toast
        void showLoadingView(boolean isShow,int loadingTxtRes);//显示loading
        void canRecord(boolean isCan);//是否可以录制
        void checkMusicEmpty();//bgm不可用
        void checkMusicLength(int duration);//原BGM长度
        void createSpeedAudioSuccess();//创建各个速度的音频文件成功

        void combineVideoSuccess(String outputVideoPath);//合成视频成功
        void combineVideoFail(String errorMsg);//合成视频失败

        void startRecordSuccess(float progress);//开始录制成功

        void recordProgress(boolean isMinProgress,float progress);//录制状态回调
        void recordProgressForm120(float progress);//120秒模式
        void recordProgressMax();//达到了最大录制时间

        void resetRecordFinish();//重置录制完成
        void deleteRecordVideoFinish(boolean isCanSave);//删除录制内容完成

        void changeFlashModeFinish(String flashMode);//切换闪光灯模式完成
        void switchCameraFinish();//转换摄像头完成
        void changeBeautyOpenFinish(boolean isOpen);//切换美颜完成
    }

    public interface Presenter{

    }

}
