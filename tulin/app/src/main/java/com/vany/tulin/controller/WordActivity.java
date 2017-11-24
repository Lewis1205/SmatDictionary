package com.vany.tulin.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vany.tulin.R;
import com.vany.tulin.adapter.WordAdapter;
import com.vany.tulin.dao.WordDaoUtil;
import com.vany.tulin.dto.Word;
import com.vany.tulin.utils.AudioUtil;

import java.util.List;

public class WordActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<Word> mWordDatas;
    private WordAdapter mWordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        //初始化菜单数据
        initDatas();
        //初始化布局View
        initViews();
        mWordAdapter = new WordAdapter(this, mWordDatas);
        mRecyclerView.setAdapter(mWordAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());   //设置默认的动画
        mWordAdapter.setmOnItemClickListener(new WordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dealWithItemClick(view, position);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                dealWithItemLongClick(view, position);
            }
        });
    }

    private void dealWithItemClick(View view, int position) {
        String itemWordNameTV = ((TextView) view.findViewById(R.id.itemWordNameTV)).getText() + "";
        String itemEnPsTV = ((TextView) view.findViewById(R.id.itemEnPsTV)).getText() + "";
        final String itemEnPronunceTV = ((TextView) view.findViewById(R.id.itemEnPronunceTV)).getText() + "";
        String itemUsPsTV = ((TextView) view.findViewById(R.id.itemUsPsTV)).getText() + "";
        final String itemUsPronunceTV = ((TextView) view.findViewById(R.id.itemUsPronunceTV)).getText() + "";
        String itemWordMeaningTV = ((TextView) view.findViewById(R.id.itemWordMeaningTV)).getText() + "";

        View v = getLayoutInflater().inflate(R.layout.word_dialog, null);   //渲染对话框单词布局文件

        ((TextView) v.findViewById(R.id.dialog_word_TV)).setText(itemWordNameTV);
        ((TextView) v.findViewById(R.id.dialog_enps_TV)).setText(itemEnPsTV);
        ((TextView) v.findViewById(R.id.dialog_usps_TV)).setText(itemUsPsTV);
        ((TextView) v.findViewById(R.id.dialog_meaning_TV)).setText(itemWordMeaningTV);

        final ImageView enproIV = (ImageView) v.findViewById(R.id.dialog_enpro_IV);
        if (itemEnPsTV == null||itemEnPsTV.equals("")) {    //如果没有英式音标就隐藏
            enproIV.setVisibility(View.GONE);
            enproIV.setVisibility(View.GONE);
        } else {
            enproIV.setVisibility(View.VISIBLE);
            enproIV.setVisibility(View.VISIBLE);
        }
        enproIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enproIV.setBackgroundResource(R.mipmap.voice_open32);
                AudioUtil.open(itemEnPronunceTV);
                Toast.makeText(getApplicationContext(), "英式发音", Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView usproIV = (ImageView) v.findViewById(R.id.dialog_uspro_IV);
        if (itemUsPsTV == null||itemUsPsTV.equals("")) {    //如果没有美式发音音标就隐藏
            usproIV.setVisibility(View.GONE);
            usproIV.setVisibility(View.GONE);
        } else {
            usproIV.setVisibility(View.VISIBLE);
            usproIV.setVisibility(View.VISIBLE);
        }
        usproIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usproIV.setBackgroundResource(R.mipmap.voice_open32);
                AudioUtil.open(itemUsPronunceTV);
                Toast.makeText(getApplicationContext(), "美式发音", Toast.LENGTH_SHORT).show();
            }
        });

        new AlertDialog.Builder(WordActivity.this)
                .setView(v).show();
    }

    private void dealWithItemLongClick(View view, final int position) {
        TextView itemIdTV = (TextView) view.findViewById(R.id.itemIdTV);
        final long id = Long.parseLong(itemIdTV.getText() + "");    //获取其在数据库中id
        AlertDialog dialog = new AlertDialog.Builder(WordActivity.this)
                .setTitle("温馨提示")
                .setMessage("确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WordDaoUtil.getSingleTon().deleteById(id);  //从数据中删除
                        mWordDatas.remove(position);                //在更新界面之前要把List中的数据删除，否则界面还是会把List中的数据重新显示出来
                        mWordAdapter.notifyItemRemoved(position);   //从界面中删除该item，并刷新界面
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void initDatas() {
        mWordDatas = getIntent().getParcelableArrayListExtra("wordDatas");
    }
}
