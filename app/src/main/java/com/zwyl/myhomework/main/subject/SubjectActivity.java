package com.zwyl.myhomework.main.subject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.RequestOptions;
import com.mayigeek.frame.http.state.HttpSucess;
import com.zwyl.myhomework.App;
import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;
import com.zwyl.myhomework.base.ComFlag;
import com.zwyl.myhomework.base.adapter.CommonAdapter;

import com.zwyl.myhomework.base.adapter.ViewHolder;
import com.zwyl.myhomework.customveiw.CustomLinearLayoutManager;
import com.zwyl.myhomework.dialog.AddNoteDialog;
import com.zwyl.myhomework.dialog.AnalysisDialog;
import com.zwyl.myhomework.dialog.TitleDialog;
import com.zwyl.myhomework.http.ApiUtil;
import com.zwyl.myhomework.main.PlayActivity;
import com.zwyl.myhomework.service.UserService;
import com.zwyl.myhomework.util.UIUtils;
import com.zwyl.myhomework.util.Utils;
import com.zwyl.myhomework.util.ViewUtil;
import com.zwyl.myhomework.views.GlideSimpleLoader;
import com.zwyl.myhomework.views.ImageWatcherHelper;
import com.zwyl.myhomework.viewstate.ViewControlUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubjectActivity extends BaseActivity {
    @BindView(R.id.rl_subject)
    RecyclerView rlSubject;
    List mlist = new ArrayList<BeanSubject.ExercisesItemListBean>();
    List mRadio = new ArrayList<BeanAbc>();
    int[] mJudge = {R.mipmap.right, R.mipmap.close};//判断
    @BindView(R.id.tv_sunbject_save)
    TextView tvSunbjectSave;
    @BindView(R.id.ll_subject_title)
    LinearLayout ll_subject_title;
    @BindView(R.id.tv_subject_num)
    TextView tv_subject_num;
    @BindView(R.id.tv_total_num)
    TextView tv_total_num;
    private CustomLinearLayoutManager layoutManager;
    private String onlineWorkId;
    private String onlineWorkType;
    private String workPushLogId;
    private String isCompleted;
    private CommonAdapter mAdapter;
    private UserService api;
    List<BeanInfo> infoList = new ArrayList<BeanInfo>();
    List<String> picturePathList = new ArrayList<String>();
    private int subjectSize;
    protected static final int REQUEST_CODE = 1, REFRESH_MESSAGE = 8;
    private Activity context;
    private boolean isSeeAnswer = false;
    private String token;
    private String onlineWorkName;
    private int Status;
    private String workCommand;
    private boolean isSubmit;
    private ImageWatcherHelper iwHelper;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_subject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isTranslucentStatus = false;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//            isTranslucentStatus = true;
//        }
        super.onCreate(savedInstanceState);
        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(SubjectActivity.this) : 0); // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确

    }

    @Override
    protected void initView() {
        context = SubjectActivity.this;
        super.initView();
        setShowFilter(false);
        setShowRefresh(false);
        setShowRightHeadTwo(true, false);
        setTwoClick(v -> {
            new AnalysisDialog(SubjectActivity.this, "作业要求", workCommand).show();
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == REFRESH_MESSAGE) {
                    picturePathList = (List<String>) msg.obj;
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        //  webViewParticipationDetail.loadDataWithBaseURL(null, "<p>asdfasdfas<img src=\"http://qkc-oss.oss-cn-beijing.aliyuncs.com/1547541255125.jpg\" title=\"Tulips.jpg\" alt=\"Tulips.jpg\"></p>", "text/html", "UTF-8", null);
        onlineWorkId = getIntent().getStringExtra("onlineWorkId");
        workPushLogId = getIntent().getStringExtra("workPushLogId");
        onlineWorkType = getIntent().getStringExtra("onlineWorkType");
        onlineWorkName = getIntent().getStringExtra("onlineWorkName");
        isCompleted = getIntent().getStringExtra("isCompleted");
        Status = getIntent().getIntExtra("Status", -1);
        if (!TextUtils.isEmpty(onlineWorkName))
            setTitleCenter(onlineWorkName);
        else
            setTitleCenter("作业");
        if (Status != ComFlag.NumFlag.WORKSTATUS_SUBMIT) {
            tvSunbjectSave.setVisibility(View.VISIBLE);
            tvSunbjectSave.setClickable(true);
            tvSunbjectSave.setSelected(true);
        } else {
            tvSunbjectSave.setVisibility(View.GONE);
            tvSunbjectSave.setClickable(false);
            tvSunbjectSave.setSelected(false);
        }
        token = getIntent().getStringExtra("token");
        layoutManager = new CustomLinearLayoutManager(App.mContext);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        rlSubject.setLayoutManager(layoutManager);
        rlSubject.setHasFixedSize(true);
        rlSubject.setNestedScrollingEnabled(false);
        mAdapter = new CommonAdapter<BeanSubject.ExercisesItemListBean>(App.mContext, R.layout.item_subject, mlist) {
            @Override
            protected void convert(ViewHolder holder, BeanSubject.ExercisesItemListBean beanSubject, int position) {
                holder.setIsRecyclable(false);
                LinearLayout ll_bottom = holder.getView(R.id.ll_bottom);
                LinearLayout ll_item_subject_listen = holder.getView(R.id.ll_item_subject_listen);
                LinearLayout ll_item_subject_look = holder.getView(R.id.ll_item_subject_look);
                LinearLayout ll_item_subject_addFalse = holder.getView(R.id.ll_item_subject_addFalse);
                LinearLayout ll_item_subject_sure = holder.getView(R.id.ll_item_subject_sure);
                LinearLayout ll_item_subject_explain = holder.getView(R.id.ll_item_subject_explain);
                LinearLayout ll_analysis = holder.getView(R.id.ll_analysis);
                LinearLayout ll_item_subject_remark = holder.getView(R.id.ll_item_subject_remark);
                LinearLayout ll_item_subject_down = holder.getView(R.id.ll_item_subject_down);
                TextView tv_item_subject_name = holder.getView(R.id.tv_item_subject_type);
                RelativeLayout rl_bottom_subject = holder.getView(R.id.rl_bottom_subject);
                WebView webView_participation_detail = holder.getView(R.id.webView_participation_detail);
//                WebView webView_participation_detail_item = holder.getView(R.id.webView_participation_detail_item);
                WebView webView_participation_detail_answer = holder.getView(R.id.webView_participation_detail_answer);
                ImageView iv_participation_detail_item = holder.getView(R.id.iv_participation_detail_item);
                ImageView iv_item_subject_false =holder.getView(R.id.iv_item_subject_false);
                TextView tv_myanswer = holder.getView(R.id.tv_myanswer);
                TextView tv_sureanswer = holder.getView(R.id.tv_sureanswer);
                TextView tv_analysis = holder.getView(R.id.tv_analysis);
                TextView tv_myanswer_str = holder.getView(R.id.tv_myanswer_str);
                TextView tv_sureanswer_str = holder.getView(R.id.tv_sureanswer_str);
                TextView tv_answer_click = holder.getView(R.id.tv_answer_click);
                NestedScrollView myscrollview = holder.getView(R.id.myscrollview);
                webView_participation_detail.loadDataWithBaseURL(null, beanSubject.exercisesTitle, "text/html", "UTF-8", null);
                int exerciseTypeCode = beanSubject.exerciseTypeCode;
                String exercisesId = beanSubject.exercisesId;
                tv_item_subject_name.setText((position + 1) + ". " + Utils.getsubjectType(exerciseTypeCode));
                View ic_answer_subjectivity = holder.getView(R.id.ic_answer_subjectivity);//主观题
                View ic_answer_objective = holder.getView(R.id.ic_answer_objective);//客观题判断是否显示答案//Todo
                View ic_exercise_answer = holder.getView(R.id.ic_exercise_answer);//主观题答案
                ll_bottom.removeAllViews();
                ll_item_subject_explain.setVisibility(View.GONE);
                //修改
//                WebSettings webSettings = webView_participation_detail_item.getSettings();
//                webSettings.setSupportZoom(false);
//                webSettings.setUseWideViewPort(true);
//                webSettings.setLoadWithOverviewMode(true);
//                webSettings.setAllowUniversalAccessFromFileURLs(true);
//                webSettings.setAllowFileAccess(true);
//                webSettings.setAllowFileAccessFromFileURLs(true);
//                webSettings.setJavaScriptEnabled(true);
//                webView_participation_detail_item.setWebViewClient(new MyWebViewClient(webView_participation_detail_item));
                if (isSeeAnswer) {
                    //显示答案
                    tvSunbjectSave.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(beanSubject.studentAnswerOptionId)) {
                        //提交按钮置灰
//                        tvSunbjectSave.setSelected(false);
//                        tvSunbjectSave.setClickable(false);
                        tvSunbjectSave.setVisibility(View.GONE);
                    }
                    if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                        ic_answer_subjectivity.setVisibility(View.GONE);
                        ic_exercise_answer.setVisibility(View.GONE);
                        ll_item_subject_sure.setVisibility(View.GONE);
                        rl_bottom_subject.setVisibility(View.VISIBLE);
                        ll_bottom.setVisibility(View.GONE);
                        if (!beanSubject.studentAnswerOptionId.equals(beanSubject.exerciseAnswer)) {
                            if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
                                tv_myanswer.setTextColor(Color.RED);
                                tv_myanswer.setText(Utils.getJudge(beanSubject.studentAnswerOptionId));
                                tv_sureanswer.setText(Utils.getJudge(beanSubject.exerciseAnswer));
                            } else {
                                tv_myanswer.setTextColor(Color.RED);
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                                tv_sureanswer.setText(beanSubject.exerciseAnswer);
                            }
                        } else {
                            if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
                                tv_myanswer.setText(Utils.getJudge(beanSubject.studentAnswerOptionId));
                                tv_sureanswer.setText(Utils.getJudge(beanSubject.exerciseAnswer));
                            } else {
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                                tv_sureanswer.setText(beanSubject.exerciseAnswer);
                            }
                        }
                    } else {
                        ic_exercise_answer.setVisibility(View.VISIBLE);
                        ic_answer_subjectivity.setVisibility(View.VISIBLE);
                        ic_answer_objective.setVisibility(View.VISIBLE);
                        rl_bottom_subject.setVisibility(View.VISIBLE);
                        ll_item_subject_sure.setVisibility(View.GONE);
                        ll_bottom.setVisibility(View.GONE);
                    }
                } else {
                    if (isSubmit) {
                        if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                            ic_answer_subjectivity.setVisibility(View.GONE);
                            tv_myanswer_str.setText("我的答案:");
                            if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
                                tv_myanswer.setText(Utils.getJudge(beanSubject.studentAnswerOptionId));
                            } else {
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                            }
                        } else {
                            ic_answer_subjectivity.setVisibility(View.VISIBLE);
                            ic_answer_objective.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(beanSubject.workContentFileUri)) {
                                iv_participation_detail_item.setVisibility(View.VISIBLE);
                                UIUtils.updateOptions(new RequestOptions(), beanSubject.workContentFileUri, System.currentTimeMillis(), iv_participation_detail_item);
                            } else {
                                if (!TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                                    if (Utils.isPic(beanSubject.studentAnswerFileUri)) {
                                        iv_participation_detail_item.setVisibility(View.VISIBLE);
                                        UIUtils.updateOptions(new RequestOptions(), beanSubject.studentAnswerFileUri, System.currentTimeMillis(), iv_participation_detail_item);
//                                webView_participation_detail_item.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.studentAnswerFileUri), "text/html", "UTF-8", null);
                                    }
                                }
                            }
                            iv_participation_detail_item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final List<String> longPictureList = new ArrayList<>();
                                    if (!TextUtils.isEmpty(beanSubject.workContentFileUri)) {
                                        longPictureList.add(beanSubject.workContentFileUri);
                                    } else if (!TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                                        longPictureList.add(beanSubject.studentAnswerFileUri);
                                    }
                                    final SparseArray<ImageView> mappingViews = new SparseArray<>();
                                    mappingViews.put(0, (ImageView) v);
                                    iwHelper.show((ImageView) v, mappingViews, convertList(longPictureList));
                                }
                            });
                        }
                        ll_bottom.setVisibility(View.GONE);
                        tv_sureanswer.setVisibility(View.GONE);
                        tv_sureanswer_str.setVisibility(View.GONE);
                        ll_item_subject_listen.setVisibility(View.GONE);

                    } else {
                        ll_bottom.setVisibility(View.VISIBLE);
                        ic_answer_subjectivity.setVisibility(View.GONE);
                        rl_bottom_subject.setVisibility(View.GONE);
                        ic_answer_objective.setVisibility(View.GONE);
                        ll_item_subject_explain.setVisibility(View.VISIBLE);
                        ll_analysis.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = createIntent(SubjectActivityWrite.class);
                                intent.putExtra("exercisesId", beanSubject.exercisesId);
                                intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
                                intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        });
                    }
                    //显示题目
//                    ll_bottom.setVisibility(View.VISIBLE);
//                    rl_bottom_subject.setVisibility(View.GONE);
//                    ic_answer_subjectivity.setVisibility(View.GONE);
//                    ic_answer_objective.setVisibility(View.GONE);
//                    ll_item_subject_explain.setVisibility(View.VISIBLE);
                }
                if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                    //单选
                    if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO) {
//                    ll_bottom.addView(ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
//                        @Override
//                        public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                            deWeightRadio(exercisesId, optionId);
//                        }
//                    }));
                        LinearLayout view = ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
                            @Override
                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                                deWeightRadio(exercisesId, optionId);
                            }
                        });
                        ll_bottom.addView(view);
                        if (infoList != null && infoList.size() != 0)
                            for (int i = 0; i < infoList.size(); i++) {
                                for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                    if (infoList.get(i).studentAnswer.equals(beanSubject.exerciseOptionList.get(j).optionId) && infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
                                        CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                        checkBox.setChecked(true);
                                    }
                                }
                            }
                    } else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
                        LinearLayout view = ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
                            @Override
                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                                deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");
                            }
                        });
                        ll_bottom.addView(view);
                        if (infoList != null && infoList.size() != 0)
                            for (int i = 0; i < infoList.size(); i++) {
                                for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                    if (Utils.getJudge(infoList.get(i).studentAnswer).equals(Utils.getJudge(beanSubject.exerciseOptionList.get(j).optionNo)) && infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
                                        CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                        checkBox.setChecked(true);
                                    }
                                }
                            }
                    }
//                    ll_bottom.addView(ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
//                        @Override
//                        public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                            deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");//判断题传1:对, 0:错
//                        }
//                    }));
//                }
//                else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
//                    ll_bottom.addView(ViewUtil.getRadioGroup(null, mJudge, subjectNum, new ViewUtil.OncheckedClick() {
//                        @Override
//                        public void OnChecked(int indexNum, String optionId) {
//                            deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");//判断题传1:对, 0:错;
//
//                        }
//                    }));
//                }
                    else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                        List<BeanSubject.ExercisesItemListBean.ExerciseOptionListBean> exerciseList = beanSubject.exerciseOptionList;
                        List<String> optionList = new ArrayList<>();
                        LinearLayout view = ViewUtil.getRadioGroupMultiple(exerciseList, new ViewUtil.OnMultipleClick() {
                            @Override
                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                                if (isChecked) {
                                    exerciseList.get(indexNum).ischeck = true;
                                    if (!optionList.contains(exerciseList.get(indexNum).optionId)) {
                                        optionList.add(exerciseList.get(indexNum).optionId);
                                    }
                                } else {
                                    exerciseList.get(indexNum).ischeck = false;
                                    if (optionList != null && optionList.size() != 0) {
                                        optionList.remove(exerciseList.get(indexNum).optionId);
                                    }
                                }
                                //修改 选项保存
                                StringBuilder sbuid = new StringBuilder();
                                for (int i = 0; i < optionList.size(); i++) {
                                    if (sbuid.length() == 0) {
                                        sbuid.append(optionList.get(i));
                                    } else {
                                        sbuid.append("," + optionList.get(i));
                                    }
                                }
//                    for (int j = 0; j < exerciseList.size(); j++) {
//                        if (exerciseList.get(j).ischeck) {
//                            String optionIdcheck = exerciseList.get(j).optionId;
//                            if (sbuid.length() == 0) {
//                                sbuid.append(optionIdcheck);
//                            } else {
//                                sbuid.append("," + optionIdcheck);
//                            }
//                        }
//                    }
                                deWeightRadio(exercisesId, sbuid.toString());
                            }
                        });

                        ll_bottom.addView(view);
                        if (infoList != null && infoList.size() != 0) {
                            for (int i = 0; i < infoList.size(); i++) {
                                if (infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
                                    String[] infoArr = infoList.get(i).studentAnswer.split(",");
                                    for (int k = 0; k < infoArr.length; k++) {
                                        for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                            Log.e("http", "infoArr : " + infoArr[k] + " exerciseOptionList : " + beanSubject.exerciseOptionList.get(j).optionId);
                                            if (infoArr[k].equals(beanSubject.exerciseOptionList.get(j).optionId)) {
                                                CheckBox checkBox = (CheckBox) view.getChildAt(j);
                                                checkBox.setChecked(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                    ll_bottom.addView(ViewUtil.getRadioGroupMultiple(exerciseList, new ViewUtil.OnMultipleClick() {
//                        @Override
//                        public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                            if (isChecked) {
//                                exerciseList.get(indexNum).ischeck = true;
//                            } else {
//                                exerciseList.get(indexNum).ischeck = false;
//                            }
//                        }
//                    }));
                    }
//                else {
//                    //主观题
//                    View textViewTwo = ViewUtil.getTextViewTwo("作答: ", "点击此处开始作答");
//                    ll_bottom.addView(textViewTwo);
//                    textViewTwo.setOnClickListener(v -> {
//                        Intent intent = createIntent(SubjectActivityWrite.class);
//                        intent.putExtra("exercisesId", beanSubject.exercisesId);
//                        intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
//                        intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
//                        intent.putExtra("onlineWorkName", onlineWorkName);
//                        startActivityForResult(intent, REQUEST_CODE);
//                    });
//                }
                } else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_GAP || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_COUNT || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_SHORT) {
                    if (isSeeAnswer) {
                        ic_answer_objective.setVisibility(View.GONE);
                        tv_analysis.setText("作答: ");
                        if (!TextUtils.isEmpty(beanSubject.workContentFileUri)) {
                            iv_participation_detail_item.setVisibility(View.VISIBLE);
                            UIUtils.updateOptions(new RequestOptions(), beanSubject.workContentFileUri, System.currentTimeMillis(), iv_participation_detail_item);
                        } else {
                            if (!TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                                if (Utils.isPic(beanSubject.studentAnswerFileUri)) {
                                    iv_participation_detail_item.setVisibility(View.VISIBLE);
                                    UIUtils.updateOptions(new RequestOptions(), beanSubject.studentAnswerFileUri, System.currentTimeMillis(), iv_participation_detail_item);
//                                webView_participation_detail_item.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.studentAnswerFileUri), "text/html", "UTF-8", null);
                                }
                            }
                        }
                        iv_participation_detail_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final List<String> longPictureList = new ArrayList<>();
                                if (!TextUtils.isEmpty(beanSubject.workContentFileUri)) {
                                    longPictureList.add(beanSubject.workContentFileUri);
                                } else if (!TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                                    longPictureList.add(beanSubject.studentAnswerFileUri);
                                }
                                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                                mappingViews.put(0, (ImageView) v);
                                iwHelper.show((ImageView) v, mappingViews, convertList(longPictureList));
                            }
                        });
                        if (!TextUtils.isEmpty(beanSubject.workResultComment)) {
                            ll_item_subject_remark.setSelected(true);
                            ll_item_subject_remark.setClickable(true);
                            ll_item_subject_remark.setOnClickListener(v -> {
                                new AnalysisDialog(SubjectActivity.this, "教师评语", beanSubject.workResultComment).show();
                            });
                        } else {
                            ll_item_subject_remark.setSelected(false);
                            ll_item_subject_remark.setClickable(false);
                        }
                        if (!TextUtils.isEmpty(beanSubject.exerciseAnswer)) {
                            if (Utils.isPic(beanSubject.exerciseAnswer)) {
                                webView_participation_detail_answer.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.exerciseAnswer), "text/html", "UTF-8", null);
                            } else {
                                webView_participation_detail_answer.loadDataWithBaseURL(null, beanSubject.exerciseAnswer, "text/html", "UTF-8", null);
                            }
                        }
                    } else {
                        if (TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                            if (!isSeeAnswer) {
                                if (Status != ComFlag.NumFlag.WORKSTATUS_SUBMIT) {
//                                    View textViewTwo = ViewUtil.getTextViewTwo("作答: ", "点击此处开始作答");
//                                    ll_bottom.addView(textViewTwo);
//                                    textViewTwo.setOnClickListener(v -> {
//                                        Intent intent = createIntent(SubjectActivityWrite.class);
//                                        intent.putExtra("exercisesId", beanSubject.exercisesId);
//                                        intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
//                                        intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
//                                        startActivityForResult(intent, REQUEST_CODE);
//                                    });
                                    tv_answer_click.setVisibility(View.VISIBLE);
                                    tv_answer_click.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = createIntent(SubjectActivityWrite.class);
                                            intent.putExtra("exercisesId", beanSubject.exercisesId);
                                            intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
                                            intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
                                            startActivityForResult(intent, REQUEST_CODE);
                                        }
                                    });
                                }
                            }
                        } else {
                            tv_analysis.setText("作答: ");
                            if (Utils.isPic(beanSubject.studentAnswerFileUri)) {
                                iv_participation_detail_item.setVisibility(View.VISIBLE);
                                UIUtils.updateOptions(new RequestOptions(), beanSubject.studentAnswerFileUri, System.currentTimeMillis(), iv_participation_detail_item);
//                                webView_participation_detail_item.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.studentAnswerFileUri), "text/html", "UTF-8", null);
                            }
                        }
                    }
                }
                if (picturePathList.size() != 0) {
                    for (int i = 0; i < picturePathList.size(); i++) {
                        if (beanSubject.exercisesId.equals(picturePathList.get(i).substring(picturePathList.get(i).lastIndexOf("/") + 1, picturePathList.get(i).length() - 4))) {
                            ic_answer_subjectivity.setVisibility(View.VISIBLE);
//                            webView_participation_detail_item.setVisibility(View.GONE);
                            iv_participation_detail_item.setVisibility(View.VISIBLE);
//                            tv_answer_click.setVisibility(View.GONE);
                            tv_answer_click.setText("作答 : ");
                            tv_analysis.setText("     ");
//                            String path = "file://" + picturePathList.get(i);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                            }
//                            webView_participation_detail_item.loadUrl(path);
                            UIUtils.updateOptions(new RequestOptions(), picturePathList.get(i), System.currentTimeMillis(), iv_participation_detail_item);
                        }
                    }
                }


                if (!TextUtils.isEmpty(beanSubject.exerciseAnalysis)) {
                    ll_item_subject_look.setSelected(true);
                    ll_item_subject_look.setClickable(true);
                    //看解析
                    ll_item_subject_look.setOnClickListener(v -> {
                        new AnalysisDialog(SubjectActivity.this, "看解析", beanSubject.exerciseAnalysis).show();
                    });
                } else {
                    ll_item_subject_look.setClickable(false);
                    ll_item_subject_look.setSelected(false);
                }

                if (!TextUtils.isEmpty(beanSubject.exerciseAnswer)) {
                    ll_item_subject_sure.setSelected(true);
                    ll_item_subject_sure.setClickable(true);
                } else {
                    ll_item_subject_sure.setClickable(false);
                    ll_item_subject_sure.setSelected(false);
                }
                /**修改
                 * 缺少错题集判断条件
                 */
                //是否有听讲解内容
                if (TextUtils.isEmpty(beanSubject.exerciseExplainFileUri)) {//TODO
                    ll_item_subject_listen.setSelected(false);
                    ll_item_subject_listen.setClickable(false);
                } else {
                    ll_item_subject_listen.setClickable(true);
                    ll_item_subject_listen.setSelected(true);
                    //听讲解
                    ll_item_subject_listen.setOnClickListener(v -> {
                        Intent intent = new Intent(SubjectActivity.this, PlayActivity.class);
                        intent.putExtra("resourceUri", beanSubject.exerciseExplainFileUri);
                        startActivity(intent);
                    });
                }
                //是否已经求讲解
                if (beanSubject.isConfirnExpianation) {
                    ll_item_subject_explain.setSelected(true);
                    ll_item_subject_explain.setClickable(false);
                    ll_item_subject_down.setSelected(true);
                    ll_item_subject_down.setClickable(false);
                } else {
                    ll_item_subject_down.setSelected(false);
                    ll_item_subject_down.setClickable(true);
                    ll_item_subject_explain.setSelected(false);
                    ll_item_subject_explain.setClickable(true);
                    //求讲解
                    ll_item_subject_explain.setOnClickListener(v -> {
                        ll_item_subject_explain.setSelected(true);
                        ll_item_subject_explain.setClickable(false);
                        getExplain(beanSubject.exercisesId);
                    });
                    ll_item_subject_down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ll_item_subject_down.setSelected(true);
                            ll_item_subject_down.setClickable(false);
                            getExplain(beanSubject.exercisesId);
                        }
                    });
                }


                ll_item_subject_sure.setOnClickListener(v -> {
                    new AnalysisDialog(SubjectActivity.this, "正确答案", beanSubject.exerciseAnswer).show();
                });
                //是否加入错题集
                if (beanSubject.isMistakesCollection) {
                    ll_item_subject_addFalse.setSelected(true);
                    iv_item_subject_false.setBackgroundResource(R.mipmap.deletefalse);
                } else {
                    ll_item_subject_addFalse.setSelected(true);
                    iv_item_subject_false.setBackgroundResource(R.mipmap.addfalse);
                }
                //错题集
                ll_item_subject_addFalse.setOnClickListener(v -> {
                    //是否加入过错题集
                    if (beanSubject.isMistakesCollection) {
                        new TitleDialog(context, "是否将此题移出错题集", new TitleDialog.OnclickListener() {
                            @Override
                            public void OnSure() {
                                ApiUtil.doDefaultApi(api.deleteMistakesCollection(onlineWorkId, beanSubject.exercisesId), data -> {//workSupportId//Todo
                                    showToast("删除成功!");
                                    getdata();
                                });
                            }

                            @Override
                            public void OnCancle() {
                            }
                        }).show();
                    } else {
                        //加入错题集
                        new AddNoteDialog(context, new AddNoteDialog.OnClickListener() {
                            @Override
                            public void onClick(String etString) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("workId", onlineWorkId);//Todo
                                map.put("exerciseId", beanSubject.exercisesId);
                                map.put("reason", etString);
                                map.put("workType", ComFlag.NumFlag.WORKTYPE_BANK + "");
                                ApiUtil.doDefaultApi(api.addMistakesCollection(map), data -> {
                                    showToast("加入成功");
                                    getdata();
                                });
                            }
                        }).show();
                    }
                });
            }
        };
        rlSubject.setAdapter(mAdapter);
    }

    /**
     * 求讲解
     */
    private void getExplain(String exercisesId) {
        ApiUtil.doDefaultApi(api.requestExplain(onlineWorkId, workPushLogId, exercisesId), new HttpSucess<String>() {
            @Override
            public void onSucess(String data) {
                showToast("求讲解成功!");
            }
        });
    }

    /**
     * @param exercisesId 题目id
     * @param optionId    题目答案
     */
    //去重单选题
    private void deWeightRadio(String exercisesId, String optionId) {
        BeanInfo beanInfo = new BeanInfo(exercisesId, optionId);
        if (infoList.size() > 0) {
            for (int i = 0; i < infoList.size(); i++) {
                if (exercisesId.equals(infoList.get(i).exerciseId)) {
                    infoList.remove(infoList.get(i));
                }
            }
            infoList.add(beanInfo);
        } else {
            infoList.add(beanInfo);
        }
    }

    //提交
    @OnClick({R.id.tv_sunbject_save})
    public void onViewClicked(View view) {
        if (Status != ComFlag.NumFlag.WORKSTATUS_SUBMIT) {
            switch (view.getId()) {
                case R.id.tv_sunbject_save:
                    if (infoList.size() < subjectSize) {
                        new TitleDialog(SubjectActivity.this, "题目尚未答完,确定提交?", new TitleDialog.OnclickListener() {
                            @Override
                            public void OnSure() {
                                saveSubmit();
                            }

                            @Override
                            public void OnCancle() {

                            }
                        }).show();
                    } else {
                        saveSubmit();
                    }
                    break;
            }
        } else {
            tvSunbjectSave.setClickable(false);
            tvSunbjectSave.setSelected(false);
        }

    }

    private void saveSubmit() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cmStudentId", token);//导学本id
        map.put("workId", onlineWorkId);//导学本id
        map.put("workPushLogId", workPushLogId);//作业记录id
        map.put("workType", ComFlag.NumFlag.WORKTYPE_BANK + "");//作业类型
        map.put("info", infoList);//题目详情
        //传参
        MultipartBody.Builder builder = new MultipartBody.Builder();
        HashMap<String, Object> mapParts = new HashMap<>();
        builder.addFormDataPart("jsonParam", JSON.toJSONString(map));
        for (int i = 0; i < picturePathList.size(); i++) {
            File file = new File(picturePathList.get(i));
            if (file.exists()) {
                RequestBody bodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                builder.addFormDataPart("files", file.getName(), bodyFile);
            }
        }
        ApiUtil.doDefaultApi(api.submitWork(builder.build().parts()), new HttpSucess<String>() {
            @Override
            public void onSucess(String data) {
                tvSunbjectSave.setVisibility(View.GONE);
                SubjectActivity.this.showToast("提交成功");
                finish();
            }
        }, ViewControlUtil.create2Dialog(SubjectActivity.this));
    }


    @Override
    protected void initData() {
        super.initData();

        api = ApiUtil.createDefaultApi(UserService.class, token);
        getdata();
    }

    private void getdata() {
        ApiUtil.doDefaultApi(api.selectHomeworkInfo(onlineWorkId, workPushLogId, onlineWorkType), new HttpSucess<BeanSubject>() {
            @Override
            public void onSucess(BeanSubject data) {
                isSeeAnswer = data.isSeeAnswer;
                isSubmit = data.isSubmit;
                workCommand = data.workCommand;
                List<BeanSubject.ExercisesItemListBean> exercisesItemList = data.exercisesItemList;
                if (exercisesItemList.size() > 0 && !data.equals("")) {
                    subjectSize = exercisesItemList.size();
//                    rlSubject.setItemViewCacheSize(0);
//                    RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
//                    rlSubject.setRecycledViewPool(recycledViewPool);
//                    recycledViewPool.clear();
//                    RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(SubjectActivity.this, exercisesItemList);
//                    rlSubject.setAdapter(recyclerviewAdapter);
//                    recyclerviewAdapter.setHasStableIds(true);
                    mAdapter.setDatas(exercisesItemList);
                    ll_subject_title.setVisibility(View.VISIBLE);
                }
                tv_subject_num.setText("共" + subjectSize + "题");
                if (isSubmit) {
                    tv_total_num.setText("总分:" + data.exerciseTotalScore + "分" + " 得分:" + data.studentTotalScore + "分");
                } else {
                    tv_total_num.setText("总分:" + data.exerciseTotalScore + "分");
                }
            }
        }, ViewControlUtil.create2Dialog(context));
    }

    //修改
    private Handler handler;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    String pictureStr = data.getStringExtra("pictureStr");
                    String exercisesId = data.getStringExtra("exercisesId");
                    deWeightPicture(exercisesId, exercisesId, pictureStr);
                }
            }
        }
    }

    /**
     * @param exercisesId 题目id
     * @param optionId    题目答案id
     */
    //去重主观题
    private void deWeightPicture(String exercisesId, String optionId, String pictureStr) {

        BeanInfo beanInfo = new BeanInfo(exercisesId, optionId);
        if (picturePathList.size() > 0) {
            //去重答案
            for (int i = 0; i < infoList.size(); i++) {
                if (exercisesId.equals(infoList.get(i).exerciseId)) {
                    infoList.remove(infoList.get(i));
                }
            }
            //去重图片
            for (int i = 0; i < picturePathList.size(); i++) {
                if (pictureStr.equals(picturePathList.get(i))) {
                    picturePathList.remove(picturePathList.get(i));
                }
            }
            infoList.add(beanInfo);
            picturePathList.add(pictureStr);
        } else {
            infoList.add(beanInfo);
            picturePathList.add(pictureStr);
        }
        Message message = Message.obtain();
        message.obj = picturePathList;
        message.what = REFRESH_MESSAGE;
        handler.sendMessage(message);
    }

    private List<Uri> convertList(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
