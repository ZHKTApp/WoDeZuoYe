package com.zwyl.myhomework.main.details;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mayigeek.frame.http.state.HttpSucess;
import com.zwyl.myhomework.App;
import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;
import com.zwyl.myhomework.base.ComFlag;
import com.zwyl.myhomework.base.adapter.CommonAdapter;
import com.zwyl.myhomework.base.adapter.MultiItemTypeAdapter;
import com.zwyl.myhomework.base.adapter.ViewHolder;
import com.zwyl.myhomework.dialog.TitleDialog;
import com.zwyl.myhomework.dialog.bean.BeanAllYear;
import com.zwyl.myhomework.dialog.bean.DownloadWorkBean;
import com.zwyl.myhomework.dialog.popwindow.PopupWindowA;
import com.zwyl.myhomework.dialog.popwindow.PopupWindowB;
import com.zwyl.myhomework.dialog.popwindow.PopupWindowC;
import com.zwyl.myhomework.http.ApiUtil;
import com.zwyl.myhomework.main.BeanHomeGrid;
import com.zwyl.myhomework.main.BeanTextBookGrid;
import com.zwyl.myhomework.main.detaile.BeanCatelog;
import com.zwyl.myhomework.main.detaile.BeanDetaile;
import com.zwyl.myhomework.main.subject.SubjectActivity;
import com.zwyl.myhomework.main.subject.SubjectActivityFile;
import com.zwyl.myhomework.main.subject.SubjectActivityWrite;
import com.zwyl.myhomework.service.UserService;
import com.zwyl.myhomework.util.CleanDataUtils;
import com.zwyl.myhomework.util.DensityUtil;
//import com.zwyl.myhomework.util.DownloadManager;
import com.zwyl.myhomework.util.DownloadManager;
import com.zwyl.myhomework.util.FileUtils;
import com.zwyl.myhomework.util.MyProgress;
import com.zwyl.myhomework.util.Utils;
import com.zwyl.myhomework.viewstate.ViewControlUtil;
import com.zwyl.myhomework.viewstate.treelist.FileBean;
import com.zwyl.myhomework.viewstate.treelist.Node;
import com.zwyl.myhomework.viewstate.treelist.SimpleTreeAdapter;
import com.zwyl.myhomework.viewstate.treelist.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appsdream.nestrefresh.util.L;

/**
 *
 */
public class TextDetaileActivity extends BaseActivity {

    @BindView(R.id.tv_textclass)
    TextView tvTextclass;
    @BindView(R.id.detaile_recyclerview)
    RecyclerView detaileRecyclerview;
    List<BeanDetaile> mlistDetaile = new ArrayList<>();
    @BindView(R.id.iv_detaile_select)
    ImageView ivDetaileSelect;
    @BindView(R.id.ll_detaile_select)
    LinearLayout llDetaileSelect;
    @BindView(R.id.catalog_lisitview)
    ListView catalogLisitview;
    @BindView(R.id.tv_uncompleted)
    TextView tvUncompleted;
    @BindView(R.id.tv_completed)
    TextView tvCompleted;
    @BindView(R.id.tv_clean)
    TextView tvClean;
    private CommonAdapter cutelogAdapter;
    private CommonAdapter detaileAdapter;
    private int postionTag = -1;
    private String textBookId;
    private List<FileBean> mDatas = new ArrayList<FileBean>();
    private TreeListViewAdapter mAdapter;
    private UserService api;
    private List<BeanAllYear> years;
    private String timeasc = ComFlag.DESC;
    private int timeAscTag;//0代表降序,1代表升序
    private String clickId;//目录是否点击,有值点击.无值未点击,默认未点击(为了刷新详情时判断刷新默认说句还是选中后的数据 )
    private String schoolSubjectId, schoolTextBookName;
    private String isCompleted = ComFlag.StrFlag.UNCOMPLETED;
    private String token;
    private int GETCACHE = 9;
    private boolean isFilesNull = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_textdetaile;
    }

    @Override
    protected void initView() {
        super.initView();
        tvUncompleted.setSelected(true);
        tvCompleted.setSelected(false);//默认显示未提交数据
        setHeadView();
        ivDetaileSelect.setImageResource(R.mipmap.selecte);
        textBookId = getIntent().getStringExtra("textBookId");
        schoolSubjectId = getIntent().getStringExtra("schoolSubjectId");
        schoolTextBookName = getIntent().getStringExtra("schoolTextBookName");
        token = getIntent().getStringExtra("token");
        if (!TextUtils.isEmpty(schoolTextBookName))
            tvTextclass.setText(schoolTextBookName);
        else
            tvTextclass.setText("科目");
        //目录列表
        try {
            mAdapter = new SimpleTreeAdapter<FileBean>(catalogLisitview, this, mDatas, 10);
            catalogLisitview.setAdapter(mAdapter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //目录点击事件
        mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
            @Override
            public void onClick(Node node, int position) {
//                if (node.isLeaf()) {
                clickId = node.getClickId();
                TextDetaileActivity.this.getLeftItemclickData(clickId);
//                }
            }
        });
        //详情列表
        LinearLayoutManager linearLayoutManagerDetaile = new LinearLayoutManager(App.mContext, OrientationHelper.VERTICAL, false);
        detaileRecyclerview.setLayoutManager(linearLayoutManagerDetaile);
        detaileRecyclerview.setAdapter(detaileAdapter = new CommonAdapter<BeanDetaile>(App.mContext, R.layout.item_detaile, mlistDetaile) {
            @Override
            protected void convert(ViewHolder holder, BeanDetaile beanDetaile, int position) {
                holder.setText(R.id.tv_item_detaile_name, beanDetaile.onlineWorkName);
                holder.setText(R.id.tv_submitState, Utils.getSubjectStatus(beanDetaile.Status));
                holder.setText(R.id.tv_item_detaile_creatman, "开始时间:" + beanDetaile.startTime);
                holder.setText(R.id.tv_item_detaile_starttime, "结束时间:" + beanDetaile.endTime);
                ImageView iv_item_detaile_img = holder.getView(R.id.iv_item_detaile_img);
                MyProgress progressBar = holder.getView(R.id.progressBar);
                TextView tv_down = holder.getView(R.id.tv_down);
                //图标更换 修改
                if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_BANK) {
                    iv_item_detaile_img.setImageResource(R.mipmap.bank);
                } else if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_ADJUNCT) {
                    iv_item_detaile_img.setImageResource(R.mipmap.adjunct);
                } else if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_SIMPLE) {
                    iv_item_detaile_img.setImageResource(R.mipmap.simple);
                } else {
                    iv_item_detaile_img.setImageResource(R.mipmap.bank);
                }

                if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_ADJUNCT) {
                    //下载
                    tv_down.setOnClickListener(v -> {
                        ApiUtil.doDefaultApi(api.downLoadOnlineWork(beanDetaile.onlineWorkId, beanDetaile.onlineWorkType), new HttpSucess<DownloadWorkBean>() {
                            @Override
                            public void onSucess(DownloadWorkBean data) {
                                Log.e("http", "data : " + ComFlag.BASE_URL + data.downLoadUrl);
                                String downUrl = ComFlag.BASE_URL + data.downLoadUrl;
                                String fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1);
                                if (!TextUtils.isEmpty(data.downLoadUrl)) {
                                    if (data.downLoadUrl.startsWith("http")) {
                                        Log.e("http", "data : " + data.downLoadUrl);
                                        boolean isDownload = FileUtils.isFilesCreate(ComFlag.PACKAGE_NAME, fileName);
                                        if (!isDownload) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            startDownload(data.downLoadUrl, fileName, progressBar, tv_down);
                                        } else {
                                            tv_down.setText("已下载");
                                            tv_down.setClickable(true);
                                            tv_down.setSelected(true);
                                        }
                                    } else {
                                        boolean isDownload = FileUtils.isFilesCreate(ComFlag.PACKAGE_NAME, fileName);
                                        if (!isDownload) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            startDownload(downUrl, fileName, progressBar, tv_down);
                                        } else {
                                            tv_down.setText("已下载");
                                            tv_down.setClickable(true);
                                            tv_down.setSelected(true);
                                        }

                                    }
                                } else {
                                    showToast("文件不存在,请联系管理员");
                                }
                            }
                        });
                    });
                } else {
                    tv_down.setText("打开");
                    tv_down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = null;
                            if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_BANK) {
                                //题库
                                intent = createIntent(SubjectActivity.class);
                            } else if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_ADJUNCT) {//Todo
                                intent = createIntent(SubjectActivityFile.class);
                            } else if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_SIMPLE) {
                                intent = createIntent(SubjectActivityWrite.class);
                            }
                            intent.putExtra("onlineWorkId", beanDetaile.onlineWorkId);
                            intent.putExtra("workPushLogId", beanDetaile.workPushLogId);
                            intent.putExtra("onlineWorkType", beanDetaile.onlineWorkType + "");
                            intent.putExtra("onlineWorkName", beanDetaile.onlineWorkName);
                            intent.putExtra("Status", beanDetaile.Status);
                            intent.putExtra("token", token);
                            intent.putExtra("isCompleted", ComFlag.StrFlag.COMPLETED);
                            startActivity(intent);

                            ApiUtil.doDefaultApi(api.addlog(beanDetaile.onlineWorkId, ComFlag.NumFlag.WORKTYPE_INTERNET, "", textBookId), new HttpSucess<String>() {
                                @Override
                                public void onSucess(String data) {
                                    Log.e("http", "添加成功");
                                }
                            }, ViewControlUtil.create2Dialog(TextDetaileActivity.this));
                        }
                    });
                }
//                ApiUtil.doDefaultApi(api.downLoadOnlineWork(beanDetaile.onlineWorkId, beanDetaile.onlineWorkType), new HttpSucess<DownloadWorkBean>() {
//                    @Override
//                    public void onSucess(DownloadWorkBean data) {
//                        Log.e("http", "data : " + ComFlag.BASE_URL + data.downLoadUrl);
//                        String downUrl = ComFlag.BASE_URL + data.downLoadUrl;
//                        String fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1);
//                        if (!TextUtils.isEmpty(data.downLoadUrl)) {
//                            boolean isDownload = FileUtils.isFilesCreate(ComFlag.PACKAGE_NAME, fileName);
//                            if (isDownload) {
//                                tv_down.setText("已下载");
//                                tv_down.setClickable(true);
//                                tv_down.setSelected(true);
//                            } else
//                                tv_down.setOnClickListener(v -> {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    startDownload(downUrl, fileName, progressBar, tv_down);//TODO 换成上面的url
//                                });
//                        } else {
//                            tv_down.setText("下载");
//                            tv_down.setSelected(true);
//                            tv_down.setClickable(true);
//                        }
//                    }
//                });
            }
        });
        //条目点击
        detaileAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {

            private Intent intent;

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                BeanDetaile beanDetaile = mlistDetaile.get(position);
                if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_BANK) {
                    //题库
                    intent = createIntent(SubjectActivity.class);
                } else if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_ADJUNCT) {//Todo
                    intent = createIntent(SubjectActivityFile.class);
                } else if (beanDetaile.onlineWorkType == ComFlag.NumFlag.WORKTYPE_SIMPLE) {
                    intent = createIntent(SubjectActivityWrite.class);
                }
                intent.putExtra("onlineWorkId", beanDetaile.onlineWorkId);
                intent.putExtra("workPushLogId", beanDetaile.workPushLogId);
                intent.putExtra("onlineWorkType", beanDetaile.onlineWorkType + "");
                intent.putExtra("onlineWorkName", beanDetaile.onlineWorkName);
                intent.putExtra("Status", beanDetaile.Status);
                intent.putExtra("token", token);
                intent.putExtra("isCompleted", ComFlag.StrFlag.COMPLETED);
                startActivity(intent);

                ApiUtil.doDefaultApi(api.addlog(beanDetaile.onlineWorkId, ComFlag.NumFlag.WORKTYPE_INTERNET, "", textBookId), new HttpSucess<String>() {
                    @Override
                    public void onSucess(String data) {
                        Log.e("http", "添加成功");
                    }
                }, ViewControlUtil.create2Dialog(TextDetaileActivity.this));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }

    private void getCache() {
        //清空缓存 修改
        try {
            Log.e("http", CleanDataUtils.getTotalCacheSize(App.getContext()));
            if (CleanDataUtils.getTotalCacheSize(App.getContext()).equals("0.00K")) {
                tvClean.setSelected(false);
                tvClean.setClickable(false);
            } else {
                tvClean.setSelected(true);
                tvClean.setClickable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null)
            getcatelogList(textBookId);
        if (detaileAdapter != null)
            getDefaltDetaileData();
        getCache();
    }

    @Override
    protected void initData() {
        super.initData();
        api = ApiUtil.createDefaultApi(UserService.class, token);
        //左边目录列表
        getcatelogList(textBookId);
        getDefaltDetaileData();


        //获取所有学年列表
        ApiUtil.doDefaultApi(api.allYearsByStudentId(null), new HttpSucess<List<BeanAllYear>>() {
            @Override
            public void onSucess(List<BeanAllYear> data) {
                years = data;
            }
        }, ViewControlUtil.create2Dialog(this));

    }

    private void getDefaltDetaileData() {
        //默认请求详情列表
        ApiUtil.doDefaultApi(api.selectHomeworkByTextBookId(textBookId, timeasc, isCompleted), new HttpSucess<List<BeanDetaile>>() {
            @Override
            public void onSucess(List<BeanDetaile> data) {
                detaileAdapter.setDatas(data);
                ivDetaileSelect.setImageResource(R.mipmap.selecte);
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    private void getcatelogList(String BookId) {
        //左边目录列表
        ApiUtil.doDefaultApi(api.selectTextBookChapter(textBookId), new HttpSucess<List<ResultInfoBean>>() {
            @Override
            public void onSucess(List<ResultInfoBean> data) {
                mDatas.clear();
//                BeanCatelog beanCatelog = data.get(i);
//                FileBean fileBean = new FileBean(Integer.parseInt(beanCatelog.textBookChapterId), Integer.parseInt(beanCatelog.textBookChapterParentId), beanCatelog.textBookChapterName, beanCatelog.textBookChapterId);
//                mDatas.add(fileBean);
                traveTree(data);
                mAdapter.refreshData(mDatas, 1);
                mAdapter.notifyDataSetChanged();
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    private void traveTree(List<ResultInfoBean> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            ResultInfoBean resultInfoBean = dataList.get(i);
            FileBean fileBean = new FileBean(resultInfoBean.getTextBookChapterId(), resultInfoBean.getTextBookChapterParentId(), resultInfoBean.getTextBookChapterName(), resultInfoBean.getTextBookChapterId() + "");
            mDatas.add(fileBean);
            traveTree(dataList.get(i).getChildrenList());
        }
    }

    //设置顶部view显示及点击事件
    private void setHeadView() {
        setTitleCenter(schoolTextBookName);
        setShowLeftHead(true);//左边顶部按钮
        setShowRightHead(true);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(false);//logo筛选
        setShowRefresh(true);//刷新
        setRefreshClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//刷新点击
//                detaileAdapter.notifyDataSetChanged();
//                if (mAdapter != null)
//                    getcatelogList(textBookId);
                if (!TextUtils.isEmpty(clickId)) {
                    if (detaileAdapter != null)
                        getLeftItemclickData(clickId);
                } else {
                    getDefaltDetaileData();
                }
            }
        });
        setTimeClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//时间点击iv_item_detaile_img
                if (timeAscTag == 0) {
                    head_time.setCompoundDrawablesWithIntrinsicBounds(TextDetaileActivity.this.getResources().getDrawable(R.mipmap.asc1), null, null, null);
                    timeAscTag = 1;
                    timeasc = ComFlag.ASC;
                } else {
                    head_time.setCompoundDrawablesWithIntrinsicBounds(TextDetaileActivity.this.getResources().getDrawable(R.mipmap.desc1), null, null, null);
                    timeAscTag = 0;
                    timeasc = ComFlag.DESC;
                }
                TextDetaileActivity.this.getCommonData();
            }
        });
        setDownloadClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//全部下载
            }
        });
    }

    //获取默认或者点击后的数据
    private void getCommonData() {
        if (clickId == null) {
            getDefaltDetaileData();
        } else {
            getLeftItemclickData(clickId);
        }
    }

    private PopupWindowA mWindowA;
    private PopupWindowB mWindowB;
    private PopupWindowC mWindowC;

    //显示popwindow
    private void showPopWindow(String tag) {
        if (mWindowC != null && mWindowC.isShowing()) {
            mWindowC.dismiss();
        }
        if (mWindowB != null && mWindowB.isShowing()) {
            mWindowB.dismiss();
        }
        if (mWindowA != null && mWindowA.isShowing()) {
            mWindowA.dismiss();
            if (ComFlag.PopFlag.TITLE.equals(tag)) {
                ivDetaileSelect.setImageResource(R.mipmap.selecte);
                return;
            }
        }
        ivDetaileSelect.setImageResource(R.mipmap.selectleft);
        this.mWindowA = new PopupWindowA<BeanAllYear>(this, years, new PopupWindowA.OnClickListener() {
            @Override
            public void onClick(int position1) {
                if (mWindowB != null && mWindowB.isShowing())
                    mWindowB.dismiss();
                if (mWindowC != null && mWindowC.isShowing())
                    mWindowC.dismiss();
                TextDetaileActivity.this.getSubject(years.get(position1).academicYearId);
            }
        });
        //根据指定View定位
        PopupWindowCompat.showAsDropDown(this.mWindowA, llDetaileSelect, 0, DensityUtil.dip2px(-37), Gravity.RIGHT);
    }

    //获取所有课本目录的数据
    private void getSubject(String academicYearId) {
        ApiUtil.doDefaultApi(api.selectGradeSubejectList(academicYearId), new HttpSucess<List<BeanHomeGrid>>() {
            @Override
            public void onSucess(List<BeanHomeGrid> data) {
                if (data.size() > 0) {
                    mWindowB = new PopupWindowB<BeanHomeGrid>(TextDetaileActivity.this, data, new PopupWindowB.OnClickListener() {
                        @Override
                        public void onClick(int position2) {

                            if (mWindowC != null && mWindowC.isShowing())
                                mWindowC.dismiss();
                            if (data.size() > 0) {
                                schoolSubjectId = data.get(position2).schoolSubjectId;
                                TextDetaileActivity.this.getTextBook(academicYearId, schoolSubjectId, data.get(position2).schoolSubjectName);
                            } else {
                                TextDetaileActivity.this.showToast("没有下一级了");
                            }

                        }
                    });
                    //根据指定View定位
                    PopupWindowCompat.showAsDropDown(mWindowB, llDetaileSelect, DensityUtil.dip2px(153), DensityUtil.dip2px(-37), Gravity.RIGHT);
                } else {
                    TextDetaileActivity.this.showToast("没有下一级了");
                }
            }
        }, ViewControlUtil.create2Dialog(this));

    }

    //获取上下册数据
    private void getTextBook(String academicYearId, String schoolSubjectId, String schoolSubjectName) {
        ApiUtil.doDefaultApi(api.selectTextBook(academicYearId, schoolSubjectId), new HttpSucess<List<BeanTextBookGrid>>() {
            @Override
            public void onSucess(List<BeanTextBookGrid> data) {
                if (data.size() > 0) {
                    mWindowC = new PopupWindowC<BeanTextBookGrid>(TextDetaileActivity.this, data, new PopupWindowC.OnClickListener() {
                        @Override
                        public void onClick(int position3) {
                            TextDetaileActivity.this.getcatelogList(data.get(position3).schoolTextBookId);
                            tvTextclass.setText(data.get(position3).schoolTextBookName);
                            mWindowC.dismiss();
                            mWindowB.dismiss();
                            mWindowA.dismiss();
                            //选择上下册时也刷新详情列表
                            textBookId = data.get(position3).schoolTextBookId;
                            TextDetaileActivity.this.getDefaltDetaileData();
                        }
                    });
                    //根据指定View定位
                    PopupWindowCompat.showAsDropDown(mWindowC, llDetaileSelect, DensityUtil.dip2px(260), DensityUtil.dip2px(-37), Gravity.RIGHT);
                } else {
                    TextDetaileActivity.this.showToast("没有下一级了");
                }
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    //获取目录点击后的详情数据
    private void getLeftItemclickData(String clickId) {
        ApiUtil.doDefaultApi(api.selectHomeworkByChapterId(clickId, timeasc, isCompleted), new HttpSucess<List<BeanDetaile>>() {
            @Override
            public void onSucess(List<BeanDetaile> data) {
                detaileAdapter.setDatas(data);
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    //点击事件
    @OnClick({R.id.tv_uncompleted, R.id.tv_completed, R.id.tv_clean, R.id.ll_detaile_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_uncompleted:
                isCompleted = ComFlag.StrFlag.UNCOMPLETED;
                getDefaltDetaileData();
                tvUncompleted.setSelected(true);
                tvCompleted.setSelected(false);
                break;
            case R.id.tv_completed:
                tvCompleted.setSelected(true);
                tvUncompleted.setSelected(false);
                isCompleted = ComFlag.StrFlag.COMPLETED;
                getDefaltDetaileData();
                break;
            case R.id.tv_clean:
                new TitleDialog(this, "是否清空缓存", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        CleanDataUtils.clear(App.getContext());
                        Toast.makeText(App.getContext(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                        Message message = Message.obtain();
                        message.what = GETCACHE;
                        mhandler.sendEmptyMessage(message.what);
                    }

                    @Override
                    public void OnCancle() {
                    }
                }).show();
                break;
            case R.id.ll_detaile_select:
                showPopWindow(ComFlag.PopFlag.TITLE);
                break;
        }
    }

    Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == GETCACHE)
                getCache();

            return false;
        }
    });

    private void startDownload(String url, String name, ProgressBar progressBar, TextView tv_down) {
        DownloadManager downloadManager = DownloadManager.getInstance();
        String localUrl = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, name).getAbsolutePath();
        downloadManager.download(localUrl, url, new DownloadManager.DownloadListener() {
            @Override
            public void preDownload() {

            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_down.setSelected(true);
                        tv_down.setText("已下载");
                        progressBar.setVisibility(View.GONE);
                        detaileAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFail(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("下载失败");
                    }
                });

            }

            @Override
            public void onUpdate(int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }

            @Override
            public void onCacheUpdate() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

}
