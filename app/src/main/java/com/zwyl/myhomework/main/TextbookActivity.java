package com.zwyl.myhomework.main;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zwyl.myhomework.App;
import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;
import com.zwyl.myhomework.base.adapter.CommonAdapter;
import com.zwyl.myhomework.base.adapter.MultiItemTypeAdapter;
import com.zwyl.myhomework.base.adapter.ViewHolder;
import com.zwyl.myhomework.http.ApiUtil;
import com.zwyl.myhomework.main.details.TextDetaileActivity;
import com.zwyl.myhomework.service.UserService;
import com.zwyl.myhomework.viewstate.SpaceItemDecoration;
import com.zwyl.myhomework.viewstate.ViewControlUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TextbookActivity extends BaseActivity {

    @BindView(R.id.textbook_recyclerview)
    RecyclerView textbookRecyclerview;
    private List mlist = new ArrayList<BeanTextBookGrid>();
    private CommonAdapter mAdapter;
    private String academicYearId;
    private String schoolSubjectId;
    private String subjectName;
    private String token;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_textbook;
    }

    @Override
    protected void initView() {
        super.initView();
        //设置顶部按钮事件,日历筛选
        setHeadView();
        //获取主界面传过来的数据
        Intent intent = getIntent();
        academicYearId = intent.getStringExtra("academicYearId");
        schoolSubjectId = intent.getStringExtra("schoolSubjectId");
        subjectName = intent.getStringExtra("subjectName");
        token = intent.getStringExtra("token");
        //Gridview内容
        GridLayoutManager layoutManager = new GridLayoutManager(App.mContext, 4, OrientationHelper.VERTICAL, false);
        textbookRecyclerview.setLayoutManager(layoutManager);
        int spacingInPixels = 34;
        textbookRecyclerview.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        textbookRecyclerview.setAdapter(mAdapter = new CommonAdapter<BeanTextBookGrid>(App.mContext, R.layout.item_textbook_grid, mlist) {
            @Override
            protected void convert(ViewHolder holder, BeanTextBookGrid bean, int position) {
                holder.setText(R.id.tv_item_textbook_name, subjectName);//科目
                holder.setText(R.id.tv_item_textbook_class, bean.schoolEducationGradeName + (bean.updownType ? "上册" : "下册"));//年级
                holder.setText(R.id.tv_item_textbook_creat, bean.schoolPublisherName);//出版社
                // holder.setImageDrawable(R.id.iv_item_textbook, bean.image);
            }
        });
    }

    @Override
    protected void initControl() {
        super.initControl();
        //条目点击
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = createIntent(TextDetaileActivity.class);
                intent.putExtra("textBookId", ((BeanTextBookGrid) mlist.get(position)).schoolTextBookId);
                intent.putExtra("schoolSubjectId", schoolSubjectId);
                intent.putExtra("token", token);
                intent.putExtra("schoolTextBookName", ((BeanTextBookGrid) mlist.get(position)).schoolTextBookName);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        UserService api = ApiUtil.createDefaultApi(UserService.class, token);
        ApiUtil.doDefaultApi(api.selectTextBook(academicYearId, schoolSubjectId), data -> {
            if (data.size() > 0) {
                mAdapter.setDatas(data);
            }
        }, ViewControlUtil.create2Dialog(this));

    }

    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter(subjectName);
        setShowLeftHead(true);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(false);//logo筛选
        setShowRefresh(true);//刷新
        setRefreshClick(v -> {
            showToast("刷新");
        });
    }
}

