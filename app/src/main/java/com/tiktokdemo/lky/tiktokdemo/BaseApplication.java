package com.tiktokdemo.lky.tiktokdemo;

import android.app.Application;
import android.content.Context;
/**
 * Created by lky on 2018/12/11
 */
public class BaseApplication extends Application {

    public static Context mContext;

    @Override public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
