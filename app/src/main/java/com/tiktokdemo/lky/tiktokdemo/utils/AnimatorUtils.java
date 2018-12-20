package com.tiktokdemo.lky.tiktokdemo.utils;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lky on 2017/7/5.
 */

public class AnimatorUtils {

    public enum AnimatorPlayType{
        Sequentially,
        Together
    }

    //播放动画集合
    public static void playAnimatorArray(ArrayList<Animator> animators, AnimatorPlayType playType){
        AnimatorSet set = new AnimatorSet();
        switch (playType){
            case Sequentially:
                set.playSequentially(animators);
                break;
            case Together:
                set.playTogether(animators);
                break;
        }
        set.start();
    }
    //播放动画集合
    public static void playAnimatorArray(ArrayList<Animator> animators, AnimatorPlayType playType, @Nullable
    final FreeAnimatorListener animatorListener){
        AnimatorSet set = new AnimatorSet();
        switch (playType){
            case Sequentially:
                set.playSequentially(animators);
                break;
            case Together:
                set.playTogether(animators);
                break;
        }
        if(animatorListener != null){
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animatorListener.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animatorListener.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animatorListener.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    animatorListener.onAnimationRepeat(animation);
                }
            });
        }
        set.start();
    }


    /**
     * 控件透明度的动画
     * @param view view
     * @param isShow 消失or出现
     * @param duration 动画时长
     * @param animatorListener 动画回调监听
     * @return
     */
    public static Animator viewAlphaAnimator(final View view, final boolean isShow, long duration, @Nullable
    final FreeAnimatorListener animatorListener){
        ObjectAnimator
                objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA,isShow?0f:1f,isShow?1f:0f);
        objectAnimator.setDuration(duration);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(isShow){
                    view.setVisibility(View.VISIBLE);
                }
                if(animatorListener != null){
                    animatorListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow){
                    view.setVisibility(View.INVISIBLE);
                }
                if(animatorListener != null){
                    animatorListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
//        objectAnimator.start();
        return objectAnimator;
    }

    /**
     * 控件的平移动画
     * @param view view
     * @param startTranslationX 平移开始时的x，可为0
     * @param endTranslationX 平移结束时的x，可为0
     * @param startTranslationY 平移开始时的y，可为0
     * @param endTranslationY 平移结束时的y，可为0（如果 endTranslationX==0 && endTranslationY==0 ，则返回一个new ObjectAnimator）
     * @param duration 持续时长
     * @param animatorListener 动画回调监听
     * @return
     */
    public static Animator translationAnimator(View view, int startTranslationX, int endTranslationX, int startTranslationY, int endTranslationY, long duration, @Nullable
            TimeInterpolator timeInterpolator, @Nullable final FreeAnimatorListener animatorListener){
        ObjectAnimator objectAnimatorX = null;
        ObjectAnimator objectAnimatorY = null;
//        if(endTranslationX != 0){
            objectAnimatorX = ObjectAnimator
                    .ofFloat(view, View.TRANSLATION_X,startTranslationX,endTranslationX);
            objectAnimatorX.setDuration(duration);
//        }
//        if(endTranslationY != 0){
            objectAnimatorY = ObjectAnimator
                    .ofFloat(view, View.TRANSLATION_Y,startTranslationY,endTranslationY);
            objectAnimatorY.setDuration(duration);
//        }

//        if(objectAnimatorX != null && objectAnimatorY != null){
            AnimatorSet animatorSet = new AnimatorSet();
        if(timeInterpolator != null){
            animatorSet.setInterpolator(timeInterpolator);
        }
            if(animatorListener != null){
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animatorListener.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animatorListener.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        animatorListener.onAnimationCancel(animation);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        animatorListener.onAnimationRepeat(animation);
                    }
                });
            }
            animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
            return animatorSet;
//        }else if(objectAnimatorX != null){
//            if(animatorListener != null){
//                objectAnimatorX.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        animatorListener.onAnimationStart(animation);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        animatorListener.onAnimationEnd(animation);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        animatorListener.onAnimationCancel(animation);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//                        animatorListener.onAnimationRepeat(animation);
//                    }
//                });
//            }
//
//            return objectAnimatorX;
//        }else if(objectAnimatorY != null){
//            if(animatorListener != null){
//                objectAnimatorY.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        animatorListener.onAnimationStart(animation);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        animatorListener.onAnimationEnd(animation);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        animatorListener.onAnimationCancel(animation);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//                        animatorListener.onAnimationRepeat(animation);
//                    }
//                });
//            }
//            return objectAnimatorY;
//        }else{
//            return new ObjectAnimator();
//        }
    }

    /**
     * 控件的旋转动画
     * @param view view
     * @param startAngle 开始的角度
     * @param endAngle 结束的角度
     * @param duration 持续时长
     * @param animatorListener 动画回调监听
     * @return
     */
    public static Animator rotationAnimator(View view, int startAngle, int endAngle, int duration, @Nullable
    final FreeAnimatorListener animatorListener){
        ObjectAnimator
                objectAnimator = ObjectAnimator.ofFloat(view, View.ROTATION,startAngle,endAngle);
        objectAnimator.setDuration(duration);
        if(animatorListener != null){
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animatorListener.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animatorListener.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animatorListener.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    animatorListener.onAnimationRepeat(animation);
                }
            });
        }
        return objectAnimator;
    }


    /**
     * view背景的渐变颜色渐变动画
     * @param view view
     * @param startLeftColor 开始渐变前左边的颜色
     * @param startRightColor 开始渐变后右边的颜色
     * @param endLeftColor 渐变后的左边的颜色
     * @param endRightColor 渐变后的右边的颜色
     * @param duration 持续时长
     * @param animatorListener 动画回调监听
     * @return
     */
    public static Animator viewBackgroundEvaluateChangeAnimator(final View view, final int startLeftColor, final int startRightColor, final int endLeftColor, final int endRightColor, long duration, @Nullable
    final FreeAnimatorListener animatorListener){

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
            valueAnimator.setDuration(duration);
            final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentXLastColor = (int) (argbEvaluator.evaluate((float)animation.getAnimatedValue(), startLeftColor, endLeftColor));
                    int currentYLastColor = (int) (argbEvaluator.evaluate((float)animation.getAnimatedValue(), startRightColor, endRightColor));
//                Shader shader = new LinearGradient(x0, y0, x1, y1,
//                        new int[] { currentXLastColor, currentYLastColor},
//                        null, Shader.TileMode.CLAMP);
                    Drawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{currentXLastColor, currentYLastColor});
                    view.setBackground(drawable);
                }
            });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(animatorListener != null){
                    animatorListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(animatorListener != null){
                    animatorListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        valueAnimator.start();
        return valueAnimator;
    }

    /**
     * TextView文本颜色渐变动画
     * @param textView textView
     * @param startColor 开始的颜色
     * @param endColor 结束的颜色
     * @param duration 持续时长
     * @param animatorListener 动画回调监听
     * @return
     */
    public static Animator textViewColorChangeAnimator(final TextView textView, final int startColor, final int endColor, long duration, @Nullable
    final FreeAnimatorListener animatorListener){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(duration);
        final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentLastColor = (int) (argbEvaluator.evaluate((float)animation.getAnimatedValue(), startColor, endColor));
                textView.setTextColor(currentLastColor);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(animatorListener != null){
                    animatorListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(animatorListener != null){
                    animatorListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        valueAnimator.start();
        return valueAnimator;
    }

    public static abstract class FreeAnimatorListener{
        public void onAnimationStart(Animator animation){

        }
        public void onAnimationEnd(Animator animation) {

        }
        public void onAnimationCancel(Animator animation) {

        }
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
