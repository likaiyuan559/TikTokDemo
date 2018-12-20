package com.tiktokdemo.lky.tiktokdemo.record.manager;

import java.util.ArrayList;
import java.util.Iterator;

import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsProgressBean;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsType;
import com.heyhou.social.video.HeyhouPlayerService;
import com.heyhou.social.video.VideoPlayListener;


/**
 * Created by lky on 2017/6/26.
 */

public class SpecialEffectsPlayManager {

    private volatile static SpecialEffectsPlayManager mInstance;

    private SpecialEffectsPlayManager(){}

    public static SpecialEffectsPlayManager getInstance() {
        if(mInstance == null){
            synchronized (SpecialEffectsPlayManager.class){
                if(mInstance == null){
                    mInstance = new SpecialEffectsPlayManager();
                }
            }
        }
        return mInstance;
    }

    private static boolean isRunning;

    public static void startPlay(VideoPlayListener videoPlayListener){
        if(isRunning){
            return ;
        }
        isRunning = true;
        HeyhouPlayerService.instance.running();
        HeyhouPlayerService.instance.setListener(videoPlayListener);
        new Thread(HeyhouPlayerService.instance).start();
    }

    public static void stopPlay(){
        if(!isRunning){
            return ;
        }
        isRunning = false;
        HeyhouPlayerService.instance.removeListener();
        HeyhouPlayerService.instance.stopRun();
    }






    /**
     * 当前存在的特效
     */
    public SpecialEffectsType mCurrentSpecialEffectsFilterType = SpecialEffectsType.Default;
    private boolean isOperationFilter;

    public void setCurrentSpecialEffectsFilterType(SpecialEffectsType currentSpecialEffectsFilterType) {
        if(currentSpecialEffectsFilterType == null){
            mCurrentSpecialEffectsFilterType = SpecialEffectsType.Default;
            return ;
        }
        mCurrentSpecialEffectsFilterType = currentSpecialEffectsFilterType;
    }

    public SpecialEffectsType getCurrentSpecialEffectsFilterType() {
        return mCurrentSpecialEffectsFilterType;
    }

    public void setOperationFilter(boolean operationFilter) {
        isOperationFilter = operationFilter;
    }

    public boolean isOperationFilter() {
        return isOperationFilter;
    }

    /**
     * 滤镜特效的时间集
     */
    public ArrayList<SpecialEffectsProgressBean> mSpecialEffectsFilters;//用于撤回
    public ArrayList<SpecialEffectsProgressBean> mFiltrationSpecialEffectsFilters;//过滤过的数据，用于实际使用

    public ArrayList<SpecialEffectsProgressBean> getSpecialEffectsFilters() {
        if(mSpecialEffectsFilters == null){
            mSpecialEffectsFilters = new ArrayList<>();
        }
        synchronized (mSpecialEffectsFilters){
            return mSpecialEffectsFilters;
        }
    }

    public void setSpecialEffectsFilters(ArrayList<SpecialEffectsProgressBean> specialEffectsFilters) {
        mSpecialEffectsFilters = specialEffectsFilters;
        if(mSpecialEffectsFilters == null || mSpecialEffectsFilters.size() <= 0){
            return ;
        }
        if(mFiltrationSpecialEffectsFilters == null){
            mFiltrationSpecialEffectsFilters = new ArrayList<>();
        }
        synchronized (mSpecialEffectsFilters){
            synchronized (mFiltrationSpecialEffectsFilters){
                mFiltrationSpecialEffectsFilters.clear();
                for(SpecialEffectsProgressBean specialEffectsProgressBean:mSpecialEffectsFilters){
                    filtrationSpecialEffectsFilters(mFiltrationSpecialEffectsFilters,specialEffectsProgressBean);
                }
            }
        }
    }

    public void addSpecialEffectsFilter(SpecialEffectsProgressBean addBean){
        if(mSpecialEffectsFilters == null){
            mSpecialEffectsFilters = new ArrayList<>();
        }
        if(mFiltrationSpecialEffectsFilters == null){
            mFiltrationSpecialEffectsFilters = new ArrayList<>();
        }
        synchronized (mSpecialEffectsFilters){
            mSpecialEffectsFilters.add(addBean);
            synchronized (mFiltrationSpecialEffectsFilters){
                filtrationSpecialEffectsFilters(mFiltrationSpecialEffectsFilters,addBean);
            }
        }
    }

    public ArrayList<SpecialEffectsProgressBean> getFiltrationSpecialEffectsFilters(){
        if(mFiltrationSpecialEffectsFilters == null){
            mFiltrationSpecialEffectsFilters = new ArrayList<>();
        }
        synchronized (mFiltrationSpecialEffectsFilters){
            return mFiltrationSpecialEffectsFilters;
        }
    }

    public SpecialEffectsType getTypeFromTime(long time){
        if(mFiltrationSpecialEffectsFilters == null || mFiltrationSpecialEffectsFilters.isEmpty()){
            return SpecialEffectsType.Default;
        }
        for(SpecialEffectsProgressBean specialEffectsProgressBean:mFiltrationSpecialEffectsFilters){
            if(specialEffectsProgressBean.getTimeStart()<=time && specialEffectsProgressBean.getTimeEnd()>=time){
                return specialEffectsProgressBean.getType();
            }
        }
        return SpecialEffectsType.Default;
    }

    public void removeLastFilter(){
        if(mSpecialEffectsFilters == null || mSpecialEffectsFilters.size() <= 0){
            return ;
        }
        synchronized (mSpecialEffectsFilters){
            mSpecialEffectsFilters.remove(mSpecialEffectsFilters.size()-1);
            synchronized (mFiltrationSpecialEffectsFilters){
                mFiltrationSpecialEffectsFilters.clear();
                if(mSpecialEffectsFilters.size() <= 1){
                    mFiltrationSpecialEffectsFilters.addAll(mSpecialEffectsFilters);
                    return ;
                }
                for(SpecialEffectsProgressBean specialEffectsProgressBean:mSpecialEffectsFilters){
                    filtrationSpecialEffectsFilters(mFiltrationSpecialEffectsFilters,specialEffectsProgressBean);
                }
            }
        }
    }

    public void clearFilters(){
        if(mSpecialEffectsFilters == null){
            return ;
        }
        synchronized (mSpecialEffectsFilters){
            mSpecialEffectsFilters.clear();
            if(mFiltrationSpecialEffectsFilters != null){
                synchronized (mFiltrationSpecialEffectsFilters){
                    mFiltrationSpecialEffectsFilters.clear();
                }
            }
        }
    }

    private synchronized void filtrationSpecialEffectsFilters(ArrayList<SpecialEffectsProgressBean> filters, SpecialEffectsProgressBean addBean){
        if(filters == null || addBean == null){
            return ;
        }
        SpecialEffectsProgressBean addTempBean = new SpecialEffectsProgressBean();
        addTempBean.setTimeStart(addBean.getTimeStart());
        addTempBean.setTimeEnd(addBean.getTimeEnd());
        addTempBean.setType(addBean.getType());
        addTempBean.setShowColor(addBean.getShowColor());
        if(filters.size() <= 0){
            filters.add(addTempBean);
            return ;
        }
        ArrayList<SpecialEffectsProgressBean> tempAddFilters = new ArrayList<>();
        tempAddFilters.add(addTempBean);
        Iterator<SpecialEffectsProgressBean> iterator = filters.iterator();
        while (iterator.hasNext()){
            SpecialEffectsProgressBean tempBean = iterator.next();
            if(addTempBean.getTimeStart() <= tempBean.getTimeStart() && addTempBean.getTimeEnd() >= tempBean.getTimeEnd()){//addBean包含了该item的区间
                iterator.remove();
            }else if(addTempBean.getTimeStart() > tempBean.getTimeStart() && addTempBean.getTimeEnd() < tempBean.getTimeEnd()){//addBean在该item的区间内
                SpecialEffectsProgressBean filterBean = new SpecialEffectsProgressBean();
                filterBean.setTimeStart(addTempBean.getTimeEnd());
                filterBean.setTimeEnd(tempBean.getTimeEnd());
                filterBean.setShowColor(tempBean.getShowColor());
                filterBean.setType(tempBean.getType());
                tempBean.setTimeEnd(addTempBean.getTimeStart());
                tempAddFilters.add(filterBean);
            }else if(addTempBean.getTimeStart() <= tempBean.getTimeStart() && addTempBean.getTimeEnd() >= tempBean.getTimeStart() && addTempBean.getTimeEnd() < tempBean.getTimeEnd()){//addBean与该item的左边区间重叠了
                tempBean.setTimeStart(addTempBean.getTimeEnd());
            }else if(addTempBean.getTimeEnd() >= tempBean.getTimeEnd() && addTempBean.getTimeStart() > tempBean.getTimeStart() && addTempBean.getTimeStart() <= tempBean.getTimeEnd()){//addBean与该item的右边区间重叠了
                tempBean.setTimeEnd(addTempBean.getTimeStart());
            }else{

            }
        }
        filters.addAll(tempAddFilters);
    }
}
