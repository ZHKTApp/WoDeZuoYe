package com.zwyl.myhomework.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.ComFlag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*40101: 未提交
40102：已提交
40103：未批改
40104：已批改*/
public class Utils {
    public static String getSubjectStatus(int statusCode) {
        switch (statusCode) {
            case ComFlag.NumFlag.WORKSTATUS_UNSUBMIT:
                return "未提交";
            case ComFlag.NumFlag.WORKSTATUS_SUBMIT:
                return "已提交";
            case ComFlag.NumFlag.WORKSTATUS_UNCORRECT:
                return "未批改";
            case ComFlag.NumFlag.WORKSTATUS_CORRECT:
                return "已批改";
        }
        return "";
    }

    public static boolean isPic(String format) {
        if (format.endsWith(".jpg") || format.endsWith(".jpeg") || format.endsWith(".bmp") || format.endsWith(".png")) {
            return true;
        }
        return false;
    }


    public static String getJudge(String mJudge) {
        switch (mJudge) {
            case "0":
            case "错":
                return "×";
            case "1":
            case "对":
                return "√";
            default:
                return "";

        }
    }

    public static String getPicHtml(String mUri) {
        String body = "<img  src=\"" + mUri + "\"  width=\"60%\" />";
        String html = "<html><body>" + body + "</body></html>";
        return html;
    }

    public static String getSubjectNo(int pos) {
        switch (pos) {
            case 0:
                return "一、";
            case 1:
                return "二、";
            case 2:
                return "三、";
            case 3:
                return "四、";
            case 4:
                return "五、";
            case 5:
                return "六、";
            case 6:
                return "七、";
            case 7:
                return "八、";
            case 8:
                return "九、";
            case 9:
                return "十、";
            default:
                return "";
        }
    }

    public static String getsubjectType(int selectId) {
        switch (selectId) {
            case ComFlag.NumFlag.EXERCISE_RADIO:
                return "单选题";
            case ComFlag.NumFlag.EXERCISE_MULTIPLE:
                return "多选题";
            case ComFlag.NumFlag.EXERCISE_JUDGE:
                return "判断题";
            case ComFlag.NumFlag.EXERCISE_GAP:
                return "填空题";
            case ComFlag.NumFlag.EXERCISE_COUNT:
                return "计算题";
            case ComFlag.NumFlag.EXERCISE_SHORT:
                return "简答题";

        }
        return "";
    }

    public static String getABC(int selectId) {
        switch (selectId) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "C";
        }
        return "";
    }

    public static String bitmapToString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    public static File CreateImageFile(String imageFileName) {
        File mFilePath = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, imageFileName + ".jpg");
        return mFilePath;
    }

    /**
     * 保存相机的图片
     **/
    public static String saveCameraImage(Bitmap bmp, String pictureName) {
        // 检查sd card是否存在
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i("错误信息", "sd card is not avaiable/writeable right now.");
        }
        // 为图片命名啊
        // String name = new DateFormat().format("yyyymmdd", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        // 保存文件
        FileOutputStream fos = null;
        String pictureStr = Utils.CreateImageFile(pictureName).getAbsolutePath();
        Log.e("pic", "saveimage : " + pictureStr);
        File file = new File("/mnt/sdcard/DCIM/");
        file.mkdirs();// 创建文件夹
        String fileName = "/mnt/sdcard/DCIM/" + pictureName + ".jpg";// 保存路径

        try {// 写入SD card
            fos = new FileOutputStream(pictureStr);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            return file.getAbsolutePath() + "/" + pictureName + ".jpg";
            return pictureStr;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }// 显示图片
        // ((ImageView) findViewById(R.id.show_image)).setImageBitmap(bmp);
        return null;
    }


    /**
     * 文字转图片
     */
    public static Bitmap textAsBitmap(String text, float textSize) {


        TextPaint textPaint = new TextPaint();

        // textPaint.setARGB(0x31, 0x31, 0x31, 0);
        textPaint.setColor(Color.BLACK);

        textPaint.setTextSize(textSize);

        StaticLayout layout = new StaticLayout(text, textPaint, 450, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20, layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);

        layout.draw(canvas);
        Log.d("textAsBitmap", String.format("1:%d %d", layout.getWidth(), layout.getHeight()));
        return bitmap;
    }

    public static void fitsSystemWindows(boolean isTranslucentStatus, View view) {
        if (isTranslucentStatus) {
            view.getLayoutParams().height = calcStatusBarHeight(view.getContext());
        }
    }

    public static int calcStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

}
