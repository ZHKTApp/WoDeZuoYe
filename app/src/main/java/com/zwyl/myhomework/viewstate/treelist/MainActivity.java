package com.zwyl.myhomework.viewstate.treelist;

import java.util.ArrayList;
import java.util.List;

import android.widget.ListView;


import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private List<FileBean> mDatas = new ArrayList<FileBean>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_treelist;
    }

    @Override
    protected void initView() {
        super.initView();
        mTree = (ListView) findViewById(R.id.id_tree);
        try {

            mAdapter = new SimpleTreeAdapter<FileBean>(mTree, this, mDatas, 10);
            mTree.setAdapter(mAdapter);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        mAdapter.setOnTreeNodeClickListener((node, position) -> {
            if(node.isLeaf()) {
                showToast(node.getName());
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
//        mDatas.add(new FileBean(12, 0, "文件管理系统"));
//        mDatas.add(new FileBean(2, 12, "游戏"));
//        mDatas.add(new FileBean(3, 12, "文档"));
//        mDatas.add(new FileBean(4, 1, "程序"));
//        mDatas.add(new FileBean(5, 2, "war3"));
//        mDatas.add(new FileBean(6, 2, "刀塔传奇"));
//
//        mDatas.add(new FileBean(7, 4, "面向对象"));
//        mDatas.add(new FileBean(8, 4, "非面向对象"));
//
//        mDatas.add(new FileBean(9, 7, "C++"));
//        mDatas.add(new FileBean(10, 7, "JAVA"));
//        mDatas.add(new FileBean(11, 7, "Javascript"));
//        mDatas.add(new FileBean(12, 8, "C"));
        mAdapter.refreshData(mDatas, 0);
        mAdapter.notifyDataSetChanged();
    }


}