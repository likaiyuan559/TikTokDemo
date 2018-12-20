package com.tiktokdemo.lky.tiktokdemo.record.presenter;
/**
 * Created by lky on 2018/12/13
 */
public class VideoPlayContract {

    public interface View{
        void showToast(String msg);//显示toast
        void showLoadingView(boolean isShow,int loadingTxtRes);//显示loading
        void checkMusicEmpty();//bgm不可用
        void checkMusicLength(int duration);//原BGM长度
        void combineVideoStart();//开始合成视频
        void combineVideoFinish(boolean isLooping,String path);//合成视频完成
        void combineVideoError(String path);//合成视频失败
        void inTimeBackState(String path);//进入时光倒流状态
        void inTimeNotState(String path);//进入时光倒流的未选择状态
        void resetSpecialEffectsSeekBar(boolean isMax);//重置特效中的seekBar
        void changeSpecialEffectsModeFilterFinish(String path);//改变灵魂出窍模式成功
        void changeSpecialEffectsModeFinish(String path);//改变特效模式成功
        void combineDidTimeBackFinish();//完成时光倒流视频合成
        void completeFinish();//完成
    }

    public interface Presenter{

    }
}
