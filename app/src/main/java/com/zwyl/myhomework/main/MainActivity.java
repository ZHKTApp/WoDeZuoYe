package com.zwyl.myhomework.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mayigeek.frame.http.state.HttpSucess;
import com.zwyl.myhomework.App;
import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;
import com.zwyl.myhomework.base.ComFlag;
import com.zwyl.myhomework.base.adapter.CommonAdapter;
import com.zwyl.myhomework.base.adapter.MultiItemTypeAdapter;
import com.zwyl.myhomework.base.adapter.ViewHolder;
import com.zwyl.myhomework.dialog.RightDrawerDialog;
import com.zwyl.myhomework.dialog.bean.BeanAllYear;
import com.zwyl.myhomework.http.ApiUtil;
import com.zwyl.myhomework.service.UserService;
import com.zwyl.myhomework.util.UpdataManager;
import com.zwyl.myhomework.util.UtilPermission;
import com.zwyl.myhomework.viewstate.SpaceItemDecoration;
import com.zwyl.myhomework.viewstate.ViewControlUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.home_recyclerview)
    RecyclerView homeRecyclerview;
    private List mlist = new ArrayList<BeanHomeGrid>();
    private CommonAdapter mAdapter;
    private List<BeanAllYear> years = new ArrayList<>();
    private UserService api;
    private String academicYearId;
    private String token;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        //设置顶部按钮事件,日历筛选
        setHeadView();
        //Gridview内容
        token = getIntent().getStringExtra("token");
        Log.e("token", "token : " + token);
        if (!TextUtils.isEmpty(token)) {
            UpdataManager updataManager = new UpdataManager(this, token, ComFlag.APPID);
            updataManager.getVersion();
        }
        GridLayoutManager layoutManager = new GridLayoutManager(App.mContext, 5, OrientationHelper.VERTICAL, false);
        homeRecyclerview.setLayoutManager(layoutManager);
        int spacingInPixels = 60;
        homeRecyclerview.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        homeRecyclerview.setAdapter(mAdapter = new CommonAdapter<BeanHomeGrid>(App.mContext, R.layout.item_home_grid, mlist) {
            @Override
            protected void convert(ViewHolder holder, BeanHomeGrid beanHomeGrid, int position) {
                holder.setText(R.id.tv_item_grid_name, beanHomeGrid.schoolSubjectName);
                ImageView iv_item_grid_subjects = holder.getView(R.id.iv_item_grid_subjects);
                String schoolSubjectName = beanHomeGrid.schoolSubjectName;
                switch (schoolSubjectName) {
                    case "语文":
                        iv_item_grid_subjects.setImageResource(R.mipmap.yuwen);
                        break;
                    case "数学":
                        iv_item_grid_subjects.setImageResource(R.mipmap.shuxue);
                        break;
                    case "英语":
                        iv_item_grid_subjects.setImageResource(R.mipmap.english);
                        break;
                    case "物理":
                        iv_item_grid_subjects.setImageResource(R.mipmap.wuli);
                        break;
                    case "化学":
                        iv_item_grid_subjects.setImageResource(R.mipmap.huaxue);
                        break;
                    case "生物":
                        iv_item_grid_subjects.setImageResource(R.mipmap.shengwu);
                        break;
                    case "政治":
                        iv_item_grid_subjects.setImageResource(R.mipmap.zhengzhi);
                        break;
                    case "历史":
                        iv_item_grid_subjects.setImageResource(R.mipmap.lishi);
                        break;
                    case "地理":
                        iv_item_grid_subjects.setImageResource(R.mipmap.dili);
                        break;
                }
            }
        });
    }

    @Override
    protected void initControl() {
        super.initControl();
        //课本点击事件
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = createIntent(TextbookActivity.class);
                BeanHomeGrid item = (BeanHomeGrid) mlist.get(position);
                intent.putExtra("academicYearId", academicYearId);
                intent.putExtra("schoolSubjectId", item.schoolSubjectId);
                intent.putExtra("subjectName", item.schoolSubjectName);
                intent.putExtra("token", token);
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
        api = ApiUtil.createDefaultApi(UserService.class, token);
        //获取当前学年
        ApiUtil.doDefaultApi(api.currentYearByStudentId(null), data -> {
            head_filter.setText(data.academicYear);
            academicYearId = data.academicYearId;
            getSubject(academicYearId);
        }, ViewControlUtil.create2Dialog(this));

        //获取所有学年列表
        ApiUtil.doDefaultApi(api.allYearsByStudentId(null), data -> {
            years = data;
        }, ViewControlUtil.create2Dialog(this));
    }

    //获取课本列表数据
    private void getSubject(String academicYearId) {
        ApiUtil.doDefaultApi(api.selectGradeSubejectList(academicYearId), new HttpSucess<List<BeanHomeGrid>>() {
            @Override
            public void onSucess(List<BeanHomeGrid> data) {
                mAdapter.setDatas(data);
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter(R.string.app_name);
        setShowLeftHead(false);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(true);//日历筛选
        setShowLogo(true);//logo筛选
        setShowRefresh(false);//刷新
        setFilterClick(v -> {
            RightDrawerDialog dialog = new RightDrawerDialog(MainActivity.this, years, position -> {
                head_filter.setText(years.get(position).academicYear);
                academicYearId = years.get(position).academicYearId;
                getSubject(academicYearId);
            });
            if (dialog.isShowing()) {
                dialog.dismiss();
            } else {
                dialog.show();
            }


        });
        setLogoClick(v -> {
            finish();
        });
    }


    //权限
    private boolean needCheck = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (needCheck) {
            UtilPermission.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                showDalog();
                needCheck = false;
            }
        }
    }

    private void showDalog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle).setMessage(R.string.notifyMsg)
                .setCancelable(false).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
                finish();
            }
        }).show();
    }

    private void startAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}

