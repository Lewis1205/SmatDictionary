package com.vany.tulin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vany.tulin.R;
import com.vany.tulin.dto.CookMenu;

import java.util.List;

/**
 * Created by van元 on 2017/2/14.
 */

public class CookAdapter extends RecyclerView.Adapter<CookAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<CookMenu> mCookDatas;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CookAdapter(Context context, List<CookMenu> datas) {
        this.mContext = context;
        this.mCookDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_cook, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.cookNameTV.setText(mCookDatas.get(position).getCookName());
        holder.cookInfoTV.setText(mCookDatas.get(position).getCookInfo());
        holder.cookUrlTV.setText(mCookDatas.get(position).getCookUrl());
        //在这里设置点击监听事件回调
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mCookDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cookNameTV;
        TextView cookInfoTV;
        TextView cookUrlTV;

        public MyViewHolder(View itemView) {
            super(itemView);
            cookNameTV = (TextView) itemView.findViewById(R.id.cookNameTV);
            cookInfoTV = (TextView) itemView.findViewById(R.id.cookInfoTV);
            cookUrlTV = (TextView) itemView.findViewById(R.id.cookUrlTV);
        }
    }
}
