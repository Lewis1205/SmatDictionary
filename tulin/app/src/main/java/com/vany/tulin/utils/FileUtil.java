package com.vany.tulin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.vany.tulin.dto.Sentence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by van元 on 2017/2/5.
 */

public class FileUtil {

    private static String PHOTOROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/intelD/";
    private static SharedPreferences permissionsp;

    /**
     * 该方法只用于安卓6.0以上的用户安装完成之后，设置应用所需的权限，只执行一次
     * @param context
     * @return
     */
    public static boolean executeOnlyOnce(Context context) {
        permissionsp = context.getSharedPreferences("permission", Context.MODE_PRIVATE);
        return permissionsp.getBoolean("flag", true); //默认值为true
    }
    public static void executeOnlyOnceChangeFalse(Context context) {
        permissionsp = context.getSharedPreferences("permission", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = permissionsp.edit();
        edit.putBoolean("flag", false);
        edit.commit();
        permissionsp = null;
    }

    /**
     * 判断句子是否存在
     * @return
     */
    public static boolean isSentenceExist(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sentence", Context.MODE_PRIVATE);
        String sentence = sp.getString("sentence", "");
        if (sentence.equals("")) {
            return false;
        }
        return true;
    }

    /**
     * 获取每日一句
     * @param context
     * @return
     */
    public static Sentence getSentence(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sentence", Context.MODE_PRIVATE);
        Sentence st = new Sentence();
        st.setSentence(sp.getString("sentence",""));
        st.setMeaning(sp.getString("meaning",""));
        st.setPronunce(sp.getString("pronunce",""));
        st.setDate(sp.getString("date",""));
        return st;
    }
    /**
     * 保存每日一句的信息
     * @param context
     * @param st
     */
    public static void saveSentence(Context context, Sentence st) {
        SharedPreferences sp = context.getSharedPreferences("sentence", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("sentence", st.getSentence());
        edit.putString("meaning", st.getMeaning());
        edit.putString("pronunce", st.getPronunce());
        edit.putString("date", st.getDate());
        edit.commit();
    }

    /**
     * 判断今天的图片是否存在
     * @return
     */
    public static boolean isExist() {
        String path =PHOTOROOTPATH+new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".jpg";
        return new File(path).exists();
    }

    /**
     * 从SD卡中读图片
     * @return
     */
    public static Bitmap readBitmap() {
        String path =PHOTOROOTPATH+new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".jpg";
        if (new File(path).exists()) {
            return BitmapFactory.decodeFile(path);
        }
        return null;
    }

    /**
     * 保存图片到SD卡intelD目录下
     *
     * @param bitmap
     */
    public static void saveBitmap(final Bitmap bitmap) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String phototodayPath = PHOTOROOTPATH + sf.format(new Date()) + ".jpg";    ///storage/sdcard0/intelD/20170205.jpg
        String photoyesterdayPath = PHOTOROOTPATH + sf.format(new Date(new Date().getTime() - 1 * 24 * 60 * 60 * 1000)) + ".jpg";    ///storage/sdcard0/intelD/20170205.jpg
        //创建文件对象，用来存储新的图像文件
        File dirfile = new File(PHOTOROOTPATH);    ///storage/sdcard0/intelD
        File yesterday = new File(photoyesterdayPath);
        File today = new File(phototodayPath);
        //创建文件
        try {
            if (!dirfile.exists()) {
                dirfile.mkdirs();
            }
            if (today.exists()) { //如果是今天的图片，就不需要重新保存了直接就返回
                System.out.println("因为图片已经存在了，所以返回啦");
                return;
            }
            if (yesterday.exists()) {   //如果昨天的图片存在就删除
                yesterday.delete();
            }
            today.createNewFile();
            //定义文件输出流
            FileOutputStream fout = new FileOutputStream(today);
            //将bitmap存储为jpg格式的图片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();//刷新文件流
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
