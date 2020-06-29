package com.zwyl.myhomework.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.zwyl.myhomework.R;

/**
 * 头像选择对话框 拍照 or 相册
 */
public class AnalysisDialog extends Dialog {
    private Activity activity;

    public AnalysisDialog(Activity activity, String title, String data) {
        super(activity, R.style.dialog);
        this.activity = activity;
        View view = View.inflate(activity, R.layout.dialog_analysis, null);
        TextView tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        WebView webView_participation_detail = (WebView) view.findViewById(R.id.webView_participation_detail);
        if (!TextUtils.isEmpty(data))
            webView_participation_detail.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
        tv_dialog_title.setText(title);
        setContentView(view);

        //底部关闭按钮
        view.findViewById(R.id.tv_dialog_cancle).setOnClickListener(v -> {
            dismiss();
        });
    }
}

