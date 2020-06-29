package com.zwyl.myhomework.util;

import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zwyl.myhomework.App;
import com.zwyl.myhomework.R;
import com.zwyl.myhomework.main.subject.BeanSubject;
import com.zwyl.myhomework.main.subject.BeanSubjectFile;

import java.util.List;

/**
 * Created by BinBinWang on 2018/1/19.
 */

public class ViewUtil {

    public static final int dp9 = DensityUtil.dip2px(App.getContext(), 9);
    public static final int dp19 = DensityUtil.dip2px(App.getContext(), 19);
    public static final int dp420 = DensityUtil.dip2px(App.getContext(), 420);


    //单列文字
    public static View getTextView(CharSequence text) {
        TextView tv = new TextView(App.getContext());
        tv.setText(text);
        tv.setTextSize(27);
        tv.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        return tv;
    }

    //两列文字
    public static View getTextViewTwo(String str1, String str2) {
        LinearLayout linearLayout = new LinearLayout(App.mContext);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(App.getContext().getResources().getColor(R.color.gray_efef));
        TextView tv = new TextView(App.getContext());
        tv.setText(str1);
        tv.setTextSize(18);
        tv.setPadding(19, 0, 0, 0);
        tv.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        TextView tvStr = new TextView(App.getContext());
        tvStr.setPadding(19, 0, 0, 0);
        tvStr.setText(str2);
        tvStr.setTextSize(18);
        tvStr.setTextColor(App.getContext().getResources().getColor(R.color.gray_8888));
        // tvStr.setOnClickListener(listener);
        linearLayout.addView(tv);
        linearLayout.addView(tvStr);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    //横向线性布局
    public static LinearLayout get_ll_horizontal() {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp420);
        //        params.topMargin = ViewUtil.dp15;
        //        params.bottomMargin = ViewUtil.dp18;
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }
    public static LinearLayout getSingleView(List<BeanSubject.ExercisesItemListBean.ExerciseOptionListBean> mMultiple, int exerciseId, OnMultipleClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.topMargin = ViewUtil.dp9;
//        params.bottomMargin = ViewUtil.dp9;
//        params.leftMargin = ViewUtil.dp19;
        TextView textView = new TextView(App.mContext);
//        textView.setText(exerciseId + ". ");
        textView.setTextSize(18);
        textView.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        textView.setCompoundDrawables(App.mContext.getResources().getDrawable(R.mipmap.add), null, null, null);
        linearLayout.addView(textView);
        if (null != mMultiple) {
            CheckBox checkBoxarr[] = new CheckBox[mMultiple.size()];
            for (int i = 0; i < mMultiple.size(); i++) {
                CheckBox checkbox = new CheckBox(App.mContext);
                if (!mMultiple.get(0).optionNo.equals("A"))
                    checkbox.setText(Utils.getJudge(mMultiple.get(i).optionNo));
                else
                    checkbox.setText(mMultiple.get(i).optionNo);
                checkbox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                checkbox.setTextSize(18);
                checkbox.setPadding(19, 0, 0, 0);
                checkbox.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                int indexNum = i;
                checkBoxarr[i] = checkbox;
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        boolean checked = checkBox.isChecked();
                        listener.OnChecked(checked, indexNum, mMultiple.get(indexNum).optionId);
                    }
                });
                linearLayout.addView(checkbox);
            }
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                if (linearLayout.getChildAt(i) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                for (int j = 0; j < checkBoxarr.length; j++) {
                                    if (checkBoxarr[j].getText().toString().equals(buttonView.getText().toString())) {
                                        checkBoxarr[j].setChecked(true);
                                    } else {
                                        checkBoxarr[j].setChecked(false);
                                    }
                                }

                        }
                    });
                }
            }
        }
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    /**
     * @param mRadio //单选
     * @param mJudge //判断
     */
    //获取RadioGroup_题库
    public static LinearLayout getRadioGroup(List<BeanSubject.ExercisesItemListBean.ExerciseOptionListBean> mRadio, int[] mJudge, int exerciseNo, OncheckedClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setBackgroundColor(App.getContext().getResources().getColor(R.color.gray_efef));
        params.topMargin = ViewUtil.dp9;
        params.bottomMargin = ViewUtil.dp9;
        params.leftMargin = ViewUtil.dp19;
        TextView textView = new TextView(App.mContext);
        textView.setText(exerciseNo + ". ");
        textView.setTextSize(18);
        textView.setPadding(19, 0, 0, 0);
        textView.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        textView.setCompoundDrawables(App.mContext.getResources().getDrawable(R.mipmap.add), null, null, null);
        linearLayout.addView(textView);
        RadioGroup radioGroup = new RadioGroup(App.mContext);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        if (null != mRadio) {//单选题
            for (int i = 0; i < mRadio.size(); i++) {
                RadioButton radioButton = new RadioButton(App.mContext);
                radioButton.setPadding(10, 0, 0, 0);
                radioButton.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                radioButton.setText(mRadio.get(i).optionNo);
                radioButton.setTextSize(18);
                radioButton.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                radioGroup.addView(radioButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            //判断题
        } else {
            for (int i = 0; i < mJudge.length; i++) {
                RadioButton radioButton = new RadioButton(App.mContext);
                Drawable drawable = App.mContext.getResources().getDrawable(mJudge[i]);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                radioButton.setCompoundDrawables(drawable, null, null, null);
                radioGroup.addView(radioButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int indexNum = -1;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton viewById1 = (RadioButton) group.findViewById(checkedId);
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (group.getChildAt(i) == viewById1) {
                        indexNum = i;
                    }
                }
                //修改
                if (mRadio != null)
                    listener.OnChecked(indexNum, mRadio.get(indexNum).optionId);
                else
                    listener.OnChecked(indexNum, indexNum == 0 ? "1" : "0");
            }
        });
        linearLayout.addView(radioGroup);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }
    //附件修改后单选题
    public static LinearLayout getSingleViewFile(List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> mMultiple, int exerciseId, ViewUtil.OnMultipleClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ViewUtil.dp9;
        params.bottomMargin = ViewUtil.dp9;
        params.leftMargin = ViewUtil.dp19;
        TextView textView = new TextView(App.mContext);
        textView.setText(exerciseId + ". ");
        textView.setTextSize(18);
        textView.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        textView.setCompoundDrawables(App.mContext.getResources().getDrawable(R.mipmap.add), null, null, null);
        linearLayout.addView(textView);
        if (null != mMultiple) {
            CheckBox checkBoxarr[] = new CheckBox[mMultiple.size()];
            for (int i = 0; i < mMultiple.size(); i++) {
                CheckBox checkbox = new CheckBox(App.mContext);
                if (!mMultiple.get(0).optionNo.equals("A"))
                    checkbox.setText(Utils.getJudge(mMultiple.get(i).optionNo));
                else
                    checkbox.setText(mMultiple.get(i).optionNo);
                checkbox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                checkbox.setTextSize(18);
                checkbox.setPadding(19, 0, 0, 0);
                checkbox.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                int indexNum = i;
                checkBoxarr[i] = checkbox;
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        boolean checked = checkBox.isChecked();
                        listener.OnChecked(checked, indexNum, mMultiple.get(indexNum).optionId);
                    }
                });
                linearLayout.addView(checkbox);
            }
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                if (linearLayout.getChildAt(i) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                for (int j = 0; j < checkBoxarr.length; j++) {
                                    if (checkBoxarr[j].getText().toString().equals(buttonView.getText().toString())) {
                                        checkBoxarr[j].setChecked(true);
                                    } else {
                                        checkBoxarr[j].setChecked(false);
                                    }
                                }

                        }
                    });
                }
            }
        }
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    /**
     * @param mRadio //单选
     * @param mJudge //判断
     * @param rankNo
     */
    //获取RadioGroup_附件
    public static LinearLayout getRadioGroupFile(List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> mRadio, int[] mJudge, int rankNo, OncheckedClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        linearLayout.setBackgroundColor(App.getContext().getResources().getColor(R.color.gray_efef));
        params.topMargin = ViewUtil.dp9;
        params.bottomMargin = ViewUtil.dp9;
        params.leftMargin = ViewUtil.dp19;
        TextView textView = new TextView(App.mContext);
        textView.setText(rankNo + ". ");
        textView.setTextSize(18);
        textView.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        textView.setCompoundDrawables(App.mContext.getResources().getDrawable(R.mipmap.add), null, null, null);
        linearLayout.addView(textView);
        RadioGroup radioGroup = new RadioGroup(App.mContext);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        if (null != mRadio) {//单选题
            for (int i = 0; i < mRadio.size(); i++) {
                RadioButton radioButton = new RadioButton(App.getContext());
                radioButton.setPadding(10, 10, 10, 10);
                radioButton.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                radioButton.setText(mRadio.get(i).optionNo);
                radioButton.setTextSize(18);
                radioButton.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                radioButton.setButtonDrawable(android.R.color.transparent);
                radioButton.setBackgroundResource(R.drawable.select_frame_grayorgreen);
                radioGroup.addView(radioButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            //判断题
        } else {
            for (int i = 0; i < mJudge.length; i++) {
                RadioButton radioButton = new RadioButton(App.mContext);
                Drawable drawable = App.mContext.getResources().getDrawable(mJudge[i]);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                radioButton.setCompoundDrawables(drawable, null, null, null);
                radioGroup.addView(radioButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int indexNum = -1;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton viewById1 = (RadioButton) group.findViewById(checkedId);
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (group.getChildAt(i) == viewById1) {
                        indexNum = i;
                    }
                }
                listener.OnChecked(indexNum, mRadio.get(indexNum).optionId);
            }
        });
        linearLayout.addView(radioGroup);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    /**
     * @param
     */
    //题库多选题
    public static LinearLayout getRadioGroupMultiple(List<BeanSubject.ExercisesItemListBean.ExerciseOptionListBean> mMultiple, OnMultipleClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        linearLayout.setBackgroundColor(App.getContext().getResources().getColor(R.color.gray_efef));
//        params.topMargin = ViewUtil.dp9;
//        params.bottomMargin = ViewUtil.dp9;
//        params.leftMargin = ViewUtil.dp19;
        if (null != mMultiple) {
            for (int i = 0; i < mMultiple.size(); i++) {
                CheckBox checkbox = new CheckBox(App.mContext);
                checkbox.setText(mMultiple.get(i).optionNo);
                checkbox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                checkbox.setTextSize(18);
                checkbox.setPadding(19, 0, 0, 0);
                checkbox.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                int indexNum = i;
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        boolean checked = checkBox.isChecked();
                        listener.OnChecked(checked, indexNum, mMultiple.get(indexNum).optionId);
                    }
                });
                linearLayout.addView(checkbox);
            }
        }
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    /**
     * @param
     */
    //附件多选题
    public static LinearLayout getRadioGroupMultipleFile(List<BeanSubjectFile.FileWorkAnswerCardMapListBean.ExerciseOptionListBean> mMultiple, int exerciseId, OnMultipleClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ViewUtil.dp9;
        params.bottomMargin = ViewUtil.dp9;
        params.leftMargin = ViewUtil.dp19;
        TextView textView = new TextView(App.mContext);
        textView.setText(exerciseId + ". ");
        textView.setTextSize(18);
        textView.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        textView.setCompoundDrawables(App.mContext.getResources().getDrawable(R.mipmap.add), null, null, null);
        linearLayout.addView(textView);
        if (null != mMultiple) {
            for (int i = 0; i < mMultiple.size(); i++) {
                CheckBox checkbox = new CheckBox(App.mContext);
                checkbox.setText(mMultiple.get(i).optionNo);
                checkbox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                checkbox.setTextSize(18);
                checkbox.setPadding(19, 0, 0, 0);
                checkbox.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                int indexNum = i;
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        boolean checked = checkBox.isChecked();
                        listener.OnChecked(checked, indexNum, mMultiple.get(indexNum).optionId);
                    }
                });
                linearLayout.addView(checkbox);
            }
        }
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    public static View showAnswerFrame(String str2) {
        LinearLayout linearLayout = new LinearLayout(App.mContext);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, 10, 0, 0);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
//        linearLayout.setBackgroundColor(App.getContext().getResources().getColor(R.color.gray_efef));
//        webView.setImageResource(R.mipmap.textbook);
//        TextView tv = new TextView(App.getContext());
//        tv.setText(str1);
//        tv.setTextSize(18);
//        tv.setPadding(19, 0, 0, 0);
//        tv.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        TextView tvStr = new TextView(App.getContext());
        tvStr.setPadding(19, 0, 0, 0);
        tvStr.setText(str2);
        tvStr.setTextSize(18);
        tvStr.setTextColor(App.getContext().getResources().getColor(R.color.gray_8888));
        ImageView img = new ImageView(App.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 150);
        img.setPadding(2, 2, 2, 2);
        img.setBackgroundResource(R.drawable.bg_frame_item);
        img.setLayoutParams(layoutParams);
//        ImageView imgExplain = new ImageView(App.getContext());
//        imgExplain.setBackgroundResource(R.mipmap.explain);
//        imgExplain.setPadding(100,0,0,0);
        // tvStr.setOnClickListener(listener);
        linearLayout.addView(tvStr);
        linearLayout.addView(img);
//        linearLayout.addView(imgExplain);
//        linearLayout.addView(tv);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }


    public interface OncheckedClick {
        void OnChecked(int indexNum, String optionId);

    }

    public interface OnMultipleClick {
        void OnChecked(boolean isChecked, int indexNum, String optionId);
    }

}
