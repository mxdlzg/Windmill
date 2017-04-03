package android.mxdlzg.com.windmill.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.mxdlzg.com.windmill.config.Config;

import java.io.File;

/**
 * Created by 廷江 on 2017/3/28.
 */

public class ManageSetting {
    /**
     * 是否存在设置
     * @param context
     * @return
     */
    public static boolean settingExisted(Context context){
        File file = new File("/data/data/"+context.getPackageName()+"shared_prefs", Config.SETTING);
        if (file.exists()){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 添加字符串类型设置
     * @param context
     * @param name
     * @param value
     */
    public static void addStringSetting(Context context, String name, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(Config.SETTING,Context.MODE_APPEND).edit();
        editor.putString(name,value);
        editor.apply();
    }


    /**
     * 添加int型设置
     * @param context
     * @param name
     * @param value
     */
    public static void addIntSetting(Context context,String name,int value){
        SharedPreferences.Editor editor = context.getSharedPreferences(Config.SETTING,Context.MODE_APPEND).edit();
        editor.putInt(name,value);
        editor.apply();
    }

    /**
     * 添加Long型设置
     * @param context
     * @param name
     * @param value
     */
    public static void addLongSetting(Context context,String name,Long value){
        SharedPreferences.Editor editor = context.getSharedPreferences(Config.SETTING,Context.MODE_APPEND).edit();
        editor.putLong(name,value);
        editor.apply();
    }

    /**
     * 获取String设置
     * @param context
     * @param name
     * @return
     */
    public static String getStringSetting(Context context,String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SETTING,Context.MODE_PRIVATE);
        return sharedPreferences.getString(name,"");
    }

    /**
     * 获取Long设置
     * @param context
     * @param name
     * @return
     */
    public static Long getLongSetting(Context context,String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SETTING,Context.MODE_PRIVATE);
        return sharedPreferences.getLong(name,0);
    }

}
