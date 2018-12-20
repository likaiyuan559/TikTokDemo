package com.tiktokdemo.lky.tiktokdemo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tiktokdemo.lky.tiktokdemo.BaseApplication;
/**
 * Created by lky on 2018/12/11
 */
public class HomeCacheUtil {

    private static final String KEY = "HOME_CACHE_KEY";

    /**
     * 保存一个对象
     *
     * @param key 保存的键
     * @param obj 需要保存的对象
     * @return 是否保存成功
     */
    private static boolean saveItem(String key, Object obj) {
        try {
            saveObject(key, serialize(obj));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取一个保存到了本地的对象
     *
     * @param key 保存的键
     * @return 返回一个从本地取出来的对象
     */
    private static Object getItem(String key) {
        try {
            return deSerialization(getObject(key));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //出现异常返回null
        return null;
    }

    /**
     * 序列化对象
     *
     * @param obj 需要序列化的对象
     * @return 序列化过的对象，变成String
     * @throws IOException IO操作异常
     */
    public static String serialize(Object obj) throws IOException {
        //        long startTime = System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        //        Log.d("serial", "serialize str =" + serStr);
        //        long endTime = System.currentTimeMillis();
        //        Log.d("serial", "序列化耗时为:" + (endTime - startTime));
        return serStr;
    }

    /**
     * 反序列化对象
     *
     * @param str 需要反序列化的对象的String
     * @return 返回一个反序列化过的对象
     * @throws IOException            IO操作的异常
     * @throws ClassNotFoundException 异常
     */
    public static Object deSerialization(String str) throws IOException,
            ClassNotFoundException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        //        long startTime = System.currentTimeMillis();
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        //        long endTime = System.currentTimeMillis();
        //        Log.d("serial", "反序列化耗时为:" + (endTime - startTime));
        return obj;
    }

    /**
     * 保存一个对象到SharedPreferences
     *
     * @param key       键
     * @param strObject 需要保存的序列化过的对象
     */
    private static void saveObject(String key, String strObject) {
        SharedPreferences sp = BaseApplication.mContext.getSharedPreferences(KEY, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, strObject);
        edit.commit();
    }

    /**
     * 获取到一个对象
     *
     * @param key 键
     * @return 序列化过的对象，取出后需要反序列化
     */
    private static String getObject(String key) {
        SharedPreferences sp = BaseApplication.mContext.getSharedPreferences(KEY, 0);
        return sp.getString(key, null);
    }

    /**
     * 清除SharedPreferences里所有的信息
     */
    private static void clearAllSharedPreferencesData() {
        SharedPreferences sp = BaseApplication.mContext.getSharedPreferences(KEY, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
    }

}
