package com.tiktokdemo.lky.tiktokdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 李凯源 on 2016/4/12.
 * 缓存类, 所有的数据都是采用SharedPreferences方式存储和获取
 */
public class CacheUtil {
    private static final String CACHE_FILE_NAME = "LkyDemo";
    private static SharedPreferences mSharedPreferences;


    /**
     * @param context
     * @param key
     *            要取的数据的键
     * @param defValue
     *            缺省值
     * @return
     */
    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 存储一个boolean类型数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 存储一个String类型的数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 根据key取出一个String类型的值
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * 存储一个String类型的数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(Context context, String key, int value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 根据key取出一个int类型的值
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getInt(key, defValue);
    }

    public static void clearData(Context context) {

        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        } else {
            mSharedPreferences.edit().clear().commit();
        }
    }
}
