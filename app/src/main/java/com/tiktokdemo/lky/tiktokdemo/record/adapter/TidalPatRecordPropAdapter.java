package com.tiktokdemo.lky.tiktokdemo.record.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tiktokdemo.lky.tiktokdemo.R;
import com.tiktokdemo.lky.tiktokdemo.record.camera.widget.CommRecyclerViewHolder;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatPropFactory;
import com.tiktokdemo.lky.tiktokdemo.record.helper.TidalPatPropType;

/**
 * Created by lky on 2017/5/3.
 */

public class TidalPatRecordPropAdapter extends RecyclerView.Adapter<CommRecyclerViewHolder> {

    private ArrayList<TidalPatPropType> mTidalPatPropTypes;

    private OnPropItemClickListener mOnPropItemClickListener;

    public TidalPatRecordPropAdapter(ArrayList<TidalPatPropType> tidalPatPropTypes){
        mTidalPatPropTypes = tidalPatPropTypes;
    }

    public void setOnPropItemClickListener(OnPropItemClickListener onPropItemClickListener) {
        mOnPropItemClickListener = onPropItemClickListener;
    }

    @Override
    public CommRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.layout_tadal_pat_record_prop,parent,false);
        return new RecordPropViewHolder(parent.getContext(),view);
    }

    @Override
    public void onBindViewHolder(CommRecyclerViewHolder holder, int position) {
        bindViewHolder((RecordPropViewHolder) holder,position);
    }

    public void bindViewHolder(RecordPropViewHolder holder, final int position){
        holder.mImageView.setImageResource(mTidalPatPropTypes.get(position).getBackgroundRes());
        if(mTidalPatPropTypes.get(position) == TidalPatPropFactory.getInstance().getPropType()){
            holder.mView.setVisibility(View.VISIBLE);
        }else{
            holder.mView.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnPropItemClickListener != null){
                    mOnPropItemClickListener.onPropItemClick(mTidalPatPropTypes.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTidalPatPropTypes.size();
    }

    public class RecordPropViewHolder extends CommRecyclerViewHolder{

        public ImageView mImageView;
        public View mView;

        public RecordPropViewHolder(Context context, View itemView) {
            super(context, itemView);
            mImageView = getView(R.id.layout_tidal_pat_record_prop_img);
            mView = getView(R.id.layout_tidal_pat_record_prop_view);
        }
    }

    public interface OnPropItemClickListener{
        void onPropItemClick(TidalPatPropType tidalPatPropType);
    }

}
