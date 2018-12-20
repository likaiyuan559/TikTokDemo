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

public class TidalPatSpecialEffectsFilterAdapter extends RecyclerView.Adapter<CommRecyclerViewHolder> {

    private ArrayList<SpecialEffectsType> mSpecialEffectsTypes;

    private TidalPatSpecialEffectsFilterClickListener mTidalPatSpecialEffectsFilterClickListener;

    public TidalPatSpecialEffectsFilterAdapter(){
        mSpecialEffectsTypes = new ArrayList<>();
        mSpecialEffectsTypes.add(SpecialEffectsType.SoulOut);
    }

    public void setTidalPatSpecialEffectsFilterClickListener(TidalPatSpecialEffectsFilterClickListener tidalPatSpecialEffectsFilterClickListener) {
        mTidalPatSpecialEffectsFilterClickListener = tidalPatSpecialEffectsFilterClickListener;
    }

    @Override
    public CommRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_special_effects_selector_item,parent,false);
        return new SpecialEffectsFilterHolder(parent.getContext(),view);
    }

    @Override
    public void onBindViewHolder(CommRecyclerViewHolder holder, int position) {
//        tidal_pat_special_effects_item_btn
        if(holder instanceof SpecialEffectsFilterHolder){
            bindFilterHolder((SpecialEffectsFilterHolder)holder,position);
        }
    }

    private void bindFilterHolder(SpecialEffectsFilterHolder holder, final int position) {
        switch (mSpecialEffectsTypes.get(position)){
            case SoulOut:
                holder.mSpecialEffectsSelectorButton.setDefaultRes(R.mipmap.se_soul_out);
                holder.mSpecialEffectsSelectorButton.setSelectedRes(R.mipmap.se_soul_out_selector);
                holder.mSpecialEffectsSelectorButton.setTouchMode(SpecialEffectsSelectorButton.TouchMode.TOUCH);
                holder.mTextView.setText(R.string.tidal_pat_upload_se_soul_out);
                holder.mSpecialEffectsSelectorButton.setSpecialEffectsSelectorListener(new SpecialEffectsSelectorButton.SpecialEffectsSelectorListener() {
                    @Override
                    public void onTouchDown() {
                        mTidalPatSpecialEffectsFilterClickListener.onItemTouchDown(position,mSpecialEffectsTypes.get(position));
                    }

                    @Override
                    public void onTouchUp() {
                        mTidalPatSpecialEffectsFilterClickListener.onItemTouchUp(position);
                    }

                    @Override
                    public void onStateChange(boolean isSelector) {
                        mTidalPatSpecialEffectsFilterClickListener.onItemStateChange(position,isSelector,mSpecialEffectsTypes.get(position));
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSpecialEffectsTypes.size();
    }


    public class SpecialEffectsFilterHolder extends CommRecyclerViewHolder{
        SpecialEffectsSelectorButton mSpecialEffectsSelectorButton;
        TextView mTextView;

        public SpecialEffectsFilterHolder(Context context, View itemView) {
            super(context, itemView);
            mSpecialEffectsSelectorButton = getView(R.id.tidal_pat_special_effects_item_btn);
            mTextView = getView(R.id.tidal_pat_special_effects_item_txt);
        }
    }

}
