package com.zwyl.myhomework.main.subject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.RequestOptions;
import com.mayigeek.frame.http.state.HttpError;
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
import com.zwyl.myhomework.util.MyWebViewClient;
import com.zwyl.myhomework.util.UIUtils;
import com.zwyl.myhomework.util.Utils;
import com.zwyl.myhomework.util.ViewUtil;
import com.zwyl.myhomework.views.GlideSimpleLoader;
import com.zwyl.myhomework.views.ImageWatcherHelper;
import com.zwyl.myhomework.viewstate.ViewControlUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubjectActivityFile extends BaseActivity {
    @BindView(R.id.rl_subject)
    RecyclerView rlSubject;
    List mlist = new ArrayList<BeanSubjectFile.FileWorkAnswerCardMapListBean>();
    @BindView(R.id.tv_split_score)
    TextView tvSplitScore;
    @BindView(R.id.iv_split_subject)
    ImageView ivSplitSubject;
    @BindView(R.id.tv_full_score)
    TextView tvFullScore;
    @BindView(R.id.iv_full_subject)
    ImageView ivFullSubject;
    @BindView(R.id.fl_split_unfold)
    FrameLayout flSplitUnfold;
    @BindView(R.id.fl_full_fewer)
    FrameLayout flFullFewer;
    @BindView(R.id.webView_split_subject)
    WebView webView_split_subject;
    @BindView(R.id.webView_full_subject)
    WebView webView_full_subject;
    private CustomLinearLayoutManager layoutManager;
    private String onlineWorkId;
    private String onlineWorkType;
    private String workPushLogId, onlineWorkName;
    private CommonAdapter mAdapter;
    private UserService api;
    List<BeanInfo> infoList = new ArrayList<BeanInfo>();
    List<String> picturePathList = new ArrayList<String>();
    private int subjectSize;
    protected static final int REQUEST_CODE = 1;
    private Activity context;
    private boolean isSeeAnswer;
    private boolean isSubmit;
    private View ic_split;
    private View ic_full;
    private String token;
    private int REFRESH_MESSAGE = 8;
    private int Status;
    private Handler handler;
    private ImageWatcherHelper iwHelper;
    private String workCommand;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_subject_file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isTranslucentStatus = false;
        super.onCreate(savedInstanceState);
        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(SubjectActivityFile.this) : 0); // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确

    }

    @Override
    protected void initView() {
        super.initView();
        setShowFilter(false);
        setShowRefresh(false);

        setOneClick(v -> {
            new AnalysisDialog(SubjectActivityFile.this, "作业要求", workCommand).show();
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
        context = SubjectActivityFile.this;
        ic_split = findViewById(R.id.ic_split);
        ic_full = findViewById(R.id.ic_full);
        onlineWorkId = getIntent().getStringExtra("onlineWorkId");
        workPushLogId = getIntent().getStringExtra("workPushLogId");
        onlineWorkType = getIntent().getStringExtra("onlineWorkType");
        onlineWorkName = getIntent().getStringExtra("onlineWorkName");
        token = getIntent().getStringExtra("token");
        Status = getIntent().getIntExtra("Status", -1);
        layoutManager = new CustomLinearLayoutManager(App.mContext);
        if (!TextUtils.isEmpty(onlineWorkName))
            setTitleCenter(onlineWorkName);
        else
            setTitleCenter("附件作业");

        WebSettings webSettingsFull = webView_full_subject.getSettings();
        webSettingsFull.setLoadWithOverviewMode(true);
        webSettingsFull.setSupportZoom(true);
        webSettingsFull.setBuiltInZoomControls(true);
        webSettingsFull.setJavaScriptEnabled(true);
        webView_full_subject.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                view.loadUrl(url);
                return true;
            }
            //WebViewClient帮助WebView去处理一些页面控制和请求通知
        });

        WebSettings webSettings = webView_split_subject.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webView_split_subject.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                view.loadUrl(url);
                return true;
            }
            //WebViewClient帮助WebView去处理一些页面控制和请求通知
        });

        rlSubject.setLayoutManager(layoutManager);
        rlSubject.setAdapter(mAdapter = new CommonAdapter<BeanSubjectFile.FileWorkAnswerCardMapListBean>(App.mContext, R.layout.item_subject_file, mlist) {
            @Override
            protected void convert(ViewHolder holder, BeanSubjectFile.FileWorkAnswerCardMapListBean beanSubject, int position) {
                holder.setIsRecyclable(false);
                LinearLayout ll_bottom = holder.getView(R.id.ll_bottom);
                RelativeLayout rl_bottom = holder.getView(R.id.rl_bottom);//题目选项
                RelativeLayout rl_clickItem = holder.getView(R.id.rl_clickItem);//题目底部选项按钮
                ImageView ll_item_subject_listen = holder.getView(R.id.ll_item_subject_listen);//听讲解
                ImageView ll_item_subject_listen_subject = holder.getView(R.id.ll_item_subject_listen_subject);//听讲解主观题
                LinearLayout ll_item_subject_teachcomment = holder.getView(R.id.ll_item_subject_teachcomment);//教师评语
                ImageView iv_explain = holder.getView(R.id.iv_explain);//Q求讲解
                ImageView iv_explain_object = holder.getView(R.id.iv_explain_object);//Q求讲解客观题
                ImageView iv_explain_subject = holder.getView(R.id.iv_explain_subject);//Q求讲解主观题
                LinearLayout ll_item_subject_look = holder.getView(R.id.ll_item_subject_look);
                LinearLayout ll_item_subject_addFalse = holder.getView(R.id.ll_item_subject_addFalse);
                TextView tv_item_subject_type = holder.getView(R.id.tv_item_subject_type);
                ImageView iv_item_subject_false = holder.getView(R.id.iv_item_subject_false);
                View ic_answer_subjectivity = holder.getView(R.id.ic_answer_subjectivity);//主观题
                View ic_answer_objective = holder.getView(R.id.ic_answer_objective);//客观题判断是否显示答案//Todo
                TextView tv_myanswer = holder.getView(R.id.tv_myanswer); //客观题我的答案
                TextView tv_sureanswer = holder.getView(R.id.tv_sureanswer); //正确答案
                TextView tv_sureanswer_str = holder.getView(R.id.tv_sureanswer_str);//正确答案tv
                LinearLayout ll_item_subject_rightanswer = holder.getView(R.id.ll_item_subject_rightanswer);
//                WebView webView_participation_detail = holder.getView(R.id.webView_participation_detail);
                TextView tv_analysis = holder.getView(R.id.tv_analysis);
                LinearLayout ll_analysis_file = holder.getView(R.id.ll_analysis_file);
                ImageView iv_file_detail_item = holder.getView(R.id.iv_file_detail_item);
                int exerciseTypeCode = beanSubject.cardTypeCode;//题目类型
                String exercisesId = beanSubject.exerciseId;
                int exerciseNo = beanSubject.exerciseNo;
                String exerciseAnalysis = beanSubject.exerciseAnalysis;
                ll_bottom.removeAllViews();
//                WebSettings webSettings = webView_participation_detail.getSettings();
//                webSettings.setSupportZoom(false);
//                webSettings.setUseWideViewPort(true);
//                webSettings.setLoadWithOverviewMode(true);
//                webSettings.setAllowUniversalAccessFromFileURLs(true);
//                webSettings.setAllowFileAccess(true);
//                webSettings.setAllowFileAccessFromFileURLs(true);
//                webSettings.setBlockNetworkImage(false);
                if (isSeeAnswer) {
                    //显示答案
                    rl_bottom.setVisibility(View.GONE);
                    rl_clickItem.setVisibility(View.VISIBLE);
                    if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                        ic_answer_objective.setVisibility(View.VISIBLE);
                        ic_answer_subjectivity.setVisibility(View.GONE);
                        ll_item_subject_rightanswer.setVisibility(View.GONE);
                    } else {
                        ic_answer_objective.setVisibility(View.GONE);
                        ic_answer_subjectivity.setVisibility(View.VISIBLE);
                        ll_item_subject_rightanswer.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(beanSubject.exerciseAnswer)) {
                            ll_item_subject_rightanswer.setSelected(true);
                            ll_item_subject_rightanswer.setClickable(true);
                            ll_item_subject_rightanswer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AnalysisDialog(SubjectActivityFile.this, "正确答案", beanSubject.exerciseAnswer).show();
                                }
                            });
                        } else {
                            ll_item_subject_rightanswer.setSelected(false);
                            ll_item_subject_rightanswer.setClickable(false);
                        }

                    }   //判断显示主观题答案还是客观题答案
                } else {
                    //显e示题目
                    if (isSubmit) {
                        if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                            ic_answer_subjectivity.setVisibility(View.GONE);
                        } else {
                            ic_answer_subjectivity.setVisibility(View.VISIBLE);
                        }
                        rl_bottom.setVisibility(View.GONE);
                        tv_sureanswer.setVisibility(View.GONE);
                        tv_sureanswer_str.setVisibility(View.GONE);
                        iv_explain_object.setVisibility(View.GONE);
                        ll_item_subject_listen.setVisibility(View.GONE);
                    } else {
                        rl_bottom.setVisibility(View.VISIBLE);
                        ic_answer_subjectivity.setVisibility(View.GONE);
                        ic_answer_subjectivity.setVisibility(View.GONE);
                        ic_answer_objective.setVisibility(View.GONE);
                    }
//                    rl_bottom.setVisibility(View.VISIBLE);
//                    ic_answer_subjectivity.setVisibility(View.GONE);
//                    ic_answer_objective.setVisibility(View.GONE);
                    rl_clickItem.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(beanSubject.exerciseAnswer))
                    if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE)
                        tv_sureanswer.setText(Utils.getJudge(beanSubject.exerciseAnswer));
                    else
                        tv_sureanswer.setText(beanSubject.exerciseAnswer);
                else
                    tv_sureanswer.setText("");
                if (!TextUtils.isEmpty(beanSubject.studentAnswerOptionId)) {
                    if (isSeeAnswer) {
                        if (Utils.getJudge(beanSubject.studentAnswerOptionId).equals(Utils.getJudge(beanSubject.exerciseAnswer))) {
                            if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE)
                                tv_myanswer.setText(Utils.getJudge(beanSubject.studentAnswerOptionId));
                            else
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                        } else {
                            if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
                                tv_myanswer.setTextColor(Color.RED);
                                tv_myanswer.setText(Utils.getJudge(beanSubject.studentAnswerOptionId));
                            } else {
                                tv_myanswer.setTextColor(Color.RED);
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                            }
                        }
                    } else {
                        if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE)
                            tv_myanswer.setText(Utils.getJudge(beanSubject.studentAnswerOptionId));
                        else
                            tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                    }
                } else
                    tv_myanswer.setText("");
                setTitleType(tv_item_subject_type, exerciseNo, Utils.getSubjectNo(position), Utils.getsubjectType(exerciseTypeCode));
                //单选
                if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO) {
                    List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> fileList = beanSubject.exerciseOptionList;
                    LinearLayout view = ViewUtil.getSingleViewFile(fileList, (position + 1), new ViewUtil.OnMultipleClick() {
                        @Override
                        public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                            deWeightRadio(exercisesId, optionId);
                        }
                    });
                    ll_bottom.addView(view);
                    //复现答案
                    if (infoList != null && infoList.size() != 0)
                        for (int i = 0; i < infoList.size(); i++) {
                            for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                if (infoList.get(i).studentAnswer.equals(beanSubject.exerciseOptionList.get(j).optionId) && infoList.get(i).exerciseId.equals(beanSubject.exerciseId)) {
                                    CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                    checkBox.setChecked(true);
                                }
                            }
                        }
                } else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
                    LinearLayout view = ViewUtil.getSingleViewFile(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
                        @Override
                        public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                            deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");
                        }
                    });
                    ll_bottom.addView(view);
                    if (infoList != null && infoList.size() != 0)
                        for (int i = 0; i < infoList.size(); i++) {
                            for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                if (Utils.getJudge(infoList.get(i).studentAnswer).equals(Utils.getJudge(beanSubject.exerciseOptionList.get(j).optionNo)) && infoList.get(i).exerciseId.equals(beanSubject.exerciseId)) {
                                    CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                    checkBox.setChecked(true);
                                }
                            }
                        }
                }
//                if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO) {
//                    List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> fileList = beanSubject.exerciseOptionList;
//                    ll_bottom.addView(ViewUtil.getRadioGroupFile(fileList, null, (position + 1), new ViewUtil.OncheckedClick() {
//                        @Override
//                        public void OnChecked(int indexNum, String optionId) {
//                            deWeightRadio(exercisesId, optionId);
//                        }
//                    }));
                //判断
//                }
//                else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
////                    setTitleType(tv_item_subject_type, exerciseNo, Utils.getSubjectNo(position), Utils.getsubjectType(ComFlag.NumFlag.EXERCISE_JUDGE));
//                    ll_bottom.addView(ViewUtil.getRadioGroup(null, mJudge, (position + 1), new ViewUtil.OncheckedClick() {
//                        @Override
//                        public void OnChecked(int indexNum, String optionId) {
//                            deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");//判断题传1:对, 0:错;
//                        }
//                    }));
//                    //多选
//                }
                else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                    List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> exerciseList = beanSubject.exerciseOptionList;
                    List<String> optionList = new ArrayList<>();
                    LinearLayout view = ViewUtil.getRadioGroupMultipleFile(exerciseList, (position + 1), new ViewUtil.OnMultipleClick() {
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
                            //修改
                            StringBuilder sbuid = new StringBuilder();
                            for (int i = 0; i < optionList.size(); i++) {
                                if (sbuid.length() == 0) {
                                    sbuid.append(optionList.get(i));
                                } else {
                                    sbuid.append("," + optionList.get(i));
                                }
                            }
                            deWeightRadio(exercisesId, sbuid.toString());
                        }
                    });
                    ll_bottom.addView(view);
                    if (infoList != null && infoList.size() != 0) {
                        for (int i = 0; i < infoList.size(); i++) {
                            if (infoList.get(i).exerciseId.equals(beanSubject.exerciseId)) {
                                String[] infoArr = infoList.get(i).studentAnswer.split(",");
                                for (int k = 0; k < infoArr.length; k++) {
                                    for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                        if (infoArr[k].equals(beanSubject.exerciseOptionList.get(j).optionId)) {
                                            CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                            checkBox.setChecked(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
//                    ll_bottom.addView(ViewUtil.getRadioGroupMultipleFile(exerciseList, (position + 1), new ViewUtil.OnMultipleClick() {
//                        @Override
//                        public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                            if (isChecked) {
//                                exerciseList.get(indexNum).ischeck = true;
//                            } else {
//                                exerciseList.get(indexNum).ischeck = false;
//                            }
//                        }
//                    }));
//                            for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
//                                if (infoList.get(i).studentAnswer.equals(beanSubject.exerciseOptionList.get(j).optionId)) {
////                                    Log.e("http", "info ---------------------->: " + infoList.get(i).studentAnswer.toString() + "  " + beanSubject.exerciseOptionList.get(j).optionId);
//                                    CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
//                                    checkBox.setChecked(true);
//                                }
//                            }
                } else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_GAP || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_COUNT || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_SHORT) {
//                    if (isSeeAnswer) {
//                        ic_answer_subjectivity.setVisibility(View.VISIBLE);
//                    } else {
//                        if (TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
//                            if (picturePathList.size() != 0) {
//                                for (int i = 0; i < picturePathList.size(); i++) {
//                                    if (beanSubject.exerciseId.equals(picturePathList.get(i).substring(picturePathList.get(i).lastIndexOf("/") + 1, picturePathList.get(i).length() - 4))) {
//                                        ic_answer_subjectivity.setVisibility(View.VISIBLE);
//                                        String path = "file://" + picturePathList.get(i);
//                                        webView_participation_detail.setWebViewClient(new MyWebViewClient(webView_participation_detail));
//                                        webView_participation_detail.loadUrl(path);
//                                    }
//                                }
//                            } else {
//                                //主观题
//                                View textViewTwo = ViewUtil.showAnswerFrame((position + 1) + ".");
//                                ll_bottom.addView(textViewTwo);
//                                textViewTwo.setOnClickListener(v -> {
//                                    Intent intent = createIntent(SubjectActivityWrite.class);
//                                    intent.putExtra("exercisesId", beanSubject.exerciseId);
//                                    startActivityForResult(intent, REQUEST_CODE);
//                                });
//                            }
//                        } else {
//                            ic_answer_subjectivity.setVisibility(View.VISIBLE);
//                        }
//                    }
                    if (isSubmit) {
                        rl_bottom.setVisibility(View.GONE);
                        ll_item_subject_listen_subject.setVisibility(View.VISIBLE);
                        iv_file_detail_item.setOnClickListener(new View.OnClickListener() {
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
                    } else {
                        rl_bottom.setVisibility(View.GONE);
                        ll_item_subject_listen_subject.setVisibility(View.GONE);
                        ic_answer_subjectivity.setVisibility(View.VISIBLE);
//                        webView_participation_detail.setVisibility(View.GONE);
                        ll_analysis_file.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = createIntent(SubjectActivityWrite.class);
                                intent.putExtra("exercisesId", beanSubject.exerciseId);
                                intent.putExtra("fileUri", fileUri);
                                intent.putExtra("onlineWorkType", onlineWorkType);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        });
                    }
                }
                //修改
                tv_analysis.setText((position + 1) + ". ");
//                String body = "<img  src=\"" + beanSubject.studentAnswerFileUri + "\"/>";
//                String html = "<html><body>" + body + "</html></body>";
//                webView_participation_detail.setWebViewClient(new MyWebViewClient(webView_participation_detail));
//                webView_participation_detail.addJavascriptInterface(new MyWebViewClient.JavaScriptInterface(context), "imagelistner");//这个是给图片设置点击监听的，如果你项目需要webview中图片，点击查看大图功能，可以这么添加
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                }
                if (TextUtils.isEmpty(beanSubject.workContentFileUri)) {
                    if (!TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                        iv_file_detail_item.setVisibility(View.VISIBLE);
                        UIUtils.updateOptions(new RequestOptions(), beanSubject.studentAnswerFileUri, System.currentTimeMillis(), iv_file_detail_item);
//                    webView_participation_detail.loadDataWithBaseURL("https://yishengjiaoyu.oss-cn-beijing.aliyuncs.com/", html, "text/html", "UTF-8", null);
                    }
                } else {
                    iv_file_detail_item.setVisibility(View.VISIBLE);
                    UIUtils.updateOptions(new RequestOptions(), beanSubject.workContentFileUri, System.currentTimeMillis(), iv_file_detail_item);
                }

                if (picturePathList.size() != 0) {
                    for (int i = 0; i < picturePathList.size(); i++) {
                        if (beanSubject.exerciseId.equals(picturePathList.get(i).substring(picturePathList.get(i).lastIndexOf("/") + 1, picturePathList.get(i).length() - 4))) {
                            ic_answer_subjectivity.setVisibility(View.VISIBLE);
//                            String path = "file://" + picturePathList.get(i);
//                            webView_participation_detail.setWebViewClient(new MyWebViewClient(webView_participation_detail));
//                            webView_participation_detail.loadUrl(path);
//                            webView_participation_detail.setVisibility(View.GONE);
                            iv_file_detail_item.setVisibility(View.VISIBLE);
//                            UIUtils.setCenterCrop(beanSubject.studentAnswerFileUri, iv_file_detail_item);
                            Log.e("http", "picuri : " + picturePathList.get(i));
                            UIUtils.updateOptions(new RequestOptions(), picturePathList.get(i), System.currentTimeMillis(), iv_file_detail_item);
                        }
                    }
                }

                //                //是否有听讲解内容exerciseExplainFileUri
                if (TextUtils.isEmpty(beanSubject.exerciseExplainFileUri)) {//Todo
                    ll_item_subject_listen.setSelected(false);
                    ll_item_subject_listen.setClickable(false);
                    ll_item_subject_listen_subject.setSelected(false);
                    ll_item_subject_listen_subject.setClickable(false);
                } else {
                    ll_item_subject_listen.setClickable(true);
                    ll_item_subject_listen.setSelected(true);
                    ll_item_subject_listen_subject.setClickable(true);
                    ll_item_subject_listen_subject.setSelected(true);
                }

                //是否加入错题集
                if (beanSubject.isMistakesCollection) {
                    ll_item_subject_addFalse.setSelected(true);
                    iv_item_subject_false.setBackgroundResource(R.mipmap.deletefalse);
                } else {
                    ll_item_subject_addFalse.setSelected(true);
                    iv_item_subject_false.setBackgroundResource(R.mipmap.addfalse);
                }
                //求讲解
                if (!beanSubject.isConfirnExpianation) {
                    iv_explain.setSelected(true);
                    iv_explain_object.setSelected(true);
                    iv_explain_subject.setSelected(true);
                    //求讲解
                    iv_explain.setOnClickListener(v -> {
                        getExplain(beanSubject,iv_explain);
                    });
                    //求讲解
                    iv_explain_object.setOnClickListener(v -> {
                        getExplain(beanSubject,iv_explain_object);
                    });
                    //求讲解主观题
                    iv_explain_subject.setOnClickListener(v -> {
                        getExplain(beanSubject,iv_explain_subject);
                    });
                } else {
                    //求讲解
                    iv_explain.setOnClickListener(v -> {
                        iv_explain.setSelected(false);
                    });
                    //求讲解
                    iv_explain_object.setOnClickListener(v -> {
                        iv_explain_object.setSelected(false);
                    });
                    //求讲解主观题
                    iv_explain_subject.setOnClickListener(v -> {
                        iv_explain_subject.setSelected(false);
                    });
                }
                if (!TextUtils.isEmpty(beanSubject.exerciseExplainFileUri)) {
                    //听讲解
                    ll_item_subject_listen.setOnClickListener(v -> {
                        Intent intent = new Intent(SubjectActivityFile.this, PlayActivity.class);
                        intent.putExtra("resourceUri", beanSubject.exerciseExplainFileUri);
                        startActivity(intent);
                    });
                    //听讲解
                    ll_item_subject_listen_subject.setOnClickListener(v -> {
                        Intent intent = new Intent(SubjectActivityFile.this, PlayActivity.class);
                        intent.putExtra("resourceUri", beanSubject.exerciseExplainFileUri);
                        startActivity(intent);
                    });
                }

                if (TextUtils.isEmpty(exerciseAnalysis)) {
                    ll_item_subject_look.setClickable(false);
                    ll_item_subject_look.setSelected(false);
                } else {
                    ll_item_subject_look.setSelected(true);
                    ll_item_subject_look.setClickable(true);
                }
                //看解析
                ll_item_subject_look.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(exerciseAnalysis)) {
                        new AnalysisDialog(SubjectActivityFile.this , "看解析", beanSubject.exerciseAnalysis).show();
                    } else showToast("暂无解析");
                });
                //教师评语
                if (!TextUtils.isEmpty(beanSubject.workResultComment)) {
                    ll_item_subject_teachcomment.setSelected(true);
                    ll_item_subject_teachcomment.setClickable(true);
                    ll_item_subject_teachcomment.setOnClickListener(v -> {
                        new AnalysisDialog(SubjectActivityFile.this, "教师评语", beanSubject.workResultComment).show();
                    });
                } else {
                    ll_item_subject_teachcomment.setSelected(false);
                    ll_item_subject_teachcomment.setClickable(false);
                }
                //错题集
                ll_item_subject_addFalse.setOnClickListener(v -> {
                    //是否加入过错题集
                    if (beanSubject.isMistakesCollection) {
                        new TitleDialog(context, getResources().getString(R.string.notifyMsg), new TitleDialog.OnclickListener() {
                            @Override
                            public void OnSure() {
                                ApiUtil.doDefaultApi(api.deleteMistakesCollection(onlineWorkId, beanSubject.exerciseId), data -> {//workSupportId//Todo
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
                                map.put("workId", onlineWorkId);
                                map.put("exerciseId", beanSubject.exerciseId);
                                map.put("reason", etString);
                                map.put("workType", ComFlag.NumFlag.WORKTYPE_ADJUNCT + "");
                                ApiUtil.doDefaultApi(api.addMistakesCollection(map), data -> {
                                    showToast("加入成功!");
                                    getdata();

                                });
                            }
                        }).show();

                    }
                });
            }

            private void getExplain(BeanSubjectFile.FileWorkAnswerCardMapListBean beanSubject,ImageView iv) {
                ApiUtil.doDefaultApi(api.requestExplain(onlineWorkId, workPushLogId, beanSubject.exerciseId), new HttpSucess<String>() {
                    @Override
                    public void onSucess(String data) {
                        iv.setSelected(false);
                        showToast("求讲解成功!");
                        getdata();
                    }
                });
            }

        });
    }

    //设置大标题
    private void setTitleType(TextView tv_item_subject_type, int exerciseNo, String rankNo, String title) {
        if (exerciseNo == 1) {
            tv_item_subject_type.setText(rankNo + title);
            tv_item_subject_type.setVisibility(View.VISIBLE);
        } else {
            tv_item_subject_type.setVisibility(View.GONE);
        }
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

    private void sureSubmit() {
        if (infoList.size() < subjectSize) {
            new TitleDialog(SubjectActivityFile.this, "题目尚未答完,确定提交?", new TitleDialog.OnclickListener() {
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
    }

    private void saveSubmit() {
        //多选选择答案 修改
//        List<BeanSubjectFile.FileWorkAnswerCardMapListBean> datas = mAdapter.getDatas();
//        for (int i = 0; i < datas.size(); i++) {
//            BeanSubjectFile.FileWorkAnswerCardMapListBean beanSubject = datas.get(i);
//            if (beanSubject.cardTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
//                List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> exerciseList = beanSubject.exerciseOptionList;
//                StringBuilder sbuid = new StringBuilder();
//                for (int j = 0; j < exerciseList.size(); j++) {
//                    if (exerciseList.get(j).ischeck) {
//                        String optionIdcheck = exerciseList.get(j).optionId;
//                        if (sbuid.length() == 0) {
//                            sbuid.append(optionIdcheck);
//                        } else {
//                            sbuid.append("," + optionIdcheck);
//                        }
//                    }
//                }
//                deWeightRadio(beanSubject.exerciseId, sbuid.toString());
//            }
//        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("cmStudentId", token);//导学本id//TODO
        map.put("workId", onlineWorkId);//导学本id
        map.put("workPushLogId", workPushLogId);//作业记录id
        map.put("workType", ComFlag.NumFlag.WORKTYPE_ADJUNCT + "");//作业类型
        map.put("info", infoList);//题目详情
        //传参
        MultipartBody.Builder builder = new MultipartBody.Builder();
        HashMap<String, Object> mapParts = new HashMap<>();
        builder.addFormDataPart("jsonParam", JSON.toJSONString(map));
        for (int i = 0; i < picturePathList.size(); i++) {
            File file = new File(picturePathList.get(i));
            if (file.exists()) {
                RequestBody bodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                String name = file.getName();
                builder.addFormDataPart("files", file.getName(), bodyFile);
            }
        }
        ApiUtil.doDefaultApi(api.submitWork(builder.build().parts()), new HttpSucess<String>() {
            @Override
            public void onSucess(String data) {
                SubjectActivityFile.this.showToast("提交成功");
                finish();
            }
        }, ViewControlUtil.create2Dialog(SubjectActivityFile.this));
    }

    private String fileUri;

    private void getdata() {
        ApiUtil.doDefaultApi(api.selectHomeworkInfoFile(onlineWorkId, workPushLogId, onlineWorkType), data -> {
            isSeeAnswer = data.isSeeAnswer;
            isSubmit = data.isSubmit;
            workCommand = data.workCommand;
            Log.e("http", " issubmit : " + isSubmit);
            if (!isSubmit) {
                setShowRightHeadTwo(true, true);
                setTwoClick(v -> {
                    if (Status != ComFlag.NumFlag.WORKSTATUS_SUBMIT)
                        sureSubmit();
                });
                setOneClick(v -> {
                    new AnalysisDialog(SubjectActivityFile.this, "作业要求", workCommand).show();
                });
            } else {
                setShowRightHeadTwo(true, false);
                setTwoClick(v -> {
                    new AnalysisDialog(SubjectActivityFile.this, "作业要求", workCommand).show();
                });
            }
            fileUri = data.fileUri;
            List<BeanSubjectFile.FileWorkAnswerCardMapListBean> fileWorkList = data.fileWorkAnswerCardMapList;
            if (fileWorkList.size() > 0 && !data.equals("")) {
                subjectSize = fileWorkList.size();
                mAdapter.setDatas(fileWorkList);
                if (isSubmit) {
                    tvSplitScore.setText("共" + subjectSize + "题 " + "总计:" + data.exerciseTotalScore + "分 " + "得分:" + data.studentTotalScore + "分");
                } else {
                    tvSplitScore.setText("共" + subjectSize + "题" + "总计:" + data.exerciseTotalScore + "分");
                }
                tvFullScore.setText("共" + subjectSize + "题 " + "总计:" + data.exerciseTotalScore + "分");
            }

            if (!TextUtils.isEmpty(fileUri)) {
                webView_split_subject.loadUrl(fileUri);
                webView_full_subject.loadUrl(fileUri);
            } else {
                ivSplitSubject.setVisibility(View.VISIBLE);
            }

        }, new HttpError() {
            @Override
            public void onError(@NotNull Throwable e) {
                Log.e("http", "error : " + e);
            }
        }, ViewControlUtil.create2Dialog(SubjectActivityFile.this));
    }

    @Override
    protected void initData() {
        super.initData();

        api = ApiUtil.createUploadApi(UserService.class, token);
        getdata();
    }

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


    @OnClick({R.id.fl_split_unfold, R.id.fl_full_fewer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_split_unfold:
                ic_full.setVisibility(View.VISIBLE);
                ic_split.setVisibility(View.GONE);
                break;
            case R.id.fl_full_fewer:
                ic_full.setVisibility(View.GONE);
                ic_split.setVisibility(View.VISIBLE);
                break;
        }
    }

    private List<Uri> convertList(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }
}
