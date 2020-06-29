package com.zwyl.myhomework.dialog.popwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.zwyl.myhomework.R;
import com.zwyl.myhomework.util.DensityUtil;

public class Popup_SunjectTive<T> extends PopupWindow {

    public Popup_SunjectTive(Context context,  OnClickListener listener) {
        super(context);
        setHeight(DensityUtil.dip2px(364));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View contentView = LayoutInflater.from(context).inflate(R.layout.popwindow_subjective, null, false);
        setContentView(contentView);

    }


    public interface OnClickListener {
        void onClick(int position);
    }
}

