package com.vany.tulin.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vany.tulin.R;
import com.vany.tulin.dao.WordDaoUtil;
import com.vany.tulin.dto.Word;

import java.util.List;

/**
 * Created by van元 on 2017/2/15.
 */

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Word> mWordDatas;
    private OnItemClickListener mOnItemClickListener;

    private int MAX_COUNT = 20;     //最大显示数
    private int PAGE = 1;

    private final int NORMAL_TYPE = 1;  //表示itemView类型
    private final int FOOT_TYPE = 0;    //表示FootView类型

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public WordAdapter(Context context, List<Word> datas) {
        this.mContext = context;
        this.mWordDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View normal_view = mInflater.inflate(R.layout.item_word, parent, false);
        View foot_view = mInflater.inflate(R.layout.quick_view_load_more, parent, false);
        if (viewType ==FOOT_TYPE) return new MyViewHolder(foot_view, viewType);
        return  new MyViewHolder(normal_view, viewType);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (getItemViewType(position) == FOOT_TYPE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<Word> twentyDatas = WordDaoUtil.getSingleTon().getTwentyDatas(PAGE);
                    if (twentyDatas.size()==0) {
                        holder.itemView.findViewById(R.id.load_more_loading_view).setVisibility(View.GONE);
                        holder.itemView.findViewById(R.id.load_more_load_end_view).setVisibility(View.VISIBLE);
                        return;
                    }
                    System.out.println("本次加载了"+twentyDatas.size()+"条数据@@@@@@@@@@@@@@");
                    PAGE++;
                    MAX_COUNT += twentyDatas.size();
                    mWordDatas.addAll(twentyDatas);
                    notifyDataSetChanged();
                }
            }, 1000);
        } else {
            holder.itemIdTV.setText(mWordDatas.get(position).getId()+"");
            holder.itemEnPsTV.setText(mWordDatas.get(position).getEnps());
            holder.itemEnPronunceTV.setText(mWordDatas.get(position).getEnpronunce());
            holder.itemUsPsTV.setText(mWordDatas.get(position).getUsps());
            holder.itemUsPronunceTV.setText(mWordDatas.get(position).getUspronunce());
            holder.itemWordNameTV.setText(mWordDatas.get(position).getWord());
            holder.itemWordMeaningTV.setText(mWordDatas.get(position).getMeaning());
            //在这里设置点击监听事件回调
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
                //设置长按事件监听
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(holder.itemView, position);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mWordDatas.size() < MAX_COUNT ? mWordDatas.size() : MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position == MAX_COUNT - 1 ? FOOT_TYPE : NORMAL_TYPE;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //itemView里的内容
        TextView itemIdTV;
        TextView itemEnPsTV;
        TextView itemEnPronunceTV;
        TextView itemUsPsTV;
        TextView itemUsPronunceTV;
        TextView itemWordNameTV;
        TextView itemWordMeaningTV;

        //FootView里的内容
        LinearLayout load_more_loading_view;    //表示正在加载
        FrameLayout load_more_load_fail_view;  //表示加载失败
        FrameLayout load_more_load_end_view;   //表示加载完毕，没有更多数据了

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == NORMAL_TYPE) {
                itemIdTV = (TextView) itemView.findViewById(R.id.itemIdTV);
                itemEnPsTV = (TextView) itemView.findViewById(R.id.itemEnPsTV);
                itemEnPronunceTV = (TextView) itemView.findViewById(R.id.itemEnPronunceTV);
                itemUsPsTV = (TextView) itemView.findViewById(R.id.itemUsPsTV);
                itemUsPronunceTV = (TextView) itemView.findViewById(R.id.itemUsPronunceTV);
                itemWordNameTV = (TextView) itemView.findViewById(R.id.itemWordNameTV);
                itemWordMeaningTV = (TextView) itemView.findViewById(R.id.itemWordMeaningTV);
            } else {//否则就加载FootView
                load_more_loading_view = (LinearLayout) itemView.findViewById(R.id.load_more_loading_view);
                load_more_load_fail_view = (FrameLayout) itemView.findViewById(R.id.load_more_load_fail_view);
                load_more_load_end_view = (FrameLayout) itemView.findViewById(R.id.load_more_load_end_view);
            }

        }
    }
}
