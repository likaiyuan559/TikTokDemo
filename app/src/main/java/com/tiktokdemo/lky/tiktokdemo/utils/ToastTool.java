package com.tiktokdemo.lky.tiktokdemo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastTool {

    private static Toast mToast;

    private ToastTool() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context 上下文对象
     * @param message 需要显示的字符串信息
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow){
            if(mToast == null){
                mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            }else{
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context 上下文对象
     * @param message 资源ID
     */
    public static void showShort(Context context, int message) {
//        if (isShow)
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        if (isShow){
            if(mToast == null){
                mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            }else{
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context 上下文对象
     * @param message 需要显示的字符串信息
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context 上下文对象
     * @param message 资源ID
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context  上下文对象
     * @param message  需要显示的字符串信息
     * @param duration 需要显示的时间，单位毫秒
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context  上下文对象
     * @param message  资源ID
     * @param duration 需要显示的时间，单位毫秒
     */
    public static void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义Toast,
     * @param context
     * @param resId
     */
    public static void showWhiteToast(Context context, int resId) {
//        Toast toast = new Toast(context);
////        TextView toastView = (TextView) View.inflate(context, R.layout.layout_white_toast, null);
////        toastView.setText(resId);
////        toast.setView(toastView);
//        toast.setText(resId);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.show();
        showShort(context,resId);
    }

    /**
     * 自定义toast
     * @param context
     * @param text
     */
    public static void showWhiteToast(Context context, CharSequence text) {
//        Toast toast = new Toast(context);
////        TextView toastView = (TextView) View.inflate(context, R.layout.layout_white_toast, null);
////        toastView.setMinWidth(DensityUtils.dp2px(context,100));
////        toastView.setGravity(Gravity.CENTER);
////        toastView.setText(text);
////        toast.setView(toastView);
//        toast.setText(text);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.show();
        showShort(context,text);
    }
}
