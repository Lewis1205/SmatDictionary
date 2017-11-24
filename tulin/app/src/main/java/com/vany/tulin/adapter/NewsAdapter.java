package com.vany.tulin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vany.tulin.R;
import com.vany.tulin.dto.News;

import java.util.List;

/**
 * Created by van元 on 2017/2/15.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<News> mNewsDatas;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public NewsAdapter(Context context, List<News> datas) {
        this.mContext = context;
        this.mNewsDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_news, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.newsArticleTV.setText(mNewsDatas.get(position).getNewsArticle());
        holder.newsTimeTV.setText(mNewsDatas.get(position).getNewsTime());
        holder.newsSourceTV.setText(mNewsDatas.get(position).getNewsSource());
        holder.newsUrlTV.setText(mNewsDatas.get(position).getNewsUrl());
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
        return mNewsDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView newsArticleTV;
        TextView newsTimeTV;
        TextView newsSourceTV;
        TextView newsUrlTV;

        public MyViewHolder(View itemView) {
            super(itemView);
            newsArticleTV = (TextView) itemView.findViewById(R.id.newsArticleTV);
            newsTimeTV = (TextView) itemView.findViewById(R.id.newsTimeTV);
            newsSourceTV = (TextView) itemView.findViewById(R.id.newsSourceTV);
            newsUrlTV = (TextView) itemView.findViewById(R.id.newsUrlTV);
        }
    }
}
