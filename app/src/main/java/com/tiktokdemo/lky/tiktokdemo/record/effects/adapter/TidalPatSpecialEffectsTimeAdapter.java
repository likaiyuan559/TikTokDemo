package com.tiktokdemo.lky.tiktokdemo.record.effects.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.bean.SpecialEffectsType;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.CommRecyclerViewHolder;
import com.tiktokdemo.lky.tiktokdemo.record.weight.SpecialEffectsSelectorButton;


/**
 * Created by lky on 2017/6/23.
 */

public class TidalPatSpecialEffectsTimeAdapter extends RecyclerView.Adapter<CommRecyclerViewHolder>{

    private ArrayList<SpecialEffectsType> mSpecialEffectsTypes;

    private TidalPatSpecialEffectsFilterClickListener mTidalPatSpecialEffectsFilterClickListener;

    private SpecialEffectsType mCurrentType = SpecialEffectsType.Default;

    public TidalPatSpecialEffectsTimeAdapter(){
        mSpecialEffectsTypes = new ArrayList<>();
        mSpecialEffectsTypes.add(SpecialEffectsType.Default);
        mSpecialEffectsTypes.add(SpecialEffectsType.TimeBack);
    }

    public void setTidalPatSpecialEffectsFilterClickListener(TidalPatSpecialEffectsFilterClickListener tidalPatSpecialEffectsFilterClickListener) {
        mTidalPatSpecialEffectsFilterClickListener = tidalPatSpecialEffectsFilterClickListener;
    }

    public void setCurrentType(SpecialEffectsType currentType) {
        mCurrentType = currentType;
        notifyDataSetChanged();
    }

    @Override
    public CommRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_special_effects_selector_item,parent,false);
        return new SpecialEffectsTimeHolder(parent.getContext(),view);
    }

    @Override
    public void onBindViewHolder(CommRecyclerViewHolder holder, int position) {
        if(holder instanceof SpecialEffectsTimeHolder){
            bindTimeHolder((SpecialEffectsTimeHolder)holder,position);
        }
    }

    private void bindTimeHolder(SpecialEffectsTimeHolder holder, final int position) {
        holder.mSpecialEffectsSelectorButton.setSpecialEffectsSelectorListener(new SpecialEffectsSelectorButton.SpecialEffectsSelectorListener() {
            @Override
            public void onTouchDown() {
                mTidalPatSpecialEffectsFilterClickListener.onItemTouchDown(position,mSpecialEffectsTypes.get(position));
            }

            @Override
            public void onTouchUp() {
                mTidalPatSpecialEffectsFilterClickListener.onItemTouchDown(position,mSpecialEffectsTypes.get(position));
            }

            @Override
            public void onStateChange(boolean isSelector) {
                if(isSelector){
                    mCurrentType = mSpecialEffectsTypes.get(position);
                    notifyDataSetChanged();
                    mTidalPatSpecialEffectsFilterClickListener.onItemStateChange(position,isSelector,mSpecialEffectsTypes.get(position));
                }
            }
        });
        holder.mSpecialEffectsSelectorButton.setTouching(mSpecialEffectsTypes.get(position) == mCurrentType);


        switch (mSpecialEffectsTypes.get(position)){
            case Default:
                holder.mSpecialEffectsSelectorButton.setDefaultRes(R.mipmap.se_un_state);
                holder.mSpecialEffectsSelectorButton.setSelectedRes(R.mipmap.se_un_state_selector);
                holder.mSpecialEffectsSelectorButton.setTouchMode(SpecialEffectsSelectorButton.TouchMode.SELECTOR);
                holder.mTextView.setText(R.string.tidal_pat_upload_se_un_state);
                break;
            case TimeBack:
                holder.mSpecialEffectsSelectorButton.setDefaultRes(R.mipmap.se_time_back);
                holder.mSpecialEffectsSelectorButton.setSelectedRes(R.mipmap.se_time_back_seletcor);
                holder.mSpecialEffectsSelectorButton.setTouchMode(SpecialEffectsSelectorButton.TouchMode.SELECTOR);
                holder.mTextView.setText(R.string.tidal_pat_upload_se_time_back);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSpecialEffectsTypes.size();
    }

    public class SpecialEffectsTimeHolder extends CommRecyclerViewHolder{
        SpecialEffectsSelectorButton mSpecialEffectsSelectorButton;
        TextView mTextView;

        public SpecialEffectsTimeHolder(Context context, View itemView) {
            super(context, itemView);
            mSpecialEffectsSelectorButton = getView(R.id.tidal_pat_special_effects_item_btn);
            mTextView = getView(R.id.tidal_pat_special_effects_item_txt);
        }
    }
}
