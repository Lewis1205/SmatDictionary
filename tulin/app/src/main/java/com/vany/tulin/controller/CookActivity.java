package com.vany.tulin.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vany.tulin.R;
import com.vany.tulin.adapter.CookAdapter;
import com.vany.tulin.dto.CookMenu;

import java.util.List;

public class CookActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<CookMenu> mCookDatas;
    private CookAdapter mCookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        //初始化菜单数据
        initDatas();
        //初始化布局View
        initViews();
        mCookAdapter = new CookAdapter(this, mCookDatas);
        mRecyclerView.setAdapter(mCookAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());   //设置默认的动画
        mCookAdapter.setmOnItemClickListener(new CookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = (TextView) view.findViewById(R.id.cookUrlTV);
                Intent intent = new Intent(CookActivity.this, TBSActivity.class);
                intent.putExtra("url", tv.getText());
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
    }

    private void initDatas() {
        mCookDatas = getIntent().getParcelableArrayListExtra("cookDatas");
    }

}
