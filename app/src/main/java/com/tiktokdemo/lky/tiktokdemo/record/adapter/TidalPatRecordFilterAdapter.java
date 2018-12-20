package com.tiktokdemo.lky.tiktokdemo.record.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.CommRecyclerViewHolder;
import com.tiktokdemo.lky.tiktokdemo.record.helper.MagicFilterFactory;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatFilterType;


/**
 * Created by lky on 2017/4/28.
 */

public class TidalPatRecordFilterAdapter extends RecyclerView.Adapter<CommRecyclerViewHolder> {

    private ArrayList<TidalPatFilterType> mTidalPatFilterTypes;

    private OnTidalPatFilterItemClickListener mOnTidalPatFilterItemClickListener;

    public TidalPatRecordFilterAdapter(){
        mTidalPatFilterTypes = MagicFilterFactory.getInstance().getTidalPatRecordFilterTypes();
    }

    public void setOnTidalPatFilterItemClickListener(OnTidalPatFilterItemClickListener tidalPatFilterItemClickListener) {
        mOnTidalPatFilterItemClickListener = tidalPatFilterItemClickListener;
    }

    @Override
    public CommRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.layout_tidal_pat_filter,parent,false);
        return new TidalPatFilterViewHolder(parent.getContext(),view);
    }

    @Override
    public void onBindViewHolder(CommRecyclerViewHolder holder, int position) {
        if(holder instanceof TidalPatFilterViewHolder){
            bindTidalPatFilterViewHolder((TidalPatFilterViewHolder) holder,position);
        }
    }

    public void bindTidalPatFilterViewHolder(TidalPatFilterViewHolder holder,int position){
        final TidalPatFilterType tidalPatFilterType = mTidalPatFilterTypes.get(position);
        holder.mImageView.setImageResource(tidalPatFilterType.getFilterBackgroundRes());
        holder.mTextView.setText(tidalPatFilterType.getFilterName());
        if(MagicFilterFactory.getInstance().getCurrentFilterType() == tidalPatFilterType){
            holder.mView.setBackgroundResource(R.drawable.bg_tidal_pat_filter_selected);
            holder.mTextView.setTextColor(0xFFFACE15);
        }else{
            holder.mView.setBackgroundColor(0x00000000);
            holder.mTextView.setTextColor(0xFFFFFFFF);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnTidalPatFilterItemClickListener != null){
                    mOnTidalPatFilterItemClickListener.onFilterItemClick(tidalPatFilterType);
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTidalPatFilterTypes.size();
    }


    class TidalPatFilterViewHolder extends CommRecyclerViewHolder{

        public ImageView mImageView;
        public TextView mTextView;
        public View mView;

        public TidalPatFilterViewHolder(Context context, View itemView) {
            super(context, itemView);
            mImageView = getView(R.id.layout_tidal_pat_filter_item_img);
            mTextView = getView(R.id.layout_tidal_pat_filter_item_txt);
            mView = getView(R.id.layout_tidal_pat_filter_item_view);
        }
    }


    public interface OnTidalPatFilterItemClickListener {
        void onFilterItemClick(TidalPatFilterType type);
    }

}
