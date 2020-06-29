package com.zwyl.myhomework.main.subject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mayigeek.frame.http.state.HttpSucess;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;
import com.zwyl.myhomework.base.ComFlag;
import com.zwyl.myhomework.customveiw.PaletteView;
import com.zwyl.myhomework.dialog.TitleDialog;
import com.zwyl.myhomework.http.ApiUtil;
import com.zwyl.myhomework.service.UserService;
import com.zwyl.myhomework.util.FileUtils;
import com.zwyl.myhomework.util.MyWebViewClient;
import com.zwyl.myhomework.util.Utils;
import com.zwyl.myhomework.viewstate.ViewControlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubjectActivityWrite extends BaseActivity implements PaletteView.Callback, Handler.Callback {
    @BindView(R.id.tv_delete)
    RadioButton tvDelete;
    @BindView(R.id.tv_repeal)
    RadioButton tvRepeal;
    @BindView(R.id.tv_renew)
    RadioButton tvRenew;
    @BindView(R.id.tv_wipe)
    RadioButton tvWipe;
    @BindView(R.id.tv_write)
    RadioButton tvWrite;
    @BindView(R.id.rg_group)
    RadioGroup rgGroup;
    @BindView(R.id.rg_group_top)
    RadioGroup rgGroupTop;
    @BindView(R.id.tv_sunbjectWrite_save)
    TextView tvSunbjectWriteSave;
    @BindView(R.id.rb_top_write)
    RadioButton rbTopWrite;
    @BindView(R.id.rb_top_edit)
    RadioButton rbTopEdit;
    @BindView(R.id.rb_top_photo)
    RadioButton rbTopPhoto;
    @BindView(R.id.palette_write)
    PaletteView mPaletteView;
    @BindView(R.id.ed_content)
    EditText edContent;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.webView_subjectwrite)
    WebView webView_subjectwrite;
    @BindView(R.id.tvSubjectTitle)
    TextView tvSubjectTitle;
    @BindView(R.id.sv_subjectWrite_answer)
    ScrollView sv_subjectWrite_answer;
    @BindView(R.id.ic_answer_edit)
    LinearLayout ll_answer_edit;
    @BindView(R.id.ic_myanswer)
    RelativeLayout rl_answer_show;
    @BindView(R.id.webView_participation_detail)
    WebView webView_participation_detail;
    private UserService api;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private Handler mHandler;
    private int toptag;//0手写笔,1输入法,2拍照
    private Bitmap bmpPhoto;
    private String exercisesId, exercisesTitle, exerciseAnalysis;
    private String onlineWorkName, onlineWorkId, workPushLogId, onlineWorkType, token, exercisesType, fileUri;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_subjectwrite;
    }

    @Override
    protected void initView() {
        super.initView();
        exercisesId = getIntent().getStringExtra("exercisesId");
        exercisesTitle = getIntent().getStringExtra("exercisesTitle");
        exerciseAnalysis = getIntent().getStringExtra("exerciseAnalysis");
        onlineWorkId = getIntent().getStringExtra("onlineWorkId");
        workPushLogId = getIntent().getStringExtra("workPushLogId");
        onlineWorkType = getIntent().getStringExtra("onlineWorkType");
        exercisesType = getIntent().getStringExtra("exercisesType");
        onlineWorkName = getIntent().getStringExtra("onlineWorkName");
        fileUri = getIntent().getStringExtra("fileUri");
        token = getIntent().getStringExtra("token");
        rbTopWrite.setChecked(true);//默认选中手写笔

        String filePath = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, exercisesId).getAbsolutePath();
        if (!TextUtils.isEmpty(filePath)) {
            Bitmap bm = BitmapFactory.decodeFile(filePath);
            mPaletteView.setBitmap(bm);
        }

        WebSettings webSettingsFull = webView_subjectwrite.getSettings();
//        webSettingsFull.setLoadWithOverviewMode(true);
        webSettingsFull.setSupportZoom(true);
//        webSettingsFull.setBuiltInZoomControls(true);
        webSettingsFull.setJavaScriptEnabled(true);
//        webSettingsFull.setUseWideViewPort(true);
        webSettingsFull.setAllowUniversalAccessFromFileURLs(true);
        webSettingsFull.setAllowFileAccess(true);
        webSettingsFull.setAllowFileAccessFromFileURLs(true);
        webView_subjectwrite.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                view.loadUrl(url);
                return true;
            }
            //WebViewClient帮助WebView去处理一些页面控制和请求通知
        });

        if (!TextUtils.isEmpty(onlineWorkType))
            if (onlineWorkType.equals(ComFlag.NumFlag.WORKTYPE_SIMPLE + "")) {
                tvSunbjectWriteSave.setText("提交");
                tvSubjectTitle.setVisibility(View.GONE);
            } else if (onlineWorkType.endsWith(ComFlag.NumFlag.WORKTYPE_ADJUNCT + "")) {
                webView_subjectwrite.loadUrl(fileUri);
            }

        if (!TextUtils.isEmpty(onlineWorkName))
            setTitleCenter(onlineWorkName);
        else
            setTitleCenter("作业");
        setShowFilter(false);
        mHandler = new Handler(this);
        if (!TextUtils.isEmpty(exercisesTitle))
            webView_subjectwrite.loadDataWithBaseURL(null, exercisesTitle, "text/html", "UTF-8", null);
        tvSubjectTitle.setText(exercisesType);
    }

    private int tag = 0;

    @OnClick({R.id.tv_delete, R.id.tv_repeal, R.id.tv_renew, R.id.tv_wipe, R.id.tv_write, R.id.tv_sunbjectWrite_save, R.id.rb_top_write, R.id.rb_top_edit, R.id.rb_top_photo, R.id.iv_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                tag = 0;
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                mPaletteView.clear();
                break;
            case R.id.tv_repeal:
                tag = 0;
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                mPaletteView.undo();
                break;
            case R.id.tv_renew:
                tag = 0;
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                mPaletteView.redo();
                break;
            case R.id.tv_wipe:
                if (tag == 0) {
                    showToast("选中");
                    tag = 1;
                    mPaletteView.setMode(PaletteView.Mode.ERASER);
                    return;
                } else {
                    tag = 0;
                    showToast("未选中");
                    rgGroup.clearCheck();
                    mPaletteView.setMode(PaletteView.Mode.DRAW);
                }
            case R.id.rb_top_write:
//                if (toptag != ComFlag.NumFlag.RB_TOP_WRITE) {
                new TitleDialog(this, "当前数据将会被删除!", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        edContent.setVisibility(View.GONE);
                        ivPhoto.setVisibility(View.GONE);
                        rgGroup.setVisibility(View.VISIBLE);
                        mPaletteView.setVisibility(View.VISIBLE);
                        toptag = ComFlag.NumFlag.RB_TOP_WRITE;
                    }

                    @Override
                    public void OnCancle() {
                        if (toptag == ComFlag.NumFlag.RB_TOP_PHOTO) {
                            rbTopPhoto.setChecked(true);
                        } else {
                            rbTopEdit.setChecked(true);
                        }
                    }
                }).show();
//                } else {
//                    toptag = ComFlag.NumFlag.RB_TOP_WRITE;
//                }
                break;
            case R.id.rb_top_edit:
//                if (toptag != ComFlag.NumFlag.RB_TOP_EDIT) {
                edContent.setText("");
                new TitleDialog(this, "当前数据将会被删除!", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        edContent.setVisibility(View.VISIBLE);
                        ivPhoto.setVisibility(View.GONE);
                        rgGroup.setVisibility(View.GONE);
                        mPaletteView.setVisibility(View.GONE);
                        mPaletteView.clear();
                        toptag = ComFlag.NumFlag.RB_TOP_EDIT;
                    }

                    @Override
                    public void OnCancle() {
                        if (toptag == ComFlag.NumFlag.RB_TOP_WRITE) {
                            rbTopWrite.setChecked(true);
                        } else {
                            rbTopPhoto.setChecked(true);
                        }
                    }
                }).show();
//                } else {
//                    toptag = ComFlag.NumFlag.RB_TOP_EDIT;
//                }

                break;
            case R.id.rb_top_photo:
//                if (toptag != ComFlag.NumFlag.RB_TOP_PHOTO) {
                new TitleDialog(this, "当前数据将会被删除!", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        rgGroup.setVisibility(View.GONE);
                        edContent.setVisibility(View.GONE);
                        mPaletteView.setVisibility(View.GONE);
                        mPaletteView.clear();
                        ivPhoto.setVisibility(View.VISIBLE);
                        toptag = ComFlag.NumFlag.RB_TOP_PHOTO;
                        Acp.getInstance(SubjectActivityWrite.this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA).build(), new AcpListener() {
                            @Override
                            public void onGranted() {
                                //showToast("同意了相机权限");
//                                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File photoFile = null;
                                photoFile = Utils.CreateImageFile(exercisesId);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                takePictureIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                startActivityForResult(takePictureIntent, 1);
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                showToast(permissions.toString() + "权限拒绝");
                            }
                        });
                    }

                    @Override
                    public void OnCancle() {
                        if (toptag == ComFlag.NumFlag.RB_TOP_EDIT) {
                            rbTopEdit.setChecked(true);
                        } else {
                            rbTopWrite.setChecked(true);
                        }

                    }
                }).show();
//                } else {
//                    toptag = ComFlag.NumFlag.RB_TOP_PHOTO;
//                }
                break;

            case R.id.tv_sunbjectWrite_save:
                showToast("提交");
                if (toptag == ComFlag.NumFlag.RB_TOP_WRITE) {
                    if (mSaveProgressDlg == null) {
                        initSaveProgressDlg();
                    }
                    mSaveProgressDlg.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //                        Bitmap bm = mPaletteView.buildBitmap();
                            bmpPhoto = loadBitmapFromView(mPaletteView);
                            if (bmpPhoto != null) {
                                mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                            } else {
                                mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                            }
                        }
                    }).start();
                } else if (toptag == ComFlag.NumFlag.RB_TOP_PHOTO) {
                    if (mSaveProgressDlg == null) {
                        initSaveProgressDlg();
                    }
                    mSaveProgressDlg.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //                        Bitmap bm = mPaletteView.buildBitmap();
//                            bmpPhoto = loadBitmapFromView(ivPhoto);
                            if (bmpPhoto != null) {
                                mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                            } else {
                                mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                            }
                        }
                    }).start();

                } else {
                    //输入法文字
                    bmpPhoto = Utils.textAsBitmap(edContent.getText().toString().trim(), 16);
                    if (mSaveProgressDlg == null) {
                        initSaveProgressDlg();
                    }
                    mSaveProgressDlg.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //                        Bitmap bm = mPaletteView.buildBitmap();
//                            bmpPhoto = loadBitmapFromView(edContent);
                            if (bmpPhoto != null) {
                                mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                            } else {
                                mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                            }
                        }
                    }).start();
                }

                break;
            case R.id.iv_photo:
                Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA).build(), new AcpListener() {
                    @Override
                    public void onGranted() {
                        //showToast("同意了相机权限");
//                        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;
                        photoFile = Utils.CreateImageFile(exercisesId);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        takePictureIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        startActivityForResult(takePictureIntent, 1);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        showToast(permissions.toString() + "权限拒绝");
                    }
                });
                break;
        }
    }

    private void submitResult() {
        if (!TextUtils.isEmpty(onlineWorkType))
            if (onlineWorkType.equals(ComFlag.NumFlag.WORKTYPE_SIMPLE + "")) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                HashMap<String, Object> mapParts = new HashMap<>();
                builder.addFormDataPart("onlineWorkId", onlineWorkId);
                builder.addFormDataPart("workPushLogId", workPushLogId);
                String pictureStr = Utils.saveCameraImage(bmpPhoto, onlineWorkId);
                File file = new File(pictureStr);
                if (file.exists()) {
                    RequestBody bodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    builder.addFormDataPart("file", file.getName(), bodyFile);
                }
                ApiUtil.doDefaultApi(api.submitSimpleHomework(builder.build().parts()), new HttpSucess<String>() {
                    @Override
                    public void onSucess(String data) {
                        SubjectActivityWrite.this.showToast("提交成功");
                        finish();
                        return;
                    }
                }, ViewControlUtil.create2Dialog(SubjectActivityWrite.this));
            } else
                setResult();
        else
            setResult();
    }

    private void setResult() {
        if (bmpPhoto != null) {
            String pictureStr = Utils.saveCameraImage(bmpPhoto, exercisesId);
//            String pictureStr = Utils.CreateImageFile(exercisesId).getAbsolutePath();
            Intent intent = getIntent();
            intent.putExtra("pictureStr", pictureStr);
            intent.putExtra("exercisesId", exercisesId);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    @Override
    protected void initData() {
        super.initData();
        api = ApiUtil.createUploadApi(UserService.class, token);
        getData();
    }

    private void getData() {
        ApiUtil.doDefaultApi(api.selectHomeworkInfo(onlineWorkId, workPushLogId, onlineWorkType), new HttpSucess<BeanSubject>() {
            @Override
            public void onSucess(BeanSubject data) {
                if (!TextUtils.isEmpty(data.workName))
                    webView_subjectwrite.loadDataWithBaseURL(null, data.workName, "text/html", "UTF-8", null);
                if (!TextUtils.isEmpty(data.studentAnswerFileUri)) {
                    ll_answer_edit.setVisibility(View.GONE);
                    sv_subjectWrite_answer.setVisibility(View.VISIBLE);
                    rl_answer_show.setVisibility(View.VISIBLE);
                    tvSunbjectWriteSave.setVisibility(View.GONE);
                    String body = "<img  src=\"" + data.studentAnswerFileUri + "\"/>";
                    String html = "<html><body>" + body + "</html></body>";
                    WebSettings webSettings = webView_participation_detail.getSettings();
                    webSettings.setLoadWithOverviewMode(true);
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setBlockNetworkImage(false);
                    webView_participation_detail.setWebViewClient(new MyWebViewClient(webView_participation_detail));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    }
                    webView_participation_detail.loadDataWithBaseURL("https://yishengjiaoyu.oss-cn-beijing.aliyuncs.com/", html, "text/html", "UTF-8", null);
                }
            }
        });
    }

    //加载progress
    private void initSaveProgressDlg() {
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }


    @Override
    public void onUndoRedoStatusChanged() {
        tvRepeal.setEnabled(mPaletteView.canUndo());
        tvRenew.setEnabled(mPaletteView.canRedo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                submitResult();
                Toast.makeText(this, "画板已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    // 使用startActivityForResult返回结果时调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回值是正常的话
        if (resultCode == Activity.RESULT_OK) {
            // 验证请求码是否一至，也就是startActivityForResult的第二个参数
            switch (requestCode) {
                case 1:
                    File mFilePath = Utils.CreateImageFile(exercisesId);
                    FileInputStream is = null;
                    try {
                        is = new FileInputStream(mFilePath);
                        // 把流解析成bitmap,此时就得到了清晰的原图
                        bmpPhoto = BitmapFactory.decodeStream(is);
                        //接下来就可以展示了（或者做上传处理）
                        ivPhoto.setVisibility(View.VISIBLE);
                        ivPhoto.setImageBitmap(bmpPhoto);
                        ivPhoto.setBackground(null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    bmpPhoto = (Bitmap) data.getExtras().get("data");
//                    ivPhoto.setVisibility(View.VISIBLE);
//                    ivPhoto.setImageBitmap(bmpPhoto);
//                    ivPhoto.setBackground(null);
                    //saveCameraImage(data);
                    break;
            }
        }
    }
}
